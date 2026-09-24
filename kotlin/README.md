# NewNanManager Kotlin SDK

当前实现是 com.nanmanager.bukkit 下的 OpenAPI generated transport + OkHttp/Jackson 业务 facade。网络调用应放在 Bukkit 异步线程；没有可用的 Ktor 协程版本。生成源码来自 `../generated/kotlin/`，由 `codegen/generate.py` 管理，不手工编辑。

## 使用

```kotlin
import com.nanmanager.bukkit.NewNanManagerClient
import com.nanmanager.bukkit.models.*
import com.nanmanager.bukkit.exceptions.ApiException

fun queryPlayers(baseUrl: String, token: String) {
    NewNanManagerClient(token = token, baseUrl = baseUrl, timeout = 30L).use { client ->
        try {
            val result = client.players.listPlayers(ListPlayersRequest(page = 1, pageSize = 20))
            println(result.total)
            val server = client.servers.getServer(GetServerRequest(id = 1, detail = true))
            println(server.status?.currentPlayers)
        } catch (error: ApiException) {
            println("status=${error.statusCode}, requestId=${error.requestId}")
        }
    }
}
```

默认使用 Bearer；接入只提供 `X-API-Token` 的服务端时传入 `authScheme = AuthScheme.API_TOKEN`。服务器插件先创建会话，再把返回的 `SessionContext` 传给心跳、玩家验证和批量离线方法：

```kotlin
import com.nanmanager.bukkit.AuthScheme
import com.nanmanager.bukkit.SessionContext

NewNanManagerClient(token = token, baseUrl = baseUrl).use { client ->
    val session = client.monitor.createServerSession(serverId = 7)
    client.monitor.heartbeat(7, HeartbeatRequest(currentPlayers = 1, maxPlayers = 20), session)
    client.players.validate(validateRequest, session)
}
```

玩家、城镇、服务器、Token、IP 和玩家服务器关系方法接收 models 中的请求类型。心跳用 client.monitor.heartbeat(serverId, HeartbeatRequest(...))；统计用 client.monitor.getMonitorStats(GetMonitorStatsRequest(serverId = 1))。

创建玩家、批量验证、服务器注册与 Bukkit 线程切换见 [调用示例](../docs/examples/kotlin.md)。

## HTTP 约定

只使用选定的一种认证头（Authorization 或 X-API-Token），拒绝自动重定向和隐式连接重试。连接、读、写及整个调用均有 timeout，默认 30 秒。查询参数通过生成客户端和 OkHttp URL builder 编码。

JSON detail 错误保持 ApiException 类型，包含 statusCode/requestId/retryAfter；非 JSON 错误为 HttpException。JSON 解析异常只报告异常类别，不保存可能含响应体片段的原始解析异常链。

`validate(request)`、`heartbeat(serverId, request)` 和 `setPlayersOffline(request)` 保留旧签名以兼容旧调用，但不会伪造 fencing 头；连接服务器时应使用带 `SessionContext` 的重载。会话 ID 遵循服务端 32–64 字符约束。生成客户端的错误响应统一映射回 facade 异常，保留状态码、request ID、trace ID 和 Retry-After。

构造器和原异常构造签名保留；错误元数据为兼容新增属性。服务端未提供的头为 null。

## 新增契约

Token 请求与响应增加 serverId；ServerStatus/MonitorStatRecord 增加 measurementType/latencyMetric。GetMonitorStatsRequest 增加可选 limit/cursor，响应提供 nextCursor；缺失元数据保持 null。名称、0..1000人完整快照和分页约束见 [契约与分页](../docs/contracts.md)。

新数据类字段追加在构造参数尾部且有默认值，现有 Kotlin 源码调用可重新编译；JVM 构造器及 copy 二进制签名变化，下游必须重新构建。原 HTTP/异常构造器未变，本轮改动尚未发布。

## 依赖验收

Jackson直接依赖从2.18.3兼容升到2.18.10；解析出的annotations/BOM同步升级，其余7个非Jackson运行组件不变。Kotlin2.2.0、Java21、OkHttp5.1.0保持不变。对实际runtimeClasspath的12个精确组件坐标查询公开OSV，匹配由9条降到0。

新增回归先用64字节分块数字复现旧Jackson约束绕过，再验证修复版；另经实际SDK请求验证int64分页、false和可选空字段。依赖级复现不是SDK可利用证明，扫描不包含构建插件或测试依赖。证据和官方修复线见[依赖验收](../docs/audits/2026-09-06-dependency-acceptance.md)。

## 验证

使用已缓存的 Java 21、Gradle 与依赖，在 kotlin/ 中执行：

```powershell
.\gradlew.bat --offline test
```

当前回归覆盖构造、编码、两种认证、生成客户端路径/查询、结构化错误、session headers、空响应、Token绑定、分页、未知监控字段、完整快照和Jackson兼容性。不连接真实 API，不代表 Minecraft 或生产验收。
