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

    /// <summary>发送携带服务器会话 fencing 头的心跳。</summary>
    public async Task<HeartbeatData> HeartbeatAsync(
        int serverId,
        HeartbeatRequest request,
        SessionContext session,
        CancellationToken cancellationToken = default
    )
    {
        ArgumentNullException.ThrowIfNull(session);
        return await PostAsync<HeartbeatData>(
            $"/api/v1/monitor/{serverId}/heartbeat",
            request,
            cancellationToken,
            session.Headers
        );
    }

    /// <summary>签发服务器实例会话。</summary>
    public async Task<SessionContext> CreateServerSessionAsync(
        int serverId,
        CancellationToken cancellationToken = default
    )
    {
        var response = await PostAsync<ServerSessionResponse>(
            $"/api/v1/monitor/{serverId}/session",
            new { },
            cancellationToken: cancellationToken
        );
        return response.ToContext();
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
        return await GetMonitorStatsPageAsync(serverId, new MonitorStatsQuery { Since = since, Duration = duration }, cancellationToken);
    }

    /// <summary>获取一页监控记录；NextCursor存在时可继续请求下一页。</summary>
    public async Task<MonitorStatsData> GetMonitorStatsPageAsync(
        int serverId,
        MonitorStatsQuery query,
        CancellationToken cancellationToken = default
    )
    {
        ArgumentNullException.ThrowIfNull(query);
        var queryString = BuildQueryString(new Dictionary<string, object?>
        {
            ["since"] = query.Since,
            ["duration"] = query.Duration,
            ["limit"] = query.Limit,
            ["cursor"] = query.Cursor,
        });

        return await GetAsync<MonitorStatsData>(
            $"/api/v1/monitor/{serverId}/stats{queryString}",
            cancellationToken
        );
    }
}
