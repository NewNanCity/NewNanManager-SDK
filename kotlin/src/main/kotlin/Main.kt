package com.nanmanager.client

import com.nanmanager.client.models.*
import kotlinx.coroutines.runBlocking
import kotlin.system.measureTimeMillis

data class TestResult(
    val name: String,
    val description: String,
    val success: Boolean,
    val duration: Long,
    val error: String? = null
)

class IntegrationTester(
    private val baseUrl: String,
    private val token: String
) {
    private val client = NewNanManagerClient(token, baseUrl, enableLogging = false)
    private val results = mutableListOf<TestResult>()

    private suspend fun testApi(name: String, description: String, apiCall: suspend () -> Any?): Any? {
        var result: Any? = null
        val duration = measureTimeMillis {
            try {
                result = apiCall()
                results.add(TestResult(name, description, true, 0))
                println("✓ ${description}成功")
            } catch (e: Exception) {
                results.add(TestResult(name, description, false, 0, e.message))
                println("✗ ${description}失败: ${e.message}")
                throw e
            }
        }
        // 更新持续时间
        if (results.isNotEmpty()) {
            val lastIndex = results.size - 1
            results[lastIndex] = results[lastIndex].copy(duration = duration)
            println("   耗时: ${duration}ms")
        }
        return result
    }

    suspend fun testPlayerManagement() {
        println("\n=== 1. 测试玩家管理功能 ===")
        var createdPlayerId: Int? = null

        try {
            // 1.1 获取玩家列表
            val players = testApi("ListPlayers", "获取玩家列表") {
                client.listPlayers()
            } as ListPlayersResponse
            println("   共 ${players.total} 个玩家")

            // 1.2 创建玩家
            val testPlayerName = "TestPlayer_${System.currentTimeMillis()}"
            val player = testApi("CreatePlayer", "创建玩家") {
                client.createPlayer(CreatePlayerRequest(
                    name = testPlayerName,
                    inQqGroup = true,
                    inQqGuild = false,
                    inDiscord = false
                ))
            } as Player
            createdPlayerId = player.id
            println("   ID: ${player.id}, 名称: ${player.name}")

            // 1.3 获取玩家详情
            testApi("GetPlayer", "获取玩家详情") {
                client.getPlayer(createdPlayerId!!)
            }

            // 1.4 更新玩家信息
            val newName = "${testPlayerName}_Updated"
            testApi("UpdatePlayer", "更新玩家信息") {
                client.updatePlayer(createdPlayerId!!, UpdatePlayerRequest(id = createdPlayerId!!, name = newName))
            }

            // 1.5 封禁玩家
            testApi("BanPlayer", "封禁玩家") {
                client.banPlayer(createdPlayerId!!, BanPlayerRequest(
                    playerId = createdPlayerId!!,
                    banMode = BanMode.TEMPORARY,
                    reason = "测试封禁",
                    durationSeconds = 60
                ))
            }

            // 1.6 解封玩家
            testApi("UnbanPlayer", "解封玩家") {
                client.unbanPlayer(createdPlayerId!!)
            }

            // 1.7 验证登录
            testApi("ValidateLogin", "验证玩家登录") {
                client.validateLogin(ValidateLoginRequest(
                    playerName = newName,
                    serverId = 1
                ))
            }

            // 1.8 删除玩家
            testApi("DeletePlayer", "删除玩家") {
                client.deletePlayer(createdPlayerId!!)
            }

        } catch (e: Exception) {
            println("玩家管理测试中断: ${e.message}")
        }
    }

    suspend fun testServerManagement() {
        println("\n=== 2. 测试服务器管理功能 ===")
        var createdServerId: Int? = null

        try {
            // 2.1 获取服务器列表
            val servers = testApi("ListServers", "获取服务器列表") {
                client.listServers()
            } as ListServersResponse
            println("   共 ${servers.total} 个服务器")

            // 2.2 注册服务器
            val testServerName = "TestServer_${System.currentTimeMillis()}"
            val testServerAddress = "test-${System.currentTimeMillis()}.example.com:25565"
            val server = testApi("RegisterServer", "注册服务器") {
                client.registerServer(RegisterServerRequest(
                    name = testServerName,
                    address = testServerAddress,
                    description = "Test Minecraft server"
                ))
            } as ServerRegistry
            createdServerId = server.id
            println("   ID: ${server.id}, 名称: ${server.name}")

            // 2.3 获取服务器信息
            testApi("GetServer", "获取服务器信息") {
                client.getServer(createdServerId!!)
            }

            // 2.4 更新服务器信息
            val newServerName = "${testServerName}_Updated"
            val newAddress = "updated-${System.currentTimeMillis()}.example.com:25565"
            testApi("UpdateServer", "更新服务器信息") {
                client.updateServer(createdServerId!!, UpdateServerRequest(
                    id = createdServerId!!,
                    name = newServerName,
                    address = newAddress
                ))
            }

            // 2.5 获取服务器详细信息
            testApi("GetServerDetail", "获取服务器详细信息") {
                client.getServerDetail(createdServerId!!)
            }

            // 2.6 删除服务器
            testApi("DeleteServer", "删除服务器") {
                client.deleteServer(createdServerId!!)
            }

        } catch (e: Exception) {
            println("服务器管理测试中断: ${e.message}")
        }
    }

    suspend fun testTownManagement() {
        println("\n=== 3. 测试城镇管理功能 ===")
        var createdTownId: Int? = null

        try {
            // 3.1 获取城镇列表
            val towns = testApi("ListTowns", "获取城镇列表") {
                client.listTowns()
            } as ListTownsResponse
            println("   共 ${towns.total} 个城镇")

            // 3.2 创建城镇
            val testTownName = "TestTown_${System.currentTimeMillis()}"
            val town = testApi("CreateTown", "创建城镇") {
                client.createTown(CreateTownRequest(
                    name = testTownName,
                    level = 1
                ))
            } as Town
            createdTownId = town.id
            println("   ID: ${town.id}, 名称: ${town.name}")

            // 3.3 获取城镇详情
            testApi("GetTown", "获取城镇详情") {
                client.getTown(createdTownId!!)
            }

            // 3.4 更新城镇信息
            val newTownName = "${testTownName}_Updated"
            testApi("UpdateTown", "更新城镇信息") {
                client.updateTown(createdTownId!!, UpdateTownRequest(
                    id = createdTownId!!,
                    name = newTownName,
                    level = 2
                ))
            }

            // 3.5 获取城镇成员列表
            testApi("GetTownMembers", "获取城镇成员列表") {
                client.getTownMembers(createdTownId!!)
            }

            // 3.6 删除城镇
            testApi("DeleteTown", "删除城镇") {
                client.deleteTown(createdTownId!!)
            }

        } catch (e: Exception) {
            println("城镇管理测试中断: ${e.message}")
        }
    }

    suspend fun testTokenManagement() {
        println("\n=== 4. 测试Token管理功能 ===")

        try {
            // 4.1 获取Token列表
            val tokens = testApi("ListApiTokens", "获取Token列表") {
                client.listApiTokens()
            } as ListApiTokensResponse
            println("   共 ${tokens.tokens.size} 个Token")
        } catch (e: Exception) {
            println("Token管理测试中断: ${e.message}")
        }
    }

    suspend fun testMonitoringFeatures() {
        println("\n=== 5. 测试监控功能 ===")
        println("⚠ 监控功能测试需要有效的服务器ID，跳过详细测试")

        // 测试错误处理
        try {
            testApi("GetServerStatus", "获取服务器状态（测试错误处理）") {
                client.getServerStatus(999999)
            }
            println("✗ 错误处理测试失败，应该返回错误但没有")
        } catch (e: Exception) {
            println("✓ 错误处理测试成功，正确返回了错误: ${e.message}")
            // 修正测试结果
            if (results.isNotEmpty()) {
                val lastResult = results.last()
                if (lastResult.name == "GetServerStatus") {
                    results[results.size - 1] = lastResult.copy(success = true, error = null)
                }
            }
        }
    }

    fun generateReport() {
        val successCount = results.count { it.success }
        val failCount = results.size - successCount
        val avgDuration = if (results.isNotEmpty()) results.map { it.duration }.average() else 0.0

        println("\n" + "=".repeat(60))
        println("                    测试报告")
        println("=".repeat(60))
        println("总测试数: ${results.size}")
        println("成功: $successCount, 失败: $failCount, 成功率: ${"%.1f".format(successCount.toDouble() / results.size * 100)}%")
        println("平均耗时: ${"%.2f".format(avgDuration)}ms")
        println("=".repeat(60))

        when {
            failCount == 0 -> println("🎉 所有测试都通过了！API客户端工作正常。")
            successCount > failCount -> println("⚠ 大部分测试通过，但有 $failCount 个测试失败，请检查相关功能。")
            else -> println("❌ 测试失败较多（${failCount}个），请检查API连接和权限配置。")
        }

        // 显示失败的测试
        val failedTests = results.filter { !it.success }
        if (failedTests.isNotEmpty()) {
            println("\n失败的测试:")
            failedTests.forEachIndexed { index, test ->
                println("${index + 1}. ${test.name}: ${test.error}")
            }
        }
    }

    suspend fun runAllTests() {
        println("=== NewNanManager API Kotlin客户端测试 ===")
        println("Base URL: $baseUrl")
        println("Token: $token")

        testPlayerManagement()
        testServerManagement()
        testTownManagement()
        testTokenManagement()
        testMonitoringFeatures()

        generateReport()
        client.close()
    }
}

fun main() = runBlocking {
    val tester = IntegrationTester(
        baseUrl = "https://manager-api.newnan.city",
        token = "7p9piy2NagtMAryeyBBY7vzUKK1qDJOq"
    )

    tester.runAllTests()
}