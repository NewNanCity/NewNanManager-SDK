"""Contract regressions with model fixtures and fake HTTP responses."""

import json
import unittest
from unittest.mock import AsyncMock, MagicMock
from urllib.parse import parse_qs, urlsplit

import aiohttp
from pydantic import BaseModel

from newnanmanager.config import ClientConfig
from newnanmanager.http_client import HttpClient
from newnanmanager.session import SessionContext
from newnanmanager.models import (
    ApiToken,
    CreateApiTokenRequest,
    HeartbeatRequest,
    MonitorStatRecord,
    MonitorStatsData,
    PlayerValidateInfo,
    ServerStatus,
    UpdateApiTokenRequest,
    ValidateRequest,
)
from newnanmanager.services.monitor import MonitorService
from newnanmanager.services.player import PlayerService
from newnanmanager.services.player_server import PlayerServerService


VALID_SESSION_ID = "0123456789abcdef0123456789abcdef"
from newnanmanager.services.token import TokenService

TOKEN: dict[str, object] = {
    "id": 1, "name": "audit", "role": "server", "active": True, "server_id": 7,
    "created_at": "2026-09-06T00:00:00Z", "updated_at": "2026-09-06T00:00:00Z",
}


class ModelContractTests(unittest.TestCase):
    def test_new_optional_fields_survive_model_parsing(self) -> None:
        cases: list[tuple[type[BaseModel], dict[str, object], dict[str, object]]] = [
            (ApiToken, TOKEN, {"server_id": 7}),
            (CreateApiTokenRequest, {"name": "audit", "role": "server", "server_id": 7}, {"server_id": 7}),
            (UpdateApiTokenRequest, {"server_id": 7, "active": False}, {"server_id": 7, "active": False}),
            (ServerStatus, {"server_id": 7, "online": True, "current_players": 0, "max_players": 10,
                            "expire_at": "2026-09-06T00:01:00Z", "last_heartbeat": "2026-09-06T00:00:00Z",
                            "measurement_type": "pull", "latency_metric": "rtt"},
             {"measurement_type": "pull", "latency_metric": "rtt"}),
            (MonitorStatRecord, {"timestamp": 1, "current_players": 0, "measurement_type": "unknown", "latency_metric": "legacy"},
             {"measurement_type": "unknown", "latency_metric": "legacy"}),
            (MonitorStatsData, {"server_id": 7, "stats": [], "next_cursor": "opaque&+/#"}, {"next_cursor": "opaque&+/#"}),
        ]
        for model_type, payload, expected in cases:
            with self.subTest(model=model_type.__name__):
                fields = model_type.model_validate(payload).model_dump(exclude_none=True)
                for key, value in expected.items():
                    self.assertEqual(fields.get(key), value)


class HttpContractTests(unittest.IsolatedAsyncioTestCase):
    def make_client(self, body: dict[str, object]) -> tuple[HttpClient, MagicMock]:
        client = HttpClient(ClientConfig(base_url="https://audit.invalid", token="synthetic"))
        session = MagicMock(spec=aiohttp.ClientSession)
        session.closed = False
        response = MagicMock(spec=aiohttp.ClientResponse)
        response.status = 200
        response.text = AsyncMock(return_value=json.dumps(body))
        session.request.return_value.__aenter__ = AsyncMock(return_value=response)
        client._session = session
        return client, session

    async def test_token_binding_is_sent_and_decoded(self) -> None:
        http, session = self.make_client({"token_info": TOKEN, "token_value": "synthetic"})
        created = await TokenService(http).create_api_token(CreateApiTokenRequest(name="audit", role="server", server_id=7))
        self.assertEqual(session.request.call_args.kwargs["json"], {"name": "audit", "role": "server", "server_id": 7})
        self.assertEqual(created.token_info.server_id, 7)
        http, session = self.make_client(TOKEN)
        updated = await TokenService(http).update_api_token(1, UpdateApiTokenRequest(server_id=7, active=False))
        self.assertEqual(session.request.call_args.kwargs["json"], {"server_id": 7, "active": False})
        self.assertEqual(updated.server_id, 7)

    async def test_monitor_cursor_and_unknown_metadata_are_preserved(self) -> None:
        cursor = "opaque&+/#"
        http, session = self.make_client({"server_id": 7, "stats": [
            {"timestamp": 1, "current_players": 0},
            {"timestamp": 2, "current_players": 0, "measurement_type": "pull", "latency_metric": "rtt"},
        ], "next_cursor": cursor})
        result = await MonitorService(http).get_monitor_stats(7, 0, 86400, limit=10000, cursor=cursor)
        query = parse_qs(urlsplit(session.request.call_args.args[1]).query)
        self.assertEqual(query, {"since": ["0"], "duration": ["86400"], "limit": ["10000"], "cursor": [cursor]})
        self.assertEqual(result.next_cursor, cursor)
        self.assertIsNone(result.stats[0].measurement_type)
        self.assertIsNone(result.stats[0].latency_metric)
        self.assertEqual(result.stats[1].measurement_type, "pull")
        self.assertEqual(result.stats[1].latency_metric, "rtt")

    async def test_periodic_snapshots_are_sent_once_without_name_changes(self) -> None:
        for count in (0, 1000):
            http, session = self.make_client({"results": [], "processed_at": 1})
            request = ValidateRequest(server_id=7, login=False, players=[
                PlayerValidateInfo(player_name=f"MiXeD{index}", ip="192.0.2.1") for index in range(count)
            ])
            await PlayerService(http).validate(request)
            self.assertEqual(session.request.call_count, 1)
            self.assertEqual(session.request.call_args.kwargs["json"], request.model_dump(exclude_none=True))

    async def test_session_fencing_headers_are_sent_only_for_explicit_session(self) -> None:
        session_context = SessionContext(id=VALID_SESSION_ID, epoch=7)
        http, session = self.make_client({"results": [], "processed_at": 1})
        await PlayerService(http).validate(
            ValidateRequest(server_id=7, login=False, players=[]), session_context
        )
        headers = session.request.call_args.kwargs["headers"]
        self.assertEqual(headers, {
            "X-NNM-Session-ID": VALID_SESSION_ID,
            "X-NNM-Session-Epoch": "7",
        })

        http, session = self.make_client({
            "received_at": 1,
            "response_at": 2,
            "expire_duration_ms": 30000,
        })
        await MonitorService(http).heartbeat(
            7,
            HeartbeatRequest(current_players=0, max_players=20),
            session_context,
        )
        self.assertEqual(
            session.request.call_args.kwargs["headers"]["X-NNM-Session-Epoch"], "7"
        )

        http, session = self.make_client({})
        await PlayerServerService(http).set_players_offline(7, [1], session_context)
        self.assertEqual(
            session.request.call_args.kwargs["headers"]["X-NNM-Session-ID"], VALID_SESSION_ID
        )

    async def test_server_session_is_created_from_api_response(self) -> None:
        http, session = self.make_client({"session_id": VALID_SESSION_ID, "session_epoch": 8})
        session_context = await MonitorService(http).create_server_session(7)
        self.assertEqual(session_context, SessionContext(id=VALID_SESSION_ID, epoch=8))
        self.assertEqual(session.request.call_args.kwargs["json"], {})

    def test_invalid_session_is_rejected_before_use(self) -> None:
        with self.assertRaises(ValueError):
            SessionContext(id=" ", epoch=1)
        with self.assertRaises(ValueError):
            SessionContext(id="too-short", epoch=1)
        with self.assertRaises(ValueError):
            SessionContext(id=VALID_SESSION_ID, epoch=0)
