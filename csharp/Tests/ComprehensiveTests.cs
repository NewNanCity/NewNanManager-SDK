using Microsoft.Extensions.Logging;
using NewNanManager.Client;
using NewNanManager.Client.Exceptions;
using NewNanManager.Client.Models;
using Xunit;
using Xunit.Abstractions;

namespace NewNanManager.Client.Tests;

/// <summary>
/// 完整的API功能测试
/// </summary>
public class ComprehensiveTests : IDisposable
{
    private readonly NewNanManagerClient _client;
    private readonly ILogger<ComprehensiveTests> _logger;

    // 测试配置
    private const string TestBaseUrl = "https://your-api-server.com";
    private const string TestToken = "your-test-token-here";

    public ComprehensiveTests(ITestOutputHelper output)
    {
        // 创建日志记录器
        using var loggerFactory = LoggerFactory.Create(builder =>
            builder.AddConsole().SetMinimumLevel(LogLevel.Debug)
        );
        _logger = loggerFactory.CreateLogger<ComprehensiveTests>();

        // 创建客户端
        _client = new NewNanManagerClient(TestBaseUrl, TestToken, _logger);
    }

    [Fact]
    public async Task PlayerManagement_FullWorkflow_ShouldWork()
    {
        var testPlayerName = $"TestPlayer_{DateTimeOffset.UtcNow.ToUnixTimeSeconds()}";
        int? createdPlayerId = null;

        try
        {
            // 1. 获取玩家列表
            var playersList = await _client.Players.ListPlayersAsync(page: 1, pageSize: 10);
            Assert.NotNull(playersList);
            _logger.LogInformation("Found {Count} players", playersList.Total);

            // 2. 创建玩家
            var createRequest = new CreatePlayerRequest
            {
                Name = testPlayerName,
                InQQGroup = true,
                InQQGuild = false,
                InDiscord = false,
            };

            var createdPlayer = await _client.Players.CreatePlayerAsync(createRequest);
            Assert.NotNull(createdPlayer);
            Assert.Equal(testPlayerName, createdPlayer.Name);
            createdPlayerId = createdPlayer.Id;
            _logger.LogInformation(
                "Created player: {Name} (ID: {Id})",
                createdPlayer.Name,
                createdPlayer.Id
            );

            // 3. 获取玩家详情
            var player = await _client.Players.GetPlayerAsync(createdPlayer.Id);
            Assert.NotNull(player);
            Assert.Equal(createdPlayer.Id, player.Id);
            Assert.Equal(testPlayerName, player.Name);

            // 4. 更新玩家信息
            var newPlayerName = testPlayerName + "_Updated";
            var updateRequest = new UpdatePlayerRequest { Name = newPlayerName };

            var updatedPlayer = await _client.Players.UpdatePlayerAsync(
                createdPlayer.Id,
                updateRequest
            );
            Assert.NotNull(updatedPlayer);
            Assert.Equal(newPlayerName, updatedPlayer.Name);
            _logger.LogInformation("Updated player name to: {Name}", updatedPlayer.Name);

            // 5. 封禁玩家
            var banRequest = new BanPlayerRequest
            {
                BanMode = BanMode.Temporary,
                DurationSeconds = 60,
                Reason = "测试封禁",
            };

            await _client.Players.BanPlayerAsync(createdPlayer.Id, banRequest);
            _logger.LogInformation("Banned player successfully");

            // 6. 解封玩家
            await _client.Players.UnbanPlayerAsync(createdPlayer.Id);
            _logger.LogInformation("Unbanned player successfully");

            // 7. 验证登录
            var validateRequest = new ValidateLoginRequest
            {
                PlayerName = newPlayerName,
                ServerId = 1,
            };

            var validateResult = await _client.Players.ValidateLoginAsync(validateRequest);
            Assert.NotNull(validateResult);
            _logger.LogInformation("Login validation result: {Allowed}", validateResult.Allowed);
        }
        finally
        {
            // 清理：删除测试玩家
            if (createdPlayerId.HasValue)
            {
                try
                {
                    await _client.Players.DeletePlayerAsync(createdPlayerId.Value);
                    _logger.LogInformation("Deleted test player");
                }
                catch (Exception ex)
                {
                    _logger.LogWarning(ex, "Failed to delete test player");
                }
            }
        }
    }

