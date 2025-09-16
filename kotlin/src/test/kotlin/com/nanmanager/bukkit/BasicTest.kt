package com.nanmanager.bukkit

import com.nanmanager.bukkit.models.*
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class BasicTest {

    @Test
    fun testEnumValues() {
        // 测试枚举值
        assertEquals(0, BanMode.NORMAL.value)
        assertEquals(1, BanMode.TEMPORARY.value)
        assertEquals(2, BanMode.PERMANENT.value)

        assertEquals(1, LoginAction.LOGIN.value)
        assertEquals(2, LoginAction.LOGOUT.value)

        assertEquals(0, ThreatLevel.LOW.value)
        assertEquals(3, ThreatLevel.CRITICAL.value)
    }

    @Test
    fun testModelCreation() {
        // 测试创建玩家请求
        val createPlayerRequest = CreatePlayerRequest(
            name = "TestPlayer",
            qq = "123456789",
            inQqGroup = true
        )

        assertEquals("TestPlayer", createPlayerRequest.name)
        assertEquals("123456789", createPlayerRequest.qq)
        assertEquals(true, createPlayerRequest.inQqGroup)
        assertEquals(false, createPlayerRequest.inQqGuild)

        // 测试创建服务器请求
        val createServerRequest = CreateServerRequest(
            name = "TestServer",
            address = "mc.example.com:25565",
            description = "Test server"
        )

        assertEquals("TestServer", createServerRequest.name)
        assertEquals("mc.example.com:25565", createServerRequest.address)
        assertEquals("Test server", createServerRequest.description)

        // 测试心跳请求
        val heartbeatRequest = HeartbeatRequest(
            currentPlayers = 10,
            maxPlayers = 50,
            tps = 19.8
        )

        assertEquals(10, heartbeatRequest.currentPlayers)
        assertEquals(50, heartbeatRequest.maxPlayers)
        assertEquals(19.8, heartbeatRequest.tps)
    }

    @Test
    fun testClientCreation() {
        // 测试客户端创建
        val client = NewNanManagerClient(
            token = "test-token",
            baseUrl = "https://api.example.com",
            timeout = 30L
        )

        assertNotNull(client.players)
        assertNotNull(client.servers)
        assertNotNull(client.towns)
        assertNotNull(client.tokens)
        assertNotNull(client.ips)
        assertNotNull(client.playerServers)

        client.close()
    }

    @Test
    fun testValidateRequest() {
        val validateRequest = ValidateRequest(
            players = listOf(
                PlayerValidateInfo(
                    playerName = "Player1",
                    ip = "192.168.1.100",
                    clientVersion = "1.20.1"
                ),
                PlayerValidateInfo(
                    playerName = "Player2",
                    ip = "192.168.1.101"
                )
            ),
            serverId = 1,
            login = true,
            timestamp = System.currentTimeMillis() / 1000
        )

        assertEquals(2, validateRequest.players.size)
        assertEquals(1, validateRequest.serverId)
        assertEquals(true, validateRequest.login)

        val firstPlayer = validateRequest.players[0]
        assertEquals("Player1", firstPlayer.playerName)
        assertEquals("192.168.1.100", firstPlayer.ip)
        assertEquals("1.20.1", firstPlayer.clientVersion)
    }
}
