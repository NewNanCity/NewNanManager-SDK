using System.Net;
using System.Net.Sockets;
using System.Text;
using Microsoft.Extensions.Logging;
using NewNanManager.Client.Exceptions;
using NewNanManager.Client.Models;
using Xunit;

namespace NewNanManager.Client.Tests;

public class HttpClientTests
{
    private const string ValidSessionId = "0123456789abcdef0123456789abcdef";

    [Fact]
    public async Task DebugLogDoesNotContainTokenResponse()
    {
        var logger = new RecordingLogger();
        using var http = new HttpClient(new FakeHandler(_ => new HttpResponseMessage(HttpStatusCode.OK)
        {
            Content = new StringContent("""{"token_info":{"id":1,"name":"audit","role":"monitor","active":true,"created_at":"2026-09-06T00:00:00Z","updated_at":"2026-09-06T00:00:00Z"},"token_value":"synthetic-response-secret"}""")
        })) { BaseAddress = new Uri("https://audit.invalid") };
        using var client = new NewNanManagerClient(http, logger: logger);
        var result = await client.Tokens.CreateApiTokenAsync(new CreateApiTokenRequest { Name = "audit", Role = "monitor" });
        Assert.Equal("synthetic-response-secret", result.TokenValue);
        Assert.DoesNotContain(logger.Messages, message => message.Contains("synthetic-response-secret"));
    }

    [Fact]
    public async Task ErrorPreservesRequestIdentifier()
    {
        using var http = new HttpClient(new FakeHandler(_ =>
        {
            var response = new HttpResponseMessage(HttpStatusCode.TooManyRequests)
            {
                Content = new StringContent("""{"detail":"rate limited"}""")
            };
            response.Headers.Add("X-Request-ID", "audit-request");
            response.Headers.Add("Retry-After", "60");
            return response;
        })) { BaseAddress = new Uri("https://audit.invalid") };
        using var client = new NewNanManagerClient(http);
        var error = await Assert.ThrowsAsync<ApiErrorException>(() => client.Players.GetPlayerAsync(1));
        Assert.Equal(429, error.ErrorCode);
        Assert.Equal("audit-request", error.RequestId);
        Assert.Equal("60", error.RetryAfter);
        Assert.Equal(429, error.StatusCode);
    }

    [Fact]
    public async Task TemplateErrorPreservesMachineMetadata()
    {
        using var http = new HttpClient(new FakeHandler(_ =>
        {
            var response = new HttpResponseMessage(HttpStatusCode.BadRequest)
            {
                Content = new StringContent("""{"code":1004000,"message":"invalid request parameters","request_id":"body-request","trace_id":"trace-1","error":{"category":"invalid_argument","code":"common.invalid_argument"}}""")
            };
            return response;
        })) { BaseAddress = new Uri("https://audit.invalid") };
        using var client = new NewNanManagerClient(http);
        var error = await Assert.ThrowsAsync<ApiErrorException>(() => client.Players.GetPlayerAsync(1));
        Assert.Equal("invalid request parameters", error.Message);
        Assert.Equal(1004000, error.ApiCode);
        Assert.Equal("invalid_argument", error.ErrorCategory);
        Assert.Equal("common.invalid_argument", error.MachineCode);
        Assert.Equal("body-request", error.RequestId);
        Assert.Equal("trace-1", error.TraceId);
    }

    [Fact]
    public async Task DefaultClientDoesNotForwardAuthenticatedRedirects()
    {
        await using var target = new OneShotServer("200 OK", "", "{\"id\":1}");
        await using var source = new OneShotServer("302 Found", $"Location: {target.Url}\r\n", "");
        using var client = new NewNanManagerClient(new NewNanManagerClientOptions
        {
            BaseUrl = source.Url,
            Token = "synthetic",
            Timeout = TimeSpan.FromSeconds(2)
        });
        var error = await Assert.ThrowsAsync<NewNanManagerHttpException>(() => client.Players.GetPlayerAsync(1));
        Assert.Equal(302, error.StatusCode);
        Assert.Equal("Bearer synthetic", source.Headers["Authorization"]);
        Assert.False(source.Headers.ContainsKey("X-API-Token"));
        Assert.Equal(0, target.RequestCount);
    }

    [Fact]
    public async Task ApiTokenSchemeSendsOnlyApiTokenHeader()
    {
        HttpRequestMessage? captured = null;
        using var http = NewNanManagerClient.CreateHttpClient(
            new NewNanManagerClientOptions
            {
                BaseUrl = "https://audit.invalid",
                Token = "api-secret",
                AuthScheme = AuthScheme.ApiToken,
            },
            new FakeHandler(request =>
            {
                captured = request;
                return new HttpResponseMessage(HttpStatusCode.OK)
                {
                    Content = new StringContent("{\"id\":1,\"name\":\"audit\"}")
                };
            })
        );
        using var client = new NewNanManagerClient(http, disposeHttpClient: true);

        await client.Players.GetPlayerAsync(1);

        Assert.NotNull(captured);
        Assert.Equal("api-secret", captured!.Headers.GetValues("X-API-Token").Single());
        Assert.False(captured.Headers.Contains("Authorization"));
    }

