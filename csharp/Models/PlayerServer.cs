using System.Text.Json.Serialization;

namespace NewNanManager.Client.Models;

/// <summary>
/// 玩家服务器列表数据
/// </summary>
public class PlayerServersData : PaginatedData<ServerRegistry> { }

/// <summary>
/// 服务器玩家列表数据
/// </summary>
public class ServerPlayersData : PaginatedData<Player> { }

/// <summary>
/// 在线玩家列表数据
/// </summary>
public class OnlinePlayersData : PaginatedData<Player> { }

/// <summary>
/// 封禁IP请求
/// </summary>
public class BanIPRequest
{
    /// <summary>
    /// IP地址
    /// </summary>
    [JsonPropertyName("ip")]
    public string IP { get; set; } = string.Empty;

    /// <summary>
    /// 封禁原因
    /// </summary>
    [JsonPropertyName("reason")]
    public string? Reason { get; set; }
}

/// <summary>
/// 批量封禁IP请求
/// </summary>
public class BatchBanIPsRequest
{
    /// <summary>
    /// IP地址列表
    /// </summary>
    [JsonPropertyName("ips")]
    public List<string> IPs { get; set; } = new();

    /// <summary>
    /// 封禁原因
    /// </summary>
    [JsonPropertyName("reason")]
    public string? Reason { get; set; }
}

/// <summary>
/// 批量封禁IP响应
/// </summary>
public class BatchBanIPsResponse
{
    /// <summary>
    /// 成功封禁的IP列表
    /// </summary>
    [JsonPropertyName("success")]
    public List<string> Success { get; set; } = new();

    /// <summary>
    /// 封禁失败的IP列表
    /// </summary>
    [JsonPropertyName("failed")]
    public List<string> Failed { get; set; } = new();

    /// <summary>
    /// 总数
    /// </summary>
    [JsonPropertyName("total")]
    public int Total { get; set; }
}
