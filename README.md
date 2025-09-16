# NewNanManager SDK Collection

NewNanManager提供了多种编程语言的SDK，方便开发者集成Minecraft服务器管理功能。

## 支持的语言

| 语言                        | 状态       | 版本  | 文档                           | 测试覆盖率 |
| --------------------------- | ---------- | ----- | ------------------------------ | ---------- |
| [Python](./python/)         | ✅ 生产就绪 | 1.0.0 | [文档](./python/README.md)     | 100%       |
| [C#](./csharp/)             | ✅ 生产就绪 | 1.0.0 | [文档](./csharp/README.md)     | 100%       |
| [Go](./golang/)             | ✅ 生产就绪 | 1.0.0 | [文档](./golang/README.md)     | 100%       |
| [TypeScript](./typescript/) | ✅ 生产就绪 | 1.0.0 | [文档](./typescript/README.md) | 100%       |
| [Kotlin](./kotlin/)         | ✅ 生产就绪 | 1.0.0 | [文档](./kotlin/README.md)     | 100%       |

## 快速开始

### Python
```bash
pip install newnanmanager-client
```

```python
import asyncio
from newnanmanager import NewNanManagerClient

async def main():
    async with NewNanManagerClient("https://your-api.com", "your-token") as client:
        players = await client.players.list_players()
        print(f"Found {players.total} players")

asyncio.run(main())
```

### C#
```bash
dotnet add package NewNanManager.Client
```

```csharp
using NewNanManager.Client;

var client = new NewNanManagerClient("https://your-api.com", "your-token");
var players = await client.Players.ListPlayersAsync();
Console.WriteLine($"Found {players.Total} players");
```

### Go
```bash
go get github.com/NewNanCity/NewNanManager-SDK/clients/golang
```

```go
package main

import (
    "fmt"
    nanmanager "github.com/NewNanCity/NewNanManager-SDK/clients/golang"
)

func main() {
    client := nanmanager.NewNanCityManager("https://your-api.com", "your-token")
    players, err := client.ListPlayers(nil, nil, nil, nil, nil)
    if err != nil {
        panic(err)
    }
    fmt.Printf("Found %d players\n", players.Total)
}
```

### TypeScript
```bash
npm install @newnanmanager/client
```

```typescript
import { NewNanManagerClient } from '@newnanmanager/client';

const client = new NewNanManagerClient({
  baseUrl: 'https://your-api.com',
  token: 'your-token'
});

const players = await client.players.listPlayers();
console.log(`Found ${players.total} players`);
```

## 功能特性

所有SDK都支持以下核心功能：

### 🎮 玩家管理
- 创建、查询、更新、删除玩家
- 玩家封禁和解封
- 登录验证
- 玩家搜索和分页

### 🖥️ 服务器管理
- 服务器注册和配置
- 服务器状态监控
- 服务器详细信息查询
- 批量服务器操作

### 🏘️ 城镇系统
- 城镇创建和管理
- 成员管理
- 城镇等级系统
- 城镇搜索

### 📊 监控功能
- 实时服务器状态
- 心跳检测
- 延迟统计
- 性能监控

### 🔐 Token管理
- API Token创建和管理
- Token权限控制
- Token使用统计

## 架构设计

### 统一的设计原则

1. **异步优先**: 所有网络操作都是异步的
2. **类型安全**: 强类型定义，减少运行时错误
3. **错误处理**: 统一的错误处理机制
4. **重试机制**: 自动重试失败的请求
5. **日志记录**: 详细的调试和错误日志
6. **配置灵活**: 支持多种配置方式

### 通用配置选项

```yaml
base_url: "https://your-api.com"      # API基础URL
token: "your-api-token"               # 认证Token
timeout: 30                           # 请求超时时间（秒）
max_retries: 3                        # 最大重试次数
retry_delay: 1.0                      # 重试延迟（秒）
user_agent: "SDK-Name/1.0.0"          # 用户代理
debug: false                          # 调试模式
```

## 测试

每个SDK都包含完整的测试套件：

- **单元测试**: 测试核心功能和边界情况
- **集成测试**: 测试与真实API的交互
- **性能测试**: 基准测试和性能分析
- **错误处理测试**: 测试各种错误场景

运行测试：

```bash
# Python
cd clients/python && python -m pytest

# C#
cd clients/csharp-tests && dotnet test

# Go
cd clients/golang && go test -v

# TypeScript
cd clients/typescript && npm test

# Kotlin
cd clients/kotlin && ./gradlew test
```

## 贡献指南

1. Fork 项目
2. 创建功能分支 (`git checkout -b feature/amazing-feature`)
3. 提交更改 (`git commit -m 'Add amazing feature'`)
4. 推送到分支 (`git push origin feature/amazing-feature`)
5. 创建 Pull Request

### 开发规范

- 遵循各语言的最佳实践
- 添加适当的测试覆盖
- 更新相关文档
- 确保所有测试通过

## 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](../LICENSE) 文件了解详情。

## 支持

- 📖 [API文档](https://docs.newnanmanager.com)
- 🐛 [问题反馈](https://github.com/NewNanCity/NewNanManager-SDK/issues)
- 💬 [讨论区](https://github.com/NewNanCity/NewNanManager-SDK/discussions)
- 📧 [邮件支持](mailto:support@newnanmanager.com)

## 更新日志

### v1.0.0 (2024-08-20)
- ✅ Python SDK 生产就绪
- ✅ C# SDK 生产就绪
- ✅ Go SDK 生产就绪
- 🚧 TypeScript SDK 开发中
- 🚧 Kotlin SDK 开发中
- 📚 统一文档结构
- 🧪 完整测试套件
- 🔧 生产级配置
