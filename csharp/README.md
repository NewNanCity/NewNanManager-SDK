# NewNanManager C# SDK

C# SDK for NewNanManager API - Minecraft server management system.

## HTTP 行为

默认超时 30 秒，默认发送 `Authorization: Bearer ...`；需要 `X-API-Token` 时在 `NewNanManagerClientOptions.AuthScheme` 设置为 `AuthScheme.ApiToken`，每个请求只发送选中的一种凭证头。拒绝自动重定向。Debug 日志不记录响应体。`ApiErrorException` 与 `NewNanManagerHttpException` 提供 `StatusCode`、`RequestId`、`RetryAfter`，响应未提供的元数据为 null。

服务器插件先调用 `await client.Monitor.CreateServerSessionAsync(serverId)`，再把返回的 `SessionContext` 传给 `Players.ValidateAsync`、`Monitor.HeartbeatAsync` 和 `PlayerServers.SetPlayersOfflineAsync`。会话 ID 遵循服务端 32–64 字符约束；省略会话参数时保留旧 HTTP 入口，不会伪造 fencing 头。

传入自定义 `HttpClient` 时，其基础地址、认证、超时和 redirect handler 由调用者负责配置；推荐使用 `new HttpClientHandler { AllowAutoRedirect = false }`。现有 CancellationToken 参数保留。

## 安装

本轮源码修复尚未发布；以下包名不表示公共源的版本已经包含这些修改。

```bash
dotnet add package NewNanManager.Client
```

## 快速开始

新增契约：Token 请求与响应提供 `ServerId`；状态和历史记录提供可选 `MeasurementType/LatencyMetric`。监控单页入口为 `GetMonitorStatsPageAsync(serverId, new MonitorStatsQuery { Limit = 1000, Cursor = cursor }, cancellationToken)`，响应含 `NextCursor`。旧 `GetMonitorStatsAsync` 签名和取消参数保持不变，也只返回一页。

字段缺失时保留 `null`。名称、Token绑定、0..1000人完整快照和分页上限见 [契约与分页](../docs/contracts.md)；本地 fake 回归覆盖这些字段及快照传输。

Token列表支持 `client.Tokens.ListApiTokensPageAsync(1, 20, cancellationToken)`，返回Tokens/Total/Page/PageSize；原 `ListApiTokensAsync(cancellationToken)` 保留默认分页行为。`ServerRegistry.Active`对应服务端字段，`ServerType`保留原类型并标记Obsolete/JsonIgnore，仅作为本地兼容属性，不能把它的默认值当成服务端事实。

新增契约后15项本地测试通过。NuGet生产项目公开源检查未命中公告；测试项目有两个旧System包的图级公告，最终net8.0产物未选择其运行资产，详见[依赖验收](../docs/audits/2026-09-06-dependency-acceptance.md)。

```csharp
using NewNanManager.Client;
using NewNanManager.Client.Models;

// 创建客户端
var client = new NewNanManagerClient("https://your-server.com", "your-api-token");

// 获取服务器列表
var servers = await client.Servers.ListServersAsync();

// 获取玩家列表
var players = await client.Players.ListPlayersAsync();

// 创建玩家
var newPlayer = await client.Players.CreatePlayerAsync(new CreatePlayerRequest
{
    Name = "PlayerName",
    QQ = "123456789"
});
```

## 功能特性

- ✅ 玩家管理（创建、查询、更新、删除、封禁）
- ✅ 服务器管理（注册、查询、更新、删除）
- ✅ 城镇管理（创建、查询、更新、删除、成员管理）
- ✅ 监控服务（心跳、延迟统计、状态查询）
- ✅ Token管理（创建、查询、更新、删除）
- ✅ 完整的类型支持
- ✅ 异步/等待模式
- ✅ 自动错误处理
- ✅ 新的响应格式：成功时直接返回数据，错误时返回 `{"detail": "错误信息"}`

## 高级配置

```csharp
// 使用配置选项
var options = new NewNanManagerClientOptions
{
    BaseUrl = "https://your-server.com",
    Token = "your-api-token",
    AuthScheme = AuthScheme.ApiToken,
    Timeout = TimeSpan.FromSeconds(60),
    UserAgent = "MyApp/1.0.0"
};

var client = new NewNanManagerClient(options, logger);

// 或使用自定义HttpClient
var httpClient = new HttpClient(new HttpClientHandler { AllowAutoRedirect = false });
var client = new NewNanManagerClient(httpClient, disposeHttpClient: true, logger);
```

