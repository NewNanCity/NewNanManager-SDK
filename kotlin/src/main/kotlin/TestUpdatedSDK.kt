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
        // 测试1: 数据模型
        println("🧪 测试1: 数据模型")
        println("BanMode.NORMAL = ${BanMode.NORMAL.value}")
        println("BanMode.TEMPORARY = ${BanMode.TEMPORARY.value}")
        println("BanMode.PERMANENT = ${BanMode.PERMANENT.value}")
        println("LoginAction.LOGIN = ${LoginAction.LOGIN.value}")
        println("LoginAction.LOGOUT = ${LoginAction.LOGOUT.value}")
        println("✅ 数据模型测试完成\n")

        // 测试2: 玩家服务模块
        println("👤 测试2: 玩家服务模块")
        var createdPlayerId: Int? = null
        try {
            // 使用新的模块化API
            val playersRequest = ListPlayersRequest(page = 1, pageSize = 5)
            val players = client.players.listPlayers(playersRequest)
            println("✅ 获取玩家列表成功: 总数 ${players.total}")

            if (players.players.isNotEmpty()) {
                createdPlayerId = players.players[0].id
                println("ℹ️ 使用现有玩家: ${players.players[0].name} (ID: $createdPlayerId)")
            }

            // 测试创建玩家
            val timestamp = System.currentTimeMillis()
            val createRequest = CreatePlayerRequest(
                name = "TestPlayerKt$timestamp",
                qq = "123456789",
                inQqGroup = true,
                inQqGuild = false,
                inDiscord = false
            )
            val newPlayer = client.players.createPlayer(createRequest)
            println("✅ 创建玩家成功: ${newPlayer.name} (ID: ${newPlayer.id})")
            createdPlayerId = newPlayer.id
        } catch (e: Exception) {
            println("❌ 玩家服务测试失败: ${e.message}")
        }

        // 测试3: 批量玩家验证（使用玩家服务模块）
        println("\n🔍 测试3: 批量玩家验证")
        try {
            val validateRequest = ValidateRequest(
                players = listOf(
                    PlayerValidateInfo(
                        playerName = "TestPlayerKt123",
                        ip = "192.168.1.100",
                        clientVersion = "1.20.1",
                        protocolVersion = "763"
                    )
                ),
                serverId = 1,
                login = true,
                timestamp = System.currentTimeMillis()
            )
            val validateResult = client.players.validate(validateRequest)
            println("✅ 批量验证成功: 处理了${validateResult.results.size}个玩家")
            validateResult.results.forEach { result ->
                val status = if (result.allowed) "允许" else "拒绝(${result.reason})"
                println("   - ${result.playerName}: $status")
            }
        } catch (e: Exception) {
            println("❌ 批量验证失败: ${e.message}")
        }

        // 测试4: 服务器服务模块
        println("\n🖥️ 测试4: 服务器服务模块")
        var createdServerId: Int? = null
        try {
            // 获取服务器列表
            val serversRequest = ListServersRequest(page = 1, pageSize = 5)
            val servers = client.servers.listServers(serversRequest)
            println("✅ 获取服务器列表成功: 总数 ${servers.total}")

            if (servers.servers.isNotEmpty()) {
                createdServerId = servers.servers[0].id
                println("ℹ️ 使用现有服务器: ${servers.servers[0].name} (ID: $createdServerId)")
            }

            // 测试注册服务器
            val timestamp = System.currentTimeMillis()
            val registerRequest = RegisterServerRequest(
                name = "TestServerKt$timestamp",
                address = "testkt$timestamp.example.com:25565",
                description = "Test Kotlin server"
            )
            val newServer = client.servers.registerServer(registerRequest)
            println("✅ 注册服务器成功: ${newServer.name} (ID: ${newServer.id})")
            createdServerId = newServer.id
        } catch (e: Exception) {
            println("❌ 服务器服务测试失败: ${e.message}")
            createdServerId = 1 // 使用默认服务器ID
        }

        // 测试5: 城镇服务模块
        println("\n🏘️ 测试5: 城镇服务模块")
        try {
            // 获取城镇列表
            val townsRequest = ListTownsRequest(page = 1, pageSize = 5)
            val towns = client.towns.listTowns(townsRequest)
            println("✅ 获取城镇列表成功: 总数 ${towns.total}")

            // 测试创建城镇
            val timestamp = System.currentTimeMillis()
            val createRequest = CreateTownRequest(
                name = "TestTownKt$timestamp",
                level = 1,
                description = "Test Kotlin town"
            )
            val newTown = client.towns.createTown(createRequest)
            println("✅ 创建城镇成功: ${newTown.name} (ID: ${newTown.id})")
        } catch (e: Exception) {
            println("❌ 城镇服务测试失败: ${e.message}")
        }

        // 测试6: IP服务模块
        println("\n🌐 测试6: IP服务模块")
        try {
            // 获取IP信息
            val ipInfo = client.ips.getIPInfo("8.8.8.8")
            println("✅ 获取IP信息成功: ${ipInfo.ip} (${ipInfo.country ?: "Unknown"})")

            // 获取IP列表
            val ipsRequest = ListIPsRequest(page = 1, pageSize = 5)
            val ips = client.ips.listIPs(ipsRequest)
            println("✅ 获取IP列表成功: 总数 ${ips.total}")

            // 测试封禁IP
            client.ips.banIP("192.168.1.200", "Kotlin测试封禁")
            println("✅ 封禁IP成功")

            // 测试解封IP
            client.ips.unbanIP("192.168.1.200")
            println("✅ 解封IP成功")
        } catch (e: Exception) {
            println("❌ IP服务测试失败: ${e.message}")
        }

        // 测试7: 玩家服务器关系服务模块
        println("\n🔗 测试7: 玩家服务器关系服务模块")
        if (createdPlayerId != null && createdServerId != null) {
            try {
                val setOnlineRequest = SetPlayerOnlineRequest(
                    playerId = createdPlayerId,
                    serverId = createdServerId,
                    online = true
                )
                client.playerServers.setPlayerOnline(setOnlineRequest)
                println("✅ 设置玩家在线状态成功")
            } catch (e: Exception) {
                println("❌ 设置玩家在线状态失败: ${e.message}")
            }
        } else {
            println("⚠️ 跳过玩家服务器关系测试（需要先创建玩家和服务器）")
        }

        try {
            val onlinePlayersRequest = GetOnlinePlayersRequest(page = 1, pageSize = 10)
            val onlinePlayers = client.playerServers.getOnlinePlayers(onlinePlayersRequest)
            println("✅ 获取在线玩家成功: 共${onlinePlayers.players.size}个在线玩家")
        } catch (e: Exception) {
            println("❌ 获取在线玩家失败: ${e.message}")
        }

        // 测试8: Token服务模块
        println("\n🔑 测试8: Token服务模块")
        try {
            val tokens = client.tokens.listApiTokens()
            println("✅ 获取Token列表成功: 共${tokens.tokens.size}个Token")
            tokens.tokens.take(3).forEach { token ->
                println("  - Token: ${token.name} (角色: ${token.role})")
            }
        } catch (e: Exception) {
            println("❌ Token服务测试失败: ${e.message}")
        }

        // 测试9: 监控服务模块
        println("\n📊 测试9: 监控服务模块")
        if (createdServerId != null) {
            try {
                val serverStatus = client.monitor.getServerStatus(createdServerId)
                println("✅ 获取服务器状态成功: 在线=${serverStatus.online}")
            } catch (e: Exception) {
                println("❌ 监控服务测试失败: ${e.message}")
            }
        }

        // 测试10: 向后兼容性
        println("\n🔄 测试10: 向后兼容性")
        try {
            @Suppress("DEPRECATION")
            val compatPlayers = client.listPlayers(page = 1, pageSize = 3)
            println("✅ 向后兼容测试成功: 获取到${compatPlayers.players.size}个玩家")
        } catch (e: Exception) {
            println("❌ 向后兼容测试失败: ${e.message}")
        }

        println("\n🎉 Kotlin SDK模块化架构测试完成!")

    } catch (e: Exception) {
        println("❌ 测试过程中发生错误: ${e.message}")
        e.printStackTrace()
    } finally {
        client.close()
    }
}
