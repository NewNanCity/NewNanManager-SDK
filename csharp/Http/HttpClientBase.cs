using System.Net;
using System.Text;
using System.Text.Json;
using Microsoft.Extensions.Logging;
using NewNanManager.Client.Exceptions;
using NewNanManager.Client.Models;

namespace NewNanManager.Client.Http;

/// <summary>
/// HTTP客户端基础类
/// </summary>
public abstract class HttpClientBase
{
    private readonly HttpClient _httpClient;
    private readonly ILogger? _logger;
    private readonly JsonSerializerOptions _jsonOptions;

    protected HttpClientBase(HttpClient httpClient, ILogger? logger = null)
    {
        _httpClient = httpClient ?? throw new ArgumentNullException(nameof(httpClient));
        _logger = logger;

        _jsonOptions = new JsonSerializerOptions
        {
            PropertyNamingPolicy = JsonNamingPolicy.SnakeCaseLower,
            WriteIndented = false,
            DefaultIgnoreCondition = System
                .Text
                .Json
                .Serialization
                .JsonIgnoreCondition
                .WhenWritingNull,
        };
    }

    /// <summary>
    /// 发送GET请求
    /// </summary>
    /// <typeparam name="T">响应数据类型</typeparam>
    /// <param name="endpoint">API端点</param>
    /// <param name="cancellationToken">取消令牌</param>
    /// <returns>响应数据</returns>
    /// <exception cref="NewNanManagerException">API调用异常</exception>
    protected async Task<T> GetAsync<T>(
        string endpoint,
        CancellationToken cancellationToken = default
    )
    {
        _logger?.LogDebug("Sending GET request to {Endpoint}", endpoint);

        try
        {
            var response = await _httpClient.GetAsync(endpoint, cancellationToken);
            return await HandleResponseAsync<T>(response, cancellationToken);
        }
        catch (HttpRequestException ex)
        {
            _logger?.LogError(ex, "HTTP request failed for GET {Endpoint}", endpoint);
            throw new NewNanManagerHttpException(0, $"HTTP request failed: {ex.Message}", ex);
        }
        catch (TaskCanceledException ex) when (ex.InnerException is TimeoutException)
        {
            _logger?.LogError(ex, "Request timeout for GET {Endpoint}", endpoint);
            throw new NewNanManagerException($"Request timeout: {endpoint}", ex);
        }
    }

    /// <summary>
    /// 发送POST请求
    /// </summary>
    protected async Task<T> PostAsync<T>(
        string endpoint,
        object? body = null,
        CancellationToken cancellationToken = default
    )
    {
        _logger?.LogDebug("Sending POST request to {Endpoint}", endpoint);

        var content = CreateJsonContent(body);
        var response = await _httpClient.PostAsync(endpoint, content, cancellationToken);
        return await HandleResponseAsync<T>(response, cancellationToken);
    }

    /// <summary>
    /// 发送POST请求（无返回数据）
    /// </summary>
    protected async Task PostAsync(
        string endpoint,
        object? body = null,
        CancellationToken cancellationToken = default
    )
    {
        _logger?.LogDebug("Sending POST request to {Endpoint}", endpoint);

        var content = CreateJsonContent(body);
        var response = await _httpClient.PostAsync(endpoint, content, cancellationToken);
        await HandleResponseAsync(response, cancellationToken);
    }

    /// <summary>
    /// 发送PUT请求
    /// </summary>
    protected async Task<T> PutAsync<T>(
        string endpoint,
        object? body = null,
        CancellationToken cancellationToken = default
    )
    {
        _logger?.LogDebug("Sending PUT request to {Endpoint}", endpoint);

        var content = CreateJsonContent(body);
        var response = await _httpClient.PutAsync(endpoint, content, cancellationToken);
        return await HandleResponseAsync<T>(response, cancellationToken);
    }

    /// <summary>
    /// 发送DELETE请求
    /// </summary>
    protected async Task DeleteAsync(string endpoint, CancellationToken cancellationToken = default)
    {
        _logger?.LogDebug("Sending DELETE request to {Endpoint}", endpoint);

        var response = await _httpClient.DeleteAsync(endpoint, cancellationToken);
        await HandleResponseAsync(response, cancellationToken);
    }

