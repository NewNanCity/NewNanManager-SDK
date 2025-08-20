using Microsoft.Extensions.Logging;
using NewNanManager.Client;
using NewNanManager.Client.Models;

namespace NewNanManager.Client.Examples;

/// <summary>
/// 基本使用示例
/// </summary>
public class BasicUsage
{
    public static async Task Main(string[] args)
    {
        // 配置日志
        using var loggerFactory = LoggerFactory.Create(builder =>
            builder.AddConsole().SetMinimumLevel(LogLevel.Information));
        var logger = loggerFactory.CreateLogger<BasicUsage>();

        // 创建客户端
        var client = new NewNanManagerClient("https://your-server.com", "your-api-token", logger);

        try
        {
            await PlayerManagementExample(client);
            await ServerManagementExample(client);
            await TownManagementExample(client);
            await MonitoringExample(client);
            await TokenManagementExample(client);
        }
        catch (Exception ex)
        {
            logger.LogError(ex, "示例执行失败");
        }
        finally
        {
            client.Dispose();
        }
    }

    /// <summary>
    /// 玩家管理示例
    /// </summary>
    private static async Task PlayerManagementExample(NewNanManagerClient client)
    {
        Console.WriteLine("=== 玩家管理示例 ===");

        // 获取玩家列表
        var players = await client.Players.ListPlayersAsync(page: 1, pageSize: 10);
        Console.WriteLine($"找到 {players.Total} 个玩家");

        // 创建新玩家
        var newPlayer = await client.Players.CreatePlayerAsync(new CreatePlayerRequest
        {
            Name = "ExamplePlayer",
            QQ = "123456789",
            InQQGroup = true
        });
        Console.WriteLine($"创建玩家: {newPlayer.Name} (ID: {newPlayer.Id})");

        // 更新玩家信息
        var updatedPlayer = await client.Players.UpdatePlayerAsync(newPlayer.Id, new UpdatePlayerRequest
        {
            Discord = "example#1234"
        });
        Console.WriteLine($"更新玩家Discord: {updatedPlayer.Discord}");

        // 验证登录
        var loginResult = await client.Players.ValidateLoginAsync(new ValidateLoginRequest
        {
            PlayerName = newPlayer.Name,
            ServerId = 1
        });
        Console.WriteLine($"登录验证结果: {(loginResult.Allowed ? "允许" : "拒绝")}");

        // 清理
        await client.Players.DeletePlayerAsync(newPlayer.Id);
        Console.WriteLine("删除示例玩家");
    }

    /// <summary>
    /// 服务器管理示例
    /// </summary>
    private static async Task ServerManagementExample(NewNanManagerClient client)
    {
        Console.WriteLine("\n=== 服务器管理示例 ===");

        // 获取服务器列表
        var servers = await client.Servers.ListServersAsync();
        Console.WriteLine($"找到 {servers.Total} 个服务器");

        // 注册新服务器
        var newServer = await client.Servers.RegisterServerAsync(new RegisterServerRequest
        {
            Name = "ExampleServer",
            Address = "127.0.0.1:25565",
            ServerType = ServerType.Minecraft,
            Description = "示例服务器"
        });
        Console.WriteLine($"注册服务器: {newServer.Name} (ID: {newServer.Id})");

        // 获取服务器详细信息
        var serverDetail = await client.Servers.GetServerDetailAsync(newServer.Id);
        Console.WriteLine($"服务器详情: {serverDetail.Server.Name}");

        // 清理
        await client.Servers.DeleteServerAsync(newServer.Id);
        Console.WriteLine("删除示例服务器");
    }

    /// <summary>
    /// 城镇管理示例
    /// </summary>
    private static async Task TownManagementExample(NewNanManagerClient client)
    {
        Console.WriteLine("\n=== 城镇管理示例 ===");

        // 获取城镇列表
        var towns = await client.Towns.ListTownsAsync();
        Console.WriteLine($"找到 {towns.Total} 个城镇");

        // 创建新城镇
        var newTown = await client.Towns.CreateTownAsync(new CreateTownRequest
        {
            Name = "ExampleTown",
            Level = 1,
            Description = "示例城镇"
        });
        Console.WriteLine($"创建城镇: {newTown.Name} (ID: {newTown.Id})");

        // 获取城镇成员
        var members = await client.Towns.GetTownMembersAsync(newTown.Id);
        Console.WriteLine($"城镇成员数量: {members.Total}");

        // 清理
        await client.Towns.DeleteTownAsync(newTown.Id);
        Console.WriteLine("删除示例城镇");
    }

    /// <summary>
    /// 监控示例
    /// </summary>
    private static async Task MonitoringExample(NewNanManagerClient client)
    {
        Console.WriteLine("\n=== 监控示例 ===");

        // 首先创建一个服务器用于监控
        var server = await client.Servers.RegisterServerAsync(new RegisterServerRequest
        {
            Name = "MonitorServer",
            Address = "127.0.0.1:25565",
            ServerType = ServerType.Minecraft
        });

        try
        {
            // 发送心跳
            var heartbeat = await client.Monitor.HeartbeatAsync(server.Id, new HeartbeatRequest
            {
                Timestamp = DateTimeOffset.UtcNow.ToUnixTimeSeconds(),
                CurrentPlayers = 10,
                MaxPlayers = 50,
                TPS = 19.5,
                Version = "1.20.1"
            });
            Console.WriteLine($"心跳响应: {heartbeat.Status}");

            // 获取服务器状态
            var status = await client.Monitor.GetServerStatusAsync(server.Id);
            Console.WriteLine($"服务器状态: {(status.Online ? "在线" : "离线")}");

            // 获取延迟统计
            var latencyStats = await client.Monitor.GetLatencyStatsAsync(server.Id);
            Console.WriteLine($"平均延迟: {latencyStats.Average}ms");
        }
        finally
        {
            // 清理
            await client.Servers.DeleteServerAsync(server.Id);
        }
    }

    /// <summary>
    /// Token管理示例
    /// </summary>
    private static async Task TokenManagementExample(NewNanManagerClient client)
    {
        Console.WriteLine("\n=== Token管理示例 ===");

        // 获取Token列表
        var tokens = await client.Tokens.ListApiTokensAsync();
        Console.WriteLine($"找到 {tokens.Tokens.Count} 个Token");

        // 创建新Token
        var newToken = await client.Tokens.CreateApiTokenAsync(new CreateApiTokenRequest
        {
            Name = "ExampleToken",
            Role = "example",
            Description = "示例Token",
            ExpireDays = 30
        });
        Console.WriteLine($"创建Token: {newToken.TokenInfo.Name}");
        Console.WriteLine($"Token值: {newToken.TokenValue}");

        try
        {
            // 更新Token
            var updatedToken = await client.Tokens.UpdateApiTokenAsync(newToken.TokenInfo.Id, new UpdateApiTokenRequest
            {
                Description = "更新后的示例Token"
            });
            Console.WriteLine($"更新Token描述: {updatedToken.Description}");
        }
        finally
        {
            // 清理
            await client.Tokens.DeleteApiTokenAsync(newToken.TokenInfo.Id);
            Console.WriteLine("删除示例Token");
        }
    }
}
