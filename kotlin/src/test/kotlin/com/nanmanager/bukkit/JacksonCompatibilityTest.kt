package com.nanmanager.bukkit

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.core.StreamReadConstraints
import com.fasterxml.jackson.core.async.ByteArrayFeeder
import com.fasterxml.jackson.core.exc.StreamConstraintsException
import com.nanmanager.bukkit.http.HttpClient
import com.nanmanager.bukkit.models.ListApiTokensRequest
import com.nanmanager.bukkit.services.TokenService
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull

class JacksonCompatibilityTest {
    @Test
    fun chunkedNumberHonorsLimitBeforeItsTerminatorArrives() {
        // Dependency-level regression; the SDK itself parses responses synchronously.
        val factory = JsonFactory.builder()
            .streamReadConstraints(StreamReadConstraints.builder().maxNumberLength(32).build())
            .build()
        factory.createNonBlockingByteArrayParser().use { parser ->
            val feeder = parser.nonBlockingInputFeeder as ByteArrayFeeder
            val prefix = "{\"n\":".toByteArray(Charsets.UTF_8)
            feeder.feedInput(prefix, 0, prefix.size)
            assertEquals(JsonToken.START_OBJECT, parser.nextToken())
            assertEquals(JsonToken.FIELD_NAME, parser.nextToken())
            assertEquals(JsonToken.NOT_AVAILABLE, parser.nextToken())
            val digits = "1".repeat(16).toByteArray(Charsets.UTF_8)
            assertFailsWith<StreamConstraintsException> {
                repeat(4) {
                    feeder.feedInput(digits, 0, digits.size)
                    assertEquals(JsonToken.NOT_AVAILABLE, parser.nextToken())
                }
            }
        }
    }

    @Test
    fun sdkTokenPagePreservesInt64CountsAndOptionalFields() {
        MockWebServer().use { server ->
            server.enqueue(MockResponse().setBody("""{"tokens":[{"id":1,"name":"audit","role":"monitor","active":false,"created_at":"2026-09-06T00:00:00Z","updated_at":"2026-09-06T00:00:00Z"}],"total":4294967296,"page":2,"page_size":100}"""))
            HttpClient(server.url("/").toString().trimEnd('/'), "synthetic").use { http ->
                val result = TokenService(http).listApiTokens(ListApiTokensRequest(page = 2, pageSize = 100))
                val query = server.takeRequest().requestUrl!!
                assertEquals("2", query.queryParameter("page"))
                assertEquals("100", query.queryParameter("page_size"))
                assertEquals(4294967296L, result.total)
                assertEquals(2, result.page)
                assertEquals(100, result.pageSize)
                assertFalse(result.tokens.single().active)
                assertNull(result.tokens.single().serverId)
                assertNull(result.tokens.single().description)
            }
        }
    }
}
