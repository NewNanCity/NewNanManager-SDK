using Microsoft.Extensions.Logging;
using NewNanManager.Client.Http;
using NewNanManager.Client.Models;

namespace NewNanManager.Client.Services;

/// <summary>
/// Token管理服务
/// </summary>
public class TokenService : HttpClientBase
{
    public TokenService(HttpClient httpClient, ILogger? logger = null) : base(httpClient, logger)
    {
    }

    /// <summary>
    /// 获取API Token列表
    /// </summary>
    /// <param name="cancellationToken">取消令牌</param>
    /// <returns>Token列表数据</returns>
    public async Task<ListApiTokensData> ListApiTokensAsync(CancellationToken cancellationToken = default)
    {
        return await GetAsync<ListApiTokensData>("/api/v1/tokens", cancellationToken);
    }

    /// <summary>
    /// 创建API Token
    /// </summary>
    /// <param name="request">创建Token请求</param>
    /// <param name="cancellationToken">取消令牌</param>
    /// <returns>创建的Token数据（包含Token值）</returns>
    public async Task<CreateApiTokenData> CreateApiTokenAsync(CreateApiTokenRequest request, CancellationToken cancellationToken = default)
    {
        return await PostAsync<CreateApiTokenData>("/api/v1/tokens", request, cancellationToken);
    }

    /// <summary>
    /// 获取API Token详情
    /// </summary>
    /// <param name="id">Token ID</param>
    /// <param name="cancellationToken">取消令牌</param>
    /// <returns>Token信息</returns>
    public async Task<ApiToken> GetApiTokenAsync(int id, CancellationToken cancellationToken = default)
    {
        return await GetAsync<ApiToken>($"/api/v1/tokens/{id}", cancellationToken);
    }

    /// <summary>
    /// 更新API Token
    /// </summary>
    /// <param name="id">Token ID</param>
    /// <param name="request">更新请求</param>
    /// <param name="cancellationToken">取消令牌</param>
    /// <returns>更新后的Token信息</returns>
    public async Task<ApiToken> UpdateApiTokenAsync(int id, UpdateApiTokenRequest request, CancellationToken cancellationToken = default)
    {
        return await PutAsync<ApiToken>($"/api/v1/tokens/{id}", request, cancellationToken);
    }

    /// <summary>
    /// 删除API Token
    /// </summary>
    /// <param name="id">Token ID</param>
    /// <param name="cancellationToken">取消令牌</param>
    public async Task DeleteApiTokenAsync(int id, CancellationToken cancellationToken = default)
    {
        await DeleteAsync($"/api/v1/tokens/{id}", cancellationToken);
    }
}
