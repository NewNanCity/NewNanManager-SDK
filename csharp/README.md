# NewNanManager C# SDK

C# SDK for NewNanManager API - Minecraft server management system.

## 安装

```bash
dotnet add package NewNanManager.Client
```

## 快速开始

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
    Timeout = TimeSpan.FromSeconds(60),
    UserAgent = "MyApp/1.0.0"
};

var client = new NewNanManagerClient(options, logger);

// 或使用自定义HttpClient
var httpClient = new HttpClient();
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
// 注册服务器
var server = await client.Servers.RegisterServerAsync(new RegisterServerRequest
{
    Name = "MyServer",
    Address = "127.0.0.1:25565",
    ServerType = ServerType.Minecraft,
    Description = "我的Minecraft服务器"
});

// 获取服务器详细信息
var detail = await client.Servers.GetServerDetailAsync(server.Id);
```

### 监控服务

```csharp
// 发送心跳
var heartbeat = await client.Monitor.HeartbeatAsync(serverId, new HeartbeatRequest
{
    Timestamp = DateTimeOffset.UtcNow.ToUnixTimeSeconds(),
    CurrentPlayers = 10,
    MaxPlayers = 50,
    TPS = 19.8,
    Version = "1.20.1"
});

// 获取服务器状态
var status = await client.Monitor.GetServerStatusAsync(serverId);

// 获取延迟统计
var latencyStats = await client.Monitor.GetLatencyStatsAsync(serverId);
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

// 获取城镇成员
var members = await client.Towns.GetTownMembersAsync(town.Id);
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

运行集成测试：

```bash
cd Tests
dotnet test
```

注意：集成测试需要真实的API服务器运行。请在测试前修改 `IntegrationTests.cs` 中的配置。

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
