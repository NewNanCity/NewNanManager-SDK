using System.Text.Json.Serialization;

namespace NewNanManager.Client.Models;

/// <summary>
/// 服务器注册信息
/// </summary>
public class ServerRegistry
{
    /// <summary>
    /// 服务器ID
    /// </summary>
    [JsonPropertyName("id")]
    public int Id { get; set; }

    /// <summary>
    /// 服务器名称
    /// </summary>
    [JsonPropertyName("name")]
    public string Name { get; set; } = string.Empty;

    /// <summary>
    /// 服务器地址
    /// </summary>
    [JsonPropertyName("address")]
    public string Address { get; set; } = string.Empty;

    /// <summary>
    /// 服务器类型
    /// </summary>
    [JsonPropertyName("server_type")]
    public ServerType ServerType { get; set; }

    /// <summary>
    /// 服务器描述
    /// </summary>
    [JsonPropertyName("description")]
    public string? Description { get; set; }

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
/// 服务器状态
/// </summary>
public class ServerStatus
{
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
    /// 当前玩家数
    /// </summary>
    [JsonPropertyName("current_players")]
    public int CurrentPlayers { get; set; }

    /// <summary>
    /// 最大玩家数
    /// </summary>
    [JsonPropertyName("max_players")]
    public int MaxPlayers { get; set; }

    /// <summary>
    /// 延迟（毫秒）
    /// </summary>
    [JsonPropertyName("latency_ms")]
    public int? LatencyMs { get; set; }

    /// <summary>
    /// TPS（每秒tick数）
    /// </summary>
    [JsonPropertyName("tps")]
    public double? TPS { get; set; }

    /// <summary>
    /// 服务器版本
    /// </summary>
    [JsonPropertyName("version")]
    public string? Version { get; set; }

    /// <summary>
    /// 服务器MOTD
    /// </summary>
    [JsonPropertyName("motd")]
    public string? MOTD { get; set; }

    /// <summary>
    /// 状态过期时间
    /// </summary>
    [JsonPropertyName("expire_at")]
    public DateTime ExpireAt { get; set; }

    /// <summary>
    /// 最后心跳时间
    /// </summary>
    [JsonPropertyName("last_heartbeat")]
    public DateTime LastHeartbeat { get; set; }
}

/// <summary>
/// 服务器列表数据
/// </summary>
public class ServersListData : PagedData<ServerRegistry>
{
    /// <summary>
    /// 服务器列表
    /// </summary>
    [JsonPropertyName("servers")]
    public List<ServerRegistry> Servers
    {
        get => Items;
        set => Items = value;
    }
}

/// <summary>
/// 服务器详细信息数据
/// </summary>
public class ServerDetailData
{
    /// <summary>
    /// 服务器信息
    /// </summary>
    [JsonPropertyName("server")]
    public ServerRegistry Server { get; set; } = new();

    /// <summary>
    /// 服务器状态
    /// </summary>
    [JsonPropertyName("status")]
    public ServerStatus? Status { get; set; }
}

/// <summary>
/// 延迟统计数据
/// </summary>
public class LatencyStatsData
{
    /// <summary>
    /// 服务器ID
    /// </summary>
    [JsonPropertyName("server_id")]
    public int ServerId { get; set; }

    /// <summary>
    /// 统计数量
    /// </summary>
    [JsonPropertyName("count")]
    public long Count { get; set; }

    /// <summary>
    /// 当前延迟
    /// </summary>
    [JsonPropertyName("current")]
    public long Current { get; set; }

    /// <summary>
    /// 平均延迟
    /// </summary>
    [JsonPropertyName("average")]
    public long Average { get; set; }

    /// <summary>
    /// 最小延迟
    /// </summary>
    [JsonPropertyName("min")]
    public long Min { get; set; }

    /// <summary>
    /// 最大延迟
    /// </summary>
    [JsonPropertyName("max")]
    public long Max { get; set; }

    /// <summary>
    /// 方差
    /// </summary>
    [JsonPropertyName("variance")]
    public double Variance { get; set; }

    /// <summary>
    /// 最后更新时间
    /// </summary>
    [JsonPropertyName("last_updated")]
    public DateTime LastUpdated { get; set; }
}

/// <summary>
/// 心跳响应数据
/// </summary>
public class HeartbeatData
{
    /// <summary>
    /// 接收时间戳
    /// </summary>
    [JsonPropertyName("received_at")]
    public long ReceivedAt { get; set; }

    /// <summary>
    /// 响应时间戳
    /// </summary>
    [JsonPropertyName("response_at")]
    public long ResponseAt { get; set; }

    /// <summary>
    /// 序列ID
    /// </summary>
    [JsonPropertyName("sequence_id")]
    public long SequenceId { get; set; }

    /// <summary>
    /// 服务器时间
    /// </summary>
    [JsonPropertyName("server_time")]
    public long ServerTime { get; set; }

    /// <summary>
    /// 状态
    /// </summary>
    [JsonPropertyName("status")]
    public string Status { get; set; } = string.Empty;

    /// <summary>
    /// 下次心跳时间
    /// </summary>
    [JsonPropertyName("next_heartbeat")]
    public long NextHeartbeat { get; set; }

    /// <summary>
    /// 过期时间
    /// </summary>
    [JsonPropertyName("expire_at")]
    public long ExpireAt { get; set; }
}
