package com.nanmanager.bukkit

import com.nanmanager.bukkit.exceptions.ApiException
import com.nanmanager.bukkit.http.HttpClient
import com.nanmanager.bukkit.models.*
import com.nanmanager.bukkit.services.MonitorService
import com.nanmanager.bukkit.services.IPService
import com.nanmanager.bukkit.services.PlayerService
import com.nanmanager.bukkit.services.PlayerServerService
import com.nanmanager.bukkit.services.ServerService
import com.nanmanager.bukkit.services.TokenService
import com.nanmanager.bukkit.services.TownService
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull

class ContractTest {
    private val validSessionId = "0123456789abcdef0123456789abcdef"
    private val token = """{"id":1,"name":"audit","role":"server","active":true,"server_id":7,"created_at":"2026-09-06T00:00:00Z","updated_at":"2026-09-06T00:00:00Z"}"""

    @Test
    fun tokenBindingIsSentAndDecoded() {
        MockWebServer().use { server ->
            server.enqueue(MockResponse().setBody("""{"token_info":$token,"token_value":"synthetic"}"""))
            server.enqueue(MockResponse().setBody(token))
            HttpClient(server.url("/").toString().trimEnd('/'), "synthetic").use { http ->
                val service = TokenService(http)
                val created = service.createApiToken(CreateApiTokenRequest(name = "audit", role = "server", serverId = 7))
                assertEquals(7, created.tokenInfo.serverId)
                val creation = http.objectMapper.readTree(server.takeRequest().body.readUtf8())
                assertEquals(7, creation["server_id"].intValue())
                val updated = service.updateApiToken(UpdateApiTokenRequest(id = 1, serverId = 7, active = false))
                assertEquals(7, updated.serverId)
                val update = http.objectMapper.readTree(server.takeRequest().body.readUtf8())
                assertEquals(7, update["server_id"].intValue())
                assertFalse(update["active"].booleanValue())
            }
        }
    }

    @Test
    fun monitorCursorIsEncodedAndUnknownMetadataIsPreserved() {
        val cursor = "opaque&+/#"
        MockWebServer().use { server ->
            server.enqueue(MockResponse().setBody("""{"server_id":7,"stats":[{"timestamp":1,"current_players":0},{"timestamp":2,"current_players":0,"measurement_type":"unknown","latency_metric":"legacy"}],"next_cursor":"opaque&+/#"}"""))
            HttpClient(server.url("/").toString().trimEnd('/'), "synthetic").use { http ->
                val result = MonitorService(http).getMonitorStats(GetMonitorStatsRequest(serverId = 7, since = 0, duration = 86400, limit = 10000, cursor = cursor))
                val query = server.takeRequest().requestUrl!!
                assertEquals(4, query.querySize)
                assertEquals("0", query.queryParameter("since"))
                assertEquals("86400", query.queryParameter("duration"))
                assertEquals("10000", query.queryParameter("limit"))
                assertEquals(cursor, query.queryParameter("cursor"))
                assertEquals(cursor, result.nextCursor)
                assertNull(result.stats[0].measurementType)
                assertNull(result.stats[0].latencyMetric)
                assertEquals("unknown", result.stats[1].measurementType)
                assertEquals("legacy", result.stats[1].latencyMetric)
            }
        }
    }

    @Test
    fun serverStatusRetainsMeasurementSourceAndMetric() {
        MockWebServer().use { server ->
            val status = """"server_id":7,"online":true,"current_players":0,"max_players":10,"expire_at":"2026-09-06T00:01:00Z","last_heartbeat":"2026-09-06T00:00:00Z""""
            server.enqueue(MockResponse().setBody("{$status,\"measurement_type\":\"pull\",\"latency_metric\":\"rtt\"}"))
            server.enqueue(MockResponse().setBody("{$status}"))
            HttpClient(server.url("/").toString().trimEnd('/'), "synthetic").use { http ->
                val known = http.get<ServerStatus>("/status-fixture")
                assertEquals("pull", known.measurementType)
                assertEquals("rtt", known.latencyMetric)
                val unknown = http.get<ServerStatus>("/status-fixture")
                assertNull(unknown.measurementType)
                assertNull(unknown.latencyMetric)
            }
        }
    }

    @Test
    fun periodicSnapshotsAreSentOnceWithoutChangingNames() {
        MockWebServer().use { server ->
            HttpClient(server.url("/").toString().trimEnd('/'), "synthetic").use { http ->
                for (count in listOf(0, 1000)) {
                    server.enqueue(MockResponse().setBody("""{"results":[],"processed_at":1}"""))
                    val before = server.requestCount
                    PlayerService(http).validate(ValidateRequest(serverId = 7, login = false,
                        players = List(count) { PlayerValidateInfo(playerName = "MiXeD$it", ip = "192.0.2.1") }))
                    val body = http.objectMapper.readTree(server.takeRequest().body.readUtf8())
                    assertFalse(body["login"].booleanValue())
                    assertEquals(count, body["players"].size())
                    if (count > 0) assertEquals("MiXeD0", body["players"][0]["player_name"].textValue())
                    assertEquals(before + 1, server.requestCount)
                }
            }
        }
    }

