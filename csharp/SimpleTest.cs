using System;
using System.Net.Http;
using System.Text;
using System.Text.Json;
using System.Threading.Tasks;

namespace SimpleTest;

/// <summary>
/// Simple Test for NewNanManager C# SDK using direct HTTP calls
/// Tests basic functionality and error handling
/// </summary>
public class Program
{
    private static readonly HttpClient httpClient = new HttpClient();
    private const string BaseUrl = "http://localhost:8000";
    private const string Token = "7p9piy2NagtMAryeyBBY7vzUKK1qDJOq";

    public static async Task Main(string[] args)
    {
        Console.WriteLine("=== NewNanManager C# SDK Simple Test ===\n");

        // Set up HTTP client
        httpClient.DefaultRequestHeaders.Add("Authorization", $"Bearer {Token}");
        httpClient.DefaultRequestHeaders.Add("User-Agent", "NewNanManager-CSharp-SDK/1.0.0");

        try
        {
            // ========== 1. Player Management Tests ==========
            Console.WriteLine("=== 1. Player Management Tests ===");

            // 1.1 List existing players
            Console.WriteLine("1.1 Listing existing players...");
            try
            {
                var playersResponse = await httpClient.GetAsync($"{BaseUrl}/api/v1/players?page=1&page_size=10");
                var playersContent = await playersResponse.Content.ReadAsStringAsync();
                
                if (playersResponse.IsSuccessStatusCode)
                {
                    var playersData = JsonSerializer.Deserialize<JsonElement>(playersContent);
                    var total = playersData.GetProperty("total").GetInt32();
                    Console.WriteLine($"✓ Found {total} players");
                    
                    if (playersData.GetProperty("players").GetArrayLength() > 0)
                    {
                        var firstPlayer = playersData.GetProperty("players")[0];
                        var playerId = firstPlayer.GetProperty("id").GetInt32();
                        var playerName = firstPlayer.GetProperty("name").GetString();
                        Console.WriteLine($"  Using existing player: {playerName} (ID: {playerId})");
                    }
                }
                else
                {
                    await HandleErrorResponse(playersResponse, playersContent, "List players");
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
                var createRequest = new
                {
                    name = $"TestPlayerCS_{timestamp}",
                    qq = "123456789",
                    in_qq_group = true,
                    in_qq_guild = false,
                    in_discord = false
                };

                var json = JsonSerializer.Serialize(createRequest);
                var content = new StringContent(json, Encoding.UTF8, "application/json");
                
                var response = await httpClient.PostAsync($"{BaseUrl}/api/v1/players", content);
                var responseContent = await response.Content.ReadAsStringAsync();

                if (response.IsSuccessStatusCode)
                {
                    var playerData = JsonSerializer.Deserialize<JsonElement>(responseContent);
                    var playerId = playerData.GetProperty("id").GetInt32();
                    var playerName = playerData.GetProperty("name").GetString();
                    Console.WriteLine($"✓ Created player: {playerName} (ID: {playerId})");
                }
                else
                {
                    await HandleErrorResponse(response, responseContent, "Create player");
                }
            }
            catch (Exception e)
            {
                Console.WriteLine($"✗ Create player failed: {e.Message}");
            }

            // ========== 2. Server Management Tests ==========
            Console.WriteLine("\n=== 2. Server Management Tests ===");

            // 2.1 List existing servers
            Console.WriteLine("2.1 Listing existing servers...");
            try
            {
                var serversResponse = await httpClient.GetAsync($"{BaseUrl}/api/v1/servers?page=1&page_size=10");
                var serversContent = await serversResponse.Content.ReadAsStringAsync();
                
                if (serversResponse.IsSuccessStatusCode)
                {
                    var serversData = JsonSerializer.Deserialize<JsonElement>(serversContent);
                    var total = serversData.GetProperty("total").GetInt32();
                    Console.WriteLine($"✓ Found {total} servers");
                    
                    if (serversData.GetProperty("servers").GetArrayLength() > 0)
                    {
                        var firstServer = serversData.GetProperty("servers")[0];
                        var serverId = firstServer.GetProperty("id").GetInt32();
                        var serverName = firstServer.GetProperty("name").GetString();
                        Console.WriteLine($"  Using existing server: {serverName} (ID: {serverId})");
                    }
                }
                else
                {
                    await HandleErrorResponse(serversResponse, serversContent, "List servers");
                }
            }
            catch (Exception e)
            {
                Console.WriteLine($"✗ List servers failed: {e.Message}");
            }

            // ========== 3. Town Management Tests ==========
            Console.WriteLine("\n=== 3. Town Management Tests ===");

            // 3.1 List existing towns
            Console.WriteLine("3.1 Listing existing towns...");
            try
            {
                var townsResponse = await httpClient.GetAsync($"{BaseUrl}/api/v1/towns?page=1&page_size=10");
                var townsContent = await townsResponse.Content.ReadAsStringAsync();
                
                if (townsResponse.IsSuccessStatusCode)
                {
                    var townsData = JsonSerializer.Deserialize<JsonElement>(townsContent);
                    var total = townsData.GetProperty("total").GetInt32();
                    Console.WriteLine($"✓ Found {total} towns");
                    
                    if (townsData.GetProperty("towns").GetArrayLength() > 0)
                    {
                        var firstTown = townsData.GetProperty("towns")[0];
                        var townId = firstTown.GetProperty("id").GetInt32();
                        var townName = firstTown.GetProperty("name").GetString();
                        var townLevel = firstTown.GetProperty("level").GetInt32();
                        Console.WriteLine($"  Using existing town: {townName} (ID: {townId}, Level: {townLevel})");
                    }
                }
                else
                {
                    await HandleErrorResponse(townsResponse, townsContent, "List towns");
                }
            }
            catch (Exception e)
            {
                Console.WriteLine($"✗ List towns failed: {e.Message}");
            }

            // ========== 4. Token Management Tests ==========
            Console.WriteLine("\n=== 4. Token Management Tests ===");

            Console.WriteLine("4.1 Listing API tokens...");
            try
            {
                var tokensResponse = await httpClient.GetAsync($"{BaseUrl}/api/v1/tokens");
                var tokensContent = await tokensResponse.Content.ReadAsStringAsync();
                
                if (tokensResponse.IsSuccessStatusCode)
                {
                    var tokensData = JsonSerializer.Deserialize<JsonElement>(tokensContent);
                    var tokens = tokensData.GetProperty("tokens");
                    Console.WriteLine($"✓ Found {tokens.GetArrayLength()} tokens");
                    
                    var count = 0;
                    foreach (var token in tokens.EnumerateArray())
                    {
                        if (count >= 3) break;
                        var tokenName = token.GetProperty("name").GetString();
                        var tokenRole = token.GetProperty("role").GetString();
                        Console.WriteLine($"  - {tokenName} (Role: {tokenRole})");
                        count++;
                    }
                }
                else
                {
                    await HandleErrorResponse(tokensResponse, tokensContent, "List tokens");
                }
            }
            catch (Exception e)
            {
                Console.WriteLine($"✗ List tokens failed: {e.Message}");
            }

            // ========== 5. Error Handling Tests ==========
            Console.WriteLine("\n=== 5. Error Handling Tests ===");

            Console.WriteLine("5.1 Testing invalid player ID...");
            try
            {
                var response = await httpClient.GetAsync($"{BaseUrl}/api/v1/players/999999");
                var content = await response.Content.ReadAsStringAsync();
                
                if (!response.IsSuccessStatusCode)
                {
                    await HandleErrorResponse(response, content, "Get invalid player", expectError: true);
                }
                else
                {
                    Console.WriteLine("✗ Should have thrown an error for invalid player ID");
                }
            }
            catch (Exception e)
            {
                Console.WriteLine($"✓ Correctly handled error: {e.Message}");
            }

            Console.WriteLine("\n=== Simple Test Completed ===");
        }
        catch (Exception e)
        {
            Console.WriteLine($"Critical error during testing: {e.Message}");
            Console.WriteLine(e.StackTrace);
        }
        finally
        {
            httpClient.Dispose();
        }
    }

    private static async Task HandleErrorResponse(HttpResponseMessage response, string content, string operation, bool expectError = false)
    {
        try
        {
            // Try to parse error response format {"detail": "..."}
            var errorData = JsonSerializer.Deserialize<JsonElement>(content);
            if (errorData.TryGetProperty("detail", out var detailElement))
            {
                var detail = detailElement.GetString();
                if (expectError)
                {
                    Console.WriteLine($"✓ Correctly handled error: {detail}");
                }
                else
                {
                    Console.WriteLine($"✗ {operation} failed: {detail}");
                }
                return;
            }
        }
        catch (JsonException)
        {
            // If not JSON, use raw content
        }

        var message = $"HTTP {(int)response.StatusCode}: {response.ReasonPhrase}";
        if (!string.IsNullOrEmpty(content))
        {
            message += $" - {content}";
        }

        if (expectError)
        {
            Console.WriteLine($"✓ Correctly handled error: {message}");
        }
        else
        {
            Console.WriteLine($"✗ {operation} failed: {message}");
        }
    }
}
