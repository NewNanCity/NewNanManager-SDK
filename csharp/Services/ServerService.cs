using Microsoft.Extensions.Logging;
using NewNanManager.Client.Http;
using NewNanManager.Client.Models;

namespace NewNanManager.Client.Services;

/// <summary>
/// 服务器管理服务
/// </summary>
public class ServerService : HttpClientBase
{
    public ServerService(HttpClient httpClient, ILogger? logger = null)
        : base(httpClient, logger) { }

    /// <summary>
    /// 获取服务器列表
    /// </summary>
    /// <param name="page">页码</param>
    /// <param name="pageSize">每页大小</param>
    /// <param name="search">搜索关键词</param>
    /// <param name="onlineOnly">仅显示在线服务器</param>
    /// <param name="cancellationToken">取消令牌</param>
    /// <returns>服务器列表数据</returns>
    public async Task<ServersListData> ListServersAsync(
        int? page = null,
        int? pageSize = null,
        string? search = null,
        bool? onlineOnly = null,
        CancellationToken cancellationToken = default
    )
    {
        var queryParams = new Dictionary<string, object?>
        {
            ["page"] = page,
            ["page_size"] = pageSize,
            ["search"] = search,
            ["online_only"] = onlineOnly,
        };

        var queryString = BuildQueryString(queryParams);
        return await GetAsync<ServersListData>($"/api/v1/servers{queryString}", cancellationToken);
    }

    /// <summary>
    /// 创建服务器
    /// </summary>
    /// <param name="request">创建服务器请求</param>
    /// <param name="cancellationToken">取消令牌</param>
    /// <returns>注册的服务器信息</returns>
    public async Task<ServerRegistry> CreateServerAsync(
        CreateServerRequest request,
        CancellationToken cancellationToken = default
    )
    {
        return await PostAsync<ServerRegistry>("/api/v1/servers", request, cancellationToken);
    }

    /// <summary>
    /// 获取服务器信息
    /// </summary>
    /// <param name="id">服务器ID</param>
    /// <param name="cancellationToken">取消令牌</param>
    /// <returns>服务器信息</returns>
    public async Task<ServerRegistry> GetServerAsync(
        int id,
        CancellationToken cancellationToken = default
    )
    {
        return await GetAsync<ServerRegistry>($"/api/v1/servers/{id}", cancellationToken);
    }

    /// <summary>
    /// 更新服务器信息
    /// </summary>
    /// <param name="id">服务器ID</param>
    /// <param name="request">更新请求</param>
    /// <param name="cancellationToken">取消令牌</param>
    /// <returns>更新后的服务器信息</returns>
    public async Task<ServerRegistry> UpdateServerAsync(
        int id,
        UpdateServerRequest request,
        CancellationToken cancellationToken = default
    )
    {
        return await PutAsync<ServerRegistry>($"/api/v1/servers/{id}", request, cancellationToken);
    }

    /// <summary>
    /// 删除服务器
    /// </summary>
    /// <param name="id">服务器ID</param>
    /// <param name="cancellationToken">取消令牌</param>
    public async Task DeleteServerAsync(int id, CancellationToken cancellationToken = default)
    {
        await DeleteAsync($"/api/v1/servers/{id}", cancellationToken);
    }

    /// <summary>
    /// 获取服务器详细信息
    /// </summary>
    /// <param name="id">服务器ID</param>
    /// <param name="cancellationToken">取消令牌</param>
    /// <returns>服务器详细信息</returns>
    public async Task<ServerDetailData> GetServerDetailAsync(
        int id,
        CancellationToken cancellationToken = default
    )
    {
        return await GetAsync<ServerDetailData>($"/api/v1/servers/{id}/detail", cancellationToken);
    }
}
