package com.nanmanager.client.services

import com.nanmanager.client.models.*
import io.ktor.client.*

/**
 * 玩家管理服务模块
 * 基于模块化架构的标准化API定义
 */
class PlayerService(
    httpClient: HttpClient,
    baseUrl: String,
    token: String
) : BaseService(httpClient, baseUrl, token) {

    /**
     * 创建玩家
     */
    suspend fun createPlayer(request: CreatePlayerRequest): Player {
        return post("/api/v1/players", request)
    }

    /**
     * 获取玩家详情
     */
    suspend fun getPlayer(id: Int): Player {
        return get("/api/v1/players/$id")
    }

    /**
     * 更新玩家信息
     */
    suspend fun updatePlayer(id: Int, request: UpdatePlayerRequest): Player {
        return put("/api/v1/players/$id", request)
    }

    /**
     * 删除玩家
     */
    suspend fun deletePlayer(id: Int) {
        delete("/api/v1/players/$id")
    }

    /**
     * 获取玩家列表
     */
    suspend fun listPlayers(request: ListPlayersRequest = ListPlayersRequest()): ListPlayersResponse {
        val params = buildParams(mapOf(
            "page" to request.page,
            "page_size" to request.pageSize,
            "search" to request.search,
            "town_id" to request.townId,
            "ban_mode" to request.banMode?.value,
            "name" to request.name,
            "qq" to request.qq,
            "qqguild" to request.qqguild,
            "discord" to request.discord
        ))
        return get("/api/v1/players", params)
    }

    /**
     * 封禁玩家
     */
    suspend fun banPlayer(playerId: Int, request: BanPlayerRequest) {
        val body = mapOf(
            "ban_mode" to request.banMode,
            "duration_seconds" to request.durationSeconds,
            "reason" to request.reason
        )
        post<EmptyResponse>("/api/v1/players/$playerId/ban", body)
    }

    /**
     * 解封玩家
     */
    suspend fun unbanPlayer(playerId: Int) {
        post<EmptyResponse>("/api/v1/players/$playerId/unban", emptyMap<String, Any>())
    }

    /**
     * 批量玩家验证
     */
    suspend fun validate(request: ValidateRequest): ValidateResponse {
        val body = mapOf(
            "players" to request.players.map { player ->
                mapOf(
                    "player_name" to player.playerName,
                    "ip" to player.ip,
                    "client_version" to player.clientVersion,
                    "protocol_version" to player.protocolVersion
                )
            },
            "server_id" to request.serverId,
            "login" to request.login,
            "timestamp" to (if (request.timestamp == 0L) System.currentTimeMillis() else request.timestamp)
        )
        return post("/api/v1/players/validate", body)
    }

    /**
     * 验证玩家登录（向后兼容）
     * @deprecated 请使用 validate 方法进行批量验证
     */
    @Deprecated("请使用 validate 方法进行批量验证")
    suspend fun validateLogin(request: ValidateLoginRequest): ValidateLoginResponse {
        return post("/api/v1/players/validate-login", request)
    }
}