    [Fact]
    public async Task ServerManagement_FullWorkflow_ShouldWork()
    {
        var testServerName = $"TestServer_{DateTimeOffset.UtcNow.ToUnixTimeSeconds()}";
        var testServerAddress =
            $"test-{DateTimeOffset.UtcNow.ToUnixTimeSeconds()}.example.com:25565";
        int? createdServerId = null;

        try
        {
            // 1. 获取服务器列表
            var serversList = await _client.Servers.ListServersAsync(page: 1, pageSize: 10);
            Assert.NotNull(serversList);
            _logger.LogInformation("Found {Count} servers", serversList.Total);

            // 2. 注册服务器
            var registerRequest = new RegisterServerRequest
            {
                Name = testServerName,
                Address = testServerAddress,
                ServerType = ServerType.Minecraft,
            };

            var registeredServer = await _client.Servers.RegisterServerAsync(registerRequest);
            Assert.NotNull(registeredServer);
            Assert.Equal(testServerName, registeredServer.Name);
            createdServerId = registeredServer.Id;
            _logger.LogInformation(
                "Registered server: {Name} (ID: {Id})",
                registeredServer.Name,
                registeredServer.Id
            );

            // 3. 获取服务器信息
            var server = await _client.Servers.GetServerAsync(registeredServer.Id);
            Assert.NotNull(server);
            Assert.Equal(registeredServer.Id, server.Id);

            // 4. 更新服务器信息
            var newServerName = testServerName + "_Updated";
            var newAddress =
                $"updated-{DateTimeOffset.UtcNow.ToUnixTimeSeconds()}.example.com:25565";
            var updateRequest = new UpdateServerRequest
            {
                Name = newServerName,
                Address = newAddress,
            };

            var updatedServer = await _client.Servers.UpdateServerAsync(
                registeredServer.Id,
                updateRequest
            );
            Assert.NotNull(updatedServer);
            Assert.Equal(newServerName, updatedServer.Name);
            _logger.LogInformation("Updated server name to: {Name}", updatedServer.Name);

            // 5. 获取服务器详细信息
            var serverDetail = await _client.Servers.GetServerDetailAsync(registeredServer.Id);
            Assert.NotNull(serverDetail);
            Assert.NotNull(serverDetail.Server);
            Assert.Equal(registeredServer.Id, serverDetail.Server.Id);
        }
        finally
        {
            // 清理：删除测试服务器
            if (createdServerId.HasValue)
            {
                try
                {
                    await _client.Servers.DeleteServerAsync(createdServerId.Value);
                    _logger.LogInformation("Deleted test server");
                }
                catch (Exception ex)
                {
                    _logger.LogWarning(ex, "Failed to delete test server");
                }
            }
        }
    }

    [Fact]
    public async Task TownManagement_FullWorkflow_ShouldWork()
    {
        var testTownName = $"TestTown_{DateTimeOffset.UtcNow.ToUnixTimeSeconds()}";
        int? createdTownId = null;

        try
        {
            // 1. 获取城镇列表
            var townsList = await _client.Towns.ListTownsAsync(page: 1, pageSize: 10);
            Assert.NotNull(townsList);
            _logger.LogInformation("Found {Count} towns", townsList.Total);

            // 2. 创建城镇
            var createRequest = new CreateTownRequest { Name = testTownName, Level = 1 };

            var createdTown = await _client.Towns.CreateTownAsync(createRequest);
            Assert.NotNull(createdTown);
            Assert.Equal(testTownName, createdTown.Name);
            createdTownId = createdTown.Id;
            _logger.LogInformation(
                "Created town: {Name} (ID: {Id})",
                createdTown.Name,
                createdTown.Id
            );

            // 3. 获取城镇详情
            var town = await _client.Towns.GetTownAsync(createdTown.Id);
            Assert.NotNull(town);
            Assert.Equal(createdTown.Id, town.Id);

            // 4. 更新城镇信息
            var newTownName = testTownName + "_Updated";
            var updateRequest = new UpdateTownRequest { Name = newTownName, Level = 2 };

            var updatedTown = await _client.Towns.UpdateTownAsync(createdTown.Id, updateRequest);
            Assert.NotNull(updatedTown);
            Assert.Equal(newTownName, updatedTown.Name);
            Assert.Equal(2, updatedTown.Level);
            _logger.LogInformation(
                "Updated town: {Name}, Level: {Level}",
                updatedTown.Name,
                updatedTown.Level
            );

            // 5. 获取城镇成员列表
            var members = await _client.Towns.GetTownMembersAsync(
                createdTown.Id,
                page: 1,
                pageSize: 10
            );
            Assert.NotNull(members);
            _logger.LogInformation("Town has {Count} members", members.Total);
        }
        finally
        {
            // 清理：删除测试城镇
            if (createdTownId.HasValue)
            {
                try
                {
                    await _client.Towns.DeleteTownAsync(createdTownId.Value);
                    _logger.LogInformation("Deleted test town");
                }
                catch (Exception ex)
                {
                    _logger.LogWarning(ex, "Failed to delete test town");
                }
            }
        }
    }

    [Fact]
    public async Task TokenManagement_ListTokens_ShouldWork()
    {
        // 获取Token列表
        var tokensList = await _client.Tokens.ListApiTokensAsync();
        Assert.NotNull(tokensList);
        _logger.LogInformation("Found {Count} tokens", tokensList.Tokens.Count);
    }

    [Fact]
    public async Task ErrorHandling_ShouldWorkCorrectly()
    {
        // 测试获取不存在的资源
        await Assert.ThrowsAsync<ApiErrorException>(async () =>
        {
            await _client.Players.GetPlayerAsync(999999);
        });

        _logger.LogInformation("Error handling test passed");
    }

    public void Dispose()
    {
        _client?.Dispose();
    }
}
