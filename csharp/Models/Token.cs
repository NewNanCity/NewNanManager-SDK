using System.Text.Json.Serialization;

namespace NewNanManager.Client.Models;

/// <summary>
/// API Token实体
/// </summary>
public class ApiToken
{
    /// <summary>
    /// Token ID
    /// </summary>
    [JsonPropertyName("id")]
    public int Id { get; set; }

    /// <summary>
    /// Token名称
    /// </summary>
    [JsonPropertyName("name")]
    public string Name { get; set; } = string.Empty;

    /// <summary>
    /// Token角色
    /// </summary>
    [JsonPropertyName("role")]
    public string Role { get; set; } = string.Empty;

    /// <summary>server角色绑定的服务器ID。</summary>
    [JsonPropertyName("server_id")]
    public int? ServerId { get; set; }

    /// <summary>
    /// Token描述
    /// </summary>
    [JsonPropertyName("description")]
    public string? Description { get; set; }

    /// <summary>
    /// 是否激活
    /// </summary>
    [JsonPropertyName("active")]
    public bool Active { get; set; }

    /// <summary>
    /// 过期时间
    /// </summary>
    [JsonPropertyName("expire_at")]
    public DateTime? ExpireAt { get; set; }

    /// <summary>
    /// 最后使用时间
    /// </summary>
    [JsonPropertyName("last_used_at")]
    public DateTime? LastUsedAt { get; set; }

    /// <summary>
    /// 最后使用IP
    /// </summary>
    [JsonPropertyName("last_used_ip")]
    public string? LastUsedIP { get; set; }

    /// <summary>
    /// 创建时间
    /// </summary>
    [JsonPropertyName("created_at")]
    public DateTime CreatedAt { get; set; }

    /// <summary>
    /// 更新时间
    /// </summary>
    [JsonPropertyName("updated_at")]
    public DateTime UpdatedAt { get; set; }
}

/// <summary>
/// 创建Token响应数据
/// </summary>
public class CreateApiTokenData
{
    /// <summary>
    /// Token信息
    /// </summary>
    [JsonPropertyName("token_info")]
    public ApiToken TokenInfo { get; set; } = new();

    /// <summary>
    /// Token值（仅在创建时返回）
    /// </summary>
    [JsonPropertyName("token_value")]
    public string TokenValue { get; set; } = string.Empty;
}

/// <summary>
/// Token列表数据
/// </summary>
public class ListApiTokensData
{
    /// <summary>
    /// Token列表
    /// </summary>
    [JsonPropertyName("tokens")]
    public List<ApiToken> Tokens { get; set; } = new();

    /// <summary>
    /// 总数量
    /// </summary>
    [JsonPropertyName("total")]
    public long Total { get; set; }

    /// <summary>
    /// 当前页码
    /// </summary>
    [JsonPropertyName("page")]
    public int Page { get; set; }

    /// <summary>
    /// 每页大小
    /// </summary>
    [JsonPropertyName("page_size")]
    public int PageSize { get; set; }
}
