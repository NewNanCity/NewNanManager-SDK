package com.newnancity.newnanmanager;

import com.newnancity.newnanmanager.exceptions.ApiExceptionMapper;
import com.newnancity.newnanmanager.exceptions.ConfigurationException;
import com.newnancity.newnanmanager.exceptions.NewNanManagerException;
import com.newnancity.newnanmanager.generated.ApiClient;
import com.newnancity.newnanmanager.generated.api.HealthApi;
import com.newnancity.newnanmanager.generated.api.IpServiceApi;
import com.newnancity.newnanmanager.generated.api.MonitorServiceApi;
import com.newnancity.newnanmanager.generated.api.PlayerServerServiceApi;
import com.newnancity.newnanmanager.generated.api.PlayerServiceApi;
import com.newnancity.newnanmanager.generated.api.ServerServiceApi;
import com.newnancity.newnanmanager.generated.api.TokenServiceApi;
import com.newnancity.newnanmanager.generated.api.TownServiceApi;
import com.newnancity.newnanmanager.services.HealthService;
import com.newnancity.newnanmanager.services.IpService;
import com.newnancity.newnanmanager.services.MonitorService;
import com.newnancity.newnanmanager.services.PlayerServerService;
import com.newnancity.newnanmanager.services.PlayerService;
import com.newnancity.newnanmanager.services.ServerService;
import com.newnancity.newnanmanager.services.TokenService;
import com.newnancity.newnanmanager.services.TownService;
import okhttp3.OkHttpClient;

import java.net.URI;
import java.util.concurrent.TimeUnit;

/**
 * Synchronous NewNanManager client facade.
 *
 * <p>The facade owns HTTP configuration, selects exactly one credential
 * scheme, and translates generated OpenAPI exceptions. Service wrappers keep
 * the generated models and endpoint names so regeneration does not create a
 * second model hierarchy.</p>
 */
public final class NewNanManagerClient implements AutoCloseable {
    public static final long DEFAULT_TIMEOUT_MILLIS = 30_000L;

    @FunctionalInterface
    public interface GeneratedRequest<T> {
        T execute() throws com.newnancity.newnanmanager.generated.ApiException;
    }

    private final ApiClient apiClient;
    private final String baseUrl;
    private final AuthScheme authScheme;
    private final long timeoutMillis;
    private final PlayerService players;
    private final ServerService servers;
    private final TownService towns;
    private final TokenService tokens;
    private final IpService ips;
    private final PlayerServerService playerServers;
    private final MonitorService monitor;
    private final HealthService health;
    private boolean closed;

