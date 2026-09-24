# NewNanManager TypeScript SDK

基于 Axios 与 canonical OpenAPI 生成 transport 的 Promise 客户端。`NewNanManagerClient` 的服务调用通过构建前同步的 `src/generated/` 进入生成 API；该目录由 `scripts/sync-generated.mjs` 从 `../generated/typescript/` 重建，不能手工编辑。

## 使用

```typescript
import { AuthScheme, NewNanManagerClient, NewNanManagerHttpError } from '@newnanmanager/typescript-sdk';

const client = new NewNanManagerClient({
  baseUrl: 'https://your-api.example',
  token: process.env.NANMANAGER_TOKEN!,
  timeout: 30000,
  authScheme: AuthScheme.BEARER
});

try {
  const players = await client.players.listPlayers({ page: 1, pageSize: 20 });
  console.log(players.total);
} catch (error: unknown) {
  if (error instanceof NewNanManagerHttpError) {
    console.error(error.statusCode, error.requestId, error.retryAfter);
  } else {
    throw error;
  }
}
```

入口为 players、servers、towns、monitor、tokens、ips、playerServers。所有调用使用请求对象：

```typescript
await client.players.getPlayer({ id: 1 });
await client.players.updatePlayer({ id: 1, qq: '', inDiscord: false });
await client.servers.getServer({ id: 1, detail: true });
await client.towns.getTown({ id: 1, detail: true });
await client.monitor.heartbeat({ serverId: 1, currentPlayers: 10, maxPlayers: 50 });
await client.playerServers.getServerPlayers({ serverId: 1, onlineOnly: true });

const session = await client.monitor.createServerSession(1);
await client.monitor.heartbeat({ serverId: 1, currentPlayers: 10, maxPlayers: 50 }, session);
await client.players.validate({ serverId: 1, login: false, players: [] }, session);
```

创建玩家、批量验证、服务器注册和 IP 查询的完整函数见 [调用示例](../docs/examples/typescript.md)。

## HTTP 与兼容

默认超时 30 秒，默认使用 Authorization；传入 `authScheme: AuthScheme.API_TOKEN` 时只发送 `X-API-Token`。在 Node HTTP 适配器中拒绝自动重定向。NewNanManagerHttpError 兼容普通 Error 捕获，提供 statusCode、code、requestId、retryAfter；不保存原始 Axios config、请求头、完整响应体或带凭证的 cause。

浏览器自身处理重定向，Axios 的 maxRedirects 仅适用于 Node。本轮仅完成 Node fake/loopback 验证，未验证浏览器跨源重定向；浏览器场景必须使用最终 API URL 和服务端重定向控制。

`monitor.heartbeat`、`players.validate` 和 `playerServers.setPlayersOffline` 的第二个参数是 `SessionContext`，会生成 `X-NNM-Session-ID` 与 `X-NNM-Session-Epoch`；省略时保留旧 HTTP 兼容入口，不伪造 fencing 头。会话 ID 为空、长度不在 32–64 之间或代次非正整数会在网络请求前拒绝。

未提供的更新字段不发送，显式 false 和空字符串保留。以下契约纠正需要调用方检查源码：

- PlayerServersResponse 只包含 servers/total，移除服务端没有的 page/pageSize。
- IPBan.bannedAt 改为可选弃用字段，不再把 IP 创建时间当成封禁时间；返回值保持未知。activeOnly/search 标记弃用，被忽略且不发出，封禁列表仅支持分页。

日期格式化和分页组件需按 [契约决策](../docs/audits/2026-09-06-contract-decisions.md) 调整。

## 新增契约

Token 请求与响应增加 serverId；ServerStatus/MonitorStatRecord 增加 measurementType/latencyMetric。getMonitorStats({ serverId, since, duration, limit, cursor }) 返回 nextCursor，保留范围并传回游标可继续查询。缺失元数据保持 undefined，不补造来源或时间。

名称、0..1000人完整快照、绑定约束和监控上限见 [契约与分页](../docs/contracts.md)。本轮改动尚未发布。

Token列表使用 `client.tokens.listApiTokens({ page: 1, pageSize: 20 })` 并返回真实分页元数据；`search` 仅保留弃用类型，不再发送后端不支持的筛选参数。

## 依赖验收

Axios最低版本提高到1.20.0，Lodash显式声明为4.18.1及同主版本兼容更新，以约束既有运行时peer依赖。npm/pnpm锁文件的86组包名与版本一致，包含follow-redirects1.16.0和form-data4.0.6；官方源audit由8个命中包降至0，11项本地回归与构建通过。

复现安装可用 `npm ci --ignore-scripts --registry=https://registry.npmjs.org --replace-registry-host=always`，随后执行下列测试。部分未变更锁条目保留旧镜像URL；显式registry与replace-registry-host确保安装使用指定公开源。glob10.5.0仍被上游标记deprecated，当前audit未命中；本轮没有跨主版本升级开发工具。完整范围见[依赖验收](../docs/audits/2026-09-06-dependency-acceptance.md)。

## 本地检查

```bash
npm test
npm run build
```

npm test 使用 Node test runner 和 fake/loopback，覆盖生成客户端路径、错误元数据及脱敏、两种认证、session headers、可选更新、重定向、实际响应字段、Token绑定、分页、完整快照和集成配置缺失分支。构建清理旧 dist，并在构建前同步生成源码；不编译/打包集成 runner。

生成层同步也可单独执行：`npm run sync-generated`。这一步只复制带 `.nnm-generated.json` 标记的五个生成源文件，生成器漂移检查仍由 SDK 根目录的 `python codegen/generate.py --all --check` 负责。

test:integration 与 test:comprehensive 必须同时配置 NANMANAGER_TOKEN 和 NANMANAGER_BASE_URL；缺失时非零退出。它们会操作指定服务的测试数据，不属于本地验收。历史凭证未轮换或改写历史；本地源码不再提供凭证默认值。
