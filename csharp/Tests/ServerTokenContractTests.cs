using System.Net;
using System.Reflection;
using System.Text.Json;
using System.Web;
using NewNanManager.Client.Models;
using Xunit;

namespace NewNanManager.Client.Tests;

public class ServerTokenContractTests
{
    [Theory]
    [InlineData(true)]
    [InlineData(false)]
    public void ServerRegistryRetainsActiveFlag(bool active)
    {
        var body = active ? "{\"active\":true}" : "{\"active\":false}";
        var server = JsonSerializer.Deserialize<ServerRegistry>(body)!;
        var encoded = JsonSerializer.SerializeToElement(server);
        Assert.Equal(active, encoded.GetProperty("active").GetBoolean());
    }

    [Fact]
    public void LegacyServerTypeRemainsAvailableButIsDeprecatedAndNotSerialized()
    {
        var property = typeof(ServerRegistry).GetProperty("ServerType")!;
        var server = new ServerRegistry();
        property.SetValue(server, ServerType.Proxy);
        Assert.Equal(ServerType.Proxy, property.GetValue(server));
        var encoded = JsonSerializer.SerializeToElement(server);
        Assert.False(encoded.TryGetProperty("server_type", out _));
        Assert.NotNull(property.GetCustomAttribute<ObsoleteAttribute>());
    }

    [Theory]
    [InlineData(0L, 1, 20)]
    [InlineData(4294967296L, 2, 100)]
    public async Task TokenListPreservesPaginationMetadata(long total, int page, int pageSize)
    {
        using var http = new HttpClient(new FakeHandler(request =>
        {
            Assert.Equal(HttpMethod.Get, request.Method);
            Assert.Equal("/api/v1/tokens", request.RequestUri!.AbsolutePath);
            return new HttpResponseMessage(HttpStatusCode.OK)
            {
                Content = new StringContent(JsonSerializer.Serialize(new
                {
                    tokens = Array.Empty<object>(), total, page, page_size = pageSize
                }))
            };
        })) { BaseAddress = new Uri("https://audit.invalid") };
        using var client = new NewNanManagerClient(http);
        var result = await client.Tokens.ListApiTokensAsync();
        Assert.Empty(result.Tokens);
        var encoded = JsonSerializer.SerializeToElement(result);
        Assert.Equal(total, encoded.GetProperty("total").GetInt64());
        Assert.Equal(page, encoded.GetProperty("page").GetInt32());
        Assert.Equal(pageSize, encoded.GetProperty("page_size").GetInt32());
    }

    [Fact]
    public async Task TokenPageSendsPaginationAndLegacyCallKeepsQueryEmpty()
    {
        var paths = new List<Uri>();
        using var http = new HttpClient(new FakeHandler(request =>
        {
            Assert.Equal(HttpMethod.Get, request.Method);
            paths.Add(request.RequestUri!);
            return new HttpResponseMessage(HttpStatusCode.OK)
            {
                Content = new StringContent("""{"tokens":[],"total":0,"page":2,"page_size":100}""")
            };
        })) { BaseAddress = new Uri("https://audit.invalid") };
        using var client = new NewNanManagerClient(http);
        var page = await client.Tokens.ListApiTokensPageAsync(2, 100);
        var query = HttpUtility.ParseQueryString(paths[0].Query);
        Assert.Equal("/api/v1/tokens", paths[0].AbsolutePath);
        Assert.Equal(2, query.Count);
        Assert.Equal("2", query["page"]);
        Assert.Equal("100", query["page_size"]);
        Assert.Equal(2, page.Page);
        Assert.Equal(100, page.PageSize);
        await client.Tokens.ListApiTokensAsync(CancellationToken.None);
        Assert.Empty(paths[1].Query);
    }

    [Theory]
    [InlineData(true)]
    [InlineData(false)]
    public async Task TokenListRequestsHonorCancellation(bool paginated)
    {
        var handler = new CancellationHandler();
        using var http = new HttpClient(handler) { BaseAddress = new Uri("https://audit.invalid") };
        using var client = new NewNanManagerClient(http);
        using var cancellation = new CancellationTokenSource();
        var pending = paginated
            ? client.Tokens.ListApiTokensPageAsync(2, 100, cancellation.Token)
            : client.Tokens.ListApiTokensAsync(cancellation.Token);
        await handler.Started.Task.WaitAsync(TimeSpan.FromSeconds(2));
        cancellation.Cancel();
        await Assert.ThrowsAnyAsync<OperationCanceledException>(async () => await pending.WaitAsync(TimeSpan.FromSeconds(2)));
    }

    private sealed class FakeHandler(Func<HttpRequestMessage, HttpResponseMessage> response) : HttpMessageHandler
    {
        protected override Task<HttpResponseMessage> SendAsync(HttpRequestMessage request, CancellationToken cancellationToken) => Task.FromResult(response(request));
    }

    private sealed class CancellationHandler : HttpMessageHandler
    {
        public TaskCompletionSource Started { get; } = new(TaskCreationOptions.RunContinuationsAsynchronously);

        protected override async Task<HttpResponseMessage> SendAsync(HttpRequestMessage request, CancellationToken cancellationToken)
        {
            Started.TrySetResult();
            await Task.Delay(Timeout.InfiniteTimeSpan, cancellationToken);
            throw new InvalidOperationException("The request should have been canceled.");
        }
    }
}
