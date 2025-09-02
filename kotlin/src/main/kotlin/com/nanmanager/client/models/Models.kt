package com.nanmanager.client.models

import kotlinx.serialization.*

// ========== 枚举类型定义 ==========

/**
 * 封禁模式枚举
 */
@Serializable
enum class BanMode(val value: Int) {
    @SerialName("0") NORMAL(0),      // 正常
    @SerialName("1") TEMPORARY(1),   // 临时封禁
    @SerialName("2") PERMANENT(2)    // 永久封禁
}

/**
 * 登录动作枚举
 */
@Serializable
enum class LoginAction(val value: Int) {
    @SerialName("1") LOGIN(1),
    @SerialName("2") LOGOUT(2)
}

/**
 * IP威胁等级枚举
 */
@Serializable
enum class ThreatLevel(val value: Int) {
    @SerialName("0") LOW(0),         // 低威胁
    @SerialName("1") MEDIUM(1),      // 中等威胁
    @SerialName("2") HIGH(2),        // 高威胁
    @SerialName("3") CRITICAL(3)     // 严重威胁
}

/**
 * IP查询状态枚举
 */
@Serializable
enum class QueryStatus(val value: Int) {
    @SerialName("0") PENDING(0),     // 待查询
    @SerialName("1") COMPLETED(1),   // 已完成
    @SerialName("2") FAILED(2)       // 查询失败
}

// ========== 基础数据结构 ==========

/**
 * 空请求
 */
@Serializable
data class EmptyRequest(
    val dummy: String? = null
)

/**
 * 空响应
 */
@Serializable
data class EmptyResponse(
    val dummy: String? = null
)

/**
 * 分页请求基类
 */
@Serializable
data class PaginationRequest(
    val page: Int? = 1,
    @SerialName("page_size") val pageSize: Int? = 20
)

/**
 * 分页响应基类
 */
@Serializable
data class PaginationResponse(
    val total: Long,
    val page: Int,
    @SerialName("page_size") val pageSize: Int
)

// ========== 玩家相关数据结构 ==========

/**
 * 玩家信息
 */
@Serializable
data class Player(
    val id: Int,                                    // 玩家ID
    val name: String,                               // 游戏ID
    @SerialName("town_id") val townId: Int? = null, // 所属城镇ID
    val qq: String? = null,                         // QQ号
    val qqguild: String? = null,                    // QQ频道ID
    val discord: String? = null,                    // Discord ID
    @SerialName("in_qq_group") val inQqGroup: Boolean,  // 是否在QQ群
    @SerialName("in_qq_guild") val inQqGuild: Boolean,  // 是否在QQ频道
    @SerialName("in_discord") val inDiscord: Boolean,   // 是否在Discord群
    @SerialName("ban_mode") val banMode: BanMode,       // 封禁模式
    @SerialName("ban_expire") val banExpire: String? = null,  // 封禁到期时间(ISO8601格式)
    @SerialName("ban_reason") val banReason: String? = null,  // 封禁原因
    @SerialName("created_at") val createdAt: String,    // 创建时间(ISO8601格式)
    @SerialName("updated_at") val updatedAt: String     // 更新时间(ISO8601格式)
)

/**
 * 创建玩家请求
 */
@Serializable
data class CreatePlayerRequest(
    val name: String,                               // 游戏ID：1-100字符
    @SerialName("town_id") val townId: Int? = null, // 所属城镇ID：正整数或null
    val qq: String? = null,                         // QQ号：null、空或1-20字符
    val qqguild: String? = null,                    // QQ频道ID：null、空或1-30字符
    val discord: String? = null,                    // Discord ID：null、空或1-30字符
    @SerialName("in_qq_group") val inQqGroup: Boolean = false,  // 是否在QQ群，默认false
    @SerialName("in_qq_guild") val inQqGuild: Boolean = false,  // 是否在QQ频道，默认false
    @SerialName("in_discord") val inDiscord: Boolean = false    // 是否在Discord群，默认false
)

/**
 * 获取玩家请求
 */
@Serializable
data class GetPlayerRequest(
    val id: Int                                     // 玩家ID
)

/**
 * 更新玩家请求
 */
