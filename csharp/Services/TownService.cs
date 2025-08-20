using Microsoft.Extensions.Logging;
using NewNanManager.Client.Http;
using NewNanManager.Client.Models;

namespace NewNanManager.Client.Services;

/// <summary>
/// 城镇管理服务
/// </summary>
public class TownService : HttpClientBase
{
    public TownService(HttpClient httpClient, ILogger? logger = null) : base(httpClient, logger)
    {
    }

    /// <summary>
    /// 获取城镇列表
    /// </summary>
    /// <param name="page">页码</param>
    /// <param name="pageSize">每页大小</param>
    /// <param name="search">搜索关键词</param>
    /// <param name="minLevel">最小等级</param>
    /// <param name="maxLevel">最大等级</param>
    /// <param name="cancellationToken">取消令牌</param>
    /// <returns>城镇列表数据</returns>
    public async Task<TownsListData> ListTownsAsync(
        int? page = null,
        int? pageSize = null,
        string? search = null,
        int? minLevel = null,
        int? maxLevel = null,
        CancellationToken cancellationToken = default)
    {
        var queryParams = new Dictionary<string, object?>
        {
            ["page"] = page,
            ["page_size"] = pageSize,
            ["search"] = search,
            ["min_level"] = minLevel,
            ["max_level"] = maxLevel
        };

        var queryString = BuildQueryString(queryParams);
        return await GetAsync<TownsListData>($"/api/v1/towns{queryString}", cancellationToken);
    }

    /// <summary>
    /// 创建城镇
    /// </summary>
    /// <param name="request">创建城镇请求</param>
    /// <param name="cancellationToken">取消令牌</param>
    /// <returns>创建的城镇信息</returns>
    public async Task<Town> CreateTownAsync(CreateTownRequest request, CancellationToken cancellationToken = default)
    {
        return await PostAsync<Town>("/api/v1/towns", request, cancellationToken);
    }

    /// <summary>
    /// 获取城镇详情
    /// </summary>
    /// <param name="id">城镇ID</param>
    /// <param name="cancellationToken">取消令牌</param>
    /// <returns>城镇信息</returns>
    public async Task<Town> GetTownAsync(int id, CancellationToken cancellationToken = default)
    {
        return await GetAsync<Town>($"/api/v1/towns/{id}", cancellationToken);
    }

    /// <summary>
    /// 更新城镇信息
    /// </summary>
    /// <param name="id">城镇ID</param>
    /// <param name="request">更新请求</param>
    /// <param name="cancellationToken">取消令牌</param>
    /// <returns>更新后的城镇信息</returns>
    public async Task<Town> UpdateTownAsync(int id, UpdateTownRequest request, CancellationToken cancellationToken = default)
    {
        return await PutAsync<Town>($"/api/v1/towns/{id}", request, cancellationToken);
    }

    /// <summary>
    /// 删除城镇
    /// </summary>
    /// <param name="id">城镇ID</param>
    /// <param name="cancellationToken">取消令牌</param>
    public async Task DeleteTownAsync(int id, CancellationToken cancellationToken = default)
    {
        await DeleteAsync($"/api/v1/towns/{id}", cancellationToken);
    }

    /// <summary>
    /// 获取城镇成员列表
    /// </summary>
    /// <param name="townId">城镇ID</param>
    /// <param name="page">页码</param>
    /// <param name="pageSize">每页大小</param>
    /// <param name="cancellationToken">取消令牌</param>
    /// <returns>城镇成员数据</returns>
    public async Task<TownMembersData> GetTownMembersAsync(
        int townId,
        int? page = null,
        int? pageSize = null,
        CancellationToken cancellationToken = default)
    {
        var queryParams = new Dictionary<string, object?>
        {
            ["page"] = page,
            ["page_size"] = pageSize
        };

        var queryString = BuildQueryString(queryParams);
        return await GetAsync<TownMembersData>($"/api/v1/towns/{townId}/members{queryString}", cancellationToken);
    }

    /// <summary>
    /// 管理城镇成员
    /// </summary>
    /// <param name="townId">城镇ID</param>
    /// <param name="request">成员管理请求</param>
    /// <param name="cancellationToken">取消令牌</param>
    public async Task ManageTownMemberAsync(int townId, ManageTownMemberRequest request, CancellationToken cancellationToken = default)
    {
        await PostAsync($"/api/v1/towns/{townId}/members", request, cancellationToken);
    }
}
