package com.nanmanager.client

import com.nanmanager.client.exceptions.*
import com.nanmanager.client.models.*
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

/**
 * NewNanManager客户端SDK
 *
 * 使用示例:
 * ```kotlin
 * val client = NewNanManagerClient(
 *     token = "your-api-token",
 *     baseUrl = "https://your-server.com"
 * )
 *
 * val servers = client.listServers()
 * val players = client.listPlayers()
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

                        when (response.status) {
                            HttpStatusCode.Unauthorized -> throw AuthenticationException("Invalid token or unauthorized access")
                            HttpStatusCode.BadRequest,
                            HttpStatusCode.NotFound,
                            HttpStatusCode.Conflict,
                            HttpStatusCode.UnprocessableEntity -> {
                                try {
                                    val errorResponse = response.body<ErrorResponse>()
                                    throw NanManagerException(errorResponse.code, errorResponse.message)
                                } catch (e: Exception) {
                                    throw NanManagerException(response.status.value, "HTTP ${response.status.value}: $responseBody")
                                }
                            }
                            else -> throw NanManagerException(response.status.value, "HTTP ${response.status.value}: $responseBody")
                        }
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

    // ========== 玩家管理 ==========

    /**
     * 获取玩家列表
     */
    suspend fun listPlayers(
        page: Int? = null,
        pageSize: Int? = null,
        search: String? = null,
        townId: Int? = null,
        banMode: BanMode? = null
    ): ListPlayersResponse {
        val response = httpClient.get("$baseUrl/api/v1/players") {
            headers {
                append("Authorization", "Bearer $token")
                append("X-API-Token", token)
            }
            page?.let { parameter("page", it) }
            pageSize?.let { parameter("page_size", it) }
            search?.let { parameter("search", it) }
            townId?.let { parameter("town_id", it) }
            banMode?.let { parameter("ban_mode", it.value) }
        }
        return response.body<ApiResponse<ListPlayersResponse>>().data!!
    }

    /**
     * 创建玩家
     */
    suspend fun createPlayer(request: CreatePlayerRequest): Player {
        val response = httpClient.post("$baseUrl/api/v1/players") {
            headers {
                append("Authorization", "Bearer $token")
                append("X-API-Token", token)
            }
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        return response.body<ApiResponse<Player>>().data!!
    }

    /**
     * 验证玩家登录
     */
    suspend fun validateLogin(request: ValidateLoginRequest): ValidateLoginResponse {
        val response = httpClient.post("$baseUrl/api/v1/players/validate-login") {
            headers {
                append("Authorization", "Bearer $token")
                append("X-API-Token", token)
            }
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        return response.body<ApiResponse<ValidateLoginResponse>>().data!!
    }

    /**
     * 获取玩家详情
     */
    suspend fun getPlayer(id: Int): Player {
        val response = httpClient.get("$baseUrl/api/v1/players/$id") {
            headers {
                append("Authorization", "Bearer $token")
                append("X-API-Token", token)
            }
        }
        return response.body<ApiResponse<Player>>().data!!
    }

    /**
     * 更新玩家信息
     */
    suspend fun updatePlayer(id: Int, request: UpdatePlayerRequest): Player {
        val response = httpClient.put("$baseUrl/api/v1/players/$id") {
            headers {
                append("Authorization", "Bearer $token")
                append("X-API-Token", token)
            }
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        return response.body<ApiResponse<Player>>().data!!
    }

    /**
     * 删除玩家
     */
    suspend fun deletePlayer(id: Int) {
        httpClient.delete("$baseUrl/api/v1/players/$id") {
            headers {
                append("Authorization", "Bearer $token")
                append("X-API-Token", token)
            }
        }
    }

    /**
     * 封禁玩家
     */
    suspend fun banPlayer(playerId: Int, request: BanPlayerRequest) {
        httpClient.post("$baseUrl/api/v1/players/$playerId/ban") {
            headers {
                append("Authorization", "Bearer $token")
                append("X-API-Token", token)
            }
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }

    /**
     * 解封玩家
     */
    suspend fun unbanPlayer(playerId: Int) {
        httpClient.post("$baseUrl/api/v1/players/$playerId/unban") {
            headers {
                append("Authorization", "Bearer $token")
                append("X-API-Token", token)
            }
        }
    }

    // ========== 服务器管理 ==========

    /**
     * 获取服务器列表
     */
    suspend fun listServers(
        page: Int? = null,
        pageSize: Int? = null,
        search: String? = null,
        onlineOnly: Boolean? = null
    ): ListServersResponse {
        val response = httpClient.get("$baseUrl/api/v1/servers") {
            headers {
                append("Authorization", "Bearer $token")
                append("X-API-Token", token)
                append("Accept", "application/json")
            }
            page?.let { parameter("page", it) }
            pageSize?.let { parameter("page_size", it) }
            search?.let { parameter("search", it) }
            onlineOnly?.let { parameter("online_only", it) }
        }
        return response.body<ApiResponse<ListServersResponse>>().data!!
    }

    /**
     * 注册服务器
     */
    suspend fun registerServer(request: RegisterServerRequest): ServerRegistry {
        val response = httpClient.post("$baseUrl/api/v1/servers") {
            headers {
                append("Authorization", "Bearer $token")
                append("X-API-Token", token)
            }
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        return response.body<ApiResponse<ServerRegistry>>().data!!
    }

    /**
     * 获取服务器信息
     */
    suspend fun getServer(id: Int): ServerRegistry {
        val response = httpClient.get("$baseUrl/api/v1/servers/$id") {
            headers {
                append("Authorization", "Bearer $token")
                append("X-API-Token", token)
            }
        }
        return response.body<ApiResponse<ServerRegistry>>().data!!
    }

    /**
     * 更新服务器信息
     */
    suspend fun updateServer(id: Int, request: UpdateServerRequest): ServerRegistry {
        val response = httpClient.put("$baseUrl/api/v1/servers/$id") {
            headers {
                append("Authorization", "Bearer $token")
                append("X-API-Token", token)
            }
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        return response.body<ApiResponse<ServerRegistry>>().data!!
    }

    /**
     * 删除服务器
     */
    suspend fun deleteServer(id: Int) {
        httpClient.delete("$baseUrl/api/v1/servers/$id") {
            headers {
                append("Authorization", "Bearer $token")
                append("X-API-Token", token)
            }
        }
    }

    /**
     * 获取服务器详细信息
     */
    suspend fun getServerDetail(id: Int): ServerDetailResponse {
        val response = httpClient.get("$baseUrl/api/v1/servers/$id/detail") {
            headers {
                append("Authorization", "Bearer $token")
                append("X-API-Token", token)
            }
        }
        return response.body<ApiResponse<ServerDetailResponse>>().data!!
    }

    // ========== 监控服务 ==========

    /**
     * 获取服务器状态
     */
    suspend fun getServerStatus(serverId: Int): ServerStatus {
        val response = httpClient.get("$baseUrl/api/v1/servers/$serverId/status") {
            headers {
                append("Authorization", "Bearer $token")
                append("X-API-Token", token)
            }
        }
        return response.body<ApiResponse<ServerStatus>>().data!!
    }

    // ========== Token管理服务 ==========

    /**
     * 获取API Token列表
     */
    suspend fun listApiTokens(): ListApiTokensResponse {
        val response = httpClient.get("$baseUrl/api/v1/tokens") {
            headers {
                append("Authorization", "Bearer $token")
                append("X-API-Token", token)
            }
        }
        return response.body<ApiResponse<ListApiTokensResponse>>().data!!
    }

    // ========== 城镇管理服务 ==========

    /**
     * 获取城镇列表
     */
    suspend fun listTowns(
        page: Int? = null,
        pageSize: Int? = null,
        search: String? = null,
        minLevel: Int? = null,
        maxLevel: Int? = null
    ): ListTownsResponse {
        val response = httpClient.get("$baseUrl/api/v1/towns") {
            headers {
                append("Authorization", "Bearer $token")
                append("X-API-Token", token)
            }
            page?.let { parameter("page", it) }
            pageSize?.let { parameter("page_size", it) }
            search?.let { parameter("search", it) }
            minLevel?.let { parameter("min_level", it) }
            maxLevel?.let { parameter("max_level", it) }
        }
        return response.body<ApiResponse<ListTownsResponse>>().data!!
    }

    /**
     * 创建城镇
     */
    suspend fun createTown(request: CreateTownRequest): Town {
        val response = httpClient.post("$baseUrl/api/v1/towns") {
            headers {
                append("Authorization", "Bearer $token")
                append("X-API-Token", token)
            }
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        return response.body<ApiResponse<Town>>().data!!
    }

    /**
     * 获取城镇详情
     */
    suspend fun getTown(id: Int): Town {
        val response = httpClient.get("$baseUrl/api/v1/towns/$id") {
            headers {
                append("Authorization", "Bearer $token")
                append("X-API-Token", token)
            }
        }
        return response.body<ApiResponse<Town>>().data!!
    }

    /**
     * 更新城镇信息
     */
    suspend fun updateTown(id: Int, request: UpdateTownRequest): Town {
        val response = httpClient.put("$baseUrl/api/v1/towns/$id") {
            headers {
                append("Authorization", "Bearer $token")
                append("X-API-Token", token)
            }
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        return response.body<ApiResponse<Town>>().data!!
    }

    /**
     * 删除城镇
     */
    suspend fun deleteTown(id: Int) {
        httpClient.delete("$baseUrl/api/v1/towns/$id") {
            headers {
                append("Authorization", "Bearer $token")
                append("X-API-Token", token)
            }
        }
    }

    /**
     * 获取城镇成员列表
     */
    suspend fun getTownMembers(townId: Int, page: Int? = null, pageSize: Int? = null): TownMembersResponse {
        val response = httpClient.get("$baseUrl/api/v1/towns/$townId/members") {
            headers {
                append("Authorization", "Bearer $token")
                append("X-API-Token", token)
            }
            page?.let { parameter("page", it) }
            pageSize?.let { parameter("page_size", it) }
        }
        return response.body<ApiResponse<TownMembersResponse>>().data!!
    }

    override fun close() {
        httpClient.close()
    }
}