@Serializable
data class UpdatePlayerRequest(
    val id: Int,                                    // 玩家ID
    val name: String? = null,                       // 游戏ID：null、空或1-100字符
    @SerialName("town_id") val townId: Int? = null, // 所属城镇ID：null或正整数
    val qq: String? = null,                         // QQ号：null、空或1-20字符
    val qqguild: String? = null,                    // QQ频道ID：null、空或1-30字符
    val discord: String? = null,                    // Discord ID：null、空或1-30字符
    @SerialName("in_qq_group") val inQqGroup: Boolean? = null,  // 是否在QQ群
    @SerialName("in_qq_guild") val inQqGuild: Boolean? = null,  // 是否在QQ频道
    @SerialName("in_discord") val inDiscord: Boolean? = null    // 是否在Discord群
)

/**
 * 删除玩家请求
 */
@Serializable
data class DeletePlayerRequest(
    val id: Int                                     // 玩家ID
)

/**
 * 封禁玩家请求
 */
@Serializable
data class BanPlayerRequest(
    @SerialName("player_id") val playerId: Int,     // 玩家ID
    @SerialName("ban_mode") val banMode: BanMode,   // 封禁模式
    @SerialName("duration_seconds") val durationSeconds: Long? = null, // 封禁时长(秒)
    val reason: String                              // 封禁原因
)

/**
 * 解封玩家请求
 */
@Serializable
data class UnbanPlayerRequest(
    @SerialName("player_id") val playerId: Int      // 玩家ID
)

/**
 * 玩家列表请求
 */
@Serializable
data class ListPlayersRequest(
    val page: Int = 1,                              // 页码
    @SerialName("page_size") val pageSize: Int = 20, // 每页数量
    val search: String? = null,                     // 搜索关键词（模糊搜索游戏名）
    @SerialName("town_id") val townId: Int? = null, // 城镇ID过滤
    @SerialName("ban_mode") val banMode: BanMode? = null, // 封禁状态过滤
    val name: String? = null,                       // 精确游戏名过滤
    val qq: String? = null,                         // QQ号过滤
    val qqguild: String? = null,                    // QQ频道ID过滤
    val discord: String? = null                     // Discord ID过滤
)

/**
 * 玩家列表响应
 */
@Serializable
data class ListPlayersResponse(
    val players: List<Player>,                      // 玩家列表
    val total: Long,                                // 总数
    val page: Int,                                  // 当前页码
    @SerialName("page_size") val pageSize: Int      // 每页数量
)



/**
 * 单个玩家验证信息
 */
@Serializable
data class PlayerValidateInfo(
    @SerialName("player_name") val playerName: String,  // 玩家名：1-100字符
    val ip: String,                                     // IP地址：1-45字符
    @SerialName("client_version") val clientVersion: String? = null,    // 客户端版本：null、空或1-50字符
    @SerialName("protocol_version") val protocolVersion: String? = null // 协议版本：null、空或1-20字符
)

/**
 * 玩家验证请求（支持批处理）
 */
@Serializable
data class ValidateRequest(
    val players: List<PlayerValidateInfo>,              // 玩家列表（1-100个）
    @SerialName("server_id") val serverId: Int,         // 服务器ID：1-999999
    val login: Boolean,                                 // 是否为登录验证（true=登录需记录日志，false=定期检查不记录日志）
    val timestamp: Long = 0                             // 请求时间戳：0或Unix时间戳，默认0
)



/**
 * 单个玩家验证结果 - 包含IP风险信息
 */
@Serializable
data class PlayerValidateResult(
    @SerialName("player_name") val playerName: String,  // 玩家名
    val allowed: Boolean,                               // 是否允许登录
    @SerialName("player_id") val playerId: Int? = null, // 玩家ID
    val reason: String? = null,                         // 拒绝原因
    val newbie: Boolean = false,                        // 是否为新玩家
    @SerialName("ip_info") val ipInfo: IPInfo? = null // IP信息（包含风险信息）
)

/**
 * 玩家验证响应（支持批处理）
 */
@Serializable
data class ValidateResponse(
    val results: List<PlayerValidateResult>,            // 验证结果列表
    @SerialName("processed_at") val processedAt: Long   // 处理时间戳
)

