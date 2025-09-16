using Microsoft.Extensions.Logging;
using NewNanManager.Client.Http;
using NewNanManager.Client.Models;

namespace NewNanManager.Client.Services;

/// <summary>
/// 监控服务
/// </summary>
public class MonitorService : HttpClientBase
{
    public MonitorService(HttpClient httpClient, ILogger? logger = null)
        : base(httpClient, logger) { }

    /// <summary>
    /// 发送服务器心跳
    /// </summary>
    /// <param name="serverId">服务器ID</param>
    /// <param name="request">心跳请求</param>
    /// <param name="cancellationToken">取消令牌</param>
    /// <returns>心跳响应数据</returns>
    public async Task<HeartbeatData> HeartbeatAsync(
        int serverId,
        HeartbeatRequest request,
        CancellationToken cancellationToken = default
    )
    {
        return await PostAsync<HeartbeatData>(
            $"/api/v1/monitor/{serverId}/heartbeat",
            request,
            cancellationToken
        );
    }

    /// <summary>
    /// 获取监控统计信息
    /// </summary>
    /// <param name="serverId">服务器ID</param>
    /// <param name="since">起始时间戳(Unix时间戳，0表示当前时间-duration)</param>
    /// <param name="duration">持续时间(秒，默认3600秒)</param>
    /// <param name="cancellationToken">取消令牌</param>
    /// <returns>监控统计数据</returns>
    public async Task<MonitorStatsData> GetMonitorStatsAsync(
        int serverId,
        long? since = null,
        long? duration = null,
        CancellationToken cancellationToken = default
    )
    {
        var queryParams = new Dictionary<string, string>();
        if (since.HasValue)
            queryParams["since"] = since.Value.ToString();
        if (duration.HasValue)
            queryParams["duration"] = duration.Value.ToString();

        var queryString =
            queryParams.Count > 0
                ? "?" + string.Join("&", queryParams.Select(kvp => $"{kvp.Key}={kvp.Value}"))
                : string.Empty;

        return await GetAsync<MonitorStatsData>(
            $"/api/v1/monitor/{serverId}/stats{queryString}",
            cancellationToken
        );
    }
}