    @Test
    fun generatedErrorBoundaryPreservesTemplateMetadata() {
        MockWebServer().use { server ->
            server.enqueue(
                MockResponse()
                    .setResponseCode(429)
                    .setHeader("Retry-After", "12")
                    .setBody("""{"code":1004290,"message":"rate limited","request_id":"generated-request","trace_id":"generated-trace","error":{"category":"rate_limit","code":"common.rate_limited"}}""")
            )
            HttpClient(server.url("/").toString().trimEnd('/'), "synthetic").use { http ->
                val error = kotlin.test.assertFailsWith<ApiException> {
                    TokenService(http).listApiTokens(ListApiTokensRequest())
                }
                assertEquals(429, error.statusCode)
                assertEquals(1004290, error.apiCode)
                assertEquals("generated-request", error.requestId)
                assertEquals("generated-trace", error.traceId)
                assertEquals("12", error.retryAfter)
            }
        }
    }

    @Test
    fun generatedSessionCallSendsFencingHeaders() {
        MockWebServer().use { server ->
            server.enqueue(MockResponse().setBody("""{"received_at":1,"response_at":2,"expire_duration_ms":30000}"""))
            HttpClient(server.url("/").toString().trimEnd('/'), "synthetic").use { http ->
                MonitorService(http).heartbeat(
                    serverId = 7,
                    request = HeartbeatRequest(currentPlayers = 1, maxPlayers = 20),
                    session = SessionContext(validSessionId, 4)
                )
                val request = server.takeRequest()
                assertEquals(validSessionId, request.getHeader("X-NNM-Session-ID"))
                assertEquals("4", request.getHeader("X-NNM-Session-Epoch"))
            }
        }
    }

    @Test
    fun generatedSessionCreationMapsTheServerResponse() {
        MockWebServer().use { server ->
            server.enqueue(MockResponse().setBody("""{"session_id":"0123456789abcdef0123456789abcdef","session_epoch":9}"""))
            HttpClient(server.url("/").toString().trimEnd('/'), "synthetic").use { http ->
                val session = MonitorService(http).createServerSession(7)
                assertEquals(validSessionId, session.id)
                assertEquals(9, session.epoch)
                assertEquals("{}", server.takeRequest().body.readUtf8())
            }
        }
    }

    @Test
    fun generatedVoidCallAcceptsAnEmptySuccessBody() {
        MockWebServer().use { server ->
            server.enqueue(MockResponse().setResponseCode(204))
            HttpClient(server.url("/").toString().trimEnd('/'), "synthetic").use { http ->
                TokenService(http).deleteApiToken(DeleteApiTokenRequest(id = 3))
                assertEquals("DELETE", server.takeRequest().method)
            }
        }
    }

    @Test
    fun generatedReadServicesPreservePathsQueriesAndEmptyCollections() {
        MockWebServer().use { server ->
            server.enqueue(MockResponse().setBody("""{"players":[],"total":0,"page":2,"page_size":5}"""))
            server.enqueue(MockResponse().setBody("""{"servers":[],"total":0,"page":2,"page_size":5}"""))
            server.enqueue(MockResponse().setBody("""{"towns":[],"total":0,"page":2,"page_size":5}"""))
            server.enqueue(MockResponse().setBody("""{"ips":[],"total":0,"page":2,"page_size":5}"""))
            server.enqueue(MockResponse().setBody("""{"players":[],"total":0,"page":2,"page_size":5}"""))
            HttpClient(server.url("/").toString().trimEnd('/'), "synthetic").use { http ->
                PlayerService(http).listPlayers(
                    ListPlayersRequest(page = 2, pageSize = 5, banMode = BanMode.PERMANENT)
                )
                ServerService(http).listServers(ListServersRequest(page = 2, pageSize = 5, onlineOnly = true))
                TownService(http).listTowns(ListTownsRequest(page = 2, pageSize = 5, minLevel = 3))
                IPService(http).listIPs(ListIPsRequest(page = 2, pageSize = 5, minThreatLevel = ThreatLevel.HIGH))
                PlayerServerService(http).getServerPlayers(GetServerPlayersRequest(page = 2, pageSize = 5, onlineOnly = true))

                val requests = (1..5).map { server.takeRequest() }
                assertEquals("/api/v1/players", requests[0].requestUrl?.encodedPath)
                assertEquals("2", requests[0].requestUrl?.queryParameter("page"))
                assertEquals("2", requests[0].requestUrl?.queryParameter("ban_mode"))
                assertEquals("/api/v1/servers", requests[1].requestUrl?.encodedPath)
                assertEquals("true", requests[1].requestUrl?.queryParameter("online_only"))
                assertEquals("/api/v1/towns", requests[2].requestUrl?.encodedPath)
                assertEquals("3", requests[2].requestUrl?.queryParameter("min_level"))
                assertEquals("/api/v1/ips", requests[3].requestUrl?.encodedPath)
                assertEquals("2", requests[3].requestUrl?.queryParameter("min_threat_level"))
                assertEquals("/api/v1/server-players", requests[4].requestUrl?.encodedPath)
                assertEquals("true", requests[4].requestUrl?.queryParameter("online_only"))
            }
        }
    }
}