    /// <summary>
    /// 创建JSON内容
    /// </summary>
    private HttpContent? CreateJsonContent(object? body)
    {
        if (body == null)
            return null;

        var json = JsonSerializer.Serialize(body, _jsonOptions);
        return new StringContent(json, Encoding.UTF8, "application/json");
    }

    /// <summary>
    /// 处理HTTP响应（有返回数据）
    /// </summary>
    private async Task<T> HandleResponseAsync<T>(
        HttpResponseMessage response,
        CancellationToken cancellationToken
    )
    {
        var content = await response.Content.ReadAsStringAsync(cancellationToken);

        _logger?.LogDebug(
            "Received response: Status={StatusCode}, Content={Content}",
            response.StatusCode,
            content
        );

        // 处理HTTP错误状态码
        if (!response.IsSuccessStatusCode)
        {
            await HandleHttpErrorAsync(response, content);
        }

        // 新的响应格式：成功时直接返回数据，不再有统一包装
        var result = JsonSerializer.Deserialize<T>(content, _jsonOptions);
        if (result == null)
        {
            throw new NewNanManagerException("Failed to parse API response");
        }

        return result;
    }

    /// <summary>
    /// 处理HTTP响应（无返回数据）
    /// </summary>
    private async Task HandleResponseAsync(
        HttpResponseMessage response,
        CancellationToken cancellationToken
    )
    {
        var content = await response.Content.ReadAsStringAsync(cancellationToken);

        _logger?.LogDebug(
            "Received response: Status={StatusCode}, Content={Content}",
            response.StatusCode,
            content
        );

        // 处理HTTP错误状态码
        if (!response.IsSuccessStatusCode)
        {
            await HandleHttpErrorAsync(response, content);
        }

        // 新的响应格式：对于无返回数据的请求，成功时通常返回空内容或简单确认
        // 如果有内容且是错误，会在上面的HTTP错误处理中被捕获
    }

    /// <summary>
    /// 处理HTTP错误
    /// </summary>
    private Task HandleHttpErrorAsync(HttpResponseMessage response, string content)
    {
        var statusCode = (int)response.StatusCode;

        try
        {
            // 尝试解析新的错误响应格式 {"detail": "..."}
            var errorData = JsonSerializer.Deserialize<Dictionary<string, object>>(
                content,
                _jsonOptions
            );
            if (errorData != null && errorData.ContainsKey("detail"))
            {
                throw new ApiErrorException(
                    statusCode,
                    errorData["detail"].ToString() ?? "Unknown error",
                    null
                );
            }
        }
        catch (JsonException)
        {
            // 如果无法解析为JSON错误响应，则抛出HTTP异常
        }

        var message = response.StatusCode switch
        {
            HttpStatusCode.Unauthorized => "Unauthorized: Invalid or missing API token",
            HttpStatusCode.Forbidden => "Forbidden: Insufficient permissions",
            HttpStatusCode.NotFound => "Not found: The requested resource does not exist",
            HttpStatusCode.TooManyRequests => "Too many requests: Rate limit exceeded",
            HttpStatusCode.InternalServerError => "Internal server error",
            HttpStatusCode.BadGateway => "Bad gateway",
            HttpStatusCode.ServiceUnavailable => "Service unavailable",
            HttpStatusCode.GatewayTimeout => "Gateway timeout",
            _ => $"HTTP {statusCode}: {response.ReasonPhrase}",
        };

        throw new NewNanManagerHttpException(statusCode, message);
    }

    /// <summary>
    /// 构建查询字符串
    /// </summary>
    protected static string BuildQueryString(Dictionary<string, object?> parameters)
    {
        var queryParams = parameters
            .Where(kvp => kvp.Value != null)
            .Select(kvp =>
                $"{Uri.EscapeDataString(kvp.Key)}={Uri.EscapeDataString(kvp.Value!.ToString()!)}"
            )
            .ToArray();

        return queryParams.Length > 0 ? "?" + string.Join("&", queryParams) : string.Empty;
    }
}
