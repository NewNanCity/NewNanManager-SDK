# NewNanManager Kotlin SDK (同步版本)

基于OkHttp+Jackson的同步Kotlin SDK，专为Minecraft插件开发设计。不依赖协程，所有API调用都是同步的，开发者可以自行决定在哪个线程调用。

## 🚀 特性

- **同步API调用**：所有方法都是同步的，不使用协程
- **线程安全**：可以在任何线程中调用，包括Bukkit的主线程和异步线程
- **轻量级依赖**：只依赖OkHttp和Jackson，避免复杂的依赖冲突
- **完整API覆盖**：支持所有NewNanManager API功能
- **类型安全**：完整的Kotlin类型定义，编译时错误检查
- **易于集成**：简单的初始化和使用方式

## 📦 依赖

```kotlin
dependencies {
    implementation("com.nanmanager:kotlin-sdk-sync:1.0.0")
}
```

## 🛠️ 快速开始

### 基本使用

```kotlin
import com.nanmanager.bukkit.NewNanManagerClient
import com.nanmanager.bukkit.models.*

// 创建客户端
val client = NewNanManagerClient(
    token = "your-api-token",
    baseUrl = "https://your-server.com"
)

try {
    // 获取玩家列表
    val players = client.players.listPlayers(page = 1, pageSize = 20)
    println("玩家数量: ${players.total}")

    // 创建玩家
    val newPlayer = client.players.createPlayer(CreatePlayerRequest(
        name = "TestPlayer",
        inQqGroup = true
    ))
    println("创建玩家: ${newPlayer.name}")

    // 获取服务器列表
    val servers = client.servers.listServers()
    println("服务器数量: ${servers.total}")

} finally {
    // 记得关闭客户端
    client.close()
}
```

### 在Minecraft插件中使用

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
        
        // 在异步线程中调用API
        object : BukkitRunnable() {
            override fun run() {
                try {
                    val players = nanClient.players.listPlayers()
                    logger.info("当前有 ${players.total} 个玩家")
                } catch (e: Exception) {
                    logger.warning("获取玩家列表失败: ${e.message}")
                }
            }
        }.runTaskAsynchronously(this)
    }

    override fun onDisable() {
        nanClient.close()
    }
}
```

## 📚 API文档

### 玩家管理 (PlayerService)

```kotlin
// 创建玩家
val player = client.players.createPlayer(CreatePlayerRequest(
    name = "PlayerName",
    qq = "123456789",
    inQqGroup = true
))

// 获取玩家详情
val player = client.players.getPlayer(playerId)

// 更新玩家信息
val updatedPlayer = client.players.updatePlayer(playerId, UpdatePlayerRequest(
    qq = "987654321"
))

// 封禁玩家
client.players.banPlayer(playerId, BanPlayerRequest(
    banMode = BanMode.TEMPORARY,
    durationSeconds = 3600,
    reason = "违规行为"
))

// 解封玩家
client.players.unbanPlayer(playerId)

// 批量玩家验证
val result = client.players.validate(ValidateRequest(
    players = listOf(
        PlayerValidateInfo(
            playerName = "Player1",
            ip = "192.168.1.100"
        )
    ),
    serverId = 1,
    login = true
))
```

### 服务器管理 (ServerService)

```kotlin
// 注册服务器
val server = client.servers.createServer(CreateServerRequest(
    name = "我的服务器",
    address = "mc.example.com:25565",
    description = "服务器描述"
))

// 获取服务器列表
val servers = client.servers.listServers(
    page = 1,
    pageSize = 20,
    onlineOnly = true
)

// 更新服务器信息
val updatedServer = client.servers.updateServer(serverId, UpdateServerRequest(
    name = "新服务器名称"
))
```

### 监控服务 (MonitorService)

```kotlin
// 发送心跳
val heartbeatResponse = client.monitor.heartbeat(serverId, HeartbeatRequest(
    currentPlayers = 10,
    maxPlayers = 50,
    tps = 19.8
))

// 获取服务器状态
val status = client.monitor.getServerStatus(serverId)
println("服务器在线: ${status.online}, 玩家数: ${status.currentPlayers}")

// 获取监控统计
val stats = client.monitor.getMonitorStats(serverId, duration = 3600)
```

### 城镇管理 (TownService)

```kotlin
// 创建城镇
val town = client.towns.createTown(CreateTownRequest(
    name = "新城镇",
    level = 1,
    leaderId = playerId
))

// 获取城镇详情
val townDetail = client.towns.getTown(townId, detail = true)

// 更新城镇信息
val updatedTown = client.towns.updateTown(townId, UpdateTownRequest(
    name = "更新后的城镇名",
    addPlayers = listOf(playerId1, playerId2)
))
```

## 🔧 配置选项

```kotlin
val client = NewNanManagerClient(
    token = "your-api-token",        // API Token
    baseUrl = "https://api.com",     // API基础URL
    timeout = 30L                    // 请求超时时间(秒)，默认30秒
)
```

## 🚨 错误处理

```kotlin
import com.nanmanager.bukkit.exceptions.*

try {
    val player = client.players.getPlayer(999)
} catch (e: ApiException) {
    // API返回的业务错误
    println("API错误: ${e.errorDetail}")
} catch (e: HttpException) {
    // HTTP状态码错误
    println("HTTP错误: ${e.statusCode}")
} catch (e: NetworkException) {
    // 网络连接错误
    println("网络错误: ${e.message}")
} catch (e: JsonParseException) {
    // JSON解析错误
    println("解析错误: ${e.message}")
}
```

## 🔄 线程使用建议

### Bukkit主线程
```kotlin
// 避免在主线程中进行网络请求，会阻塞服务器
// 如果必须在主线程获取结果，使用缓存或预加载
```

### Bukkit异步线程
```kotlin
// 推荐在异步线程中调用API
Bukkit.getScheduler().runTaskAsynchronously(plugin) {
    try {
        val players = client.players.listPlayers()
        // 如果需要操作Bukkit API，切回主线程
        Bukkit.getScheduler().runTask(plugin) {
            // 在主线程中更新UI或执行Bukkit操作
        }
    } catch (e: Exception) {
        plugin.logger.warning("API调用失败: ${e.message}")
    }
}
```

## 📋 完整功能列表

- ✅ **玩家管理**: 创建、查询、更新、删除、封禁、解封、批量验证
- ✅ **城镇管理**: 创建、查询、更新、删除、成员管理
- ✅ **服务器管理**: 注册、查询、更新、删除、详细信息
- ✅ **监控功能**: 心跳、状态查询、统计数据
- ✅ **Token管理**: 创建、查询、更新、删除、列表
- ✅ **IP管理**: 查询、封禁、解封、统计
- ✅ **玩家服务器关系**: 在线状态、服务器列表

## 🤝 技术支持

如有问题或建议，请联系开发团队或查看项目文档。
