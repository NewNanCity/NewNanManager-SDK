import com.nanmanager.client.NewNanManagerClient
import com.nanmanager.client.models.*
import kotlinx.coroutines.runBlocking

/**
 * NewNanManager Kotlin SDK Modular Architecture Test
 * Test the new modular architecture and all functional modules
 */
fun main() = runBlocking {
    println("=== NewNanManager Kotlin SDK Modular Architecture Test ===\n")

    // Initialize client
    val client = NewNanManagerClient(
        token = "7p9piy2NagtMAryeyBBY7vzUKK1qDJOq",
        baseUrl = "http://localhost:8000",
        enableLogging = true
    )

    try {
        // Test 1: Data Models
        println("Test 1: Data Models")
        println("BanMode.NORMAL = ${BanMode.NORMAL.value}")
        println("BanMode.TEMPORARY = ${BanMode.TEMPORARY.value}")
        println("BanMode.PERMANENT = ${BanMode.PERMANENT.value}")
        println("LoginAction.LOGIN = ${LoginAction.LOGIN.value}")
        println("LoginAction.LOGOUT = ${LoginAction.LOGOUT.value}")
        println("✓ Data models test completed\n")

        // Test 2: Player Service Module
        println("Test 2: Player Service Module")
        try {
            val playersRequest = ListPlayersRequest(page = 1, pageSize = 5)
            val players = client.players.listPlayers(playersRequest)
            println("✓ Get players list success: Total ${players.total}")
            println("✓ Current page: ${players.page}")
            players.players.take(3).forEach { player ->
                println("  - Player: ${player.name} (ID: ${player.id})")
            }
        } catch (e: Exception) {
            println("✗ Player service test failed: ${e.message}")
        }

        // Test 3: Server Service Module
        println("\nTest 3: Server Service Module")
        try {
            val serversRequest = ListServersRequest(page = 1, pageSize = 5)
            val servers = client.servers.listServers(serversRequest)
            println("✓ Get servers list success: Total ${servers.total}")
            servers.servers.take(3).forEach { server ->
                println("  - Server: ${server.name} (ID: ${server.id})")
            }
        } catch (e: Exception) {
            println("✗ Server service test failed: ${e.message}")
        }

        // Test 4: Town Service Module
        println("\nTest 4: Town Service Module")
        try {
            val townsRequest = ListTownsRequest(page = 1, pageSize = 5)
            val towns = client.towns.listTowns(townsRequest)
            println("✓ Get towns list success: Total ${towns.total}")
            towns.towns.take(3).forEach { town ->
                println("  - Town: ${town.name} (ID: ${town.id}, Level: ${town.level})")
            }
        } catch (e: Exception) {
            println("✗ Town service test failed: ${e.message}")
        }

        // Test 5: Token Service Module
        println("\nTest 5: Token Service Module")
        try {
            val tokens = client.tokens.listApiTokens()
            println("✓ Get tokens list success: Total ${tokens.tokens.size}")
            tokens.tokens.take(3).forEach { token ->
                println("  - Token: ${token.name} (ID: ${token.id}, Role: ${token.role})")
            }
        } catch (e: Exception) {
            println("✗ Token service test failed: ${e.message}")
        }

        // Test 6: IP Service Module
        println("\nTest 6: IP Service Module")
        try {
            val ipsRequest = ListIPsRequest(page = 1, pageSize = 5)
            val ips = client.ips.listIPs(ipsRequest)
            println("✓ Get IPs list success: Total ${ips.total}")
            ips.ips.take(3).forEach { ip ->
                println("  - IP: ${ip.ip} (Threat Level: ${ip.threatLevel})")
            }
        } catch (e: Exception) {
            println("✗ IP service test failed: ${e.message}")
        }

        // Test 7: Player-Server Relationship Service Module
        println("\nTest 7: Player-Server Relationship Service Module")
        try {
            val onlinePlayersRequest = GetOnlinePlayersRequest(page = 1, pageSize = 5)
            val onlinePlayers = client.playerServers.getOnlinePlayers(onlinePlayersRequest)
            println("✓ Get online players success: Total ${onlinePlayers.total}")
            onlinePlayers.players.take(3).forEach { player ->
                println("  - Online Player: ${player.playerName} (Server: ${player.serverName})")
            }
        } catch (e: Exception) {
            println("✗ Player-Server relationship service test failed: ${e.message}")
        }

        // Test 8: Monitor Service Module
        println("\nTest 8: Monitor Service Module")
        try {
            val serverStatus = client.monitor.getServerStatus(1)
            println("✓ Get server status success: Online=${serverStatus.online}")
        } catch (e: Exception) {
            println("✗ Monitor service test failed: ${e.message}")
        }

        // Test 9: Backward Compatibility
        println("\nTest 9: Backward Compatibility")
        try {
            @Suppress("DEPRECATION")
            val compatPlayers = client.listPlayers(page = 1, pageSize = 3)
            println("✓ Backward compatibility test success: Got ${compatPlayers.players.size} players")
        } catch (e: Exception) {
            println("✗ Backward compatibility test failed: ${e.message}")
        }

        println("\n=== Kotlin SDK Modular Architecture Test Completed ===")

    } catch (e: Exception) {
        println("Serious error during testing: ${e.message}")
        e.printStackTrace()
    } finally {
        client.close()
    }
}
