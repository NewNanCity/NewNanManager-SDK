package com.nanmanager.client.models

import kotlinx.serialization.*

// 枚举类型定义
@Serializable
enum class BanMode(val value: Int) {
    NORMAL(0),
    TEMPORARY(1),
    PERMANENT(2)
}

@Serializable
enum class ServerType {
    @SerialName("MINECRAFT") MINECRAFT,
    @SerialName("PROXY") PROXY,
    @SerialName("LOBBY") LOBBY
}

// 通用响应类型
@Serializable
data class ApiResponse<T>(
    val code: Int,
    val message: String,
    val data: T? = null,
    @SerialName("request_id") val requestId: String
)

@Serializable
data class ErrorResponse(
    val code: Int,
    val message: String,
    val data: ErrorData? = null,
    @SerialName("request_id") val requestId: String
)

@Serializable
data class ErrorData(
    val details: String? = null
)

// 核心实体模型
@Serializable
data class Player(
    val id: Int,
    val name: String,
    @SerialName("town_id") val townId: Int? = null,
    val qq: String? = null,
    val qqguild: String? = null,
    val discord: String? = null,
    @SerialName("in_qq_group") val inQqGroup: Boolean,
    @SerialName("in_qq_guild") val inQqGuild: Boolean,
    @SerialName("in_discord") val inDiscord: Boolean,
    @SerialName("ban_mode") val banMode: Int,
    @SerialName("ban_expire") val banExpire: String? = null,
    @SerialName("ban_reason") val banReason: String? = null,
    @SerialName("created_at") val createdAt: String,
    @SerialName("updated_at") val updatedAt: String
)

@Serializable
data class Town(
    val id: Int,
    val name: String,
    val level: Int,
    @SerialName("leader_id") val leaderId: Int? = null,
    @SerialName("qq_group") val qqGroup: String? = null,
    val description: String? = null,
    @SerialName("created_at") val createdAt: String,
    @SerialName("updated_at") val updatedAt: String
)

@Serializable
data class ServerRegistry(
    val id: Int,
    val name: String,
    val address: String,
    @SerialName("server_type") val serverType: String? = null,
    val description: String? = null,
    @SerialName("created_at") val createdAt: String,
    @SerialName("updated_at") val updatedAt: String
)

@Serializable
data class ServerStatus(
    @SerialName("server_id") val serverId: Int,
    val online: Boolean,
    @SerialName("current_players") val currentPlayers: Int,
    @SerialName("max_players") val maxPlayers: Int,
    @SerialName("latency_ms") val latencyMs: Int? = null,
    val tps: Double? = null,
    val version: String? = null,
    val motd: String? = null,
    @SerialName("expire_at") val expireAt: String,
    @SerialName("last_heartbeat") val lastHeartbeat: String
)

@Serializable
data class ApiToken(
    val id: Int,
    val name: String,
    val role: String,
    val description: String? = null,
    val active: Boolean,
    @SerialName("expire_at") val expireAt: String? = null,
    @SerialName("last_used_at") val lastUsedAt: String? = null,
    @SerialName("last_used_ip") val lastUsedIp: String? = null,
    @SerialName("created_at") val createdAt: String,
    @SerialName("updated_at") val updatedAt: String
)

// 响应数据模型
@Serializable
data class ListPlayersResponse(
    val players: List<Player>,
    val total: Long,
    val page: Int,
    @SerialName("page_size") val pageSize: Int
)

@Serializable
data class ListTownsResponse(
    val towns: List<Town>,
    val total: Long,
    val page: Int,
    @SerialName("page_size") val pageSize: Int
)

@Serializable
data class ListServersResponse(
    val servers: List<ServerRegistry>,
    val total: Long,
    val page: Int,
    @SerialName("page_size") val pageSize: Int
)

@Serializable
data class ValidateLoginResponse(
    val allowed: Boolean,
    @SerialName("player_id") val playerId: Int? = null,
    val reason: String? = null
)

@Serializable
data class ServerDetailResponse(
    val server: ServerRegistry,
    val status: ServerStatus? = null
)

@Serializable
data class ListApiTokensResponse(
    val tokens: List<ApiToken>
)

@Serializable
data class TownMembersResponse(
    val members: List<Player>,
    val total: Long,
    val page: Int,
    @SerialName("page_size") val pageSize: Int
)

// 请求体模型
@Serializable
data class CreatePlayerRequest(
    val name: String,
    @SerialName("town_id") val townId: Int? = null,
    val qq: String? = null,
    val qqguild: String? = null,
    val discord: String? = null,
    @SerialName("in_qq_group") val inQqGroup: Boolean? = null,
    @SerialName("in_qq_guild") val inQqGuild: Boolean? = null,
    @SerialName("in_discord") val inDiscord: Boolean? = null
)

@Serializable
data class UpdatePlayerRequest(
    val name: String? = null,
    @SerialName("town_id") val townId: Int? = null,
    val qq: String? = null,
    val qqguild: String? = null,
    val discord: String? = null,
    @SerialName("in_qq_group") val inQqGroup: Boolean? = null,
    @SerialName("in_qq_guild") val inQqGuild: Boolean? = null,
    @SerialName("in_discord") val inDiscord: Boolean? = null
)

@Serializable
data class BanPlayerRequest(
    @SerialName("ban_mode") val banMode: Int,
    @SerialName("duration_seconds") val durationSeconds: Long? = null,
    val reason: String
)

@Serializable
data class ValidateLoginRequest(
    @SerialName("player_name") val playerName: String,
    @SerialName("server_id") val serverId: Int
)

@Serializable
data class CreateTownRequest(
    val name: String,
    val level: Int? = null,
    @SerialName("leader_id") val leaderId: Int? = null,
    @SerialName("qq_group") val qqGroup: String? = null,
    val description: String? = null
)

@Serializable
data class UpdateTownRequest(
    val name: String? = null,
    val level: Int? = null,
    @SerialName("leader_id") val leaderId: Int? = null,
    @SerialName("qq_group") val qqGroup: String? = null,
    val description: String? = null
)

@Serializable
data class RegisterServerRequest(
    val name: String,
    val address: String,
    @SerialName("server_type") val serverType: String,
    val description: String? = null
)

@Serializable
data class UpdateServerRequest(
    val name: String? = null,
    val address: String? = null,
    @SerialName("server_type") val serverType: String? = null,
    val description: String? = null
)
