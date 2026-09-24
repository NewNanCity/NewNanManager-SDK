using System.Net;
using System.Text.Json;
using System.Web;
using NewNanManager.Client.Models;
using Xunit;

namespace NewNanManager.Client.Tests;

public class ContractTests
{
    private const string Token = """{"id":1,"name":"audit","role":"server","active":true,"server_id":7,"created_at":"2026-09-06T00:00:00Z","updated_at":"2026-09-06T00:00:00Z"}""";

    [Fact]
    public async Task TokenBindingIsSentAndDecoded()
    {
        var bodies = new List<string>();
        using var http = new HttpClient(new FakeHandler(async request =>
        {
            bodies.Add(await request.Content!.ReadAsStringAsync());
            return Json(request.Method == HttpMethod.Post ? "{\"token_info\":" + Token + ",\"token_value\":\"synthetic\"}" : Token);
        })) { BaseAddress = new Uri("https://audit.invalid") };
        using var client = new NewNanManagerClient(http);
        var created = await client.Tokens.CreateApiTokenAsync(new CreateApiTokenRequest { Name = "audit", Role = "server", ServerId = 7 });
        Assert.Equal(7, created.TokenInfo.ServerId);
        var updated = await client.Tokens.UpdateApiTokenAsync(1, new UpdateApiTokenRequest { ServerId = 7, Active = false });
        Assert.Equal(7, updated.ServerId);
        using var creation = JsonDocument.Parse(bodies[0]);
        Assert.Equal(7, creation.RootElement.GetProperty("server_id").GetInt32());
        using var update = JsonDocument.Parse(bodies[1]);
        Assert.Equal(2, update.RootElement.EnumerateObject().Count());
        Assert.Equal(7, update.RootElement.GetProperty("server_id").GetInt32());
        Assert.False(update.RootElement.GetProperty("active").GetBoolean());
    }

    [Fact]
    public async Task MonitorPagePreservesCursorAndUnknownMetadata()
    {
        const string cursor = "opaque&+/#";
        var paths = new List<Uri>();
        using var http = new HttpClient(new FakeHandler(request =>
        {
            paths.Add(request.RequestUri!);
            return Task.FromResult(Json("""{"server_id":7,"stats":[{"timestamp":1,"current_players":0},{"timestamp":2,"current_players":0,"measurement_type":"unknown","latency_metric":"legacy"}],"next_cursor":"opaque&+/#"}"""));
        })) { BaseAddress = new Uri("https://audit.invalid") };
        using var client = new NewNanManagerClient(http);
        var result = await client.Monitor.GetMonitorStatsPageAsync(7, new MonitorStatsQuery { Since = 0, Duration = 86400, Limit = 10000, Cursor = cursor });
        var query = HttpUtility.ParseQueryString(paths[0].Query);
        Assert.Equal(4, query.Count);
        Assert.Equal("0", query["since"]);
        Assert.Equal("86400", query["duration"]);
        Assert.Equal("10000", query["limit"]);
        Assert.Equal(cursor, query["cursor"]);
        Assert.Equal(cursor, result.NextCursor);
        Assert.Null(result.Stats[0].MeasurementType);
        Assert.Null(result.Stats[0].LatencyMetric);
        Assert.Equal("unknown", result.Stats[1].MeasurementType);
        Assert.Equal("legacy", result.Stats[1].LatencyMetric);
        await client.Monitor.GetMonitorStatsAsync(7);
        Assert.Empty(paths[1].Query);
    }

    [Fact]
    public void ServerStatusRetainsMeasurementSourceAndMetric()
    {
        var known = JsonSerializer.Deserialize<ServerStatus>("""{"measurement_type":"pull","latency_metric":"rtt"}""")!;
        Assert.Equal("pull", known.MeasurementType);
        Assert.Equal("rtt", known.LatencyMetric);
        var unknown = JsonSerializer.Deserialize<ServerStatus>("{}")!;
        Assert.Null(unknown.MeasurementType);
        Assert.Null(unknown.LatencyMetric);
        Assert.Null(JsonSerializer.Deserialize<ApiToken>("{}")!.ServerId);
    }

    [Fact]
    public async Task PeriodicSnapshotsAreSentOnceWithoutChangingNames()
    {
        foreach (var count in new[] { 0, 1000 })
        {
            var calls = 0;
            using var http = new HttpClient(new FakeHandler(async request =>
            {
                calls++;
                using var body = JsonDocument.Parse(await request.Content!.ReadAsStringAsync());
                Assert.False(body.RootElement.GetProperty("login").GetBoolean());
                var players = body.RootElement.GetProperty("players");
                Assert.Equal(count, players.GetArrayLength());
                if (count > 0) Assert.Equal("MiXeD0", players[0].GetProperty("player_name").GetString());
                return Json("""{"results":[],"processed_at":1}""");
            })) { BaseAddress = new Uri("https://audit.invalid") };
            using var client = new NewNanManagerClient(http);
            await client.Players.ValidateAsync(new ValidateRequest { ServerId = 7, Login = false,
                Players = Enumerable.Range(0, count).Select(index => new PlayerValidateInfo { PlayerName = $"MiXeD{index}", IP = "192.0.2.1" }).ToList() });
            Assert.Equal(1, calls);
        }
    }

    private static HttpResponseMessage Json(string body) => new(HttpStatusCode.OK) { Content = new StringContent(body) };

    private sealed class FakeHandler(Func<HttpRequestMessage, Task<HttpResponseMessage>> response) : HttpMessageHandler
    {
        protected override Task<HttpResponseMessage> SendAsync(HttpRequestMessage request, CancellationToken cancellationToken) => response(request);
    }
}
