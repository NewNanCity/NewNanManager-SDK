package com.newnancity.newnanmanager;

import com.newnancity.newnanmanager.exceptions.ApiException;
import com.newnancity.newnanmanager.exceptions.ConfigurationException;
import com.newnancity.newnanmanager.exceptions.JsonParseException;
import com.newnancity.newnanmanager.generated.model.ListPlayersResponse;
import com.newnancity.newnanmanager.generated.model.HeartbeatRequest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertFalse;

class NewNanManagerClientTest {
    private static final String VALID_SESSION_ID = "0123456789abcdef0123456789abcdef";
    private MockWebServer server;

    @BeforeEach
    void setUp() throws IOException {
        server = new MockWebServer();
        server.start();
    }

    @AfterEach
    void tearDown() throws IOException {
        server.shutdown();
    }

    @Test
    void sendsBearerTokenAndPreservesPaginationQuery() throws Exception {
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody("{\"players\":[],\"total\":0,\"page\":2,\"page_size\":5}"));

        try (NewNanManagerClient client = NewNanManagerClient.builder()
                .baseUrl(server.url("/").toString())
                .token("bearer-secret")
                .build()) {
            ListPlayersResponse response = client.players().listPlayers(
                    2, 5, null, null, null, "Steve", null, null, null);
            assertEquals(0L, response.getTotal());
        }

        RecordedRequest request = server.takeRequest(1, TimeUnit.SECONDS);
        assertEquals("Bearer bearer-secret", request.getHeader("Authorization"));
        assertNull(request.getHeader("X-API-Token"));
        assertEquals("2", request.getRequestUrl().queryParameter("page"));
        assertEquals("5", request.getRequestUrl().queryParameter("page_size"));
        assertEquals("Steve", request.getRequestUrl().queryParameter("name"));
    }

    @Test
    void sendsApiTokenWithoutAuthorizationHeader() throws Exception {
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody("{\"players\":[],\"total\":0,\"page\":1,\"page_size\":20}"));

        try (NewNanManagerClient client = NewNanManagerClient.builder()
                .baseUrl(server.url("/").toString())
                .token("api-secret")
                .authScheme(AuthScheme.API_TOKEN)
                .build()) {
            client.players().listPlayers();
        }

        RecordedRequest request = server.takeRequest(1, TimeUnit.SECONDS);
        assertNull(request.getHeader("Authorization"));
        assertEquals("api-secret", request.getHeader("X-API-Token"));
    }

    @Test
    void mapsStructuredApiErrorAndRetryMetadata() throws Exception {
        server.enqueue(new MockResponse()
                .setResponseCode(429)
                .setHeader("Content-Type", "application/json")
                .setHeader("X-Request-ID", "header-request")
                .setHeader("Retry-After", "7")
                .setBody("{\"code\":42901,\"message\":\"请求过于频繁\","
                        + "\"request_id\":\"body-request\",\"trace_id\":\"trace-1\","
                        + "\"error\":{\"category\":\"rate_limit\",\"code\":\"too_many_requests\"}}"));

        try (NewNanManagerClient client = NewNanManagerClient.builder()
                .baseUrl(server.url("/").toString())
                .token("secret")
                .build()) {
            ApiException exception = assertThrows(ApiException.class,
                    () -> client.players().listPlayers());
            assertEquals(429, exception.getStatusCode());
            assertEquals(42901, exception.getApiCode());
            assertEquals("请求过于频繁", exception.getErrorMessage());
            assertEquals("rate_limit", exception.getErrorCategory());
            assertEquals("too_many_requests", exception.getMachineCode());
            assertEquals("header-request", exception.getRequestId());
            assertEquals("trace-1", exception.getTraceId());
            assertEquals("7", exception.getRetryAfter());
        }
    }

    @Test
    void mapsMalformedSuccessfulBodyToFacadeParseException() throws Exception {
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody("{malformed"));

        try (NewNanManagerClient client = NewNanManagerClient.builder()
                .baseUrl(server.url("/").toString())
                .token("secret")
                .build()) {
            assertThrows(JsonParseException.class, () -> client.players().listPlayers());
        }
    }

    @Test
    void rejectsInvalidConfigurationBeforeOpeningAConnection() {
        assertThrows(ConfigurationException.class, () -> NewNanManagerClient.builder()
                .baseUrl("file:///tmp/newnanmanager")
                .token("secret")
                .build());
    }

    @Test
    void keepsRedirectsAndTransportRetriesDisabled() throws Exception {
        try (NewNanManagerClient client = NewNanManagerClient.builder()
                .baseUrl(server.url("/").toString())
                .token("secret")
                .timeoutMillis(1234L)
                .build()) {
            assertEquals(1234, client.getGeneratedClient().getHttpClient().connectTimeoutMillis());
            assertEquals(1234, client.getGeneratedClient().getHttpClient().readTimeoutMillis());
            assertEquals(1234, client.getGeneratedClient().getHttpClient().writeTimeoutMillis());
            assertEquals(1234, client.getGeneratedClient().getHttpClient().callTimeoutMillis());
            assertFalse(client.getGeneratedClient().getHttpClient().followRedirects());
            assertFalse(client.getGeneratedClient().getHttpClient().followSslRedirects());
            assertFalse(client.getGeneratedClient().getHttpClient().retryOnConnectionFailure());
        }
    }

    @Test
    void forwardsGuardianSessionHeaders() throws Exception {
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody("{\"received_at\":1,\"response_at\":2,\"expire_duration_ms\":30000}"));

        try (NewNanManagerClient client = NewNanManagerClient.builder()
                .baseUrl(server.url("/").toString())
                .token("secret")
                .build()) {
            client.monitor().heartbeat(
                    42,
                    VALID_SESSION_ID,
                    7L,
                    new HeartbeatRequest().currentPlayers(1).maxPlayers(20));
        }

        RecordedRequest request = server.takeRequest(1, TimeUnit.SECONDS);
        assertEquals(VALID_SESSION_ID, request.getHeader("X-NNM-Session-ID"));
        assertEquals("7", request.getHeader("X-NNM-Session-Epoch"));
    }

    @Test
    void typedSessionContextForwardsGuardianSessionHeaders() throws Exception {
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody("{\"received_at\":1,\"response_at\":2,\"expire_duration_ms\":30000}"));

        try (NewNanManagerClient client = NewNanManagerClient.builder()
                .baseUrl(server.url("/").toString())
                .token("secret")
                .build()) {
            client.monitor().heartbeat(
                    42,
                    new HeartbeatRequest().currentPlayers(1).maxPlayers(20),
                    new SessionContext(VALID_SESSION_ID, 9L));
        }

        RecordedRequest request = server.takeRequest(1, TimeUnit.SECONDS);
        assertEquals(VALID_SESSION_ID, request.getHeader("X-NNM-Session-ID"));
        assertEquals("9", request.getHeader("X-NNM-Session-Epoch"));
    }

    @Test
    void rejectsInvalidTypedSessionContext() {
        assertThrows(IllegalArgumentException.class, () -> new SessionContext(" ", 1L));
        assertThrows(IllegalArgumentException.class, () -> new SessionContext("too-short", 1L));
        assertThrows(IllegalArgumentException.class, () -> new SessionContext(VALID_SESSION_ID, 0L));
    }
}
