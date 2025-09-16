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
        var httpClientBase = new HttpClientBase(_httpClient, _logger);
        Players = new PlayerService(httpClientBase);
        Servers = new ServerService(httpClientBase);
        Towns = new TownService(httpClientBase);
        Monitor = new MonitorService(httpClientBase);
        Tokens = new TokenService(httpClientBase);
        IPs = new IPService(httpClientBase);
        PlayerServers = new PlayerServerService(httpClientBase);
    }

    /// <summary>
    /// 创建配置好的HttpClient
    /// </summary>
    private static HttpClient CreateHttpClient(NewNanManagerClientOptions options)
    {
        if (string.IsNullOrEmpty(options.BaseUrl))
            throw new ArgumentException("BaseUrl cannot be null or empty", nameof(options));

        if (string.IsNullOrEmpty(options.Token))
            throw new ArgumentException("Token cannot be null or empty", nameof(options));

        var httpClient = new HttpClient
        {
            BaseAddress = new Uri(options.BaseUrl.TrimEnd('/')),
            Timeout = options.Timeout,
        };

        // 设置认证头
        httpClient.DefaultRequestHeaders.Add("Authorization", $"Bearer {options.Token}");
        httpClient.DefaultRequestHeaders.Add("X-API-Token", options.Token);
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
