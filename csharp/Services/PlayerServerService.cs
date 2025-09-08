using Microsoft.Extensions.Logging;
using NewNanManager.Client.Http;
using NewNanManager.Client.Models;

namespace NewNanManager.Client.Services;

/// <summary>
/// 玩家服务器关系管理服务
/// </summary>
public class PlayerServerService : HttpClientBase
{
    public PlayerServerService(HttpClient httpClient, ILogger? logger = null)
        : base(httpClient, logger) { }

    /// <summary>
    /// 获取玩家的服务器关系
    /// </summary>
    /// <param name="playerId">玩家ID</param>
    /// <param name="onlineOnly">是否只显示在线服务器</param>
    /// <param name="cancellationToken">取消令牌</param>
    /// <returns>玩家的服务器关系</returns>
    public async Task<PlayerServersData> GetPlayerServersAsync(
        int playerId,
        bool? onlineOnly = null,
        CancellationToken cancellationToken = default
    )
    {
        var queryParams = new Dictionary<string, object?> { ["online_only"] = onlineOnly };

        var queryString = BuildQueryString(queryParams);
        return await GetAsync<PlayerServersData>(
            $"/api/v1/players/{playerId}/servers{queryString}",
            cancellationToken
        );
    }

    /// <summary>
    /// 获取全局在线玩家
    /// </summary>
    /// <param name="page">页码</param>
    /// <param name="pageSize">每页大小</param>
    /// <param name="search">搜索玩家名</param>
    /// <param name="serverId">服务器ID（可选）</param>
    /// <param name="onlineOnly">是否只显示在线玩家</param>
    /// <param name="cancellationToken">取消令牌</param>
    /// <returns>全局在线玩家列表</returns>
    public async Task<ServerPlayersData> GetServerPlayersAsync(
        int? page = null,
        int? pageSize = null,
        string? search = null,
        int? serverId = null,
        bool? onlineOnly = null,
        CancellationToken cancellationToken = default
    )
    {
        var queryParams = new Dictionary<string, object?>
        {
            ["page"] = page,
            ["page_size"] = pageSize,
            ["search"] = search,
            ["server_id"] = serverId,
            ["online_only"] = onlineOnly,
        };

        var queryString = BuildQueryString(queryParams);
        return await GetAsync<ServerPlayersData>(
            $"/api/v1/server-players{queryString}",
            cancellationToken
        );
    }

    /// <summary>
    /// 设置玩家离线状态 - 在玩家退出时调用
    /// </summary>
    /// <param name="serverId">服务器ID</param>
    /// <param name="playerIds">玩家ID列表</param>
    /// <param name="cancellationToken">取消令牌</param>
    /// <returns>空响应</returns>
    public async Task SetPlayersOfflineAsync(
        int serverId,
        int[] playerIds,
        CancellationToken cancellationToken = default
    )
    {
        var request = new { server_id = serverId, player_ids = playerIds };

        await PostAsync("/api/v1/servers/players/offline", request, cancellationToken);
    }
}