// ========== 城镇相关数据结构 ==========

/**
 * 城镇信息
 */
@Serializable
data class Town(
    val id: Int,                                        // 城镇ID
    val name: String,                                   // 城镇名称
    val level: Int,                                     // 城镇等级(0-5星级)
    @SerialName("leader_id") val leaderId: Int? = null, // 镇长玩家ID
    @SerialName("qq_group") val qqGroup: String? = null, // 绑定QQ群号
    val description: String? = null,                    // 城镇描述
    @SerialName("created_at") val createdAt: String,    // 创建时间(ISO8601格式)
    @SerialName("updated_at") val updatedAt: String     // 更新时间(ISO8601格式)
)

/**
 * 创建城镇请求
 */
@Serializable
data class CreateTownRequest(
    val name: String,                                   // 城镇名称：1-100字符
    val level: Int = 1,                                 // 城镇等级：1-5星级，默认1
    @SerialName("leader_id") val leaderId: Int? = null, // 镇长玩家ID：null或正整数
    @SerialName("qq_group") val qqGroup: String? = null, // 绑定QQ群号：null、空或1-20字符
    val description: String? = null                     // 城镇描述：null、空或1-1000字符
)

/**
 * 获取城镇请求
 */
@Serializable
data class GetTownRequest(
    val id: Int,                                        // 城镇ID
    val detail: Boolean = false                         // 是否返回详细信息（包括镇长和成员）
)

/**
 * 更新城镇请求
 */
@Serializable
data class UpdateTownRequest(
    val id: Int,                                        // 城镇ID
    val name: String? = null,                           // 城镇名称：null、空或1-100字符
    val level: Int? = null,                             // 城镇等级：1-5星级
    @SerialName("leader_id") val leaderId: Int? = null, // 镇长玩家ID：null或正整数
    @SerialName("qq_group") val qqGroup: String? = null, // 绑定QQ群号：null、空或1-20字符
    val description: String? = null,                    // 城镇描述：null、空或1-1000字符
    @SerialName("add_players") val addPlayers: List<Int>? = null,     // 待添加的玩家ID列表
    @SerialName("remove_players") val removePlayers: List<Int>? = null // 待移除的玩家ID列表
)

/**
 * 删除城镇请求
 */
@Serializable
data class DeleteTownRequest(
    val id: Int                                         // 城镇ID
)

/**
 * 城镇列表请求
 */
@Serializable
data class ListTownsRequest(
    val page: Int = 1,                                  // 页码
    @SerialName("page_size") val pageSize: Int = 20,    // 每页数量
    val search: String? = null,                         // 搜索关键词
    @SerialName("min_level") val minLevel: Int? = null, // 最小等级：1-5星级
    @SerialName("max_level") val maxLevel: Int? = null  // 最大等级：1-5星级
)

/**
 * 城镇列表响应
 */
@Serializable
data class ListTownsResponse(
    val towns: List<Town>,                              // 城镇列表
    val total: Long,                                    // 总数
    val page: Int,                                      // 当前页码
    @SerialName("page_size") val pageSize: Int          // 每页数量
)



/**
 * 获取城镇成员请求
 */
@Serializable
data class GetTownMembersRequest(
    @SerialName("town_id") val townId: Int,             // 城镇ID
    val page: Int = 1,                                  // 页码
    @SerialName("page_size") val pageSize: Int = 50     // 每页数量
)

/**
 * 城镇成员响应
 */
@Serializable
data class TownMembersResponse(
    val members: List<Player>,                          // 成员列表
    val total: Long,                                    // 总数
    val page: Int,                                      // 当前页码
    @SerialName("page_size") val pageSize: Int          // 每页数量
)

/**
 * 城镇详细信息响应
 */
@Serializable
data class TownDetailResponse(
    val town: Town,                                     // 城镇基本信息
    val leader: Player? = null,                         // 镇长信息
    val members: List<Player>,                          // 成员列表（包括镇长）
    @SerialName("member_count") val memberCount: Long   // 成员总数
)

// ========== 服务器相关数据结构 ==========

/**
 * 服务器注册信息 - 解耦优化版
 */
