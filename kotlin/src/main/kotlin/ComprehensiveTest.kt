import com.nanmanager.client.NewNanManagerClient
import com.nanmanager.client.models.*
import kotlinx.coroutines.runBlocking

/**
 * Comprehensive Test for NewNanManager Kotlin SDK
 * Tests all API interfaces in realistic business scenarios
 */
fun main() = runBlocking {
    println("=== NewNanManager Kotlin SDK Comprehensive Test ===\n")

    val client = NewNanManagerClient(
        token = "7p9piy2NagtMAryeyBBY7vzUKK1qDJOq",
        baseUrl = "http://localhost:8000",
        enableLogging = true
    )

    var testPlayerId: Int? = null
    var testServerId: Int? = null
    var testTownId: Int? = null

    try {
        // ========== 1. Player Management Tests ==========
        println("=== 1. Player Management Tests ===")

        // 1.1 List existing players
        println("1.1 Listing existing players...")
        try {
            val playersRequest = ListPlayersRequest(page = 1, pageSize = 10)
            val players = client.players.listPlayers(playersRequest)
            println("✓ Found ${players.total} players")
            if (players.players.isNotEmpty()) {
                testPlayerId = players.players[0].id
                println("  Using existing player: ${players.players[0].name} (ID: $testPlayerId)")
            }
        } catch (e: Exception) {
            println("✗ List players failed: ${e.message}")
        }

        // 1.2 Create a new player
        println("\n1.2 Creating a new player...")
        try {
            val timestamp = System.currentTimeMillis()
            val createRequest = CreatePlayerRequest(
                name = "TestPlayer_$timestamp",
                qq = "123456789",
                inQqGroup = true,
                inQqGuild = false,
                inDiscord = false
            )
            val newPlayer = client.players.createPlayer(createRequest)
            testPlayerId = newPlayer.id
            println("✓ Created player: ${newPlayer.name} (ID: ${newPlayer.id})")
        } catch (e: Exception) {
            println("✗ Create player failed: ${e.message}")
        }

        // 1.3 Get player details
        if (testPlayerId != null) {
            println("\n1.3 Getting player details...")
            try {
                val player = client.players.getPlayer(testPlayerId!!)
                println("✓ Player details: ${player.name}, Ban Mode: ${player.banMode}")
            } catch (e: Exception) {
                println("✗ Get player failed: ${e.message}")
            }
        }

        // 1.4 Update player information
        if (testPlayerId != null) {
            println("\n1.4 Updating player information...")
            try {
                val updateRequest = UpdatePlayerRequest(
                    id = testPlayerId!!,
                    discord = "updated_discord_id"
                )
                val updatedPlayer = client.players.updatePlayer(testPlayerId!!, updateRequest)
                println("✓ Updated player: ${updatedPlayer.name}")
            } catch (e: Exception) {
                println("✗ Update player failed: ${e.message}")
            }
        }

        // ========== 2. Server Management Tests ==========
        println("\n=== 2. Server Management Tests ===")

        // 2.1 List existing servers
        println("2.1 Listing existing servers...")
        try {
            val serversRequest = ListServersRequest(page = 1, pageSize = 10)
            val servers = client.servers.listServers(serversRequest)
            println("✓ Found ${servers.total} servers")
            if (servers.servers.isNotEmpty()) {
                testServerId = servers.servers[0].id
                println("  Using existing server: ${servers.servers[0].name} (ID: $testServerId)")
            }
        } catch (e: Exception) {
            println("✗ List servers failed: ${e.message}")
        }

        // 2.2 Register a new server
        println("\n2.2 Registering a new server...")
        try {
            val timestamp = System.currentTimeMillis()
            val registerRequest = RegisterServerRequest(
                name = "TestServer_$timestamp",
                address = "test$timestamp.example.com:25565",
                description = "Test server for comprehensive testing"
            )
            val newServer = client.servers.registerServer(registerRequest)
            testServerId = newServer.id
            println("✓ Registered server: ${newServer.name} (ID: ${newServer.id})")
        } catch (e: Exception) {
            println("✗ Register server failed: ${e.message}")
        }

        // 2.3 Get server details
        if (testServerId != null) {
            println("\n2.3 Getting server details...")
            try {
                val serverDetail = client.servers.getServerDetail(testServerId!!)
                println("✓ Server: ${serverDetail.server.name}, Active: ${serverDetail.server.active}")
            } catch (e: Exception) {
                println("✗ Get server detail failed: ${e.message}")
            }
        }

        // ========== 3. Town Management Tests ==========
        println("\n=== 3. Town Management Tests ===")

        // 3.1 List existing towns
        println("3.1 Listing existing towns...")
        try {
            val townsRequest = ListTownsRequest(page = 1, pageSize = 10)
            val towns = client.towns.listTowns(townsRequest)
            println("✓ Found ${towns.total} towns")
            if (towns.towns.isNotEmpty()) {
                testTownId = towns.towns[0].id
                println("  Using existing town: ${towns.towns[0].name} (ID: $testTownId)")
            }
        } catch (e: Exception) {
            println("✗ List towns failed: ${e.message}")
        }

        // 3.2 Create a new town
        println("\n3.2 Creating a new town...")
        try {
            val timestamp = System.currentTimeMillis()
            val createRequest = CreateTownRequest(
                name = "TestTown_$timestamp",
                level = 1,
                description = "Test town for comprehensive testing"
            )
            val newTown = client.towns.createTown(createRequest)
            testTownId = newTown.id
            println("✓ Created town: ${newTown.name} (ID: ${newTown.id})")
        } catch (e: Exception) {
            println("✗ Create town failed: ${e.message}")
        }

        // ========== 4. Player Validation Tests ==========
        println("\n=== 4. Player Validation Tests ===")

        if (testServerId != null) {
            println("4.1 Testing batch player validation...")
            try {
                val validateRequest = ValidateRequest(
                    players = listOf(
                        PlayerValidateInfo(
                            playerName = "TestPlayer123",
                            ip = "192.168.1.100",
                            clientVersion = "1.20.1",
                            protocolVersion = "763"
                        ),
                        PlayerValidateInfo(
                            playerName = "TestPlayer456",
                            ip = "192.168.1.101",
                            clientVersion = "1.19.4"
                        )
                    ),
                    serverId = testServerId!!,
                    login = true,
                    timestamp = System.currentTimeMillis()
                )
                val validateResult = client.players.validate(validateRequest)
                println("✓ Validated ${validateResult.results.size} players")
                validateResult.results.forEach { result ->
                    val status = if (result.allowed) "ALLOWED" else "DENIED (${result.reason})"
                    println("  - ${result.playerName}: $status")
                }
            } catch (e: Exception) {
                println("✗ Player validation failed: ${e.message}")
            }
        }

        // ========== 5. IP Management Tests ==========
        println("\n=== 5. IP Management Tests ===")

        println("5.1 Getting IP information...")
        try {
            val ipInfo = client.ips.getIPInfo("8.8.8.8")
            println("✓ IP Info: ${ipInfo.ip} (${ipInfo.country ?: "Unknown"})")
        } catch (e: Exception) {
            println("✗ Get IP info failed: ${e.message}")
        }

        println("\n5.2 Testing IP ban/unban...")
        try {
            client.ips.banIP("192.168.1.200", "Test ban for comprehensive testing")
            println("✓ IP banned successfully")

            client.ips.unbanIP("192.168.1.200")
            println("✓ IP unbanned successfully")
        } catch (e: Exception) {
            println("✗ IP ban/unban failed: ${e.message}")
        }

        // ========== 6. Token Management Tests ==========
        println("\n=== 6. Token Management Tests ===")

        println("6.1 Listing API tokens...")
        try {
            val tokens = client.tokens.listApiTokens()
            println("✓ Found ${tokens.tokens.size} tokens")
            tokens.tokens.take(3).forEach { token ->
                println("  - ${token.name} (Role: ${token.role})")
            }
        } catch (e: Exception) {
            println("✗ List tokens failed: ${e.message}")
        }

        // ========== 7. Player-Server Relationship Tests ==========
        println("\n=== 7. Player-Server Relationship Tests ===")

        if (testPlayerId != null && testServerId != null) {
            println("7.1 Setting player online status...")
            try {
                val setOnlineRequest = SetPlayerOnlineRequest(
                    playerId = testPlayerId!!,
                    serverId = testServerId!!,
                    online = true
                )
                client.playerServers.setPlayerOnline(setOnlineRequest)
                println("✓ Player online status set")
            } catch (e: Exception) {
                println("✗ Set player online failed: ${e.message}")
            }
        }

        println("\n7.2 Getting online players...")
        try {
            val onlinePlayersRequest = GetOnlinePlayersRequest(page = 1, pageSize = 10)
            val onlinePlayers = client.playerServers.getOnlinePlayers(onlinePlayersRequest)
            println("✓ Found ${onlinePlayers.total} online players")
            onlinePlayers.players.take(3).forEach { player ->
                println("  - ${player.playerName} on ${player.serverName}")
            }
        } catch (e: Exception) {
            println("✗ Get online players failed: ${e.message}")
        }

        // ========== 8. Monitor Service Tests ==========
        println("\n=== 8. Monitor Service Tests ===")

        if (testServerId != null) {
            println("8.1 Getting server status...")
            try {
                val serverStatus = client.monitor.getServerStatus(testServerId!!)
                println("✓ Server Status: Online=${serverStatus.online}, Players=${serverStatus.currentPlayers}/${serverStatus.maxPlayers}")
            } catch (e: Exception) {
                println("✗ Get server status failed: ${e.message}")
            }
        }

        // ========== 9. Town Member Management Tests ==========
        println("\n=== 9. Town Member Management Tests ===")

        if (testTownId != null && testPlayerId != null) {
            println("9.1 Adding player to town...")
            try {
                val manageMemberRequest = ManageTownMemberRequest(
                    townId = testTownId!!,
                    playerId = testPlayerId!!,
                    action = "add"
                )
                client.towns.manageTownMember(testTownId!!, manageMemberRequest)
                println("✓ Player added to town")
            } catch (e: Exception) {
                println("✗ Add player to town failed: ${e.message}")
            }

            println("\n9.2 Getting town members...")
            try {
                val getMembersRequest = GetTownMembersRequest(
                    townId = testTownId!!,
                    page = 1,
                    pageSize = 10
                )
                val members = client.towns.getTownMembers(getMembersRequest)
                println("✓ Town has ${members.total} members")
            } catch (e: Exception) {
                println("✗ Get town members failed: ${e.message}")
            }
        }

        // ========== 10. Error Handling Tests ==========
        println("\n=== 10. Error Handling Tests ===")

        println("10.1 Testing invalid player ID...")
        try {
            client.players.getPlayer(999999)
            println("✗ Should have thrown an error for invalid player ID")
        } catch (e: Exception) {
            println("✓ Correctly handled error: ${e.message}")
        }

        println("\n10.2 Testing invalid server ID...")
        try {
            client.servers.getServer(999999)
            println("✗ Should have thrown an error for invalid server ID")
        } catch (e: Exception) {
            println("✓ Correctly handled error: ${e.message}")
        }

        println("\n=== Comprehensive Test Completed ===")
        println("Summary:")
        println("- Test Player ID: $testPlayerId")
        println("- Test Server ID: $testServerId")
        println("- Test Town ID: $testTownId")

    } catch (e: Exception) {
        println("Critical error during testing: ${e.message}")
        e.printStackTrace()
    } finally {
        client.close()
    }
}
