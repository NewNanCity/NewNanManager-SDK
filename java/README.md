# NewNanManager Java facade

这是基于 `generated/java` 的同步 Java 业务 facade，目标运行时为 Java 8+。生成客户端负责 HTTP 路径和模型，facade 负责客户端配置、Bearer/API Token 二选一、标准错误映射以及各服务的薄调用入口。

## 构建

从 SDK 根目录执行 Maven reactor，可以同时编译生成层和 facade：

```powershell
mvn -B -ntp -f pom.xml test
```

`generated/java` 仍然可以单独构建和发布；发布 facade 前需要先发布同版本的 `com.newnancity:newnanmanager-generated-java`。

## 使用

```java
import com.newnancity.newnanmanager.AuthScheme;
import com.newnancity.newnanmanager.NewNanManagerClient;
import com.newnancity.newnanmanager.exceptions.ApiException;
import com.newnancity.newnanmanager.generated.model.ListPlayersResponse;

try (NewNanManagerClient client = NewNanManagerClient.builder()
        .baseUrl("https://manager.example.com")
        .token(System.getenv("NANMANAGER_TOKEN"))
        .authScheme(AuthScheme.API_TOKEN)
        .build()) {
    ListPlayersResponse players = client.players().listPlayers();
} catch (ApiException exception) {
    System.err.println(exception.getStatusCode());
    System.err.println(exception.getErrorCategory());
    System.err.println(exception.getMachineCode());
}
```

默认超时为 30 秒，自动重定向和 OkHttp 传输层自动重试均关闭。`429` 的 `Retry-After` 会通过 `HttpException#getRetryAfter()` 保留，由业务决定是否重试；facade 不会自动重发写请求或读取请求。

服务器插件先调用 `client.monitor().createServerSession(serverId)`，再把返回的 `SessionContext` 传给 `players().validate(request, session)`、`monitor().heartbeat(serverId, request, session)` 和 `playerServers().setPlayersOffline(request, session)`。原始 `String sessionId, long sessionEpoch` 重载保留兼容并复用相同校验；typed context 会在网络请求前拒绝空 ID、长度不在 32–64 之间的 ID 和非正 epoch。

服务方法复用生成层模型，并保留 Guardian 所需的 session/epoch 参数：`client.monitor().heartbeat(...)`、`client.players().validate(...)` 和 `client.playerServers().setPlayersOffline(...)`。需要尚未封装的生成端点时，可使用对应服务的 `raw()`，或调用 `client.execute(...)` 统一映射生成异常。

标准错误会映射为 `com.newnancity.newnanmanager.exceptions.ApiException` 或 `HttpException`，保留 HTTP 状态、响应头、响应体、`request_id`、`trace_id`、`Retry-After` 以及机器错误字段。SDK 不替代服务端的 API Token、Casbin 和业务不变量校验。