@Serializable
data class ServerRegistry(
    val id: Int,                                        // 服务器ID
    val name: String,                                   // 服务器名称
    val address: String,                                // 服务器地址（可包含端口，如 "example.com:25565" 或 "example.com" 用于SRV记录）
    val description: String? = null,                    // 服务器描述
    val active: Boolean,                                // 是否激活
    @SerialName("created_at") val createdAt: String,    // 创建时间(ISO8601格式)
    @SerialName("updated_at") val updatedAt: String     // 更新时间(ISO8601格式)
)

/**
 * 服务器状态
 */
@Serializable
data class ServerStatus(
    @SerialName("server_id") val serverId: Int,         // 服务器ID
    val online: Boolean,                                // 是否在线
    @SerialName("current_players") val currentPlayers: Int, // 当前在线人数
    @SerialName("max_players") val maxPlayers: Int,     // 最大玩家数
    @SerialName("latency_ms") val latencyMs: Int? = null, // 延迟毫秒
    val tps: Double? = null,                            // 服务器TPS
    val version: String? = null,                        // 服务器版本
    val motd: String? = null,                           // 服务器描述
    @SerialName("expire_at") val expireAt: String,      // 状态失效时间(ISO8601格式)
    @SerialName("last_heartbeat") val lastHeartbeat: String // 最后心跳时间(ISO8601格式)
)

/**
 * 创建服务器请求
 */
@Serializable
data class CreateServerRequest(
    val name: String,                                   // 服务器名称：1-100字符
    val address: String,                                // 服务器地址：1-255字符
    val description: String? = null                     // 服务器描述：null、空或1-1000字符
)

/**
 * 获取服务器请求
 */
@Serializable
data class GetServerRequest(
    val id: Int                                         // 服务器ID
)

/**
 * 更新服务器请求
 */
@Serializable
data class UpdateServerRequest(
    val id: Int,                                        // 服务器ID
    val name: String? = null,                           // 服务器名称：null、空或1-100字符
    val address: String? = null,                        // 服务器地址：null、空或1-255字符
    val description: String? = null                     // 服务器描述：null、空或1-1000字符
)

/**
 * 删除服务器请求
 */
@Serializable
data class DeleteServerRequest(
    val id: Int                                         // 服务器ID
)

/**
 * 服务器列表请求
 */
@Serializable
data class ListServersRequest(
    val page: Int = 1,                                  // 页码
    @SerialName("page_size") val pageSize: Int = 20,    // 每页数量
    val search: String? = null,                         // 搜索关键词
    @SerialName("online_only") val onlineOnly: Boolean = false // 仅显示在线服务器
)

/**
 * 服务器列表响应
 */
@Serializable
data class ListServersResponse(
    val servers: List<ServerRegistry>,                  // 服务器列表
    val total: Long,                                    // 总数
    val page: Int,                                      // 当前页码
    @SerialName("page_size") val pageSize: Int          // 每页数量
)

/**
 * 服务器详细信息请求
 */
@Serializable
data class GetServerDetailRequest(
    val id: Int                                         // 服务器ID
)

/**
 * 服务器详细信息响应
 */
@Serializable
data class ServerDetailResponse(
    val server: ServerRegistry,                         // 服务器信息
    val status: ServerStatus? = null                    // 服务器状态
)

// ========== 服务器监控相关数据结构 ==========

/**
 * 玩家登录信息
 */
@Serializable
data class PlayerLoginInfo(
    @SerialName("player_id") val playerId: Int,         // 玩家ID
    val name: String,                                   // 玩家名
    val ip: String                                      // 登录IP
)

/**
 * 心跳请求
 */
@Serializable
data class HeartbeatRequest(
    @SerialName("server_id") val serverId: Int,         // 服务器ID
    @SerialName("current_players") val currentPlayers: Int, // 当前在线人数
    @SerialName("max_players") val maxPlayers: Int,     // 最大玩家数
    val tps: Double? = null,                            // 服务器TPS
    val version: String? = null,                        // 服务器版本
    val motd: String? = null,                           // 服务器描述
    @SerialName("rtt_ms") val rttMs: Long? = null       // RTT延迟(毫秒)
)

