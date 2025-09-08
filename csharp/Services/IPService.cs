using System.Text.Json;
using Microsoft.Extensions.Logging;
using NewNanManager.Client.Http;
using NewNanManager.Client.Models;

namespace NewNanManager.Client.Services;

/// <summary>
/// IP管理服务
/// </summary>
public class IPService : HttpClientBase
{
    public IPService(HttpClient httpClient, ILogger? logger = null)
        : base(httpClient, logger) { }

    /// <summary>
    /// 获取IP信息（包含风险信息）
    /// </summary>
    /// <param name="ip">IP地址</param>
    /// <param name="cancellationToken">取消令牌</param>
    /// <returns>IP信息（包含风险信息）</returns>
    public async Task<IPInfo> GetIPInfoAsync(
        string ip,
        CancellationToken cancellationToken = default
    )
    {
        return await GetAsync<IPInfo>($"/api/v1/ips/{ip}", cancellationToken);
    }

    /// <summary>
    /// 封禁IP（支持批量）
    /// </summary>
    /// <param name="request">封禁IP请求</param>
    /// <param name="cancellationToken">取消令牌</param>
    public async Task BanIPAsync(
        BanIPRequest request,
        CancellationToken cancellationToken = default
    )
    {
        await PostAsync("/api/v1/ips/ban", request, cancellationToken);
    }

    /// <summary>
    /// 解封IP（支持批量）
    /// </summary>
    /// <param name="request">解封IP请求</param>
    /// <param name="cancellationToken">取消令牌</param>
    public async Task UnbanIPAsync(
        UnbanIPRequest request,
        CancellationToken cancellationToken = default
    )
    {
        await PostAsync("/api/v1/ips/unban", request, cancellationToken);
    }

    /// <summary>
    /// 获取被封禁的IP列表
    /// </summary>
    /// <param name="page">页码</param>
    /// <param name="pageSize">每页大小</param>
    /// <param name="cancellationToken">取消令牌</param>
    /// <returns>被封禁的IP列表</returns>
    public async Task<BannedIPsData> GetBannedIPsAsync(
        int? page = null,
        int? pageSize = null,
        CancellationToken cancellationToken = default
    )
    {
        var queryParams = new Dictionary<string, object?>
        {
            ["page"] = page,
            ["page_size"] = pageSize,
        };

        var queryString = BuildQueryString(queryParams);
        return await GetAsync<BannedIPsData>($"/api/v1/ips/banned{queryString}", cancellationToken);
    }

    /// <summary>
    /// 获取IP列表
    /// </summary>
    /// <param name="page">页码</param>
    /// <param name="pageSize">每页大小</param>
    /// <param name="bannedOnly">仅显示被封禁的IP</param>
    /// <param name="minThreatLevel">最小威胁等级</param>
    /// <param name="minRiskScore">最小风险评分</param>
    /// <param name="cancellationToken">取消令牌</param>
    /// <returns>IP列表</returns>
    public async Task<IPsListData> GetIPsAsync(
        int? page = null,
        int? pageSize = null,
        bool? bannedOnly = null,
        ThreatLevel? minThreatLevel = null,
        int? minRiskScore = null,
        CancellationToken cancellationToken = default
    )
    {
        var queryParams = new Dictionary<string, object?>
        {
            ["page"] = page,
            ["page_size"] = pageSize,
            ["banned_only"] = bannedOnly,
            ["min_threat_level"] = minThreatLevel,
            ["min_risk_score"] = minRiskScore,
        };

        var queryString = BuildQueryString(queryParams);
        return await GetAsync<IPsListData>($"/api/v1/ips{queryString}", cancellationToken);
    }

    /// <summary>
    /// 获取可疑IP列表
    /// </summary>
    /// <param name="page">页码</param>
    /// <param name="pageSize">每页大小</param>
    /// <param name="cancellationToken">取消令牌</param>
    /// <returns>可疑IP列表</returns>
    public async Task<SuspiciousIPsData> GetSuspiciousIPsAsync(
        int? page = null,
        int? pageSize = null,
        CancellationToken cancellationToken = default
    )
    {
        var queryParams = new Dictionary<string, object?>
        {
            ["page"] = page,
            ["page_size"] = pageSize,
        };

        var queryString = BuildQueryString(queryParams);
        return await GetAsync<SuspiciousIPsData>(
            $"/api/v1/ips/suspicious{queryString}",
            cancellationToken
        );
    }

    /// <summary>
    /// 获取高风险IP列表
    /// </summary>
    /// <param name="page">页码</param>
    /// <param name="pageSize">每页大小</param>
    /// <param name="cancellationToken">取消令牌</param>
    /// <returns>高风险IP列表</returns>
    public async Task<HighRiskIPsData> GetHighRiskIPsAsync(
        int? page = null,
        int? pageSize = null,
        CancellationToken cancellationToken = default
    )
    {
        var queryParams = new Dictionary<string, object?>
        {
            ["page"] = page,
            ["page_size"] = pageSize,
        };

        var queryString = BuildQueryString(queryParams);
        return await GetAsync<HighRiskIPsData>(
            $"/api/v1/ips/high-risk{queryString}",
            cancellationToken
        );
    }

    /// <summary>
    /// 获取IP统计信息
    /// </summary>
    /// <param name="cancellationToken">取消令牌</param>
    /// <returns>IP统计信息</returns>
    public async Task<IPStatistics> GetIPStatisticsAsync(
        CancellationToken cancellationToken = default
    )
    {
        return await GetAsync<IPStatistics>("/api/v1/ips/statistics", cancellationToken);
    }
}
