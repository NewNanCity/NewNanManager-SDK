using Microsoft.Extensions.Logging;
using NewNanManager.Client.Http;
using NewNanManager.Client.Models;

namespace NewNanManager.Client.Services;

/// <summary>
/// 监控服务
/// </summary>
public class MonitorService : HttpClientBase
{
    public MonitorService(HttpClient httpClient, ILogger? logger = null) : base(httpClient, logger)
    {
    }

    /// <summary>
    /// 发送服务器心跳
    /// </summary>
    /// <param name="serverId">服务器ID</param>
    /// <param name="request">心跳请求</param>
    /// <param name="cancellationToken">取消令牌</param>
    /// <returns>心跳响应数据</returns>
    public async Task<HeartbeatData> HeartbeatAsync(int serverId, HeartbeatRequest request, CancellationToken cancellationToken = default)
    {
        return await PostAsync<HeartbeatData>($"/api/v1/servers/{serverId}/heartbeat", request, cancellationToken);
    }

    /// <summary>
    /// 获取延迟统计
    /// </summary>
    /// <param name="serverId">服务器ID</param>
    /// <param name="cancellationToken">取消令牌</param>
    /// <returns>延迟统计数据</returns>
    public async Task<LatencyStatsData> GetLatencyStatsAsync(int serverId, CancellationToken cancellationToken = default)
    {
        return await GetAsync<LatencyStatsData>($"/api/v1/servers/{serverId}/latency", cancellationToken);
    }

    /// <summary>
    /// 获取服务器状态
    /// </summary>
    /// <param name="serverId">服务器ID</param>
    /// <param name="cancellationToken">取消令牌</param>
    /// <returns>服务器状态</returns>
    public async Task<ServerStatus> GetServerStatusAsync(int serverId, CancellationToken cancellationToken = default)
    {
        return await GetAsync<ServerStatus>($"/api/v1/servers/{serverId}/status", cancellationToken);
    }
}
