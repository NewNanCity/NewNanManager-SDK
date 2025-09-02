package com.nanmanager.client.services

import com.nanmanager.client.models.*
import io.ktor.client.*

/**
 * 玩家服务器关系管理服务模块
 */
class PlayerServerService(
    httpClient: HttpClient,
    baseUrl: String,
    token: String
) : BaseService(httpClient, baseUrl, token) {



    /**
     * 获取玩家的服务器关系
     */
    suspend fun getPlayerServers(playerId: Int, onlineOnly: Boolean = false): PlayerServersResponse {
        val params = buildParams(mapOf("online_only" to onlineOnly))
        return get("/api/v1/players/$playerId/servers", params)
    }

    /**
     * 获取服务器的在线玩家
     */
    suspend fun getServerPlayers(request: GetServerPlayersRequest): ServerPlayersResponse {
        val params = buildParams(mapOf(
            "page" to request.page,
            "page_size" to request.pageSize
        ))
        return get("/api/v1/servers/${request.serverId}/players", params)
    }

    /**
     * 获取全局在线玩家
     */
    suspend fun getOnlinePlayers(request: GetOnlinePlayersRequest = GetOnlinePlayersRequest()): ServerPlayersResponse {
        val params = buildParams(mapOf(
            "page" to request.page,
            "page_size" to request.pageSize,
            "search" to request.search,
            "server_id" to request.serverId
        ))
        return get("/api/v1/online-players", params)
    }

    /**
     * 设置玩家离线状态 - 在玩家退出时调用
     */
    suspend fun setPlayersOffline(serverId: Int, playerIds: List<Int>): EmptyResponse {
        val request = mapOf(
            "server_id" to serverId,
            "player_ids" to playerIds
        )
        return post("/api/v1/servers/players/offline", request)
    }
}
