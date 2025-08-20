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
