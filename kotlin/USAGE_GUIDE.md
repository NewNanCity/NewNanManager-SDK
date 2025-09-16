# NewNanManager Kotlin SDK 使用指南

## 📋 目录结构

```
sdk/kotlin/
├── src/main/kotlin/com/nanmanager/
│   ├── client/          # 协程版本 (Ktor + Kotlinx Serialization)
│   └── bukkit/          # 同步版本 (OkHttp + Jackson)
│       ├── NewNanManagerClient.kt     # 主客户端类
│       ├── models/Models.kt           # 数据模型
│       ├── exceptions/Exceptions.kt   # 异常定义
│       ├── http/HttpClient.kt         # HTTP客户端
│       ├── services/                  # 服务层
│       └── examples/                  # 使用示例
└── README.md
```

## 🚀 快速开始

### 1. 选择合适的版本

**协程版本** (推荐用于现代Kotlin应用)：
```kotlin
import com.nanmanager.client.NewNanManagerClient
```

**同步版本** (专为Minecraft插件设计)：
```kotlin
import com.nanmanager.bukkit.NewNanManagerClient
```

### 2. 创建客户端

```kotlin
// 同步版本
val client = com.nanmanager.bukkit.NewNanManagerClient(
    token = "your-api-token",
    baseUrl = "https://your-server.com",
    timeout = 30L
)

// 协程版本
val client = com.nanmanager.client.NewNanManagerClient(
    token = "your-api-token",
    baseUrl = "https://your-server.com"
)
```

### 3. 使用服务

```kotlin
// 同步版本 - 直接调用
val players = client.players.listPlayers()
val servers = client.servers.listServers()

// 协程版本 - 需要在协程中调用
runBlocking {
    val players = client.players.listPlayers()
    val servers = client.servers.listServers()
}
```

## 🎮 Minecraft插件开发

### 推荐使用同步版本

```kotlin
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import com.nanmanager.bukkit.NewNanManagerClient
import com.nanmanager.bukkit.models.*

class MyPlugin : JavaPlugin() {
    private lateinit var nanClient: NewNanManagerClient

    override fun onEnable() {
        nanClient = NewNanManagerClient(
            token = config.getString("nanmanager.token")!!,
            baseUrl = config.getString("nanmanager.baseUrl")!!
        )
    }

    override fun onDisable() {
        nanClient.close()
    }

    // 在异步线程中调用API
    fun validatePlayer(playerName: String, ip: String) {
        object : BukkitRunnable() {
            override fun run() {
                try {
                    val result = nanClient.players.validate(ValidateRequest(
                        players = listOf(PlayerValidateInfo(
                            playerName = playerName,
                            ip = ip
                        )),
                        serverId = 1,
                        login = true
                    ))
                    
                    // 切回主线程处理结果
                    object : BukkitRunnable() {
                        override fun run() {
                            // 在主线程中处理验证结果
                            handleValidationResult(result)
                        }
                    }.runTask(this@MyPlugin)
                    
                } catch (e: Exception) {
                    logger.warning("验证失败: ${e.message}")
                }
            }
        }.runTaskAsynchronously(this)
    }
}
```

## 📚 API服务说明

### 玩家管理 (PlayerService)
- `createPlayer()` - 创建玩家
- `getPlayer()` - 获取玩家详情
- `updatePlayer()` - 更新玩家信息
- `deletePlayer()` - 删除玩家
- `listPlayers()` - 获取玩家列表
- `banPlayer()` - 封禁玩家
- `unbanPlayer()` - 解封玩家
- `validate()` - 批量验证玩家

### 服务器管理 (ServerService)
- `createServer()` - 注册服务器
- `getServer()` - 获取服务器详情
- `updateServer()` - 更新服务器信息
- `deleteServer()` - 删除服务器
- `listServers()` - 获取服务器列表

### 城镇管理 (TownService)
- `createTown()` - 创建城镇
- `getTown()` - 获取城镇详情
- `updateTown()` - 更新城镇信息
- `deleteTown()` - 删除城镇
- `listTowns()` - 获取城镇列表

### 监控服务 (MonitorService)
- `heartbeat()` - 发送心跳
- `getServerStatus()` - 获取服务器状态
- `getMonitorStats()` - 获取监控统计

### Token管理 (TokenService)
- `createToken()` - 创建API Token
- `getToken()` - 获取Token详情
- `updateToken()` - 更新Token
- `deleteToken()` - 删除Token
- `listTokens()` - 获取Token列表

### IP管理 (IPService)
- `getIPInfo()` - 获取IP信息
- `banIP()` - 封禁IP
- `unbanIP()` - 解封IP
- `listIPs()` - 获取IP列表
- `getBannedIPs()` - 获取被封禁IP
- `getSuspiciousIPs()` - 获取可疑IP
- `getHighRiskIPs()` - 获取高风险IP
- `getIPStatistics()` - 获取IP统计

### 玩家服务器关系 (PlayerServerService)
- `getPlayerServers()` - 获取玩家的服务器关系
- `getServerPlayers()` - 获取服务器在线玩家
- `setPlayersOffline()` - 设置玩家离线状态

## 🔧 配置建议

### application.conf (用于配置)
```hocon
nanmanager {
    token = "your-api-token"
    baseUrl = "https://your-server.com"
    timeout = 30
}
```

### plugin.yml (Minecraft插件)
```yaml
name: MyPlugin
version: 1.0.0
main: com.example.MyPlugin
api-version: 1.20

# 配置文件
config:
  nanmanager:
    token: "your-api-token"
    baseUrl: "https://your-server.com"
```

## 🚨 错误处理

```kotlin
import com.nanmanager.bukkit.exceptions.*

try {
    val player = client.players.getPlayer(999)
} catch (e: ApiException) {
    // API返回的业务错误
    logger.warning("API错误: ${e.errorDetail}")
} catch (e: HttpException) {
    // HTTP状态码错误
    logger.warning("HTTP错误: ${e.statusCode}")
} catch (e: NetworkException) {
    // 网络连接错误
    logger.warning("网络错误: ${e.message}")
} catch (e: JsonParseException) {
    // JSON解析错误
    logger.warning("解析错误: ${e.message}")
}
```

## 🔄 版本选择指南

| 场景 | 推荐版本 | 原因 |
|------|----------|------|
| Minecraft插件开发 | 同步版本 | 避免协程依赖冲突，简化线程管理 |
| Spring Boot应用 | 协程版本 | 更好的性能和现代化API |
| Android应用 | 协程版本 | 原生协程支持 |
| 桌面应用 | 协程版本 | 更好的异步处理 |
| 简单脚本 | 同步版本 | 更简单的调用方式 |

## 📝 最佳实践

1. **资源管理**：始终调用 `client.close()` 释放资源
2. **异常处理**：捕获并处理所有可能的异常
3. **线程安全**：同步版本是线程安全的，可以在任何线程调用
4. **配置管理**：将API Token等敏感信息存储在配置文件中
5. **日志记录**：记录API调用的关键信息用于调试
