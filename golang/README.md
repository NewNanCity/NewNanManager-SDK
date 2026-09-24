# NewNanManager Go SDK

基于 Resty 的同步客户端。模块路径沿用 go.mod 的 `github.com/NewNanCity/NewNanManager-SDK/clients/golang`，本地目录是 golang/；本轮不调整模块发布路径。

## 使用

```go
package main

import (
    "context"
    "errors"
    "log"
    "os"
    "time"

    nanmanager "github.com/NewNanCity/NewNanManager-SDK/clients/golang"
    "github.com/NewNanCity/NewNanManager-SDK/clients/golang/utils"
)

func main() {
    client := nanmanager.NewNanCityManager(os.Getenv("NANMANAGER_BASE_URL"), os.Getenv("NANMANAGER_TOKEN"))
    client.SetTimeout(10 * time.Second)
    ctx, cancel := context.WithTimeout(context.Background(), 3 * time.Second)
    defer cancel()
    player, err := client.Players.GetPlayerWithContext(ctx, 1)
    if err != nil {
        var httpError *utils.HTTPError
        if errors.As(err, &httpError) {
            log.Printf("status=%d request_id=%s", httpError.StatusCode, httpError.RequestID)
        }
        return
    }
    log.Printf("player_id=%d", player.ID)
}
```

入口为 Players、Servers、Towns、Tokens、IPs、Monitor 和 PlayerServers，请求/响应类型位于 modules 包。

创建和更新玩家、批量验证、临时封禁与服务器详情见 [调用示例](../docs/examples/golang.md)。

## HTTP 约定

默认总请求超时 30 秒。兼容新增的 SetTimeout 应在并发使用之前配置；零值显式关闭截止时间。默认发送 Authorization；只提供 `X-API-Token` 的部署可使用 `NewNanCityManagerWithAuthScheme(..., AuthSchemeAPIToken)`，客户端只会发送选中的一种凭证头。拒绝自动重定向，默认不重试写入请求。

服务器插件应先调用 `client.Monitor.CreateServerSession(serverID)`，再把返回的 `modules.SessionContext` 传给 `ValidateWithSession`、`HeartbeatWithSession` 和 `SetPlayersOfflineWithSession`（或对应的 `WithContextAndSession` 入口）。会话 ID 遵循服务端 32–64 字符约束；旧方法保留兼容 HTTP 路径，不会伪造 fencing 头。

utils.HTTPError 保留 StatusCode、Detail、RequestID 和原始 RetryAfter 值。网络错误保留错误链，可用 errors.Is 判断 deadline。

每个服务操作都有 XxxWithContext(ctx, ...) 入口，ctx 只作用于本次请求，不修改共享客户端。原方法签名保留，使用 context.Background() 转调；取消一个请求不会取消其他并发请求。

六个旧统计计数已从 int32 改为 int64，全部真实计数对齐 IDL。SuspiciousIPs 仅作为弃用字段保留，服务端不提供该数量。旧 int32 变量需要迁移，见 [契约决策](../docs/audits/2026-09-06-contract-decisions.md)。

## 新增契约

Token 请求与响应增加 ServerID；ServerStatus/MonitorStatRecord 增加 MeasurementType/LatencyMetric。监控查询使用 GetMonitorStatsPageWithContext(ctx, serverID, modules.MonitorStatsQuery{...})，通过 NextCursor 续页；旧 GetMonitorStats 入口保留，也只返回一页。

完整字段限制、名称和0..1000人快照语义见 [契约与分页](../docs/contracts.md)。新增字段为指针，nil 保持未知或省略；本轮改动尚未发布。

Token单页入口为 `client.Tokens.ListApiTokensPageWithContext(ctx, modules.ListApiTokensRequest{Page: 1, PageSize: 20})`，也提供无context版本；原 `ListApiTokens[WithContext]()` 签名保留并使用服务端默认分页。响应的Total/Page/PageSize用于续页判断。

## 依赖边界

go.mod最低版本提高到Go1.25.0，x/net升级到0.56.0，Resty保持2.11.0。Go1.21至1.24不再受支持；下游需先升级工具链并更新依赖。实际验收使用Go1.25.14，部署或构建时应使用所在受支持系列的安全补丁版本。

Go1.25.14的三包race、vet、模块校验和tidy检查通过；govulncheck模块公告由上一阶段的12条降为0。扫描针对本SDK的解析结果，不能替代下游整个应用或根后端的扫描。官方最低版本依据及验收记录见[依赖验收](../docs/audits/2026-09-06-dependency-acceptance.md)。

## 验证

```powershell
$env:GOTOOLCHAIN = 'go1.25.14'
$env:GOPROXY = 'https://proxy.golang.org'
$env:GOSUMDB = 'sum.golang.org'
go test -race ./... -count=1 -timeout=120s
go vet ./...
go mod verify
go mod tidy -diff
```

测试使用 fake/loopback，覆盖请求取消及并发隔离、默认/配置超时、认证头、重定向、错误元数据、int64计数、Token绑定、监控分页与完整快照。没有真实服务或性能验收。
