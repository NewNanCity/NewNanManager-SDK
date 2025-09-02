using System.Text.Json.Serialization;

namespace NewNanManager.Client.Models;

/// <summary>
/// 玩家实体
/// </summary>
public class Player
{
    /// <summary>
    /// 玩家ID
    /// </summary>
    [JsonPropertyName("id")]
    public int Id { get; set; }

    /// <summary>
    /// 玩家名称
    /// </summary>
    [JsonPropertyName("name")]
    public string Name { get; set; } = string.Empty;

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
    public bool InQQGroup { get; set; }

    /// <summary>
    /// 是否在QQ频道中
    /// </summary>
    [JsonPropertyName("in_qq_guild")]
    public bool InQQGuild { get; set; }

    /// <summary>
    /// 是否在Discord中
    /// </summary>
    [JsonPropertyName("in_discord")]
    public bool InDiscord { get; set; }

    /// <summary>
    /// 封禁模式
    /// </summary>
    [JsonPropertyName("ban_mode")]
    public BanMode BanMode { get; set; }

    /// <summary>
    /// 封禁过期时间
    /// </summary>
    [JsonPropertyName("ban_expire")]
    public DateTime? BanExpire { get; set; }

    /// <summary>
    /// 封禁原因
    /// </summary>
    [JsonPropertyName("ban_reason")]
    public string? BanReason { get; set; }

    /// <summary>
    /// 创建时间
    /// </summary>
    [JsonPropertyName("created_at")]
    public DateTime CreatedAt { get; set; }

    /// <summary>
    /// 更新时间
    /// </summary>
    [JsonPropertyName("updated_at")]
    public DateTime UpdatedAt { get; set; }
}

/// <summary>
/// 玩家列表数据
/// </summary>
public class PlayersListData : PagedData<Player>
{
    /// <summary>
    /// 玩家列表
    /// </summary>
    [JsonPropertyName("players")]
    public List<Player> Players
    {
        get => Items;
        set => Items = value;
    }
}

/// <summary>
/// 玩家登录信息
/// </summary>
public class PlayerLoginInfo
{
    /// <summary>
    /// 玩家ID
    /// </summary>
    [JsonPropertyName("player_id")]
    public int? PlayerId { get; set; }

    /// <summary>
    /// 玩家名称
    /// </summary>
    [JsonPropertyName("name")]
    public string? Name { get; set; }

    /// <summary>
    /// IP地址
    /// </summary>
    [JsonPropertyName("ip")]
    public string? IP { get; set; }
}

/// <summary>
/// 登录验证数据
/// </summary>
public class ValidateLoginData
{
    /// <summary>
    /// 是否允许登录
    /// </summary>
    [JsonPropertyName("allowed")]
    public bool Allowed { get; set; }

    /// <summary>
    /// 玩家ID
    /// </summary>
    [JsonPropertyName("player_id")]
    public int? PlayerId { get; set; }

    /// <summary>
    /// 拒绝原因
    /// </summary>
    [JsonPropertyName("reason")]
    public string? Reason { get; set; }
}

/// <summary>
/// 单个玩家验证信息
/// </summary>
public class PlayerValidateInfo
{
    /// <summary>
    /// 玩家名
    /// </summary>
    [JsonPropertyName("player_name")]
    public string PlayerName { get; set; } = string.Empty;

    /// <summary>
    /// IP地址
    /// </summary>
    [JsonPropertyName("ip")]
    public string IP { get; set; } = string.Empty;

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
/// IP风险信息
/// </summary>
public class IPRiskInfo
{
    /// <summary>
    /// IP地址
    /// </summary>
    [JsonPropertyName("ip")]
    public string IP { get; set; } = string.Empty;

    /// <summary>
    /// 风险等级
    /// </summary>
    [JsonPropertyName("risk_level")]
    public string RiskLevel { get; set; } = string.Empty;

    /// <summary>
    /// 风险评分
    /// </summary>
    [JsonPropertyName("risk_score")]
    public int RiskScore { get; set; }

    /// <summary>
    /// 是否被封禁
    /// </summary>
    [JsonPropertyName("banned")]
    public bool Banned { get; set; }

    /// <summary>
    /// 风险描述
    /// </summary>
    [JsonPropertyName("description")]
    public string Description { get; set; } = string.Empty;
}

/// <summary>
/// 单个玩家验证结果
/// </summary>
public class PlayerValidateResult
{
    /// <summary>
    /// 玩家名
    /// </summary>
    [JsonPropertyName("player_name")]
    public string PlayerName { get; set; } = string.Empty;

    /// <summary>
    /// 是否允许登录
    /// </summary>
    [JsonPropertyName("allowed")]
    public bool Allowed { get; set; }

    /// <summary>
    /// 玩家ID
    /// </summary>
    [JsonPropertyName("player_id")]
    public int? PlayerId { get; set; }

    /// <summary>
    /// 拒绝原因
    /// </summary>
    [JsonPropertyName("reason")]
    public string? Reason { get; set; }

    /// <summary>
    /// 是否为新玩家
    /// </summary>
    [JsonPropertyName("newbie")]
    public bool Newbie { get; set; } = false;

    /// <summary>
    /// IP风险信息
    /// </summary>
    [JsonPropertyName("ip_risk")]
    public IPRiskInfo? IPRisk { get; set; }
}

/// <summary>
/// 玩家验证响应
/// </summary>
public class ValidateResponse
{
    /// <summary>
    /// 验证结果列表
    /// </summary>
    [JsonPropertyName("results")]
    public List<PlayerValidateResult> Results { get; set; } = new();

    /// <summary>
    /// 处理时间戳
    /// </summary>
    [JsonPropertyName("processed_at")]
    public long ProcessedAt { get; set; }
}
