# TypeScript 调用示例

示例按当前源码的模块入口和请求类型编写，客户端由调用方提供。创建、更新和登录验证会操作所配置服务的数据；下面的函数未在真实服务执行。包名来自本仓库 package.json，公共注册表的发布状态未核实。构建和 HTTP 配置见 [README](../../typescript/README.md)。

## 创建玩家与登录验证

```typescript
import { NewNanManagerClient, ValidateResponse } from '@newnanmanager/typescript-sdk';

export async function createAndValidatePlayer(
  client: NewNanManagerClient,
  name: string,
  ip: string,
  serverId: number
): Promise<ValidateResponse> {
  const player = await client.players.createPlayer({ name, inQqGroup: true });
  await client.players.updatePlayer({ id: player.id, inDiscord: false });
  return client.players.validate({
    players: [{ playerName: player.name, ip, clientVersion: '1.20.1' }],
    serverId,
    login: true
  });
}
```

验证结果按 `results` 中每名玩家的 `allowed/reason` 判断，不能仅用 HTTP 成功判断允许登录。批量验证和完整在线快照的业务约定以服务端契约为准。

## 服务器与 IP 查询

```typescript
import { NewNanManagerClient } from '@newnanmanager/typescript-sdk';

export async function inspectServerAndIp(
  client: NewNanManagerClient,
  serverId: number,
  ip: string
): Promise<void> {
  const server = await client.servers.getServer({ id: serverId, detail: true });
  const ipInfo = await client.ips.getIPInfo({ ip });
  console.log(server.server.id, server.status?.online, ipInfo.riskScore);
}

export async function registerServer(
  client: NewNanManagerClient,
  name: string,
  address: string
): Promise<number> {
  const server = await client.servers.createServer({ name, address });
  return server.id;
}
```

## 条件列表查询

```typescript
import { BanMode, NewNanManagerClient } from '@newnanmanager/typescript-sdk';

export async function listCommunityPlayers(client: NewNanManagerClient): Promise<void> {
  const result = await client.players.listPlayers({
    page: 1,
    pageSize: 20,
    banMode: BanMode.NORMAL
  });
  console.log(result.total, result.players.map(player => player.id));
}
```

旧文档的 `client.listPlayers`、`client.validate` 等顶层调用没有对应实现，应使用上面的服务模块。错误捕获与响应元数据见模块 README；不在日志中输出 Token 或完整异常请求配置。
