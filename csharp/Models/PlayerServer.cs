using System.Text.Json.Serialization;

namespace NewNanManager.Client.Models;

/// <summary>
/// 玩家服务器关系信息
/// </summary>
public class PlayerServer
{
    /// <summary>
    /// 玩家ID
    /// </summary>
    [JsonPropertyName("player_id")]
    public int PlayerId { get; set; }

    /// <summary>
    /// 服务器ID
    /// </summary>
    [JsonPropertyName("server_id")]
    public int ServerId { get; set; }

    /// <summary>
    /// 是否在线
    /// </summary>
    [JsonPropertyName("online")]
    public bool Online { get; set; }

    /// <summary>
    /// 加入时间(ISO8601格式)
    /// </summary>
    [JsonPropertyName("joined_at")]
    public DateTime JoinedAt { get; set; }

    /// <summary>
    /// 创建时间(ISO8601格式)
    /// </summary>
    [JsonPropertyName("created_at")]
    public DateTime CreatedAt { get; set; }

    /// <summary>
    /// 更新时间(ISO8601格式)
    /// </summary>
    [JsonPropertyName("updated_at")]
    public DateTime UpdatedAt { get; set; }
}

/// <summary>
/// 在线玩家信息
/// </summary>
public class OnlinePlayer
{
    /// <summary>
    /// 玩家ID
    /// </summary>
    [JsonPropertyName("player_id")]
    public int PlayerId { get; set; }

    /// <summary>
    /// 玩家名
    /// </summary>
    [JsonPropertyName("player_name")]
    public string PlayerName { get; set; } = string.Empty;

    /// <summary>
    /// 服务器ID
    /// </summary>
    [JsonPropertyName("server_id")]
    public int ServerId { get; set; }

    /// <summary>
    /// 服务器名
    /// </summary>
    [JsonPropertyName("server_name")]
    public string ServerName { get; set; } = string.Empty;

    /// <summary>
    /// 加入时间(ISO8601格式)
    /// </summary>
    [JsonPropertyName("joined_at")]
    public DateTime JoinedAt { get; set; }
}

/// <summary>
/// 玩家服务器关系列表数据
/// </summary>
public class PlayerServersData
{
    /// <summary>
    /// 服务器关系列表
    /// </summary>
    [JsonPropertyName("servers")]
    public List<PlayerServer> Servers { get; set; } = new();

    /// <summary>
    /// 总数
    /// </summary>
    [JsonPropertyName("total")]
    public long Total { get; set; }
}

/// <summary>
/// 服务器在线玩家列表数据
/// </summary>
public class ServerPlayersData : PagedData<OnlinePlayer>
{
    /// <summary>
    /// 在线玩家列表
    /// </summary>
    [JsonPropertyName("players")]
    public List<OnlinePlayer> Players
    {
        get => Items;
        set => Items = value;
    }
}
