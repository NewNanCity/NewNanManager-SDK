package com.nanmanager.bukkit.services

import com.nanmanager.bukkit.http.HttpClient
import com.nanmanager.bukkit.SessionContext
import com.nanmanager.bukkit.models.*

/** 玩家服务器关系管理服务；传输和模型解析由 OpenAPI 生成客户端负责。 */
class PlayerServerService(httpClient: HttpClient) : BaseService(httpClient) {
    private val api get() = httpClient.generatedApis.playerServers

    fun getPlayerServers(request: GetPlayerServersRequest): PlayerServersResponse {
        return generated<PlayerServersResponse, com.newnanmanager.generated.models.PlayerServersResponse> {
            api.getPlayerServersWithHttpInfo(request.playerId, request.onlineOnly)
        }
    }

    fun getServerPlayers(request: GetServerPlayersRequest): ServerPlayersResponse {
        return generated<ServerPlayersResponse, com.newnanmanager.generated.models.ServerPlayersResponse> {
            api.getServerPlayersWithHttpInfo(
                page = request.page,
                pageSize = request.pageSize,
                search = request.search,
                serverId = request.serverId,
                onlineOnly = request.onlineOnly
            )
        }
    }

    fun setPlayersOffline(request: SetPlayersOfflineRequest) {
        post<Unit>("/api/v1/servers/players/offline", request)
    }

    fun setPlayersOffline(request: SetPlayersOfflineRequest, session: SessionContext) {
        val body = toGenerated<com.newnanmanager.generated.models.SetPlayersOfflineRequest>(request)
        generatedVoid {
            api.setPlayersOfflineWithHttpInfo(session.id, session.epoch, body)
        }
    }
}
