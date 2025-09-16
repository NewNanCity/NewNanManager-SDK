# NewNanManager TypeScript SDK (重新设计版)

NewNanManager API 的 TypeScript SDK，重新设计，简洁高效。

## 🚀 特性

- ✅ **批量玩家验证** - 支持1-100个玩家同时验证
- ✅ **IP管理** - IP信息查询、封禁、解封
- ✅ **玩家服务器关系管理** - 在线状态、服务器列表
- ✅ **玩家管理** - 创建、查询、更新、删除、封禁
- ✅ **服务器管理** - 注册、查询、更新、删除
- ✅ **城镇管理** - 创建、查询、更新、删除、成员管理
- ✅ **完整的 TypeScript 类型支持**
- ✅ **基于 Promise 的异步 API**
- ✅ **模块化架构** - 按服务划分的清晰模块结构
- ✅ **逻辑分离** - 每个服务模块职责单一，易于维护
- ✅ **类型安全** - 完整的 TypeScript 类型推断和验证
- ✅ **向后兼容** - 保持原有API调用方式的兼容性
- ✅ **简洁的错误处理** - 统一的错误处理机制
- ✅ **新的响应格式** - 成功时直接返回数据，错误时返回 `{"detail": "错误信息"}`

## 📦 安装

```bash
npm install @newnanmanager/typescript-sdk
```

**注意**: 此版本基于 `@sttot/axios-api` 实现了全新的模块化架构，提供更好的逻辑分离、标准化API定义和类型安全。

## 🔧 快速开始

### 基本用法

```typescript
import { NewNanManagerClient, BanMode } from '@newnanmanager/typescript-sdk';

// 初始化客户端
const client = new NewNanManagerClient({
  baseUrl: 'http://your-api-server.com',
  token: 'your-api-token'
});

// 方式1: 使用模块化API（推荐）
const players = await client.players.listPlayers({
  page: 1,
  pageSize: 20,
  banMode: BanMode.NORMAL
});

// 方式2: 使用向后兼容的方法
const playersCompat = await client.listPlayers({
  page: 1,
  pageSize: 20,
  banMode: BanMode.NORMAL
});

// 批量玩家验证
const validateResult = await client.players.validate({
  players: [
    {
      playerName: 'Player1',
      ip: '192.168.1.100',
      clientVersion: '1.20.1'
    }
  ],
  serverId: 1,
  login: true
});
```

### 🏗️ 模块化架构

新版SDK采用模块化架构，每个服务都有独立的模块：

```typescript
// 玩家管理模块
await client.players.createPlayer({ name: 'TestPlayer' });
await client.players.listPlayers();
await client.players.banPlayer({ playerId: 1, reason: 'Test' });

// 服务器管理模块
await client.servers.createServer({ name: 'TestServer', address: '127.0.0.1:25565' });
await client.servers.listServers();
await client.servers.getServer({ id: 1, detail: true }); // 获取服务器详情

// 城镇管理模块
await client.towns.createTown({ name: 'TestTown', serverId: 1 });
await client.towns.listTowns();

// 监控服务模块
await client.monitor.heartbeat({ serverId: 1, playerCount: 10 });
await client.monitor.getServerStatus({ serverId: 1 });

// Token管理模块
await client.tokens.createApiToken({ name: 'TestToken' });
await client.tokens.listApiTokens();

// IP管理模块
await client.ips.getIPInfo({ ip: '192.168.1.1' });
await client.ips.banIP({ ip: '192.168.1.1', reason: 'Test' });

// 玩家服务器关系模块
await client.playerServers.setPlayerOnline({ playerId: 1, serverId: 1, isOnline: true });
await client.playerServers.getOnlinePlayers({ serverId: 1 });
```

### 📚 示例用法

```typescript
// IP管理（新功能）
const ipInfo = await client.ips.getIPInfo({ ip: '8.8.8.8' });
await client.banIP({
  ip: '192.168.1.100',
  reason: '恶意行为'
});

// 玩家服务器关系管理（新功能）
await client.setPlayerOnline({
  playerId: 1,
  serverId: 1,
  online: true
});

const onlinePlayers = await client.getOnlinePlayers({
  page: 1,
  pageSize: 20
});
```

## 📚 API 文档

### 玩家管理

```typescript
// 创建玩家
const player = await client.createPlayer({
  name: 'NewPlayer',
  qq: '123456789',
  inQqGroup: true
});

// 获取玩家列表
const players = await client.listPlayers({
  page: 1,
  pageSize: 20,
  search: 'player_name',
  townId: 1,
  banMode: BanMode.NORMAL
});
```

### 批量验证（新功能）

```typescript
const validateResult = await client.validate({
  players: [
    {
      playerName: 'Player1',
      ip: '192.168.1.100',
      clientVersion: '1.20.1',
      protocolVersion: '763'
    },
    {
      playerName: 'Player2',
      ip: '192.168.1.101'
    }
  ],
  serverId: 1,
  login: true,
  timestamp: Date.now()
});

console.log(`验证了${validateResult.results.length}个玩家`);
validateResult.results.forEach(result => {
  console.log(`${result.playerName}: ${result.allowed ? '允许' : '拒绝'}`);
});
```

### IP管理（新功能）

```typescript
// 获取IP信息
const ipInfo = await client.getIPInfo('8.8.8.8');
console.log(`IP: ${ipInfo.ip}, 国家: ${ipInfo.country}`);

// 封禁IP
await client.banIP({
  ip: '192.168.1.100',
  reason: '恶意行为'
});

// 解封IP
await client.unbanIP({
  ip: '192.168.1.100',
  reason: '误封'
});

// 获取封禁IP列表
const bannedIPs = await client.listBannedIPs({
  page: 1,
  pageSize: 20,
  activeOnly: true
});
```

### 玩家服务器关系管理（新功能）

```typescript
// 设置玩家在线状态
await client.setPlayerOnline({
  playerId: 1,
  serverId: 1,
  online: true
});

// 获取玩家的服务器列表
const playerServers = await client.getPlayerServers(1, 1, 10);

// 获取服务器的玩家列表
const serverPlayers = await client.getServerPlayers(1, 1, 10);

// 获取在线玩家列表
const onlinePlayers = await client.getOnlinePlayers({
  page: 1,
  pageSize: 20,
  serverId: 1  // 可选：过滤特定服务器
});
```

## 🧪 测试

```bash
# 安装依赖
npm install

# 运行测试
npm test
```

## 🏗️ 构建

```bash
npm run build
```

## 📄 许可证

MIT
