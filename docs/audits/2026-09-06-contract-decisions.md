# SDK 契约纠正与迁移

2026-09-06 用户授权解决待决修复后实施。事实源为父项目 `idl/player_service.thrift` 及对应 HTTP 服务；D03/D04 的以下问题已完成本地回归。本仓库未提交、发布或修改后端 IDL。

## Go IPStatistics 计数宽度

Before：`IPStatistics.TotalIPs/BannedIPs/HighRiskIPs/ProxyIPs/VPNIPs/TorIPs int32`。

After：以上六个字段均为 `int64`，与 IDL 的 `i64` 对齐；`CompletedIPs/PendingIPs/FailedIPs/DatacenterIPs` 也是 `int64`。旧 `SuspiciousIPs int32` 保留并标记 Deprecated，服务端不提供这个统计，不能把其零值解释为真实数量。

源码影响：`IPStatistics{TotalIPs: int32Value}` 改用 `int64(int32Value)`；接收统计的局部变量、函数参数和累加器改用 `int64`。需要转回 `int32` 时先检查范围。回归分别返回 `2147483648` 与 `9223372036854775807`，六项计数均完整解析。

## TypeScript 玩家服务器关系分页

Before：`interface PlayerServersResponse extends PaginationResponse`，承诺必有 `page: number; pageSize: number`。

After：`interface PlayerServersResponse { servers: PlayerServer[]; total: number }`。映射不再生成两个值为 undefined 的假分页属性；此接口没有分页。

源码影响：访问 `response.page/pageSize`、将响应当成 `PaginationResponse` 使用的代码需要修改。使用 `servers/total`；不要给结果补猜测的页码。

## TypeScript IP 封禁时间和筛选

Before：`IPBan.bannedAt: string`，从 IP 的 `created_at` 伪造封禁时间。

After：`bannedAt?: string` 且标记 Deprecated，响应不填充该字段。`unbannedAt/unbanReason` 同样只保留弃用的可选声明，接口不提供封禁时间或解封历史。调用方应展示未知时间，不应回退到创建或更新时间。

`ListBannedIPsRequest.search/activeOnly` 保留为 Deprecated 字段以减小源码影响；它们被忽略且不发送。该服务实际只使用 `page/page_size`。需要按风险条件筛选当前封禁 IP 时调用已有 `listIPs({ bannedOnly: true, minThreatLevel, minRiskScore })`；没有服务端支持的历史查询或文本搜索替代。

源码影响：将 `bannedAt` 直接传给 `new Date()` 等要求 string 的调用需要处理 undefined。依赖 `activeOnly=false` 返回解封记录的逻辑此前就不成立。

## Go 请求级取消

原有36个服务方法签名保持不变，每个增加 `XxxWithContext(ctx context.Context, ...)`。旧入口使用 `context.Background()` 转调。context 只设置到该次 Resty Request，不保存到共享客户端或服务实例；同一客户端可并发使用独立 context。

取消或超时可用 `errors.Is(err, context.Canceled/context.DeadlineExceeded)` 判断。`SetTimeout` 仍需在并发使用前配置；调用方应传入非 nil context 并调用 cancel 释放截止时间资源。

## 新增监控与 Token 字段

五语言均增加可选 Token `server_id`、状态和历史记录 `measurement_type/latency_metric`、监控 `limit/cursor/next_cursor`，详见 [约束与调用](../contracts.md)。未知元数据保持 nil/None/null/undefined，不补默认来源。

Go 新增 `GetMonitorStatsPage[WithContext](..., MonitorStatsQuery)`，C# 新增 `GetMonitorStatsPageAsync(..., MonitorStatsQuery, CancellationToken)`，保留原统计方法签名；Python 新增 keyword-only `limit/cursor`；TypeScript/Kotlin 扩展已有请求对象。原统计调用现在也应检查下一页游标，不能把受限的一页当成完整历史。

Go 新增可选字段不影响带字段名的结构体字面量；使用不带字段名的位置式字面量的下游需要修改，建议改为 keyed literal。

Kotlin 新字段追加到数据类构造参数尾部并使用默认 null，现有 Kotlin 源码调用可重新编译；数据类 JVM 构造器、copy 等二进制签名变化，下游必须重新构建。所有 SDK 修改都尚未发布，包安装命令不表示公共源已经包含这些修复。

## Token 分页与服务器模型补齐

Python `ListApiTokensData` 从仅tokens补齐IDL必填的 `total: int; page: int; page_size: int`。真实后端响应含这些字段；手工构造 `ListApiTokensData(tokens=...)` 的旧代码现在必须提供三个真实值，缺字段的fixture会ValidationError。这是模型构造兼容变化，不用默认0或猜测页码隐藏缺失。

Go新增 `ListApiTokensPage(request ListApiTokensRequest)` 与 `ListApiTokensPageWithContext(ctx, request)`；C#新增 `ListApiTokensPageAsync(int page, int pageSize, CancellationToken)`。原无分页入口和取消参数保留。C#列表响应增加long Total及int Page/PageSize；TypeScript Token搜索参数仅保留弃用类型、不发送。

C# `ServerRegistry` 增加bool Active；原 `ServerType ServerType` 的类型保持不变并标记Obsolete/JsonIgnore。服务端没有server_type字段，该属性只作为本地兼容值，不参与序列化或反序列化；不得把它的默认枚举值当作后端事实。