/**
 * 心跳响应
 */
@Serializable
data class HeartbeatResponse(
    @SerialName("received_at") val receivedAt: Long,    // 服务端接收时间戳(毫秒)
    @SerialName("response_at") val responseAt: Long,    // 服务端响应时间戳(毫秒)
    @SerialName("expire_duration_ms") val expireDurationMs: Long // 状态过期时间(毫秒)
)

/**
 * 监控统计记录
 */
@Serializable
data class MonitorStatRecord(
    val timestamp: Long,                                // 统计时间戳
    @SerialName("current_players") val currentPlayers: Int, // 当前在线人数
    val tps: Double? = null,                            // 服务器TPS
    @SerialName("latency_ms") val latencyMs: Long? = null // 延迟毫秒
)

/**
 * 监控统计响应
 */
@Serializable
data class MonitorStatsResponse(
    @SerialName("server_id") val serverId: Int,         // 服务器ID
    val stats: List<MonitorStatRecord>                  // 监控统计信息列表
)

/**
 * 获取服务器状态请求
 */
@Serializable
data class GetServerStatusRequest(
    @SerialName("server_id") val serverId: Int          // 服务器ID
)

/**
 * 获取延迟统计请求
 */
@Serializable
data class GetLatencyStatsRequest(
    @SerialName("server_id") val serverId: Int          // 服务器ID
)

/**
 * 延迟统计响应
 */
@Serializable
data class LatencyStatsResponse(
    @SerialName("server_id") val serverId: Int,         // 服务器ID
    val count: Long,                                    // 统计数量
    val current: Long,                                  // 当前延迟
    val average: Long,                                  // 平均延迟
    val min: Long,                                      // 最小延迟
    val max: Long,                                      // 最大延迟
    val variance: Double,                               // 方差
    @SerialName("last_updated") val lastUpdated: String // 最后更新时间(ISO8601格式)
)

// ========== API Token相关数据结构 ==========

/**
 * API Token信息
 */
@Serializable
data class ApiToken(
    val id: Int,                                        // Token ID
    val name: String,                                   // Token名称
    val role: String,                                   // 角色 (admin/manager/server/monitor或自定义)
    val description: String? = null,                    // Token描述
    val active: Boolean,                                // 是否激活
    @SerialName("expire_at") val expireAt: String? = null, // 过期时间(ISO8601格式)，NULL表示永不过期
    @SerialName("last_used_at") val lastUsedAt: String? = null, // 最后使用时间(ISO8601格式)
    @SerialName("last_used_ip") val lastUsedIp: String? = null, // 最后使用IP
    @SerialName("created_at") val createdAt: String,    // 创建时间(ISO8601格式)
    @SerialName("updated_at") val updatedAt: String     // 更新时间(ISO8601格式)
)

/**
 * 创建API Token请求
 */
@Serializable
data class CreateApiTokenRequest(
    val name: String,                                   // Token名称：1-100字符
    val role: String,                                   // 角色：1-50字符
    val description: String? = null,                    // Token描述：null、空或1-500字符
    @SerialName("expire_days") val expireDays: Long? = null // 过期天数：0表示永不过期，或1-3650天（10年）
)

/**
 * 创建API Token响应
 */
@Serializable
data class CreateApiTokenResponse(
    @SerialName("token_info") val tokenInfo: ApiToken, // Token信息
    @SerialName("token_value") val tokenValue: String  // Token明文值(仅创建时返回)
)

/**
 * 获取API Token请求
 */
@Serializable
data class GetApiTokenRequest(
    val id: Int                                         // Token ID
)

/**
 * 更新API Token请求
 */
@Serializable
data class UpdateApiTokenRequest(
    val id: Int,                                        // Token ID
    val name: String? = null,                           // Token名称：null、空或1-100字符
    val role: String? = null,                           // 角色：null、空或1-50字符
    val description: String? = null,                    // Token描述：null、空或1-500字符
    val active: Boolean? = null                         // 是否激活
)

/**
 * 删除API Token请求
 */
@Serializable
data class DeleteApiTokenRequest(
    val id: Int                                         // Token ID
)

/**
 * API Token列表响应
 */
