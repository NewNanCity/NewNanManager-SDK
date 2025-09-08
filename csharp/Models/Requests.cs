using System.Text.Json.Serialization;

namespace NewNanManager.Client.Models;

/// <summary>
/// 创建玩家请求
/// </summary>
public class CreatePlayerRequest
{
    /// <summary>
    /// 玩家名称
    /// </summary>
    [JsonPropertyName("name")]
    public string? Name { get; set; }

    /// <summary>
    /// 所属城镇ID
    /// </summary>
    [JsonPropertyName("town_id")]
    public int? TownId { get; set; }

    /// <summary>
    /// QQ号
    /// </summary>
    [JsonPropertyName("qq")]
    public string? QQ { get; set; }

    /// <summary>
    /// QQ频道
    /// </summary>
    [JsonPropertyName("qqguild")]
    public string? QQGuild { get; set; }

    /// <summary>
    /// Discord
    /// </summary>
    [JsonPropertyName("discord")]
    public string? Discord { get; set; }

    /// <summary>
    /// 是否在QQ群中
    /// </summary>
    [JsonPropertyName("in_qq_group")]
    public bool? InQQGroup { get; set; }

    /// <summary>
    /// 是否在QQ频道中
    /// </summary>
    [JsonPropertyName("in_qq_guild")]
    public bool? InQQGuild { get; set; }

    /// <summary>
    /// 是否在Discord中
    /// </summary>
    [JsonPropertyName("in_discord")]
    public bool? InDiscord { get; set; }
}

/// <summary>
/// 更新玩家请求
/// </summary>
public class UpdatePlayerRequest
{
    /// <summary>
    /// 玩家名称
    /// </summary>
    [JsonPropertyName("name")]
    public string? Name { get; set; }

    /// <summary>
    /// 所属城镇ID
    /// </summary>
    [JsonPropertyName("town_id")]
    public int? TownId { get; set; }

    /// <summary>
    /// QQ号
    /// </summary>
    [JsonPropertyName("qq")]
    public string? QQ { get; set; }

    /// <summary>
    /// QQ频道
    /// </summary>
    [JsonPropertyName("qqguild")]
    public string? QQGuild { get; set; }

    /// <summary>
    /// Discord
    /// </summary>
    [JsonPropertyName("discord")]
    public string? Discord { get; set; }

    /// <summary>
    /// 是否在QQ群中
    /// </summary>
    [JsonPropertyName("in_qq_group")]
    public bool? InQQGroup { get; set; }

    /// <summary>
    /// 是否在QQ频道中
    /// </summary>
    [JsonPropertyName("in_qq_guild")]
    public bool? InQQGuild { get; set; }

    /// <summary>
    /// 是否在Discord中
    /// </summary>
    [JsonPropertyName("in_discord")]
    public bool? InDiscord { get; set; }
}

/// <summary>
/// 封禁玩家请求
/// </summary>
public class BanPlayerRequest
{
    /// <summary>
    /// 封禁模式
    /// </summary>
    [JsonPropertyName("ban_mode")]
    public BanMode BanMode { get; set; }

    /// <summary>
    /// 封禁持续时间（秒）
    /// </summary>
    [JsonPropertyName("duration_seconds")]
    public long? DurationSeconds { get; set; }

    /// <summary>
    /// 封禁原因
    /// </summary>
    [JsonPropertyName("reason")]
    public string Reason { get; set; } = string.Empty;
}

/// <summary>
/// 验证登录请求
/// </summary>
public class ValidateLoginRequest
{
    /// <summary>
    /// 玩家名称
    /// </summary>
    [JsonPropertyName("player_name")]
    public string? PlayerName { get; set; }

    /// <summary>
    /// 服务器ID
    /// </summary>
    [JsonPropertyName("server_id")]
    public int? ServerId { get; set; }

    /// <summary>
    /// 客户端版本
    /// </summary>
    [JsonPropertyName("client_version")]
    public string? ClientVersion { get; set; }

    /// <summary>
    /// 协议版本
    /// </summary>
    [JsonPropertyName("protocol_version")]
    public string? ProtocolVersion { get; set; }
}

/// <summary>
/// 创建服务器请求
/// </summary>
public class CreateServerRequest
{
    /// <summary>
    /// 服务器名称
    /// </summary>
    [JsonPropertyName("name")]
    public string? Name { get; set; }

    /// <summary>
    /// 服务器地址
    /// </summary>
    [JsonPropertyName("address")]
    public string? Address { get; set; }

    /// <summary>
    /// 服务器描述
    /// </summary>
    [JsonPropertyName("description")]
    public string? Description { get; set; }
}

/// <summary>
/// 更新服务器请求
/// </summary>
public class UpdateServerRequest
{
    /// <summary>
    /// 服务器名称
    /// </summary>
    [JsonPropertyName("name")]
    public string? Name { get; set; }

    /// <summary>
    /// 服务器地址
    /// </summary>
    [JsonPropertyName("address")]
    public string? Address { get; set; }

    /// <summary>
    /// 服务器描述
    /// </summary>
    [JsonPropertyName("description")]
    public string? Description { get; set; }
}

/// <summary>
/// 心跳请求
/// </summary>
public class HeartbeatRequest
{
    /// <summary>
    /// 服务器ID
    /// </summary>
    [JsonPropertyName("server_id")]
    public int ServerId { get; set; }

    /// <summary>
    /// 当前在线人数
    /// </summary>
    [JsonPropertyName("current_players")]
    public int CurrentPlayers { get; set; }

    /// <summary>
    /// 最大玩家数
    /// </summary>
    [JsonPropertyName("max_players")]
    public int MaxPlayers { get; set; }

    /// <summary>
    /// 服务器TPS
    /// </summary>
    [JsonPropertyName("tps")]
    public double? TPS { get; set; }

