using System.Text.Json.Serialization;

namespace NewNanManager.Client.Models;

/// <summary>
/// API通用响应格式
/// </summary>
/// <typeparam name="T">数据类型</typeparam>
public class ApiResponse<T>
{
    /// <summary>
    /// 响应代码，0表示成功
    /// </summary>
    [JsonPropertyName("code")]
    public int Code { get; set; }

    /// <summary>
    /// 响应消息
    /// </summary>
    [JsonPropertyName("message")]
    public string Message { get; set; } = string.Empty;

    /// <summary>
    /// 响应数据
    /// </summary>
    [JsonPropertyName("data")]
    public T? Data { get; set; }

    /// <summary>
    /// 请求ID
    /// </summary>
    [JsonPropertyName("request_id")]
    public string RequestId { get; set; } = string.Empty;
}

/// <summary>
/// API错误响应
/// </summary>
public class ErrorResponse : ApiResponse<ErrorData>
{
}

/// <summary>
/// 错误详情数据
/// </summary>
public class ErrorData
{
    /// <summary>
    /// 错误详情
    /// </summary>
    [JsonPropertyName("details")]
    public string? Details { get; set; }
}

/// <summary>
/// 分页数据基类
/// </summary>
/// <typeparam name="T">列表项类型</typeparam>
public class PagedData<T>
{
    /// <summary>
    /// 数据列表
    /// </summary>
    public List<T> Items { get; set; } = new();

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
