# 名称、Token 绑定与监控分页

本页对应当前父项目 `contracts/newnanmanager.openapi.json` 与 HTTP 实现。旧 `idl/player_service.thrift` 只作为迁移比较基线；六语言只传递请求并解析响应，最终权限、取值范围和状态转换由服务端校验。

## 错误响应迁移

模板重建服务使用统一错误体：`code` 是数值错误码，`message` 是可安全展示的说明，`error.category` 与 `error.code` 是稳定机器标识，`request_id`/`trace_id` 用于关联请求。六语言 SDK 均优先读取这些字段，并在迁移期回退到旧的 `detail` 字段；调用方应使用异常对象中的机器字段做分支，不要解析展示文本。

完整快照的 `players` 必须是数组；`login=false` 清空在线集合时显式传 `[]`，`null` 会被服务端拒绝。SDK 不把 `null` 或缺失值改写为空数组。

## 玩家名称与完整快照

玩家名称继续作为跨平台唯一标识。服务端只对 ASCII 字母做不区分大小写的比较；SDK 不修改输入拼写，也不要求新增 UUID。验证响应的 `player_name` 回显该请求中的原拼写，不能通过大小写转换推断另一个身份。

`ValidateRequest.players` 最多1000人。`login=true` 至少1人；`login=false` 是某服务器的完整在线快照，允许 `[]`，不可拆成多个请求。空快照会表达当前没有在线玩家，请勿把暂时获取失败当成空列表。六语言均不自动分批，回归覆盖0与1000人并保留输入名称。

## Token 服务器绑定

`ApiToken`、创建及更新请求新增可选 `server_id`：Python 使用 `server_id`，Go 使用 `ServerID`，TypeScript/Kotlin 使用 `serverId`，C# 使用 `ServerId`。

创建 `role=server` Token 必须给出正数 int32 服务器ID，其它角色不接受绑定字段。更新 server Token 时，省略绑定字段保留旧值，提供正数会显式重绑；改为其它角色由服务端清空旧绑定。客户端不把省略字段替换为0，也不在本地模拟角色切换。

## Token 列表分页

Token列表实际支持 `page/page_size`（页码>=1，每页1..100），响应为 `tokens/total/page/page_size`。不支持search，TypeScript保留弃用声明但不发送该参数。

| 语言 | 指定页调用 |
| --- | --- |
| Go | `client.Tokens.ListApiTokensPageWithContext(ctx, modules.ListApiTokensRequest{Page: 1, PageSize: 20})` |
| TypeScript | `client.tokens.listApiTokens({ page: 1, pageSize: 20 })` |
| Python | `await client.tokens.list_api_tokens(page=1, page_size=20)` |
| C# | `await client.Tokens.ListApiTokensPageAsync(1, 20, cancellationToken)` |
| Kotlin | `client.tokens.listApiTokens(ListApiTokensRequest(page = 1, pageSize = 20))` |
| Java | `client.tokens().listApiTokens(1, 20)` |

Go/C#旧列表入口保留，仍使用服务端默认页。Python/C#现已保留分页元数据。Python模型中的三个分页字段按IDL必填，手工构造旧的不完整模型需要调整；不要猜测或填造服务端总数。

## 监控字段和分页

状态和历史记录新增可选 `measurement_type` 与 `latency_metric`。当前来源为 `push/pull/unknown`，延迟口径为 `rtt/legacy`；legacy 数值不能直接当成完整 RTT 比较。旧响应缺失字段时保持未知。

监控查询一次返回一页。`duration` 为1..86400秒，省略时服务端默认3600；`limit` 为1..10000，默认1000。`since=0` 沿用服务端“当前时间减duration”的语义。`cursor` 最多1024字符，是服务端生成的不透明值，SDK 负责查询编码并原样传递。

存在 `next_cursor` 时可以继续取下一页。游标冻结原时间窗口；后续请求保留同一个 `server_id/since/duration`，仅更新 cursor，不自行解析或重新计算时间范围。SDK 不自动收集所有页，调用方可逐页消费并设置自己的总量和取消界限。

| 语言 | 单页调用 | 下一页字段 |
| --- | --- | --- |
| Go | `client.Monitor.GetMonitorStatsPageWithContext(ctx, serverID, modules.MonitorStatsQuery{Since: &since, Duration: &duration, Limit: &limit, Cursor: cursor})` | `result.NextCursor` |
| TypeScript | `client.monitor.getMonitorStats({ serverId, since, duration, limit, cursor })` | `result.nextCursor` |
| Python | `await client.monitor.get_monitor_stats(server_id, since, duration, limit=limit, cursor=cursor)` | `result.next_cursor` |
| C# | `await client.Monitor.GetMonitorStatsPageAsync(serverId, new MonitorStatsQuery { Since = since, Duration = duration, Limit = limit, Cursor = cursor }, cancellationToken)` | `result.NextCursor` |
| Kotlin | `client.monitor.getMonitorStats(GetMonitorStatsRequest(serverId, since, duration, limit, cursor))` | `result.nextCursor` |
| Java | `client.monitor().getMonitorStats(serverId, since, duration, limit, cursor)` | `result.getNextCursor()` |

Go 的 since/duration 为 int64、limit 为 int32、cursor 为 `*string`；初次查询可用 nil。C# 新查询方法保留取消令牌；Python 的新分页参数只按关键字传入。TypeScript 和 Kotlin 保持原服务入口。

公开类型迁移与兼容影响见 [契约决策](audits/2026-09-06-contract-decisions.md)。本地 fake/loopback 测试证明 SDK 请求组装和响应保留，不代表服务部署、权限配置或历史数据回填已经完成。