    /// <summary>
    /// 服务器版本
    /// </summary>
    [JsonPropertyName("version")]
    public string? Version { get; set; }

    /// <summary>
    /// 服务器描述
    /// </summary>
    [JsonPropertyName("motd")]
    public string? MOTD { get; set; }

    /// <summary>
    /// RTT延迟(毫秒)
    /// </summary>
    [JsonPropertyName("rtt_ms")]
    public long? RttMs { get; set; }
}

/// <summary>
/// 创建城镇请求
/// </summary>
public class CreateTownRequest
{
    /// <summary>
    /// 城镇名称
    /// </summary>
    [JsonPropertyName("name")]
    public string? Name { get; set; }

    /// <summary>
    /// 城镇等级
    /// </summary>
    [JsonPropertyName("level")]
    public int? Level { get; set; }

    /// <summary>
    /// 城主ID
    /// </summary>
    [JsonPropertyName("leader_id")]
    public int? LeaderId { get; set; }

    /// <summary>
    /// QQ群号
    /// </summary>
    [JsonPropertyName("qq_group")]
    public string? QQGroup { get; set; }

    /// <summary>
    /// 城镇描述
    /// </summary>
    [JsonPropertyName("description")]
    public string? Description { get; set; }
}

/// <summary>
/// 更新城镇请求
/// </summary>
public class UpdateTownRequest
{
    /// <summary>
    /// 城镇名称
    /// </summary>
    [JsonPropertyName("name")]
    public string? Name { get; set; }

    /// <summary>
    /// 城镇等级
    /// </summary>
    [JsonPropertyName("level")]
    public int? Level { get; set; }

    /// <summary>
    /// 城主ID
    /// </summary>
    [JsonPropertyName("leader_id")]
    public int? LeaderId { get; set; }

    /// <summary>
    /// QQ群号
    /// </summary>
    [JsonPropertyName("qq_group")]
    public string? QQGroup { get; set; }

    /// <summary>
    /// 城镇描述
    /// </summary>
    [JsonPropertyName("description")]
    public string? Description { get; set; }

    /// <summary>
    /// 待添加的玩家ID列表
    /// </summary>
    [JsonPropertyName("add_players")]
    public List<int>? AddPlayers { get; set; }

    /// <summary>
    /// 待移除的玩家ID列表
    /// </summary>
    [JsonPropertyName("remove_players")]
    public List<int>? RemovePlayers { get; set; }
}

/// <summary>
/// 创建API Token请求
/// </summary>
public class CreateApiTokenRequest
{
    /// <summary>
    /// Token名称
    /// </summary>
    [JsonPropertyName("name")]
    public string? Name { get; set; }

    /// <summary>
    /// Token角色
    /// </summary>
    [JsonPropertyName("role")]
    public string? Role { get; set; }

    /// <summary>
    /// Token描述
    /// </summary>
    [JsonPropertyName("description")]
    public string? Description { get; set; }

    /// <summary>
    /// 过期天数
    /// </summary>
    [JsonPropertyName("expire_days")]
    public long? ExpireDays { get; set; }
}

/// <summary>
/// 更新API Token请求
/// </summary>
public class UpdateApiTokenRequest
{
    /// <summary>
    /// Token名称
    /// </summary>
    [JsonPropertyName("name")]
    public string? Name { get; set; }

    /// <summary>
    /// Token角色
    /// </summary>
    [JsonPropertyName("role")]
    public string? Role { get; set; }

    /// <summary>
    /// Token描述
    /// </summary>
    [JsonPropertyName("description")]
    public string? Description { get; set; }

    /// <summary>
    /// 是否激活
    /// </summary>
    [JsonPropertyName("active")]
    public bool? Active { get; set; }
}

/// <summary>
/// 城镇列表请求
/// </summary>
public class ListTownsRequest
{
    /// <summary>
    /// 页码
    /// </summary>
    [JsonPropertyName("page")]
    public int Page { get; set; } = 1;

    /// <summary>
    /// 每页数量
    /// </summary>
    [JsonPropertyName("page_size")]
    public int PageSize { get; set; } = 20;

    /// <summary>
    /// 城镇名称
    /// </summary>
    [JsonPropertyName("name")]
    public string? Name { get; set; }

    /// <summary>
    /// 搜索关键词
    /// </summary>
    [JsonPropertyName("search")]
    public string? Search { get; set; }

    /// <summary>
    /// 最小等级
    /// </summary>
    [JsonPropertyName("min_level")]
    public int? MinLevel { get; set; }

    /// <summary>
    /// 最大等级
    /// </summary>
    [JsonPropertyName("max_level")]
    public int? MaxLevel { get; set; }
}

/// <summary>
/// 设置玩家离线请求
/// </summary>
public class SetPlayersOfflineRequest
{
    /// <summary>
    /// 服务器ID
    /// </summary>
    [JsonPropertyName("server_id")]
    public int ServerId { get; set; }

    /// <summary>
    /// 玩家ID列表（1-1000个）
    /// </summary>
    [JsonPropertyName("player_ids")]
    public List<int> PlayerIds { get; set; } = new();
}

/// <summary>
/// 玩家验证请求（支持批处理）
/// </summary>
public class ValidateRequest
{
    /// <summary>
    /// 玩家列表（1-100个）
    /// </summary>
    [JsonPropertyName("players")]
    public List<PlayerValidateInfo> Players { get; set; } = new();

    /// <summary>
    /// 服务器ID
    /// </summary>
    [JsonPropertyName("server_id")]
    public int ServerId { get; set; }

    /// <summary>
    /// 是否为登录验证
    /// </summary>
    [JsonPropertyName("login")]
    public bool Login { get; set; }
}
