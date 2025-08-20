using Microsoft.Extensions.Logging;
using NewNanManager.Client;
using NewNanManager.Client.Models;

namespace NewNanManager.Client.Examples;

/// <summary>
/// 基本使用示例程序
/// </summary>
public class Program
{
    public static async Task Main(string[] args)
    {
        // 配置日志
        using var loggerFactory = LoggerFactory.Create(builder =>
            builder.AddConsole().SetMinimumLevel(LogLevel.Information));
        var logger = loggerFactory.CreateLogger<Program>();

        // 从命令行参数或环境变量获取配置
        var baseUrl = args.Length > 0 ? args[0] : Environment.GetEnvironmentVariable("NANMANAGER_BASE_URL") ?? "https://your-server.com";
        var token = args.Length > 1 ? args[1] : Environment.GetEnvironmentVariable("NANMANAGER_TOKEN") ?? "your-api-token";

        if (baseUrl == "https://your-server.com" || token == "your-api-token")
        {
            Console.WriteLine("请提供有效的服务器URL和API Token:");
            Console.WriteLine("方式1: 命令行参数");
            Console.WriteLine("  dotnet run <base-url> <token>");
            Console.WriteLine("方式2: 环境变量");
            Console.WriteLine("  set NANMANAGER_BASE_URL=https://your-server.com");
            Console.WriteLine("  set NANMANAGER_TOKEN=your-api-token");
            return;
        }

        // 创建客户端
        var client = new NewNanManagerClient(baseUrl, token, logger);

        try
        {
            Console.WriteLine("=== NewNanManager C# SDK 示例 ===");
            Console.WriteLine($"服务器: {baseUrl}");
            Console.WriteLine();

            await RunExamples(client);
        }
        catch (Exception ex)
        {
            logger.LogError(ex, "示例执行失败");
            Console.WriteLine($"错误: {ex.Message}");
        }
        finally
        {
            client.Dispose();
        }
    }

    private static async Task RunExamples(NewNanManagerClient client)
    {
        // 获取服务器列表
        Console.WriteLine("1. 获取服务器列表...");
        try
        {
            var servers = await client.Servers.ListServersAsync(page: 1, pageSize: 5);
            Console.WriteLine($"   找到 {servers.Total} 个服务器");
            foreach (var server in servers.Servers.Take(3))
            {
                Console.WriteLine($"   - {server.Name} ({server.Address}) - {server.ServerType}");
            }
        }
        catch (Exception ex)
        {
            Console.WriteLine($"   错误: {ex.Message}");
        }
        Console.WriteLine();

        // 获取玩家列表
        Console.WriteLine("2. 获取玩家列表...");
        try
        {
            var players = await client.Players.ListPlayersAsync(page: 1, pageSize: 5);
            Console.WriteLine($"   找到 {players.Total} 个玩家");
            foreach (var player in players.Players.Take(3))
            {
                Console.WriteLine($"   - {player.Name} (ID: {player.Id}) - 状态: {player.BanMode}");
            }
        }
        catch (Exception ex)
        {
            Console.WriteLine($"   错误: {ex.Message}");
        }
        Console.WriteLine();

        // 获取城镇列表
        Console.WriteLine("3. 获取城镇列表...");
        try
        {
            var towns = await client.Towns.ListTownsAsync(page: 1, pageSize: 5);
            Console.WriteLine($"   找到 {towns.Total} 个城镇");
            foreach (var town in towns.Towns.Take(3))
            {
                Console.WriteLine($"   - {town.Name} (等级: {town.Level})");
            }
        }
        catch (Exception ex)
        {
            Console.WriteLine($"   错误: {ex.Message}");
        }
        Console.WriteLine();

        // 获取Token列表
        Console.WriteLine("4. 获取Token列表...");
        try
        {
            var tokens = await client.Tokens.ListApiTokensAsync();
            Console.WriteLine($"   找到 {tokens.Tokens.Count} 个Token");
            foreach (var token in tokens.Tokens.Take(3))
            {
                Console.WriteLine($"   - {token.Name} ({token.Role}) - 状态: {(token.Active ? "激活" : "禁用")}");
            }
        }
        catch (Exception ex)
        {
            Console.WriteLine($"   错误: {ex.Message}");
        }
        Console.WriteLine();

        // 演示创建和删除操作（如果有权限）
        Console.WriteLine("5. 演示创建操作...");
        try
        {
            // 创建测试玩家
            var createPlayerRequest = new CreatePlayerRequest
            {
                Name = $"TestPlayer_{DateTime.Now:yyyyMMddHHmmss}",
                QQ = "123456789"
            };

            var newPlayer = await client.Players.CreatePlayerAsync(createPlayerRequest);
            Console.WriteLine($"   创建玩家成功: {newPlayer.Name} (ID: {newPlayer.Id})");

            // 立即删除测试玩家
            await client.Players.DeletePlayerAsync(newPlayer.Id);
            Console.WriteLine($"   删除玩家成功: {newPlayer.Name}");
        }
        catch (Exception ex)
        {
            Console.WriteLine($"   创建/删除操作失败: {ex.Message}");
        }
        Console.WriteLine();

        Console.WriteLine("=== 示例完成 ===");
    }
}