@Serializable
data class ListApiTokensResponse(
    val tokens: List<ApiToken>,                         // Token列表
    val total: Long,                                    // 总数
    val page: Int,                                      // 当前页码
    @SerialName("page_size") val pageSize: Int          // 每页数量
)

// ========== IP管理相关数据结构 ==========

/**
 * IP信息
 */
@Serializable
data class IPInfo(
    val ip: String,                                     // IP地址
    @SerialName("ip_type") val ipType: String,          // IP类型：ipv4/ipv6
    val country: String? = null,                        // 国家/地区
    @SerialName("country_code") val countryCode: String? = null, // 国家代码
    val region: String? = null,                         // 省份/州
    val city: String? = null,                           // 城市
    val latitude: Double? = null,                       // 纬度
    val longitude: Double? = null,                      // 经度
    val timezone: String? = null,                       // 时区
    val isp: String? = null,                            // 网络服务提供商
    val organization: String? = null,                   // 组织名称
    val asn: String? = null,                            // ASN号码
    @SerialName("is_bogon") val isBogon: Boolean,       // 是否为Bogon IP
    @SerialName("is_mobile") val isMobile: Boolean,     // 是否为移动网络
    @SerialName("is_satellite") val isSatellite: Boolean, // 是否为卫星网络
    @SerialName("is_crawler") val isCrawler: Boolean,   // 是否为爬虫
    @SerialName("is_datacenter") val isDatacenter: Boolean, // 是否为数据中心IP
    @SerialName("is_tor") val isTor: Boolean,           // 是否为Tor出口节点
    @SerialName("is_proxy") val isProxy: Boolean,       // 是否为代理IP
    @SerialName("is_vpn") val isVpn: Boolean,           // 是否为VPN
    @SerialName("is_abuser") val isAbuser: Boolean,     // 是否为滥用者
    val banned: Boolean,                                // 是否被封禁
    @SerialName("ban_reason") val banReason: String? = null, // 封禁原因
    @SerialName("threat_level") val threatLevel: ThreatLevel, // 威胁等级
    @SerialName("risk_score") val riskScore: Int,       // 风险评分（0-100）
    @SerialName("query_status") val queryStatus: QueryStatus, // 查询状态
    @SerialName("last_query_at") val lastQueryAt: String? = null, // 最后查询时间(ISO8601格式)
    @SerialName("created_at") val createdAt: String,    // 创建时间(ISO8601格式)
    @SerialName("updated_at") val updatedAt: String,    // 更新时间(ISO8601格式)
    // 风险信息字段
    @SerialName("risk_level") val riskLevel: String,    // 风险等级
    @SerialName("risk_description") val riskDescription: String // 风险描述
)

/**
 * 获取IP信息请求
 */
@Serializable
data class GetIPInfoRequest(
    val ip: String                                      // IP地址
)

/**
 * 封禁IP请求（支持批量）
 */
@Serializable
data class BanIPRequest(
    val ips: List<String>,                              // IP地址列表
    val reason: String                                  // 封禁原因
)

/**
 * 解封IP请求（支持批量）
 */
@Serializable
data class UnbanIPRequest(
    val ips: List<String>                               // IP地址列表
)

/**
 * IP列表请求
 */
@Serializable
data class ListIPsRequest(
    val page: Int = 1,                                  // 页码
    @SerialName("page_size") val pageSize: Int = 20,    // 每页数量
    @SerialName("banned_only") val bannedOnly: Boolean = false, // 仅显示被封禁的IP
    @SerialName("min_threat_level") val minThreatLevel: ThreatLevel? = null, // 最小威胁等级
    @SerialName("min_risk_score") val minRiskScore: Int? = null // 最小风险评分
)

/**
 * IP列表响应
 */
@Serializable
data class ListIPsResponse(
    val ips: List<IPInfo>,                              // IP列表
    val total: Long,                                    // 总数
    val page: Int,                                      // 当前页码
    @SerialName("page_size") val pageSize: Int          // 每页数量
)

/**
 * IP统计信息
 */
