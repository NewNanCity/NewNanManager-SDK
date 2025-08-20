using Microsoft.Extensions.Logging;
using NewNanManager.Client.Http;
using NewNanManager.Client.Models;

namespace NewNanManager.Client.Services;

/// <summary>
/// 玩家管理服务
/// </summary>
public class PlayerService : HttpClientBase
{
    public PlayerService(HttpClient httpClient, ILogger? logger = null) : base(httpClient, logger)
    {
    }

    /// <summary>
    /// 获取玩家列表
    /// </summary>
    /// <param name="page">页码</param>
    /// <param name="pageSize">每页大小</param>
    /// <param name="search">搜索关键词</param>
    /// <param name="townId">城镇ID</param>
    /// <param name="banMode">封禁模式</param>
    /// <param name="cancellationToken">取消令牌</param>
    /// <returns>玩家列表数据</returns>
    public async Task<PlayersListData> ListPlayersAsync(
        int? page = null,
        int? pageSize = null,
        string? search = null,
        int? townId = null,
        BanMode? banMode = null,
        CancellationToken cancellationToken = default)
    {
        var queryParams = new Dictionary<string, object?>
        {
            ["page"] = page,
            ["page_size"] = pageSize,
            ["search"] = search,
            ["town_id"] = townId,
            ["ban_mode"] = banMode.HasValue ? (int)banMode.Value : null
        };

        var queryString = BuildQueryString(queryParams);
        return await GetAsync<PlayersListData>($"/api/v1/players{queryString}", cancellationToken);
    }

    /// <summary>
    /// 创建玩家
    /// </summary>
    /// <param name="request">创建玩家请求</param>
    /// <param name="cancellationToken">取消令牌</param>
    /// <returns>创建的玩家信息</returns>
    public async Task<Player> CreatePlayerAsync(CreatePlayerRequest request, CancellationToken cancellationToken = default)
    {
        return await PostAsync<Player>("/api/v1/players", request, cancellationToken);
    }

    /// <summary>
    /// 验证玩家登录
    /// </summary>
    /// <param name="request">登录验证请求</param>
    /// <param name="cancellationToken">取消令牌</param>
    /// <returns>验证结果</returns>
    public async Task<ValidateLoginData> ValidateLoginAsync(ValidateLoginRequest request, CancellationToken cancellationToken = default)
    {
        return await PostAsync<ValidateLoginData>("/api/v1/players/validate-login", request, cancellationToken);
    }

    /// <summary>
    /// 获取玩家详情
    /// </summary>
    /// <param name="id">玩家ID</param>
    /// <param name="cancellationToken">取消令牌</param>
    /// <returns>玩家信息</returns>
    public async Task<Player> GetPlayerAsync(int id, CancellationToken cancellationToken = default)
    {
        return await GetAsync<Player>($"/api/v1/players/{id}", cancellationToken);
    }

    /// <summary>
    /// 更新玩家信息
    /// </summary>
    /// <param name="id">玩家ID</param>
    /// <param name="request">更新请求</param>
    /// <param name="cancellationToken">取消令牌</param>
    /// <returns>更新后的玩家信息</returns>
    public async Task<Player> UpdatePlayerAsync(int id, UpdatePlayerRequest request, CancellationToken cancellationToken = default)
    {
        return await PutAsync<Player>($"/api/v1/players/{id}", request, cancellationToken);
    }

    /// <summary>
    /// 删除玩家
    /// </summary>
    /// <param name="id">玩家ID</param>
    /// <param name="cancellationToken">取消令牌</param>
    public async Task DeletePlayerAsync(int id, CancellationToken cancellationToken = default)
    {
        await DeleteAsync($"/api/v1/players/{id}", cancellationToken);
    }

    /// <summary>
    /// 封禁玩家
    /// </summary>
    /// <param name="playerId">玩家ID</param>
    /// <param name="request">封禁请求</param>
    /// <param name="cancellationToken">取消令牌</param>
    public async Task BanPlayerAsync(int playerId, BanPlayerRequest request, CancellationToken cancellationToken = default)
    {
        await PostAsync($"/api/v1/players/{playerId}/ban", request, cancellationToken);
    }

    /// <summary>
    /// 解封玩家
    /// </summary>
    /// <param name="playerId">玩家ID</param>
    /// <param name="cancellationToken">取消令牌</param>
    public async Task UnbanPlayerAsync(int playerId, CancellationToken cancellationToken = default)
    {
        await PostAsync($"/api/v1/players/{playerId}/unban", cancellationToken: cancellationToken);
    }
}
