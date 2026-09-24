"""HTTP regressions using fake responses and loopback only."""

import asyncio
import logging
import unittest
from unittest.mock import AsyncMock, MagicMock

import aiohttp
from aiohttp import web

from newnanmanager.config import AuthScheme, ClientConfig
from newnanmanager.exceptions import ApiErrorException, HttpException, TimeoutException
from newnanmanager.http_client import HttpClient


class HttpClientTests(unittest.IsolatedAsyncioTestCase):
    def make_client(self) -> tuple[HttpClient, MagicMock]:
        client = HttpClient(ClientConfig(base_url="https://audit.invalid", token="synthetic", retry_delay=0))
        session = MagicMock(spec=aiohttp.ClientSession)
        session.closed = False
        client._session = session
        return client, session

    def response(self, body: str) -> MagicMock:
        response = MagicMock(spec=aiohttp.ClientResponse)
        response.status = 200
        response.ok = True
        response.text = AsyncMock(return_value=body)
        return response

    async def test_post_is_not_replayed_after_timeout(self) -> None:
        client, session = self.make_client()
        session.request.return_value.__aenter__ = AsyncMock(side_effect=[asyncio.TimeoutError(), self.response("{}")])
        with self.assertRaises(TimeoutException):
            await client.post("/api/v1/tokens", {"name": "audit", "role": "monitor"})
        self.assertEqual(session.request.call_count, 1)

    async def test_get_retries_transient_transport_failure(self) -> None:
        client, session = self.make_client()
        session.request.return_value.__aenter__ = AsyncMock(side_effect=[aiohttp.ClientConnectionError(), self.response('{"total": 1}')])
        self.assertEqual(await client.get("/api/v1/players"), {"total": 1})
        self.assertEqual(session.request.call_count, 2)

    async def test_debug_log_excludes_request_and_response_data(self) -> None:
        client, session = self.make_client()
        session.request.return_value.__aenter__ = AsyncMock(return_value=self.response('{"token_value": "response-secret-marker"}'))
        with self.assertLogs("newnanmanager.http_client", level=logging.DEBUG) as captured:
            await client.post("/api/v1/tokens", {"name": "request-private-marker"})
        output = "\n".join(captured.output)
        self.assertNotIn("response-secret-marker", output)
        self.assertNotIn("request-private-marker", output)

    async def test_http_error_preserves_response_metadata(self) -> None:
        client, session = self.make_client()
        response = self.response('{"detail": "rate limited"}')
        response.status = 429
        response.headers = {"X-Request-ID": "audit-request", "Retry-After": "60"}
        session.request.return_value.__aenter__ = AsyncMock(return_value=response)
        with self.assertRaises(ApiErrorException) as caught:
            await client.get("/api/v1/players")
        self.assertEqual(caught.exception.request_id, "audit-request")
        self.assertEqual(caught.exception.retry_after, "60")
        self.assertEqual(caught.exception.status_code, 429)
        self.assertEqual(session.request.call_count, 1)

    async def test_template_error_response_is_parsed_with_legacy_fallback(self) -> None:
        client, session = self.make_client()
        response = self.response(
            '{"code":1004000,"message":"invalid request parameters",'
            '"request_id":"body-request","trace_id":"trace-1",'
            '"error":{"category":"invalid_argument","code":"common.invalid_argument"}}'
        )
        response.status = 400
        response.headers = {"Retry-After": "2"}
        session.request.return_value.__aenter__ = AsyncMock(return_value=response)
        with self.assertRaises(ApiErrorException) as caught:
            await client.get("/api/v1/players")
        self.assertEqual(caught.exception.message, "invalid request parameters")
        self.assertEqual(caught.exception.api_code, 1004000)
        self.assertEqual(caught.exception.error_category, "invalid_argument")
        self.assertEqual(caught.exception.machine_code, "common.invalid_argument")
        self.assertEqual(caught.exception.request_id, "body-request")
        self.assertEqual(caught.exception.trace_id, "trace-1")

    async def test_authorization_is_not_forwarded_to_redirect_target(self) -> None:
        seen: list[dict[str, str]] = []
        source_headers: list[dict[str, str]] = []

        async def target(request: web.Request) -> web.Response:
            seen.append(dict(request.headers))
            return web.json_response({})

        target_app = web.Application()
        target_app.router.add_get("/target", target)
        target_runner = web.AppRunner(target_app)
        await target_runner.setup()
        target_site = web.TCPSite(target_runner, "127.0.0.1", 0)
        await target_site.start()
        target_port = target_runner.addresses[0][1]

        async def source(request: web.Request) -> web.Response:
            source_headers.append(dict(request.headers))
            raise web.HTTPFound(f"http://localhost:{target_port}/target")

        source_app = web.Application()
        source_app.router.add_get("/redirect", source)
        source_runner = web.AppRunner(source_app)
        await source_runner.setup()
        source_site = web.TCPSite(source_runner, "127.0.0.1", 0)
        await source_site.start()
        source_port = source_runner.addresses[0][1]
        try:
            async with HttpClient(ClientConfig(base_url=f"http://127.0.0.1:{source_port}", token="synthetic", max_retries=0)) as client:
                with self.assertRaises(HttpException) as caught:
                    await client.get("/redirect")
            self.assertEqual(caught.exception.status_code, 302)
            self.assertEqual(source_headers[0].get("Authorization"), "Bearer synthetic")
            self.assertNotIn("X-API-Token", source_headers[0])
            self.assertEqual(seen, [])
        finally:
            await source_runner.cleanup()
            await target_runner.cleanup()

    async def test_api_token_scheme_sends_only_api_token_header(self) -> None:
        client = HttpClient(
            ClientConfig(
                base_url="https://audit.invalid",
                token="api-secret",
                auth_scheme=AuthScheme.API_TOKEN,
            )
        )
        session = MagicMock(spec=aiohttp.ClientSession)
        session.closed = False
        session.request.return_value.__aenter__ = AsyncMock(
            return_value=self.response("{}")
        )
        client._session = session

        await client.get("/api/v1/players")

        self.assertEqual(client._headers["X-API-Token"], "api-secret")
        self.assertNotIn("Authorization", client._headers)
