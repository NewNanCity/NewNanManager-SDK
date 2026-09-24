package com.nanmanager.bukkit

import com.nanmanager.bukkit.exceptions.ApiException
import com.nanmanager.bukkit.exceptions.HttpException
import com.nanmanager.bukkit.exceptions.JsonParseException
import com.nanmanager.bukkit.http.HttpClient
import com.nanmanager.bukkit.models.Player
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull

class HttpClientTest {
    @Test
    fun queryValuesAreEncodedWithoutInjectingAdditionalParameters() {
        MockWebServer().use { server ->
            server.enqueue(MockResponse().setBody("{}"))
            HttpClient(server.url("/").toString().trimEnd('/'), "synthetic").use { client ->
                val search = "a&ban_mode=2#value + encoded"
                client.get<Unit>("/api/v1/players", mapOf("search" to search, "page" to 1))
                val request = server.takeRequest()
                assertEquals(search, request.requestUrl?.queryParameter("search"))
                assertNull(request.requestUrl?.queryParameter("ban_mode"))
                assertEquals("1", request.requestUrl?.queryParameter("page"))
                assertEquals("Bearer synthetic", request.getHeader("Authorization"))
                assertNull(request.getHeader("X-API-Token"))
            }
        }
    }

    @Test
    fun structuredErrorsKeepTheirApiExceptionType() {
        MockWebServer().use { server ->
            server.enqueue(MockResponse().setResponseCode(429).setBody("""{"detail":"rate limited"}""")
                .setHeader("X-Request-ID", "audit-request").setHeader("Retry-After", "60"))
            HttpClient(server.url("/").toString().trimEnd('/'), "synthetic").use { client ->
                val error = assertFailsWith<ApiException> { client.get<Unit>("/api/v1/players") }
                assertEquals("rate limited", error.errorDetail)
                assertEquals(429, error.statusCode)
                assertEquals("audit-request", error.requestId)
                assertEquals("60", error.retryAfter)
            }
        }
    }

    @Test
    fun templateErrorsPreserveMachineMetadata() {
        MockWebServer().use { server ->
            server.enqueue(MockResponse().setResponseCode(400).setBody("""
                {"code":1004000,"message":"invalid request parameters","request_id":"body-request","trace_id":"trace-1","error":{"category":"invalid_argument","code":"common.invalid_argument"}}
            """))
            HttpClient(server.url("/").toString().trimEnd('/'), "synthetic").use { client ->
                val error = assertFailsWith<ApiException> { client.get<Unit>("/api/v1/players") }
                assertEquals("invalid request parameters", error.errorDetail)
                assertEquals(1004000, error.apiCode)
                assertEquals("invalid_argument", error.errorCategory)
                assertEquals("common.invalid_argument", error.machineCode)
                assertEquals("body-request", error.requestId)
                assertEquals("trace-1", error.traceId)
            }
        }
    }

    @Test
    fun authenticatedRedirectsDoNotReachAnotherOrigin() {
        MockWebServer().use { target ->
            target.enqueue(MockResponse().setBody("{}"))
            MockWebServer().use { source ->
                source.enqueue(MockResponse().setResponseCode(302).setHeader("Location", target.url("/target")))
                HttpClient(source.url("/").toString().trimEnd('/'), "synthetic").use { client ->
                    val error = assertFailsWith<HttpException> { client.get<Unit>("/redirect") }
                    assertEquals(302, error.statusCode)
                    assertEquals(0, target.requestCount)
                }
            }
        }
    }

    @Test
    fun parseErrorsDoNotExposeTheResponseBody() {
        MockWebServer().use { server ->
            server.enqueue(MockResponse().setBody("syntheticResponseSecret"))
            HttpClient(server.url("/").toString().trimEnd('/'), "synthetic").use { client ->
                val error = assertFailsWith<JsonParseException> { client.get<Player>("/api/v1/players/1") }
                assertFalse(error.stackTraceToString().contains("syntheticResponseSecret"))
            }
        }
    }

    @Test
    fun apiTokenAuthSendsOnlyTheConfiguredCredentialHeader() {
        MockWebServer().use { server ->
            server.enqueue(MockResponse().setBody("{}"))
            HttpClient(
                server.url("/").toString().trimEnd('/'),
                "synthetic-api-token",
                authScheme = AuthScheme.API_TOKEN
            ).use { client ->
                client.get<Unit>("/api/v1/players")
                val request = server.takeRequest()
                assertEquals("synthetic-api-token", request.getHeader("X-API-Token"))
                assertNull(request.getHeader("Authorization"))
            }
        }
    }
}
