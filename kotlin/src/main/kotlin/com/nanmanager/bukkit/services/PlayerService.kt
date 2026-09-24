package com.nanmanager.bukkit.services

import com.nanmanager.bukkit.http.HttpClient
import com.nanmanager.bukkit.SessionContext
import com.nanmanager.bukkit.models.*

/** 玩家管理服务；传输和模型解析由 OpenAPI 生成客户端负责。 */
class PlayerService(httpClient: HttpClient) : BaseService(httpClient) {
    private val api get() = httpClient.generatedApis.players

    fun createPlayer(request: CreatePlayerRequest): Player {
        val body = toGenerated<com.newnanmanager.generated.models.CreatePlayerRequest>(request)
        return generated<Player, com.newnanmanager.generated.models.Player> {
            api.createPlayerWithHttpInfo(body)
        }
    }

    fun getPlayer(request: GetPlayerRequest): Player {
        return generated<Player, com.newnanmanager.generated.models.Player> {
            api.getPlayerWithHttpInfo(request.id)
        }
    }

    fun updatePlayer(request: UpdatePlayerRequest): Player {
        val body = toGenerated<com.newnanmanager.generated.models.UpdatePlayerRequest>(request)
        return generated<Player, com.newnanmanager.generated.models.Player> {
            api.updatePlayerWithHttpInfo(request.id, body)
        }
    }

    fun deletePlayer(request: DeletePlayerRequest) {
        generatedVoid { api.deletePlayerWithHttpInfo(request.id) }
    }

    fun listPlayers(request: ListPlayersRequest): ListPlayersResponse {
        val banMode = request.banMode?.let { mode ->
            com.newnanmanager.generated.apis.PlayerServiceApi.BanModeListPlayers.values()
                .first { it.value.toInt() == mode.value }
        }
        return generated<ListPlayersResponse, com.newnanmanager.generated.models.ListPlayersResponse> {
            api.listPlayersWithHttpInfo(
                page = request.page,
                pageSize = request.pageSize,
                search = request.search,
                townId = request.townId,
                banMode = banMode,
                name = request.name,
                qq = request.qq,
                qqguild = request.qqguild,
                discord = request.discord
            )
        }
    }

    fun banPlayer(request: BanPlayerRequest) {
        val body = toGenerated<com.newnanmanager.generated.models.BanPlayerRequest>(request)
        generatedVoid { api.banPlayerWithHttpInfo(request.playerId, body) }
    }

    fun unbanPlayer(request: UnbanPlayerRequest) {
        generatedVoid { api.unbanPlayerWithHttpInfo(request.playerId) }
    }

    fun validate(request: ValidateRequest): ValidateResponse {
        return post("/api/v1/players/validate", request)
    }

    fun validate(request: ValidateRequest, session: SessionContext): ValidateResponse {
        val body = toGenerated<com.newnanmanager.generated.models.ValidateRequest>(request)
        return generated<ValidateResponse, com.newnanmanager.generated.models.ValidateResponse> {
            api.validateWithHttpInfo(session.id, session.epoch, body)
        }
    }
}
