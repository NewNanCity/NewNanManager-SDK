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
    /// 兼容旧调用方的本地属性，服务端不提供该字段。
    /// </summary>
    [Obsolete("The API does not define server_type; this compatibility property is not serialized.")]
    [JsonIgnore]
    public ServerType ServerType { get; set; }

    /// <summary>
    /// 是否激活
    /// </summary>
    [JsonPropertyName("active")]
    public bool Active { get; set; }

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

    /// <summary>采样来源：push/pull/unknown；缺失时未知。</summary>
    [JsonPropertyName("measurement_type")]
    public string? MeasurementType { get; set; }

    /// <summary>延迟口径：rtt/legacy；缺失时未知。</summary>
    [JsonPropertyName("latency_metric")]
    public string? LatencyMetric { get; set; }
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
/// 心跳响应数据
/// </summary>
public class HeartbeatData
{
    /// <summary>
    /// 服务端接收时间戳(毫秒)
    /// </summary>
    [JsonPropertyName("received_at")]
    public long ReceivedAt { get; set; }

    /// <summary>
    /// 服务端响应时间戳(毫秒)
    /// </summary>
    [JsonPropertyName("response_at")]
    public long ResponseAt { get; set; }

    /// <summary>
    /// 状态过期时间(毫秒)
    /// </summary>
    [JsonPropertyName("expire_duration_ms")]
    public long ExpireDurationMs { get; set; }
}

/// <summary>
/// 监控统计记录
/// </summary>
public class MonitorStatRecord
{
    /// <summary>
    /// 统计时间戳
    /// </summary>
    [JsonPropertyName("timestamp")]
    public long Timestamp { get; set; }

    /// <summary>
    /// 当前在线人数
    /// </summary>
    [JsonPropertyName("current_players")]
    public int CurrentPlayers { get; set; }

    /// <summary>
    /// 服务器TPS
    /// </summary>
    [JsonPropertyName("tps")]
    public double? TPS { get; set; }

    /// <summary>
    /// 延迟毫秒
    /// </summary>
    [JsonPropertyName("latency_ms")]
    public long? LatencyMs { get; set; }

    /// <summary>采样来源：push/pull/unknown；缺失时未知。</summary>
    [JsonPropertyName("measurement_type")]
    public string? MeasurementType { get; set; }

    /// <summary>延迟口径：rtt/legacy；缺失时未知。</summary>
    [JsonPropertyName("latency_metric")]
    public string? LatencyMetric { get; set; }
}

/// <summary>
/// 监控统计数据
/// </summary>
public class MonitorStatsData
{
    /// <summary>
    /// 服务器ID
    /// </summary>
    [JsonPropertyName("server_id")]
    public int ServerId { get; set; }

    /// <summary>
    /// 监控统计信息列表
    /// </summary>
    [JsonPropertyName("stats")]
    public List<MonitorStatRecord> Stats { get; set; } = new();

    /// <summary>下一页不透明游标；缺失表示结束。</summary>
    [JsonPropertyName("next_cursor")]
    public string? NextCursor { get; set; }
}

/// <summary>单页监控查询；续页保留相同服务器和时间范围。</summary>
public class MonitorStatsQuery
{
    /// <summary>起始Unix时间戳；0表示当前时间减Duration。</summary>
    [JsonPropertyName("since")]
    public long? Since { get; set; }

    /// <summary>持续秒数，最多86400；服务端默认3600。</summary>
    [JsonPropertyName("duration")]
    public long? Duration { get; set; }

    /// <summary>每页1..10000条；服务端默认1000。</summary>
    [JsonPropertyName("limit")]
    public int? Limit { get; set; }

    /// <summary>不透明游标，最多1024字符。</summary>
    [JsonPropertyName("cursor")]
    public string? Cursor { get; set; }
}