@Serializable
data class IPStatistics(
    @SerialName("total_ips") val totalIps: Long,        // 总IP数
    @SerialName("completed_ips") val completedIps: Long, // 已完成查询的IP数
    @SerialName("pending_ips") val pendingIps: Long,    // 待查询IP数
    @SerialName("failed_ips") val failedIps: Long,      // 查询失败IP数
    @SerialName("banned_ips") val bannedIps: Long,      // 被封禁IP数
    @SerialName("proxy_ips") val proxyIps: Long,        // 代理IP数
    @SerialName("vpn_ips") val vpnIps: Long,            // VPN IP数
    @SerialName("tor_ips") val torIps: Long,            // Tor IP数
    @SerialName("datacenter_ips") val datacenterIps: Long, // 数据中心IP数
    @SerialName("high_risk_ips") val highRiskIps: Long  // 高风险IP数
)

// ========== 玩家服务器关系相关数据结构 ==========

/**
 * 玩家服务器关系信息
 */
@Serializable
data class PlayerServer(
    @SerialName("player_id") val playerId: Int,         // 玩家ID
    @SerialName("server_id") val serverId: Int,         // 服务器ID
    val online: Boolean,                                // 是否在线
    @SerialName("joined_at") val joinedAt: String,      // 加入时间(ISO8601格式)
    @SerialName("created_at") val createdAt: String,    // 创建时间(ISO8601格式)
    @SerialName("updated_at") val updatedAt: String     // 更新时间(ISO8601格式)
)

/**
 * 在线玩家信息
 */
@Serializable
data class OnlinePlayer(
    @SerialName("player_id") val playerId: Int,         // 玩家ID
    @SerialName("player_name") val playerName: String,  // 玩家名
    @SerialName("server_id") val serverId: Int,         // 服务器ID
    @SerialName("server_name") val serverName: String,  // 服务器名
    @SerialName("joined_at") val joinedAt: String       // 加入时间(ISO8601格式)
)



/**
 * 获取玩家服务器关系请求
 */
@Serializable
data class GetPlayerServersRequest(
    @SerialName("player_id") val playerId: Int,         // 玩家ID
    @SerialName("online_only") val onlineOnly: Boolean = false // 仅显示在线服务器
)

/**
 * 玩家服务器关系列表响应
 */
@Serializable
data class PlayerServersResponse(
    val servers: List<PlayerServer>,                    // 服务器关系列表
    val total: Long                                     // 总数
)

/**
 * 获取服务器在线玩家请求
 */
@Serializable
data class GetServerPlayersRequest(
    @SerialName("server_id") val serverId: Int,         // 服务器ID
    val page: Int = 1,                                  // 页码
    @SerialName("page_size") val pageSize: Int = 50     // 每页数量
)

/**
 * 服务器在线玩家响应
 */
@Serializable
data class ServerPlayersResponse(
    val players: List<OnlinePlayer>,                    // 在线玩家列表
    val total: Long,                                    // 总数
    val page: Int,                                      // 当前页码
    @SerialName("page_size") val pageSize: Int          // 每页数量
)

/**
 * 获取全局在线玩家请求
 */
@Serializable
data class GetOnlinePlayersRequest(
    val page: Int = 1,                                  // 页码
    @SerialName("page_size") val pageSize: Int = 50,    // 每页数量
    val search: String? = null,                         // 搜索玩家名：null或<=100字符
    @SerialName("server_id") val serverId: Int? = null  // 服务器ID过滤：null或正整数
)

// ========== 向后兼容的旧模型（已弃用） ==========

/**
 * 验证玩家登录（向后兼容）
 * @deprecated 请使用 ValidateRequest 进行批量验证
 */
@Deprecated("请使用 ValidateRequest 进行批量验证")
@Serializable
data class ValidateLoginRequest(
    @SerialName("player_name") val playerName: String,
    @SerialName("server_id") val serverId: Int
)

/**
 * 验证玩家登录响应（向后兼容）
 * @deprecated 请使用 ValidateResponse 进行批量验证
 */
@Deprecated("请使用 ValidateResponse 进行批量验证")
@Serializable
data class ValidateLoginResponse(
    val allowed: Boolean,
    @SerialName("player_id") val playerId: Int? = null,
    val reason: String? = null
)
