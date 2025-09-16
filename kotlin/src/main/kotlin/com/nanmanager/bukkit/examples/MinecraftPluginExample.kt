package com.nanmanager.bukkit.examples

import com.nanmanager.bukkit.NewNanManagerClient
import com.nanmanager.bukkit.models.*
import com.nanmanager.bukkit.exceptions.*

/**
 * Minecraft插件中使用NewNanManager SDK的示例
 *
 * 注意：这个示例展示了如何在Minecraft插件中使用SDK，
 * 但不包含实际的Bukkit API调用，因为我们不依赖Bukkit
 */
class MinecraftPluginExample {

    private lateinit var nanClient: NewNanManagerClient

    /**
     * 插件启用时初始化客户端
     */
    fun onEnable(token: String, baseUrl: String) {
        nanClient = NewNanManagerClient(
            token = token,
            baseUrl = baseUrl,
            timeout = 30L
        )

        println("✅ NewNanManager客户端已初始化")
    }

    /**
     * 插件禁用时关闭客户端
     */
    fun onDisable() {
        if (::nanClient.isInitialized) {
            nanClient.close()
            println("🔒 NewNanManager客户端已关闭")
        }
    }

    /**
     * 玩家登录验证示例
     * 在异步线程中调用，避免阻塞主线程
     */
    fun validatePlayerLogin(playerName: String, playerIP: String, serverId: Int): Boolean {
        return try {
            val result = nanClient.players.validate(ValidateRequest(
                players = listOf(
                    PlayerValidateInfo(
                        playerName = playerName,
                        ip = playerIP,
                        clientVersion = "1.20.1"
                    )
                ),
                serverId = serverId,
                login = true
            ))

            val playerResult = result.results.firstOrNull()
            if (playerResult != null) {
                if (playerResult.allowed) {
                    println("✅ 玩家 $playerName 验证通过")
                    true
                } else {
                    println("❌ 玩家 $playerName 验证失败: ${playerResult.reason}")
                    false
                }
            } else {
                println("⚠️ 玩家 $playerName 验证结果为空")
                false
            }
        } catch (e: ApiException) {
            println("❌ API错误: ${e.errorDetail}")
            false
        } catch (e: Exception) {
            println("❌ 验证失败: ${e.message}")
            false
        }
    }

    /**
     * 获取玩家信息示例
     */
    fun getPlayerInfo(playerName: String): Player? {
        return try {
            // 先通过列表搜索找到玩家ID
            val players = nanClient.players.listPlayers(
                ListPlayersRequest(search = playerName, pageSize = 1)
            )

            if (players.players.isNotEmpty()) {
                val playerId = players.players.first().id
                val player = nanClient.players.getPlayer(GetPlayerRequest(playerId))
                println("👤 玩家信息: ${player.name}, 城镇ID: ${player.townId}")
                player
            } else {
                println("⚠️ 未找到玩家: $playerName")
                null
            }
        } catch (e: Exception) {
            println("❌ 获取玩家信息失败: ${e.message}")
            null
        }
    }

    /**
     * 创建新玩家示例
     */
    fun createPlayer(playerName: String, qq: String? = null): Player? {
        return try {
            val player = nanClient.players.createPlayer(CreatePlayerRequest(
                name = playerName,
                qq = qq,
                inQqGroup = qq != null
            ))

            println("✅ 创建玩家成功: ${player.name} (ID: ${player.id})")
            player
        } catch (e: ApiException) {
            println("❌ 创建玩家失败: ${e.errorDetail}")
            null
        } catch (e: Exception) {
            println("❌ 创建玩家异常: ${e.message}")
            null
        }
    }

    /**
     * 获取服务器状态示例
     */
    /**
     * 检查IP风险示例
     */
    fun checkIPRisk(ip: String): IPInfo? {
        return try {
            val ipInfo = nanClient.ips.getIPInfo(GetIPInfoRequest(ip))
            println("🌐 IP信息: ${ipInfo.ip} - ${ipInfo.country}, 风险等级: ${ipInfo.riskLevel}")

            if (ipInfo.banned) {
                println("⚠️ 该IP已被封禁: ${ipInfo.banReason}")
            }

            ipInfo
        } catch (e: Exception) {
            println("❌ 获取IP信息失败: ${e.message}")
            null
        }
    }
}

/**
 * 使用示例
 */
fun main() {
    val plugin = MinecraftPluginExample()

    // 模拟插件启用
    plugin.onEnable(
        token = System.getenv("NANMANAGER_TOKEN") ?: "your-token-here",
        baseUrl = System.getenv("NANMANAGER_BASE_URL") ?: "https://your-server.com"
    )

    try {
        // 模拟各种操作
        plugin.validatePlayerLogin("TestPlayer", "192.168.1.100", 1)
        plugin.getPlayerInfo("TestPlayer")
        plugin.createPlayer("NewPlayer", "123456789")
        plugin.checkIPRisk("8.8.8.8")

    } finally {
        // 模拟插件禁用
        plugin.onDisable()
    }
}
