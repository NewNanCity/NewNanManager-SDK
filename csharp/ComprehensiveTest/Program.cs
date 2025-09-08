using Microsoft.Extensions.Logging;
using NewNanManager.Client;
using NewNanManager.Client.Exceptions;
using NewNanManager.Client.Models;

namespace ComprehensiveTest;

/// <summary>
/// Comprehensive Test Program for NewNanManager C# SDK
/// Tests all API interfaces in realistic business scenarios
/// </summary>
public class Program
{
    private static readonly ILogger Logger = LoggerFactory
        .Create(builder => builder.AddConsole().SetMinimumLevel(LogLevel.Information))
        .CreateLogger<Program>();

    public static async Task Main(string[] args)
    {
        Console.WriteLine("=== NewNanManager C# SDK Comprehensive Test ===\n");

        var client = new NewNanManagerClient(
            "http://localhost:8000",
            "7p9piy2NagtMAryeyBBY7vzUKK1qDJOq",
            Logger
        );

        int? testPlayerId = null;
        int? testServerId = null;
        int? testTownId = null;

        try
        {
            // ========== 1. Player Management Tests ==========
            Console.WriteLine("=== 1. Player Management Tests ===");

            // 1.1 List existing players
            Console.WriteLine("1.1 Listing existing players...");
            try
            {
                var players = await client.Players.ListPlayersAsync(page: 1, pageSize: 10);
                Console.WriteLine($"✓ Found {players.Total} players");
                if (players.Players.Any())
                {
                    testPlayerId = players.Players.First().Id;
                    Console.WriteLine(
                        $"  Using existing player: {players.Players.First().Name} (ID: {testPlayerId})"
                    );
                }
            }
            catch (Exception e)
            {
                Console.WriteLine($"✗ List players failed: {e.Message}");
            }

            // 1.2 Create a new player
            Console.WriteLine("\n1.2 Creating a new player...");
            try
            {
                var timestamp = DateTimeOffset.UtcNow.ToUnixTimeSeconds();
                var createRequest = new CreatePlayerRequest
                {
                    Name = $"TestPlayerCS_{timestamp}",
                    QQ = "123456789",
                    InQQGroup = true,
                    InQQGuild = false,
                    InDiscord = false,
                };
                var newPlayer = await client.Players.CreatePlayerAsync(createRequest);
                testPlayerId = newPlayer.Id;
                Console.WriteLine($"✓ Created player: {newPlayer.Name} (ID: {newPlayer.Id})");
            }
            catch (Exception e)
            {
                Console.WriteLine($"✗ Create player failed: {e.Message}");
            }

            // 1.3 Get player details
            if (testPlayerId.HasValue)
            {
                Console.WriteLine("\n1.3 Getting player details...");
                try
                {
                    var player = await client.Players.GetPlayerAsync(testPlayerId.Value);
                    Console.WriteLine(
                        $"✓ Player details: {player.Name}, Ban Mode: {player.BanMode}"
                    );
                }
                catch (Exception e)
                {
                    Console.WriteLine($"✗ Get player failed: {e.Message}");
                }
            }

            // 1.4 Update player information
            if (testPlayerId.HasValue)
            {
                Console.WriteLine("\n1.4 Updating player information...");
                try
                {
                    var updateRequest = new UpdatePlayerRequest { Discord = "updated_discord_id" };
                    var updatedPlayer = await client.Players.UpdatePlayerAsync(
                        testPlayerId.Value,
                        updateRequest
                    );
                    Console.WriteLine($"✓ Updated player: {updatedPlayer.Name}");
                }
                catch (Exception e)
                {
                    Console.WriteLine($"✗ Update player failed: {e.Message}");
                }
            }

            // ========== 2. Server Management Tests ==========
            Console.WriteLine("\n=== 2. Server Management Tests ===");

            // 2.1 List existing servers
            Console.WriteLine("2.1 Listing existing servers...");
            try
            {
                var servers = await client.Servers.ListServersAsync(page: 1, pageSize: 10);
                Console.WriteLine($"✓ Found {servers.Total} servers");
                if (servers.Servers.Any())
                {
                    testServerId = servers.Servers.First().Id;
                    Console.WriteLine(
                        $"  Using existing server: {servers.Servers.First().Name} (ID: {testServerId})"
                    );
                }
            }
            catch (Exception e)
            {
                Console.WriteLine($"✗ List servers failed: {e.Message}");
            }

            // 2.2 Register a new server
            Console.WriteLine("\n2.2 Registering a new server...");
            try
            {
                var timestamp = DateTimeOffset.UtcNow.ToUnixTimeSeconds();
                var registerRequest = new RegisterServerRequest
                {
                    Name = $"TestServerCS_{timestamp}",
                    Address = $"test{timestamp}.example.com:25565",
                    Description = "Test server for comprehensive testing",
                };
                var newServer = await client.Servers.RegisterServerAsync(registerRequest);
                testServerId = newServer.Id;
                Console.WriteLine($"✓ Registered server: {newServer.Name} (ID: {newServer.Id})");
            }
            catch (Exception e)
            {
                Console.WriteLine($"✗ Register server failed: {e.Message}");
            }

            // 2.3 Get server details
            if (testServerId.HasValue)
            {
                Console.WriteLine("\n2.3 Getting server details...");
                try
                {
                    var server = await client.Servers.GetServerAsync(testServerId.Value);
                    Console.WriteLine($"✓ Server: {server.Name}, Address: {server.Address}");
                }
                catch (Exception e)
                {
                    Console.WriteLine($"✗ Get server detail failed: {e.Message}");
                }
            }

            // ========== 3. Town Management Tests ==========
            Console.WriteLine("\n=== 3. Town Management Tests ===");

            // 3.1 List existing towns
            Console.WriteLine("3.1 Listing existing towns...");
            try
            {
                var towns = await client.Towns.ListTownsAsync(page: 1, pageSize: 10);
                Console.WriteLine($"✓ Found {towns.Total} towns");
                if (towns.Towns.Any())
                {
                    testTownId = towns.Towns.First().Id;
                    Console.WriteLine(
                        $"  Using existing town: {towns.Towns.First().Name} (ID: {testTownId})"
                    );
                }
            }
            catch (Exception e)
            {
                Console.WriteLine($"✗ List towns failed: {e.Message}");
            }

            // 3.2 Create a new town
            Console.WriteLine("\n3.2 Creating a new town...");
            try
            {
                var timestamp = DateTimeOffset.UtcNow.ToUnixTimeSeconds();
                var createRequest = new CreateTownRequest
                {
                    Name = $"TestTownCS_{timestamp}",
                    Level = 1,
                    Description = "Test town for comprehensive testing",
                };
                var newTown = await client.Towns.CreateTownAsync(createRequest);
                testTownId = newTown.Id;
                Console.WriteLine($"✓ Created town: {newTown.Name} (ID: {newTown.Id})");
            }
            catch (Exception e)
            {
                Console.WriteLine($"✗ Create town failed: {e.Message}");
            }

            // ========== 4. Token Management Tests ==========
            Console.WriteLine("\n=== 4. Token Management Tests ===");

            Console.WriteLine("4.1 Listing API tokens...");
            try
            {
                var tokens = await client.Tokens.ListApiTokensAsync();
                Console.WriteLine($"✓ Found {tokens.Tokens.Count} tokens");
                foreach (var token in tokens.Tokens.Take(3))
                {
                    Console.WriteLine($"  - {token.Name} (Role: {token.Role})");
                }
            }
            catch (Exception e)
            {
                Console.WriteLine($"✗ List tokens failed: {e.Message}");
            }

            // ========== 5. Monitor Service Tests ==========
            Console.WriteLine("\n=== 5. Monitor Service Tests ===");

            if (testServerId.HasValue)
            {
                Console.WriteLine("5.1 Getting server status...");
                try
                {
                    var serverStatus = await client.Monitor.GetServerStatusAsync(
                        testServerId.Value
                    );
                    Console.WriteLine(
                        $"✓ Server Status: Online={serverStatus.Online}, Players={serverStatus.CurrentPlayers}/{serverStatus.MaxPlayers}"
                    );
                }
                catch (Exception e)
                {
                    Console.WriteLine($"✗ Get server status failed: {e.Message}");
                }
            }

            // ========== 6. Player Login Validation Tests ==========
            Console.WriteLine("\n=== 6. Player Login Validation Tests ===");

            if (testServerId.HasValue)
            {
                Console.WriteLine("6.1 Testing player login validation...");
                try
                {
                    var validateRequest = new ValidateLoginRequest
                    {
                        PlayerName = "TestPlayer123",
                        IP = "192.168.1.100",
                        ServerId = testServerId.Value,
                    };
                    var validateResult = await client.Players.ValidateLoginAsync(validateRequest);
                    Console.WriteLine(
                        $"✓ Login validation completed for {validateRequest.PlayerName}"
                    );
                    Console.WriteLine(
                        $"  Result: {(validateResult.Allowed ? "ALLOWED" : $"DENIED ({validateResult.Reason})")}"
                    );
                }
                catch (Exception e)
                {
                    Console.WriteLine($"✗ Player login validation failed: {e.Message}");
                }
            }

            // ========== 7. Town Member Management Tests ==========
            Console.WriteLine("\n=== 7. Town Member Management Tests ===");

            if (testTownId.HasValue && testPlayerId.HasValue)
            {
                Console.WriteLine("\n7.2 Getting town members...");
                try
                {
                    var members = await client.Towns.GetTownMembersAsync(
                        testTownId.Value,
                        page: 1,
                        pageSize: 10
                    );
                    Console.WriteLine($"✓ Town has {members.Total} members");
                }
                catch (Exception e)
                {
                    Console.WriteLine($"✗ Get town members failed: {e.Message}");
                }
            }

            // ========== 8. Error Handling Tests ==========
            Console.WriteLine("\n=== 8. Error Handling Tests ===");

            Console.WriteLine("8.1 Testing invalid player ID...");
            try
            {
                await client.Players.GetPlayerAsync(999999);
                Console.WriteLine("✗ Should have thrown an error for invalid player ID");
            }
            catch (Exception e)
            {
                Console.WriteLine($"✓ Correctly handled error: {e.Message}");
            }

            Console.WriteLine("\n8.2 Testing invalid server ID...");
            try
            {
                await client.Servers.GetServerAsync(999999);
                Console.WriteLine("✗ Should have thrown an error for invalid server ID");
            }
            catch (Exception e)
            {
                Console.WriteLine($"✓ Correctly handled error: {e.Message}");
            }

            Console.WriteLine("\n=== Comprehensive Test Completed ===");
            Console.WriteLine("Summary:");
            Console.WriteLine($"- Test Player ID: {testPlayerId}");
            Console.WriteLine($"- Test Server ID: {testServerId}");
            Console.WriteLine($"- Test Town ID: {testTownId}");
        }
        catch (Exception e)
        {
            Console.WriteLine($"Critical error during testing: {e.Message}");
            Console.WriteLine(e.StackTrace);
        }
        finally
        {
            client.Dispose();
        }
    }
}
