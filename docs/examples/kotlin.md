# Kotlin 调用示例

当前只有 OkHttp/Jackson 同步客户端。客户端创建、超时与异常元数据见 [README](../../kotlin/README.md)。下面保留已核实的创建和批量验证用法，并将旧文档的参数式查询、更新改为现有请求类型；函数未在真实服务执行。

## 创建、更新与验证

```kotlin
import com.nanmanager.bukkit.NewNanManagerClient
import com.nanmanager.bukkit.models.*

fun createAndValidatePlayer(
    client: NewNanManagerClient,
    name: String,
    ip: String,
    serverId: Int
): ValidateResponse {
    val player = client.players.createPlayer(CreatePlayerRequest(name = name, inQqGroup = true))
    client.players.updatePlayer(UpdatePlayerRequest(id = player.id, inDiscord = false))
    return client.players.validate(ValidateRequest(
        players = listOf(PlayerValidateInfo(playerName = player.name, ip = ip)),
        serverId = serverId,
        login = true
    ))
}
```

创建、更新和登录验证会操作指定服务数据。使用验证结果时逐项检查 `allowed/reason`；批量验证的业务含义以服务端契约为准。

## 服务器注册与详情

```kotlin
import com.nanmanager.bukkit.NewNanManagerClient
import com.nanmanager.bukkit.models.*

fun registerAndInspectServer(
    client: NewNanManagerClient,
    name: String,
    address: String
): ServerDetailResponse {
    val server = client.servers.createServer(CreateServerRequest(name = name, address = address))
    return client.servers.getServer(GetServerRequest(id = server.id, detail = true))
}
```

详情中的 `status` 可为空。玩家详情使用 `GetPlayerRequest(id = ...)`，列表使用 `ListPlayersRequest(...)`，不能使用旧文档中的裸 ID 或不带请求对象的调用。

## Bukkit 线程切换

这段示例要求宿主插件提供 Bukkit API 依赖。网络请求放在异步任务；需要使用 Bukkit 游戏状态时切回主线程。

```kotlin
import com.nanmanager.bukkit.NewNanManagerClient
import com.nanmanager.bukkit.exceptions.NewNanManagerException
import com.nanmanager.bukkit.models.ListPlayersRequest
import org.bukkit.plugin.java.JavaPlugin

fun queryPlayersAsync(plugin: JavaPlugin, client: NewNanManagerClient) {
    plugin.server.scheduler.runTaskAsynchronously(plugin, Runnable {
        try {
            val players = client.players.listPlayers(ListPlayersRequest(page = 1, pageSize = 20))
            plugin.server.scheduler.runTask(plugin, Runnable {
                plugin.logger.info("player count=${players.total}")
            })
        } catch (error: NewNanManagerException) {
            plugin.logger.warning("API request failed: ${error.javaClass.simpleName}")
        }
    })
}
```

插件停用时停止提交新任务并等待自己拥有的任务退出。SDK 的 `close()` 不能替代宿主任务的生命周期管理；本轮没有运行真实 Bukkit 集成，也未核实 Maven 公共发布坐标。
