"""Token pagination contracts using fake HTTP responses."""

import json
import unittest
from unittest.mock import AsyncMock, MagicMock
from urllib.parse import parse_qs, urlsplit

import aiohttp

from newnanmanager.config import ClientConfig
from newnanmanager.http_client import HttpClient
from newnanmanager.services.token import TokenService


class TokenPaginationTests(unittest.IsolatedAsyncioTestCase):
    async def test_list_preserves_pagination_metadata_and_request_parameters(self) -> None:
        cases: list[tuple[int, int, int]] = [(0, 1, 20), (4294967296, 2, 100)]
        for total, page, page_size in cases:
            with self.subTest(total=total, page=page, page_size=page_size):
                body: dict[str, object] = {
                    "tokens": [], "total": total, "page": page, "page_size": page_size,
                }
                client = HttpClient(
                    ClientConfig(base_url="https://audit.invalid", token="synthetic")
                )
                session = MagicMock(spec=aiohttp.ClientSession)
                session.closed = False
                response = MagicMock(spec=aiohttp.ClientResponse)
                response.status = 200
                response.text = AsyncMock(return_value=json.dumps(body))
                session.request.return_value.__aenter__ = AsyncMock(return_value=response)
                client._session = session

                result = await TokenService(client).list_api_tokens(page, page_size)

                self.assertEqual(session.request.call_count, 1)
                self.assertEqual(session.request.call_args.args[0], "GET")
                request_url = urlsplit(session.request.call_args.args[1])
                self.assertEqual(request_url.path, "/api/v1/tokens")
                self.assertEqual(
                    parse_qs(request_url.query),
                    {"page": [str(page)], "page_size": [str(page_size)]},
                )
                self.assertEqual(result.tokens, [])
                self.assertEqual(result.total, total)
                self.assertEqual(result.page, page)
                self.assertEqual(result.page_size, page_size)
                self.assertEqual(result.model_dump(), body)
