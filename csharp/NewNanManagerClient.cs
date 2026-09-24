using Microsoft.Extensions.Logging;
using NewNanManager.Client.Http;
using NewNanManager.Client.Services;

namespace NewNanManager.Client;

/// <summary>
/// NewNanManager API客户端配置
/// </summary>
public class NewNanManagerClientOptions
{
    /// <summary>
    /// API基础URL
    /// </summary>
    public string BaseUrl { get; set; } = string.Empty;

    /// <summary>
    /// API Token
    /// </summary>
    public string Token { get; set; } = string.Empty;

    /// <summary>
    /// Credential header selected for requests. Defaults to Bearer.
    /// </summary>
    public AuthScheme AuthScheme { get; set; } = AuthScheme.Bearer;

    /// <summary>
    /// HTTP客户端超时时间（默认30秒）
    /// </summary>
    public TimeSpan Timeout { get; set; } = TimeSpan.FromSeconds(30);

    /// <summary>
    /// 用户代理字符串
    /// </summary>
    public string UserAgent { get; set; } = "NewNanManager-CSharp-SDK/1.0.0";
}

/// <summary>
/// NewNanManager API客户端
/// </summary>
public class NewNanManagerClient : IDisposable
{
    private readonly HttpClient _httpClient;
    private readonly bool _disposeHttpClient;
    private readonly ILogger? _logger;

    /// <summary>
    /// 玩家管理服务
    /// </summary>
    public PlayerService Players { get; }

    /// <summary>
    /// 服务器管理服务
    /// </summary>
    public ServerService Servers { get; }

    /// <summary>
    /// 城镇管理服务
    /// </summary>
    public TownService Towns { get; }

    /// <summary>
    /// 监控服务
    /// </summary>
    public MonitorService Monitor { get; }

    /// <summary>
    /// Token管理服务
    /// </summary>
    public TokenService Tokens { get; }

    /// <summary>
    /// IP管理服务
    /// </summary>
    public IPService IPs { get; }

    /// <summary>
    /// 玩家服务器关系管理服务
    /// </summary>
    public PlayerServerService PlayerServers { get; }

    /// <summary>
    /// 使用默认HttpClient创建客户端
    /// </summary>
    public NewNanManagerClient(string baseUrl, string token, ILogger? logger = null)
        : this(new NewNanManagerClientOptions { BaseUrl = baseUrl, Token = token }, logger) { }

    /// <summary>
    /// 使用配置选项创建客户端
    /// </summary>
    public NewNanManagerClient(NewNanManagerClientOptions options, ILogger? logger = null)
        : this(CreateHttpClient(options), true, logger) { }

    /// <summary>
    /// 使用自定义HttpClient创建客户端
    /// </summary>
    public NewNanManagerClient(
        HttpClient httpClient,
        bool disposeHttpClient = false,
        ILogger? logger = null
    )
    {
        _httpClient = httpClient ?? throw new ArgumentNullException(nameof(httpClient));
        _disposeHttpClient = disposeHttpClient;
        _logger = logger;

        // 初始化各个服务
        Players = new PlayerService(_httpClient, _logger);
        Servers = new ServerService(_httpClient, _logger);
        Towns = new TownService(_httpClient, _logger);
        Monitor = new MonitorService(_httpClient, _logger);
        Tokens = new TokenService(_httpClient, _logger);
        IPs = new IPService(_httpClient, _logger);
        PlayerServers = new PlayerServerService(_httpClient, _logger);
    }

    /// <summary>
    /// 创建配置好的HttpClient
    /// </summary>
    internal static HttpClient CreateHttpClient(
        NewNanManagerClientOptions options,
        HttpMessageHandler? handler = null
    )
    {
        if (string.IsNullOrEmpty(options.BaseUrl))
            throw new ArgumentException("BaseUrl cannot be null or empty", nameof(options));

        if (string.IsNullOrEmpty(options.Token))
            throw new ArgumentException("Token cannot be null or empty", nameof(options));

        var httpClient = new HttpClient(
            handler ?? new HttpClientHandler { AllowAutoRedirect = false }
        )
        {
            BaseAddress = new Uri(options.BaseUrl.TrimEnd('/')),
            Timeout = options.Timeout,
        };

        // 设置认证头；每个请求只发送一种凭证。
        if (options.AuthScheme == AuthScheme.ApiToken)
        {
            httpClient.DefaultRequestHeaders.TryAddWithoutValidation("X-API-Token", options.Token);
        }
        else
        {
            httpClient.DefaultRequestHeaders.Authorization =
                new System.Net.Http.Headers.AuthenticationHeaderValue("Bearer", options.Token);
        }
        httpClient.DefaultRequestHeaders.Add("User-Agent", options.UserAgent);
        httpClient.DefaultRequestHeaders.Add("Accept", "application/json");

        return httpClient;
    }

    /// <summary>
    /// 释放资源
    /// </summary>
    public void Dispose()
    {
        if (_disposeHttpClient)
        {
            _httpClient?.Dispose();
        }
    }
}
