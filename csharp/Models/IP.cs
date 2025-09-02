using System.Text.Json.Serialization;

namespace NewNanManager.Client.Models;

/// <summary>
/// IP信息
/// </summary>
public class IPInfo
{
    /// <summary>
    /// IP地址
    /// </summary>
    [JsonPropertyName("ip")]
    public string IP { get; set; } = string.Empty;

    /// <summary>
    /// 国家
    /// </summary>
    [JsonPropertyName("country")]
    public string? Country { get; set; }

    /// <summary>
    /// 地区
    /// </summary>
    [JsonPropertyName("region")]
    public string? Region { get; set; }

    /// <summary>
    /// 城市
    /// </summary>
    [JsonPropertyName("city")]
    public string? City { get; set; }

    /// <summary>
    /// ISP
    /// </summary>
    [JsonPropertyName("isp")]
    public string? ISP { get; set; }

    /// <summary>
    /// 是否为代理
    /// </summary>
    [JsonPropertyName("is_proxy")]
    public bool IsProxy { get; set; }

    /// <summary>
    /// 是否为VPN
    /// </summary>
    [JsonPropertyName("is_vpn")]
    public bool IsVPN { get; set; }

    /// <summary>
    /// 是否为Tor
    /// </summary>
    [JsonPropertyName("is_tor")]
    public bool IsTor { get; set; }

    /// <summary>
    /// 威胁等级
    /// </summary>
    [JsonPropertyName("threat_level")]
    public string ThreatLevel { get; set; } = string.Empty;

    /// <summary>
    /// 是否被封禁
    /// </summary>
    [JsonPropertyName("is_banned")]
    public bool IsBanned { get; set; }

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

    /// <summary>
    /// 风险等级
    /// </summary>
    [JsonPropertyName("risk_level")]
    public string RiskLevel { get; set; } = string.Empty;

    /// <summary>
    /// 风险描述
    /// </summary>
    [JsonPropertyName("risk_description")]
    public string RiskDescription { get; set; } = string.Empty;
}

/// <summary>
/// IP统计信息
/// </summary>
public class IPStatistics
{
    /// <summary>
    /// 总IP数量
    /// </summary>
    [JsonPropertyName("total_ips")]
    public int TotalIPs { get; set; }

    /// <summary>
    /// 被封禁IP数量
    /// </summary>
    [JsonPropertyName("banned_ips")]
    public int BannedIPs { get; set; }

    /// <summary>
    /// 可疑IP数量
    /// </summary>
    [JsonPropertyName("suspicious_ips")]
    public int SuspiciousIPs { get; set; }

    /// <summary>
    /// 高风险IP数量
    /// </summary>
    [JsonPropertyName("high_risk_ips")]
    public int HighRiskIPs { get; set; }

    /// <summary>
    /// 代理IP数量
    /// </summary>
    [JsonPropertyName("proxy_ips")]
    public int ProxyIPs { get; set; }

    /// <summary>
    /// VPN IP数量
    /// </summary>
    [JsonPropertyName("vpn_ips")]
    public int VPNIPs { get; set; }

    /// <summary>
    /// Tor IP数量
    /// </summary>
    [JsonPropertyName("tor_ips")]
    public int TorIPs { get; set; }
}

/// <summary>
/// 被封禁IP列表数据
/// </summary>
public class BannedIPsData : PaginatedData<IPInfo> { }

/// <summary>
/// 可疑IP列表数据
/// </summary>
public class SuspiciousIPsData : PaginatedData<IPInfo> { }

/// <summary>
/// 高风险IP列表数据
/// </summary>
public class HighRiskIPsData : PaginatedData<IPInfo> { }

/// <summary>
/// 封禁IP请求
/// </summary>
public class BanIPRequest
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
    public string Reason { get; set; } = string.Empty;
}

/// <summary>
/// 解封IP请求
/// </summary>
public class UnbanIPRequest
{
    /// <summary>
    /// IP地址列表
    /// </summary>
    [JsonPropertyName("ips")]
    public List<string> IPs { get; set; } = new();
}