    private NewNanManagerClient(Builder builder) throws ConfigurationException {
        this.baseUrl = normalizeBaseUrl(builder.baseUrl);
        this.authScheme = builder.authScheme;
        this.timeoutMillis = builder.timeoutMillis;

        OkHttpClient httpClient = new OkHttpClient.Builder()
                .connectTimeout(timeoutMillis, TimeUnit.MILLISECONDS)
                .readTimeout(timeoutMillis, TimeUnit.MILLISECONDS)
                .writeTimeout(timeoutMillis, TimeUnit.MILLISECONDS)
                .callTimeout(timeoutMillis, TimeUnit.MILLISECONDS)
                .followRedirects(false)
                .followSslRedirects(false)
                .retryOnConnectionFailure(false)
                .build();

        this.apiClient = new ApiClient(httpClient).setBasePath(baseUrl);
        configureAuthentication(apiClient, builder.token, authScheme);

        this.players = new PlayerService(new PlayerServiceApi(apiClient));
        this.servers = new ServerService(new ServerServiceApi(apiClient));
        this.towns = new TownService(new TownServiceApi(apiClient));
        this.tokens = new TokenService(new TokenServiceApi(apiClient));
        this.ips = new IpService(new IpServiceApi(apiClient));
        this.playerServers = new PlayerServerService(new PlayerServerServiceApi(apiClient));
        this.monitor = new MonitorService(new MonitorServiceApi(apiClient));
        this.health = new HealthService(new HealthApi(apiClient));
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public AuthScheme getAuthScheme() {
        return authScheme;
    }

    public long getTimeoutMillis() {
        return timeoutMillis;
    }

    public ApiClient getGeneratedClient() {
        return apiClient;
    }

    public PlayerService players() {
        return players;
    }

    public ServerService servers() {
        return servers;
    }

    public TownService towns() {
        return towns;
    }

    public TokenService tokens() {
        return tokens;
    }

    public IpService ips() {
        return ips;
    }

    public PlayerServerService playerServers() {
        return playerServers;
    }

    public MonitorService monitor() {
        return monitor;
    }

    public HealthService health() {
        return health;
    }

    /** Execute an advanced generated request through the facade error boundary. */
    public <T> T execute(GeneratedRequest<T> request) throws NewNanManagerException {
        try {
            return request.execute();
        } catch (com.newnancity.newnanmanager.generated.ApiException exception) {
            throw ApiExceptionMapper.map(exception);
        } catch (com.google.gson.JsonParseException exception) {
            throw ApiExceptionMapper.mapJsonParseFailure(exception);
        }
    }

    @Override
    public void close() {
        if (closed) {
            return;
        }
        closed = true;
        OkHttpClient httpClient = apiClient.getHttpClient();
        httpClient.connectionPool().evictAll();
        httpClient.dispatcher().executorService().shutdown();
        if (httpClient.cache() != null) {
            try {
                httpClient.cache().close();
            } catch (java.io.IOException ignored) {
                // Closing a cache is best effort after all requests have ended.
            }
        }
    }

    private static void configureAuthentication(ApiClient client, String token, AuthScheme scheme) {
        if (scheme == AuthScheme.API_TOKEN) {
            client.setBearerToken((String) null);
            client.setApiKey(token);
            client.setApiKeyPrefix((String) null);
        } else {
            client.setBearerToken(token);
            client.setApiKey((String) null);
            client.setApiKeyPrefix((String) null);
        }
    }

    private static String normalizeBaseUrl(String value) throws ConfigurationException {
        if (value == null || value.trim().isEmpty()) {
            throw new ConfigurationException("baseUrl must not be empty");
        }
        final URI uri;
        try {
            uri = URI.create(value.trim());
        } catch (IllegalArgumentException exception) {
            throw new ConfigurationException("baseUrl is not a valid URI", exception);
        }
        if (!("http".equalsIgnoreCase(uri.getScheme()) || "https".equalsIgnoreCase(uri.getScheme()))
                || uri.getHost() == null
                || uri.getQuery() != null
                || uri.getFragment() != null) {
            throw new ConfigurationException("baseUrl must be an HTTP(S) origin without query or fragment");
        }
        String normalized = value.trim();
        while (normalized.endsWith("/")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }
        return normalized;
    }

    public static final class Builder {
        private String baseUrl;
        private String token;
        private AuthScheme authScheme = AuthScheme.BEARER;
        private long timeoutMillis = DEFAULT_TIMEOUT_MILLIS;

        public Builder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public Builder token(String token) {
            this.token = token;
            return this;
        }

        public Builder authScheme(AuthScheme authScheme) {
            this.authScheme = authScheme;
            return this;
        }

        public Builder timeoutMillis(long timeoutMillis) {
            this.timeoutMillis = timeoutMillis;
            return this;
        }

        public NewNanManagerClient build() throws ConfigurationException {
            if (token == null || token.trim().isEmpty()) {
                throw new ConfigurationException("token must not be empty");
            }
            if (authScheme == null) {
                throw new ConfigurationException("authScheme must not be null");
            }
            if (timeoutMillis <= 0L) {
                throw new ConfigurationException("timeoutMillis must be greater than zero");
            }
            return new NewNanManagerClient(this);
        }
    }
}