    [Fact]
    public async Task SessionOperationsSendFencingHeaders()
    {
        var requests = new List<HttpRequestMessage>();
        using var http = new HttpClient(new FakeHandler(request =>
        {
            requests.Add(request);
            var body = request.RequestUri!.AbsolutePath.EndsWith("/session", StringComparison.Ordinal)
                ? $"{{\"session_id\":\"{ValidSessionId}\",\"session_epoch\":8}}"
                : request.RequestUri.AbsolutePath.EndsWith("/heartbeat", StringComparison.Ordinal)
                    ? "{\"received_at\":1,\"response_at\":2,\"expire_duration_ms\":30000}"
                    : "{\"results\":[],\"processed_at\":1}";
            return new HttpResponseMessage(HttpStatusCode.OK)
            {
                Content = new StringContent(body)
            };
        })) { BaseAddress = new Uri("https://audit.invalid") };
        using var client = new NewNanManagerClient(http, disposeHttpClient: true);
        var session = new SessionContext(ValidSessionId, 7);

        await client.Players.ValidateAsync(
            new ValidateRequest { ServerId = 7, Login = false }, session
        );
        await client.Monitor.HeartbeatAsync(
            7,
            new HeartbeatRequest { CurrentPlayers = 0, MaxPlayers = 20 },
            session
        );
        await client.PlayerServers.SetPlayersOfflineAsync(7, new[] { 1 }, session);

        Assert.Equal(3, requests.Count);
        Assert.All(requests, request =>
        {
            Assert.Equal(ValidSessionId, request.Headers.GetValues("X-NNM-Session-ID").Single());
            Assert.Equal("7", request.Headers.GetValues("X-NNM-Session-Epoch").Single());
        });
    }

    [Fact]
    public void InvalidSessionIsRejectedBeforeConstruction()
    {
        Assert.Throws<ArgumentException>(() => new SessionContext(" ", 1));
        Assert.Throws<ArgumentException>(() => new SessionContext("too-short", 1));
        Assert.Throws<ArgumentOutOfRangeException>(() => new SessionContext(ValidSessionId, 0));
    }

    [Fact]
    public async Task ServerSessionResponseIsMappedToContext()
    {
        string? requestBody = null;
        using var http = new HttpClient(new AsyncFakeHandler(async request =>
        {
            requestBody = request.Content is null
                ? null
                : await request.Content.ReadAsStringAsync();
            return new HttpResponseMessage(HttpStatusCode.OK)
            {
                Content = new StringContent($"{{\"session_id\":\"{ValidSessionId}\",\"session_epoch\":8}}")
            };
        })) { BaseAddress = new Uri("https://audit.invalid") };
        using var client = new NewNanManagerClient(http, disposeHttpClient: true);

        var session = await client.Monitor.CreateServerSessionAsync(7);

        Assert.Equal(ValidSessionId, session.Id);
        Assert.Equal(8, session.Epoch);
        Assert.Equal("{}", requestBody);
    }

    private sealed class FakeHandler(Func<HttpRequestMessage, HttpResponseMessage> response) : HttpMessageHandler
    {
        protected override Task<HttpResponseMessage> SendAsync(HttpRequestMessage request, CancellationToken cancellationToken)
            => Task.FromResult(response(request));
    }

    private sealed class AsyncFakeHandler(Func<HttpRequestMessage, Task<HttpResponseMessage>> response) : HttpMessageHandler
    {
        protected override Task<HttpResponseMessage> SendAsync(HttpRequestMessage request, CancellationToken cancellationToken)
            => response(request);
    }

    private sealed class RecordingLogger : ILogger
    {
        public List<string> Messages { get; } = new();
        public IDisposable? BeginScope<TState>(TState state) where TState : notnull => null;
        public bool IsEnabled(LogLevel logLevel) => true;
        public void Log<TState>(LogLevel logLevel, EventId eventId, TState state, Exception? exception, Func<TState, Exception?, string> formatter)
            => Messages.Add(formatter(state, exception));
    }

    private sealed class OneShotServer : IAsyncDisposable
    {
        private readonly TcpListener _listener = new(IPAddress.Loopback, 0);
        private readonly CancellationTokenSource _cancellation = new();
        private readonly Task _serve;
        private int _requestCount;
        public string Url { get; }
        public Dictionary<string, string> Headers { get; } = new(StringComparer.OrdinalIgnoreCase);
        public int RequestCount => Volatile.Read(ref _requestCount);

        public OneShotServer(string status, string headers, string body)
        {
            _listener.Start();
            Url = $"http://127.0.0.1:{((IPEndPoint)_listener.LocalEndpoint).Port}";
            _serve = Serve(status, headers, body);
        }

        private async Task Serve(string status, string headers, string body)
        {
            using var connection = await _listener.AcceptTcpClientAsync(_cancellation.Token);
            Interlocked.Increment(ref _requestCount);
            using var stream = connection.GetStream();
            using var reader = new StreamReader(stream, leaveOpen: true);
            while (await reader.ReadLineAsync(_cancellation.Token) is { Length: > 0 } line)
            {
                var separator = line.IndexOf(':');
                if (separator > 0) Headers[line[..separator]] = line[(separator + 1)..].Trim();
            }
            var message = $"HTTP/1.1 {status}\r\n{headers}Content-Type: application/json\r\nContent-Length: {Encoding.UTF8.GetByteCount(body)}\r\nConnection: close\r\n\r\n{body}";
            await stream.WriteAsync(Encoding.UTF8.GetBytes(message), _cancellation.Token);
        }

        public async ValueTask DisposeAsync()
        {
            await _cancellation.CancelAsync();
            _listener.Stop();
            try { await _serve; }
            catch (OperationCanceledException) { }
            _cancellation.Dispose();
        }
    }
}
