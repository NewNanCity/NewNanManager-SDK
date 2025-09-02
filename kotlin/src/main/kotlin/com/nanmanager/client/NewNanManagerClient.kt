package com.nanmanager.client

import com.nanmanager.client.exceptions.*
import com.nanmanager.client.models.*
import com.nanmanager.client.services.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.contentOrNull

/**
 * NewNanManager客户端SDK - 新版模块化架构
 *
 * 使用示例:
 * ```kotlin
 * val client = NewNanManagerClient(
 *     token = "your-api-token",
 *     baseUrl = "https://your-server.com"
 * )
 *
 * // 使用模块化服务
 * val servers = client.servers.listServers()
 * val players = client.players.listPlayers()
 * val towns = client.towns.listTowns()
 * ```
 */
class NewNanManagerClient(
    private val token: String,
    private val baseUrl: String,
    private val enableLogging: Boolean = false,
    private val timeout: Long = 30000L
) : AutoCloseable {

    private val httpClient: HttpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
                encodeDefaults = false
            })
        }

        install(HttpTimeout) {
            requestTimeoutMillis = timeout
            connectTimeoutMillis = timeout
            socketTimeoutMillis = timeout
        }

        if (enableLogging) {
            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.INFO
            }
        }

        // 全局异常处理
        HttpResponseValidator {
            handleResponseExceptionWithRequest { exception, request ->
                when (exception) {
                    is ClientRequestException -> {
                        val response = exception.response
                        val responseBody = try {
                            response.bodyAsText()
                        } catch (e: Exception) {
                            "无法读取响应体: ${e.message}"
                        }

                        println("=== HTTP客户端异常详情 ===")
                        println("请求URL: ${request.url}")
                        println("请求方法: ${request.method}")
                        println("响应状态: ${response.status}")
                        println("响应头: ${response.headers.entries()}")
                        println("响应体: $responseBody")
                        println("========================")

                        // 统一处理服务端错误响应格式 {"detail": "xxx"}
                        // 与TypeScript SDK保持一致的错误处理逻辑
                        var errorMessage = responseBody.ifEmpty { "HTTP ${response.status.value}" }

                        try {
                            // 尝试从响应体中解析JSON格式的错误信息
                            if (responseBody.isNotEmpty() && responseBody.startsWith("{")) {
                                val json = Json { ignoreUnknownKeys = true }
                                val errorData = json.parseToJsonElement(responseBody).jsonObject
                                val detail = errorData["detail"]?.jsonPrimitive?.contentOrNull
                                if (detail != null) {
                                    errorMessage = detail
                                }
                            }
                        } catch (e: Exception) {
                            // 如果无法解析JSON，使用原始响应体
                        }

                        throw NanManagerException(response.status.value, errorMessage)
                    }
                    is ServerResponseException -> {
                        println("=== 服务器异常详情 ===")
                        println("异常类型: ${exception.javaClass.simpleName}")
                        println("异常消息: ${exception.message}")
                        println("响应状态: ${exception.response.status}")
                        println("==================")
                        throw NanManagerException(500, "Server error: ${exception.message}")
                    }
                    else -> {
                        println("=== 网络异常详情 ===")
                        println("异常类型: ${exception.javaClass.simpleName}")
                        println("异常消息: ${exception.message}")
                        println("异常堆栈: ${exception.stackTraceToString()}")
                        println("================")
                        throw NetworkException("Network error: ${exception.message}", exception)
                    }
                }
            }
        }
    }

    // ========== 所有服务模块 ==========

    /**
     * 玩家管理服务
     */
    val players: PlayerService = PlayerService(httpClient, baseUrl, token)

    /**
     * 服务器管理服务
     */
    val servers: ServerService = ServerService(httpClient, baseUrl, token)

    /**
     * 城镇管理服务
     */
    val towns: TownService = TownService(httpClient, baseUrl, token)

    /**
     * 服务器监控服务
     */
    val monitor: MonitorService = MonitorService(httpClient, baseUrl, token)

    /**
     * API Token管理服务
     */
    val tokens: TokenService = TokenService(httpClient, baseUrl, token)

    /**
     * IP管理服务
     */
    val ips: IPService = IPService(httpClient, baseUrl, token)

    /**
     * 玩家服务器关系管理服务
     */
    val playerServers: PlayerServerService = PlayerServerService(httpClient, baseUrl, token)

    // ========== 向后兼容的便捷方法 ==========

    /**
     * 获取玩家列表（向后兼容）
     * @deprecated 请使用 players.listPlayers() 方法
     */
    @Deprecated("请使用 players.listPlayers() 方法")
    suspend fun listPlayers(
        page: Int? = null,
        pageSize: Int? = null,
        search: String? = null,
        townId: Int? = null,
        banMode: BanMode? = null
    ): ListPlayersResponse {
        val request = ListPlayersRequest(
            page = page ?: 1,
            pageSize = pageSize ?: 20,
            search = search,
            townId = townId,
            banMode = banMode
        )
        return players.listPlayers(request)
    }

    /**
     * 创建玩家（向后兼容）
     * @deprecated 请使用 players.createPlayer() 方法
     */
    @Deprecated("请使用 players.createPlayer() 方法")
    suspend fun createPlayer(request: CreatePlayerRequest): Player {
        return players.createPlayer(request)
    }

    /**
     * 批量玩家验证（向后兼容）
     * @deprecated 请使用 players.validate() 方法
     */
    @Deprecated("请使用 players.validate() 方法")
    suspend fun validate(request: ValidateRequest): ValidateResponse {
        return players.validate(request)
    }

    /**
     * 验证玩家登录（向后兼容）
     * @deprecated 请使用 players.validate() 方法进行批量验证
     */
    @Deprecated("请使用 players.validate() 方法进行批量验证")
    suspend fun validateLogin(request: ValidateLoginRequest): ValidateLoginResponse {
        return players.validateLogin(request)
    }

    /**
     * 获取玩家详情（向后兼容）
     * @deprecated 请使用 players.getPlayer() 方法
     */
    @Deprecated("请使用 players.getPlayer() 方法")
    suspend fun getPlayer(id: Int): Player {
        return players.getPlayer(id)
    }

    /**
     * 更新玩家信息（向后兼容）
     * @deprecated 请使用 players.updatePlayer() 方法
     */
    @Deprecated("请使用 players.updatePlayer() 方法")
    suspend fun updatePlayer(id: Int, request: UpdatePlayerRequest): Player {
        return players.updatePlayer(id, request)
    }

    /**
     * 删除玩家（向后兼容）
     * @deprecated 请使用 players.deletePlayer() 方法
     */
    @Deprecated("请使用 players.deletePlayer() 方法")
    suspend fun deletePlayer(id: Int) {
        players.deletePlayer(id)
    }

    /**
     * 封禁玩家（向后兼容）
     * @deprecated 请使用 players.banPlayer() 方法
     */
    @Deprecated("请使用 players.banPlayer() 方法")
    suspend fun banPlayer(playerId: Int, request: BanPlayerRequest) {
        players.banPlayer(playerId, request)
    }

    /**
     * 解封玩家（向后兼容）
     * @deprecated 请使用 players.unbanPlayer() 方法
     */
    @Deprecated("请使用 players.unbanPlayer() 方法")
    suspend fun unbanPlayer(playerId: Int) {
        players.unbanPlayer(playerId)
    }

    /**
     * 获取服务器列表（向后兼容）
     * @deprecated 请使用 servers.listServers() 方法
     */
    @Deprecated("请使用 servers.listServers() 方法")
    suspend fun listServers(
        page: Int? = null,
        pageSize: Int? = null,
        search: String? = null,
        onlineOnly: Boolean? = null
    ): ListServersResponse {
        val request = ListServersRequest(
            page = page ?: 1,
            pageSize = pageSize ?: 20,
            search = search,
            onlineOnly = onlineOnly ?: false
        )
        return servers.listServers(request)
    }

    /**
     * 注册服务器（向后兼容）
     * @deprecated 请使用 servers.registerServer() 方法
     */
    @Deprecated("请使用 servers.registerServer() 方法")
    suspend fun registerServer(request: RegisterServerRequest): ServerRegistry {
        return servers.registerServer(request)
    }

    /**
     * 获取服务器信息（向后兼容）
     * @deprecated 请使用 servers.getServer() 方法
     */
    @Deprecated("请使用 servers.getServer() 方法")
    suspend fun getServer(id: Int): ServerRegistry {
        return servers.getServer(id)
    }

    /**
     * 更新服务器信息（向后兼容）
     * @deprecated 请使用 servers.updateServer() 方法
     */
    @Deprecated("请使用 servers.updateServer() 方法")
    suspend fun updateServer(id: Int, request: UpdateServerRequest): ServerRegistry {
        return servers.updateServer(id, request)
    }

    /**
     * 删除服务器（向后兼容）
     * @deprecated 请使用 servers.deleteServer() 方法
     */
    @Deprecated("请使用 servers.deleteServer() 方法")
    suspend fun deleteServer(id: Int) {
        servers.deleteServer(id)
    }

    /**
     * 获取服务器详细信息（向后兼容）
     * @deprecated 请使用 servers.getServerDetail() 方法
     */
    @Deprecated("请使用 servers.getServerDetail() 方法")
    suspend fun getServerDetail(id: Int): ServerDetailResponse {
        return servers.getServerDetail(id)
    }

    /**
     * 获取服务器状态（向后兼容）
     * @deprecated 请使用 monitor.getServerStatus() 方法
     */
    @Deprecated("请使用 monitor.getServerStatus() 方法")
    suspend fun getServerStatus(serverId: Int): ServerStatus {
        return monitor.getServerStatus(serverId)
    }

    /**
     * 获取API Token列表（向后兼容）
     * @deprecated 请使用 tokens.listApiTokens() 方法
     */
    @Deprecated("请使用 tokens.listApiTokens() 方法")
    suspend fun listApiTokens(): ListApiTokensResponse {
        return tokens.listApiTokens()
    }

    /**
     * 获取城镇列表（向后兼容）
     * @deprecated 请使用 towns.listTowns() 方法
     */
    @Deprecated("请使用 towns.listTowns() 方法")
    suspend fun listTowns(
        page: Int? = null,
        pageSize: Int? = null,
        search: String? = null,
        minLevel: Int? = null,
        maxLevel: Int? = null
    ): ListTownsResponse {
        val request = ListTownsRequest(
            page = page ?: 1,
            pageSize = pageSize ?: 20,
            search = search,
            minLevel = minLevel,
            maxLevel = maxLevel
        )
        return towns.listTowns(request)
    }

    /**
     * 创建城镇（向后兼容）
     * @deprecated 请使用 towns.createTown() 方法
     */
    @Deprecated("请使用 towns.createTown() 方法")
    suspend fun createTown(request: CreateTownRequest): Town {
        return towns.createTown(request)
    }

    /**
     * 获取城镇详情（向后兼容）
     * @deprecated 请使用 towns.getTown() 方法
     */
    @Deprecated("请使用 towns.getTown() 方法")
    suspend fun getTown(id: Int): TownDetailResponse {
        return towns.getTown(id)
    }

    /**
     * 更新城镇信息（向后兼容）
     * @deprecated 请使用 towns.updateTown() 方法
     */
    @Deprecated("请使用 towns.updateTown() 方法")
    suspend fun updateTown(id: Int, request: UpdateTownRequest): Town {
        return towns.updateTown(id, request)
    }

    /**
     * 删除城镇（向后兼容）
     * @deprecated 请使用 towns.deleteTown() 方法
     */
    @Deprecated("请使用 towns.deleteTown() 方法")
    suspend fun deleteTown(id: Int) {
        towns.deleteTown(id)
    }

    /**
     * 获取城镇成员列表（向后兼容）
     * @deprecated 请使用 towns.getTownMembers() 方法
     */
    @Deprecated("请使用 towns.getTownMembers() 方法")
    suspend fun getTownMembers(townId: Int, page: Int? = null, pageSize: Int? = null): TownMembersResponse {
        val request = GetTownMembersRequest(
            townId = townId,
            page = page ?: 1,
            pageSize = pageSize ?: 50
        )
        return towns.getTownMembers(request)
    }

    /**
     * 设置玩家在线状态（向后兼容）
     * @deprecated 请使用 playerServers.setPlayerOnline() 方法
     */
    @Deprecated("请使用 playerServers.setPlayerOnline() 方法")
    suspend fun setPlayerOnline(request: SetPlayerOnlineRequest) {
        playerServers.setPlayerOnline(request)
    }

    /**
     * 获取玩家的服务器列表（向后兼容）
     * @deprecated 请使用 playerServers.getPlayerServers() 方法
     */
    @Deprecated("请使用 playerServers.getPlayerServers() 方法")
    suspend fun getPlayerServers(
        playerId: Int,
        page: Int = 1,
        pageSize: Int = 20
    ): PlayerServersResponse {
        return playerServers.getPlayerServers(playerId)
    }

    /**
     * 获取服务器的玩家列表（向后兼容）
     * @deprecated 请使用 playerServers.getServerPlayers() 方法
     */
    @Deprecated("请使用 playerServers.getServerPlayers() 方法")
    suspend fun getServerPlayers(
        serverId: Int,
        page: Int = 1,
        pageSize: Int = 20
    ): ServerPlayersResponse {
        val request = GetServerPlayersRequest(
            serverId = serverId,
            page = page,
            pageSize = pageSize
        )
        return playerServers.getServerPlayers(request)
    }

    /**
     * 获取在线玩家列表（向后兼容）
     * @deprecated 请使用 playerServers.getOnlinePlayers() 方法
     */
    @Deprecated("请使用 playerServers.getOnlinePlayers() 方法")
    suspend fun getOnlinePlayers(
        page: Int = 1,
        pageSize: Int = 20,
        search: String? = null,
        serverId: Int? = null
    ): ServerPlayersResponse {
        val request = GetOnlinePlayersRequest(
            page = page,
            pageSize = pageSize,
            search = search,
            serverId = serverId
        )
        return playerServers.getOnlinePlayers(request)
    }

    /**
     * 获取IP信息（向后兼容）
     * @deprecated 请使用 ips.getIPInfo() 方法
     */
    @Deprecated("请使用 ips.getIPInfo() 方法")
    suspend fun getIPInfo(ip: String): IPInfo {
        return ips.getIPInfo(ip)
    }

    /**
     * 封禁IP（向后兼容）
     * @deprecated 请使用 ips.banIP() 方法
     */
    @Deprecated("请使用 ips.banIP() 方法")
    suspend fun banIP(request: BanIPRequest) {
        ips.banIP(request.ip, request.reason)
    }

    /**
     * 解封IP（向后兼容）
     * @deprecated 请使用 ips.unbanIP() 方法
     */
    @Deprecated("请使用 ips.unbanIP() 方法")
    suspend fun unbanIP(request: UnbanIPRequest) {
        ips.unbanIP(request.ip)
    }

    /**
     * 获取封禁IP列表（向后兼容）
     * @deprecated 请使用 ips.getBannedIPs() 方法
     */
    @Deprecated("请使用 ips.getBannedIPs() 方法")
    suspend fun listBannedIPs(
        page: Int = 1,
        pageSize: Int = 20,
        search: String? = null,
        activeOnly: Boolean? = null
    ): ListIPsResponse {
        val request = ListIPsRequest(
            page = page,
            pageSize = pageSize,
            bannedOnly = true
        )
        return ips.getBannedIPs(request)
    }

    override fun close() {
        httpClient.close()
    }
}