## 功能特性

- ✅ 玩家管理（创建、查询、更新、删除、封禁）
- ✅ 服务器管理（注册、查询、更新、删除）
- ✅ 城镇管理（创建、查询、更新、删除、成员管理）
- ✅ 监控服务（心跳、延迟统计、状态查询）
- ✅ Token管理（创建、查询、更新、删除）
- ✅ 完整的类型安全支持
- ✅ 异步/等待模式
- ✅ 自动错误处理
- ✅ 可配置的HTTP客户端
- ✅ 结构化日志支持

## API使用示例

### 玩家管理

```csharp
// 获取玩家列表（分页）
var players = await client.Players.ListPlayersAsync(
    page: 1,
    pageSize: 20,
    search: "player_name",
    townId: 1,
    banMode: BanMode.Normal);

// 创建玩家
var player = await client.Players.CreatePlayerAsync(new CreatePlayerRequest
{
    Name = "NewPlayer",
    QQ = "123456789",
    TownId = 1,
    InQQGroup = true
});

// 封禁玩家
await client.Players.BanPlayerAsync(player.Id, new BanPlayerRequest
{
    BanMode = BanMode.Temporary,
    DurationSeconds = 3600,
    Reason = "违规行为"
});

// 解封玩家
await client.Players.UnbanPlayerAsync(player.Id);
```

### 服务器管理

```csharp
// 创建服务器
var server = await client.Servers.CreateServerAsync(new CreateServerRequest
{
    Name = "MyServer",
    Address = "127.0.0.1:25565",
    Description = "我的Minecraft服务器"
});

// 获取服务器详细信息
var detail = await client.Servers.GetServerAsync(server.Id, detail: true);
```

### 监控服务

```csharp
// 发送心跳
var heartbeat = await client.Monitor.HeartbeatAsync(serverId, new HeartbeatRequest
{
    CurrentPlayers = 10,
    MaxPlayers = 50,
    TPS = 19.8,
    Version = "1.20.1"
});

// 获取服务器状态
var status = (await client.Servers.GetServerAsync(serverId, detail: true)).Status;

// 获取延迟统计
var latencyStats = await client.Monitor.GetMonitorStatsAsync(serverId);
```

### 城镇管理

```csharp
// 创建城镇
var town = await client.Towns.CreateTownAsync(new CreateTownRequest
{
    Name = "MyTown",
    Level = 1,
    Description = "我的城镇"
});

// 获取城镇详情（包含成员）
var detail = await client.Towns.GetTownAsync(town.Id, detail: true);
var members = detail.Members;
```

### Token管理

```csharp
// 创建API Token
var tokenData = await client.Tokens.CreateApiTokenAsync(new CreateApiTokenRequest
{
    Name = "MyToken",
    Role = "admin",
    Description = "管理员Token",
    ExpireDays = 30
});

Console.WriteLine($"新Token: {tokenData.TokenValue}");

// 获取Token列表
var tokens = await client.Tokens.ListApiTokensAsync();
```

## 错误处理

```csharp
try
{
    var player = await client.Players.GetPlayerAsync(999);
}
catch (ApiErrorException ex)
{
    Console.WriteLine($"API错误: {ex.ErrorCode} - {ex.Message}");
    Console.WriteLine($"请求ID: {ex.RequestId}");
}
catch (HttpRequestException ex)
{
    Console.WriteLine($"HTTP错误: {ex.StatusCode} - {ex.Message}");
}
catch (NewNanManagerException ex)
{
    Console.WriteLine($"SDK错误: {ex.Message}");
}
```

## 测试

在 `csharp/` 中运行 fake/loopback 回归，排除真实服务集成测试：

```bash
dotnet test Tests/NewNanManager.Client.Tests.csproj --no-restore --filter Category!=Integration
```

`ComprehensiveTests` 已标记 `Category=Integration`，需要单独配置真实测试环境，不属于上述离线验收。

## 开发

构建项目：

```bash
dotnet build
```

打包NuGet包：

```bash
dotnet pack
```

## API文档

详细的API文档请参考项目主文档。

## 许可证

MIT License
