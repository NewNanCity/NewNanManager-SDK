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
    /// IP类型
    /// </summary>
    [JsonPropertyName("ip_type")]
    public string IPType { get; set; } = string.Empty;

    /// <summary>
    /// 国家
    /// </summary>
    [JsonPropertyName("country")]
    public string? Country { get; set; }

    /// <summary>
    /// 国家代码
    /// </summary>
    [JsonPropertyName("country_code")]
    public string? CountryCode { get; set; }

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
    /// 纬度
    /// </summary>
    [JsonPropertyName("latitude")]
    public double? Latitude { get; set; }

    /// <summary>
    /// 经度
    /// </summary>
    [JsonPropertyName("longitude")]
    public double? Longitude { get; set; }

    /// <summary>
    /// 时区
    /// </summary>
    [JsonPropertyName("timezone")]
    public string? Timezone { get; set; }

    /// <summary>
    /// ISP
    /// </summary>
    [JsonPropertyName("isp")]
    public string? ISP { get; set; }

    /// <summary>
    /// 组织名称
    /// </summary>
    [JsonPropertyName("organization")]
    public string? Organization { get; set; }

    /// <summary>
    /// ASN号码
    /// </summary>
    [JsonPropertyName("asn")]
    public string? ASN { get; set; }

    /// <summary>
    /// 是否为Bogon IP
    /// </summary>
    [JsonPropertyName("is_bogon")]
    public bool IsBogon { get; set; }

    /// <summary>
    /// 是否为移动网络
    /// </summary>
    [JsonPropertyName("is_mobile")]
    public bool IsMobile { get; set; }

    /// <summary>
    /// 是否为卫星网络
    /// </summary>
    [JsonPropertyName("is_satellite")]
    public bool IsSatellite { get; set; }

    /// <summary>
    /// 是否为爬虫
    /// </summary>
    [JsonPropertyName("is_crawler")]
    public bool IsCrawler { get; set; }

    /// <summary>
    /// 是否为数据中心IP
    /// </summary>
    [JsonPropertyName("is_datacenter")]
    public bool IsDatacenter { get; set; }

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
    /// 是否为滥用者
    /// </summary>
    [JsonPropertyName("is_abuser")]
    public bool IsAbuser { get; set; }

    /// <summary>
    /// 是否被封禁
    /// </summary>
    [JsonPropertyName("banned")]
    public bool Banned { get; set; }

    /// <summary>
    /// 封禁原因
    /// </summary>
    [JsonPropertyName("ban_reason")]
    public string? BanReason { get; set; }

    /// <summary>
    /// 威胁等级
    /// </summary>
    [JsonPropertyName("threat_level")]
    public ThreatLevel ThreatLevel { get; set; }

    /// <summary>
    /// 风险评分（0-100）
    /// </summary>
    [JsonPropertyName("risk_score")]
    public int RiskScore { get; set; }

    /// <summary>
    /// 查询状态
    /// </summary>
    [JsonPropertyName("query_status")]
    public QueryStatus QueryStatus { get; set; }

    /// <summary>
    /// 最后查询时间
    /// </summary>
    [JsonPropertyName("last_query_at")]
    public string? LastQueryAt { get; set; }

    /// <summary>
    /// 创建时间
    /// </summary>
    [JsonPropertyName("created_at")]
    public string CreatedAt { get; set; } = string.Empty;

    /// <summary>
    /// 更新时间
    /// </summary>
    [JsonPropertyName("updated_at")]
    public string UpdatedAt { get; set; } = string.Empty;

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
    public long TotalIPs { get; set; }

    /// <summary>
    /// 已完成查询的IP数
    /// </summary>
    [JsonPropertyName("completed_ips")]
    public long CompletedIPs { get; set; }

    /// <summary>
    /// 待查询IP数
    /// </summary>
    [JsonPropertyName("pending_ips")]
    public long PendingIPs { get; set; }

    /// <summary>
    /// 查询失败IP数
    /// </summary>
    [JsonPropertyName("failed_ips")]
    public long FailedIPs { get; set; }

    /// <summary>
    /// 被封禁IP数量
    /// </summary>
    [JsonPropertyName("banned_ips")]
    public long BannedIPs { get; set; }

    /// <summary>
    /// 代理IP数量
    /// </summary>
    [JsonPropertyName("proxy_ips")]
    public long ProxyIPs { get; set; }

    /// <summary>
    /// VPN IP数量
    /// </summary>
    [JsonPropertyName("vpn_ips")]
    public long VPNIPs { get; set; }

    /// <summary>
    /// Tor IP数量
    /// </summary>
    [JsonPropertyName("tor_ips")]
    public long TorIPs { get; set; }

    /// <summary>
    /// 数据中心IP数量
    /// </summary>
    [JsonPropertyName("datacenter_ips")]
    public long DatacenterIPs { get; set; }

    /// <summary>
    /// 高风险IP数量
    /// </summary>
    [JsonPropertyName("high_risk_ips")]
    public long HighRiskIPs { get; set; }
}

/// <summary>
/// IP列表数据
/// </summary>
public class IPsListData : PagedData<IPInfo>
{
    /// <summary>
    /// IP列表
    /// </summary>
    [JsonPropertyName("ips")]
    public List<IPInfo> IPs
    {
        get => Items;
        set => Items = value;
    }
}

/// <summary>
/// 被封禁IP列表数据
/// </summary>
public class BannedIPsData : PagedData<IPInfo>
{
    /// <summary>
    /// IP列表
    /// </summary>
    [JsonPropertyName("ips")]
    public List<IPInfo> IPs
    {
        get => Items;
        set => Items = value;
    }
}

/// <summary>
/// 可疑IP列表数据
/// </summary>
public class SuspiciousIPsData : PagedData<IPInfo>
{
    /// <summary>
    /// IP列表
    /// </summary>
    [JsonPropertyName("ips")]
    public List<IPInfo> IPs
    {
        get => Items;
        set => Items = value;
    }
}

/// <summary>
/// 高风险IP列表数据
/// </summary>
public class HighRiskIPsData : PagedData<IPInfo>
{
    /// <summary>
    /// IP列表
    /// </summary>
    [JsonPropertyName("ips")]
    public List<IPInfo> IPs
    {
        get => Items;
        set => Items = value;
    }
}

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
