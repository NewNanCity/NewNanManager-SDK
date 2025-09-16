package com.nanmanager.bukkit.examples

import com.nanmanager.bukkit.NewNanManagerClient
import com.nanmanager.bukkit.models.*
import com.nanmanager.bukkit.exceptions.*

/**
 * NewNanManager Kotlin SDK 基础使用示例
 */
fun main() {
    // 从环境变量获取配置，或使用默认值
    val baseUrl = System.getenv("NANMANAGER_BASE_URL") ?: "https://your-api-server.com"
    val token = System.getenv("NANMANAGER_TOKEN") ?: "your-api-token-here"

    println("🚀 NewNanManager Kotlin SDK 同步版本测试")
    println("📡 API地址: $baseUrl")
    println("🔑 Token: ${token.take(10)}...")
    println()

    val client = NewNanManagerClient(
        token = token,
        baseUrl = baseUrl,
        timeout = 30L
    )

    try {
        // 测试玩家管理
        testPlayerManagement(client)

        // 测试服务器管理
        testServerManagement(client)

        // 测试城镇管理
        testTownManagement(client)

        // 测试Token管理
        testTokenManagement(client)

        println("✅ 所有测试完成！")

    } catch (e: Exception) {
        println("❌ 测试失败: ${e.message}")
        e.printStackTrace()
    } finally {
        client.close()
        println("🔒 客户端已关闭")
    }
}

/**
 * 测试玩家管理功能
 */
fun testPlayerManagement(client: NewNanManagerClient) {
    println("👥 测试玩家管理功能...")

    try {
        // 获取玩家列表
        val players = client.players.listPlayers(ListPlayersRequest(page = 1, pageSize = 5))
        println("   📋 玩家列表: ${players.total} 个玩家")

        if (players.players.isNotEmpty()) {
            val firstPlayer = players.players.first()
            println("   👤 第一个玩家: ${firstPlayer.name} (ID: ${firstPlayer.id})")

            // 获取玩家详情
            val playerDetail = client.players.getPlayer(GetPlayerRequest(firstPlayer.id))
            println("   📝 玩家详情: ${playerDetail.name}, 城镇ID: ${playerDetail.townId}")
        }

        // 测试玩家验证
        val validateResult = client.players.validate(ValidateRequest(
            players = listOf(
                PlayerValidateInfo(
                    playerName = "TestPlayer",
                    ip = "192.168.1.100",
                    clientVersion = "1.20.1"
                )
            ),
            serverId = 1,
            login = true
        ))
        println("   ✅ 验证结果: ${validateResult.results.size} 个玩家验证完成")

    } catch (e: ApiException) {
        println("   ⚠️ 玩家管理API错误: ${e.errorDetail}")
    } catch (e: Exception) {
        println("   ❌ 玩家管理测试失败: ${e.message}")
    }
}

/**
 * 测试服务器管理功能
 */
fun testServerManagement(client: NewNanManagerClient) {
    println("🖥️ 测试服务器管理功能...")

    try {
        // 获取服务器列表
        val servers = client.servers.listServers(ListServersRequest(page = 1, pageSize = 5))
        println("   📋 服务器列表: ${servers.total} 个服务器")

        if (servers.servers.isNotEmpty()) {
            val firstServer = servers.servers.first()
            println("   🖥️ 第一个服务器: ${firstServer.name} (${firstServer.address})")

            // 获取服务器详情
            val serverDetail = client.servers.getServer(GetServerRequest(firstServer.id, detail = true))
            println("   📝 服务器详情: ${serverDetail.server.name}, 激活状态: ${serverDetail.server.active}")
        }

    } catch (e: ApiException) {
        println("   ⚠️ 服务器管理API错误: ${e.errorDetail}")
    } catch (e: Exception) {
        println("   ❌ 服务器管理测试失败: ${e.message}")
    }
}

/**
 * 测试城镇管理功能
 */
fun testTownManagement(client: NewNanManagerClient) {
    println("🏘️ 测试城镇管理功能...")

    try {
        // 获取城镇列表
        val towns = client.towns.listTowns(ListTownsRequest(page = 1, pageSize = 5))
        println("   📋 城镇列表: ${towns.total} 个城镇")

        if (towns.towns.isNotEmpty()) {
            val firstTown = towns.towns.first()
            println("   🏘️ 第一个城镇: ${firstTown.name} (等级: ${firstTown.level})")

            // 获取城镇详情
            val townDetail = client.towns.getTown(GetTownRequest(firstTown.id, detail = true))
            println("   📝 城镇详情: ${townDetail.town.name}, 成员数: ${townDetail.members.size}")
        }

    } catch (e: ApiException) {
        println("   ⚠️ 城镇管理API错误: ${e.errorDetail}")
    } catch (e: Exception) {
        println("   ❌ 城镇管理测试失败: ${e.message}")
    }
}

/**
 * 测试Token管理功能
 */
fun testTokenManagement(client: NewNanManagerClient) {
    println("� 测试Token管理功能...")

    try {
        // 获取Token列表
        val tokens = client.tokens.listApiTokens(ListApiTokensRequest(page = 1, pageSize = 5))
        println("   📋 Token列表: ${tokens.total} 个Token")

        if (tokens.tokens.isNotEmpty()) {
            val firstToken = tokens.tokens.first()
            println("   � 第一个Token: ${firstToken.name} (角色: ${firstToken.role})")
        }

    } catch (e: ApiException) {
        println("   ⚠️ Token管理API错误: ${e.errorDetail}")
    } catch (e: Exception) {
        println("   ❌ Token管理测试失败: ${e.message}")
    }
}
