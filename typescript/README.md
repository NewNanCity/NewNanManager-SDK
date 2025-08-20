# NewNanManager TypeScript SDK

TypeScript SDK for NewNanManager API - Minecraft server management system.

## 安装

```bash
npm install @newnanmanager/client
# 或
yarn add @newnanmanager/client
# 或
pnpm add @newnanmanager/client
```

## 快速开始

```typescript
import { NewNanManagerClient } from '@newnanmanager/client';

// 创建客户端
const client = new NewNanManagerClient({
  baseUrl: 'https://your-server.com',
  token: 'your-api-token'
});

// 获取服务器列表
const servers = await client.servers.listServers();
console.log(`找到 ${servers.total} 个服务器`);

// 获取玩家列表
const players = await client.players.listPlayers();
console.log(`找到 ${players.total} 个玩家`);

// 创建玩家
const newPlayer = await client.players.createPlayer({
  name: 'PlayerName',
  qq: '123456789',
  inQQGroup: true
});
```

## 功能特性

- ✅ 玩家管理（创建、查询、更新、删除、封禁）
- ✅ 服务器管理（注册、查询、更新、删除）
- ✅ 城镇管理（创建、查询、更新、删除、成员管理）
- ✅ 监控服务（心跳、延迟统计、状态查询）
- ✅ Token管理（创建、查询、更新、删除）
- ✅ 完整的 TypeScript 类型支持
- ✅ 基于 Promise 的异步 API
- ✅ 自动错误处理和重试机制
- ✅ 可配置的 HTTP 客户端
- ✅ 支持 Node.js 和浏览器环境

## 高级配置

```typescript
import { NewNanManagerClient, ClientConfig } from '@newnanmanager/client';

// 使用配置对象
const config: ClientConfig = {
  baseUrl: 'https://your-server.com',
  token: 'your-api-token',
  timeout: 60000,
  userAgent: 'MyApp/1.0.0',
  maxRetries: 3,
  retryDelay: 1000
};

const client = new NewNanManagerClient(config);
```

## API使用示例

### 玩家管理

```typescript
import { BanMode, CreatePlayerRequest, BanPlayerRequest } from '@newnanmanager/client';

// 获取玩家列表（分页）
const players = await client.players.listPlayers({
  page: 1,
  pageSize: 20,
  search: 'player_name',
  townId: 1,
  banMode: BanMode.NORMAL
});

// 创建玩家
const player = await client.players.createPlayer({
  name: 'NewPlayer',
  qq: '123456789',
  townId: 1,
  inQQGroup: true
} as CreatePlayerRequest);

// 封禁玩家
await client.players.banPlayer(player.id, {
  banMode: BanMode.TEMPORARY,
  durationSeconds: 3600,
  reason: '违规行为'
} as BanPlayerRequest);

// 解封玩家
await client.players.unbanPlayer(player.id);
```

### 服务器管理

```typescript
import { ServerType, RegisterServerRequest } from '@newnanmanager/client';

// 注册服务器
const server = await client.servers.registerServer({
  name: 'MyServer',
  address: '127.0.0.1:25565',
  serverType: ServerType.MINECRAFT,
  description: '我的Minecraft服务器'
} as RegisterServerRequest);

// 获取服务器详细信息
const detail = await client.servers.getServerDetail(server.id);
console.log(`服务器状态: ${detail.status?.online ? '在线' : '离线'}`);
```

### 监控服务

```typescript
import { HeartbeatRequest } from '@newnanmanager/client';

// 发送心跳
const heartbeat = await client.monitor.heartbeat(serverId, {
  timestamp: Math.floor(Date.now() / 1000),
  currentPlayers: 10,
  maxPlayers: 50,
  tps: 19.8,
  version: '1.20.1'
} as HeartbeatRequest);

// 获取服务器状态
const status = await client.monitor.getServerStatus(serverId);

// 获取延迟统计
const latencyStats = await client.monitor.getLatencyStats(serverId);
```

### 城镇管理

```typescript
import { CreateTownRequest, ManageTownMemberRequest } from '@newnanmanager/client';

// 创建城镇
const town = await client.towns.createTown({
  name: 'MyTown',
  level: 1,
  description: '我的城镇'
} as CreateTownRequest);

// 获取城镇成员
const members = await client.towns.getTownMembers(town.id);

// 管理城镇成员
await client.towns.manageTownMember(town.id, {
  playerId: playerId,
  action: 'add'
} as ManageTownMemberRequest);
```

### Token管理

```typescript
import { CreateApiTokenRequest } from '@newnanmanager/client';

// 创建API Token
const tokenData = await client.tokens.createApiToken({
  name: 'MyToken',
  role: 'admin',
  description: '管理员Token',
  expireDays: 30
} as CreateApiTokenRequest);

console.log(`新Token: ${tokenData.tokenValue}`);

// 获取Token列表
const tokens = await client.tokens.listApiTokens();
```

## 错误处理

```typescript
import {
    NewNanManagerException,
    ApiErrorException,
    HttpException
} from '@newnanmanager/client';

try {
  const player = await client.players.getPlayer(999);
} catch (error) {
  if (error instanceof ApiErrorException) {
    console.log(`API错误: ${error.errorCode} - ${error.message}`);
    console.log(`请求ID: ${error.requestId}`);
  } else if (error instanceof HttpException) {
    console.log(`HTTP错误: ${error.statusCode} - ${error.message}`);
  } else if (error instanceof NewNanManagerException) {
    console.log(`SDK错误: ${error.message}`);
  }
}
```

## 测试

运行测试：

```bash
# 安装依赖
npm install

# 运行测试
npm test

# 运行测试并生成覆盖率报告
npm run test:coverage
```

## 开发

设置开发环境：

```bash
# 克隆项目
git clone https://github.com/NewNanCity/NewNanManager-SDK.git
cd NewNanManager-SDK/clients/typescript

# 安装依赖
npm install

# 构建项目
npm run build

# 运行类型检查
npm run type-check

# 运行代码检查
npm run lint

# 格式化代码
npm run format
```

## 浏览器支持

- Chrome >= 60
- Firefox >= 60
- Safari >= 12
- Edge >= 79

## Node.js 支持

- Node.js >= 16.0.0

## 许可证

MIT License

```bash
pnpm add axios @sttot/axios-api
```

## 基本使用

### 创建客户端

```typescript
import { NewNanManagerClient, ClientConfig } from '@nanmanager/typescript-sdk';

const config: ClientConfig = {
    token: 'your-api-token',
    baseUrl: 'https://your-server.com',
    timeout: 30000,        // 可选：请求超时时间
    enableLogging: true    // 可选：启用请求日志
};

const client = new NewNanManagerClient(config);
```

### 服务器管理

```typescript
import { ServerType } from '@nanmanager/typescript-sdk';

// 获取服务器列表
const servers = await client.listServers({
    page: 1,
    pageSize: 20,
    search: 'test',
    serverType: ServerType.MINECRAFT,
    onlineOnly: true
});

// 注册新服务器
const newServer = await client.registerServer({
    name: '我的服务器',
    address: 'mc.example.com:25565',
    serverType: ServerType.MINECRAFT,
    description: '这是一个很棒的服务器'
});
```

### 玩家管理

```typescript
import { BanMode } from '@nanmanager/typescript-sdk';

// 创建玩家
const player = await client.createPlayer({
    name: 'PlayerName',
    qq: '123456789',
    inQqGroup: true
});

// 封禁玩家
await client.banPlayer(player.id, {
    banMode: BanMode.TEMPORARY,
    durationSeconds: 3600, // 1小时
    reason: '违反服务器规则'
});
```

## API覆盖

### 玩家管理 (PlayerService)
- `createPlayer()` - 创建玩家
- `getPlayer()` - 获取玩家详情
- `updatePlayer()` - 更新玩家信息
- `deletePlayer()` - 删除玩家
- `listPlayers()` - 获取玩家列表
- `banPlayer()` - 封禁玩家
- `unbanPlayer()` - 解封玩家
- `validateLogin()` - 玩家登录验证

### 城镇管理 (TownService)
- `createTown()` - 创建城镇
- `getTown()` - 获取城镇详情
- `updateTown()` - 更新城镇信息
- `deleteTown()` - 删除城镇
- `listTowns()` - 获取城镇列表
- `manageTownMember()` - 管理城镇成员
- `getTownMembers()` - 获取城镇成员列表

### 服务器管理 (ServerService)
- `registerServer()` - 注册服务器
- `getServer()` - 获取服务器信息
- `updateServer()` - 更新服务器信息
- `deleteServer()` - 删除服务器
- `listServers()` - 获取服务器列表
- `getServerDetail()` - 获取服务器详细信息

### 服务器监控 (MonitorService)
- `heartbeat()` - 服务器心跳
- `getServerStatus()` - 获取服务器状态
- `getLatencyStats()` - 获取延迟统计

### API Token管理 (TokenService)
- `createApiToken()` - 创建API Token
- `getApiToken()` - 获取API Token详情
- `updateApiToken()` - 更新API Token
- `deleteApiToken()` - 删除API Token
- `listApiTokens()` - 获取API Token列表

## 异常处理

```typescript
import {
    NanManagerError,
    AuthenticationError,
    NetworkError
} from '@nanmanager/typescript-sdk';

try {
    const servers = await client.listServers();
} catch (error) {
    if (error instanceof AuthenticationError) {
        console.error('认证失败:', error.message);
    } else if (error instanceof NanManagerError) {
        console.error(`API错误 [${error.code}]:`, error.message);
    } else if (error instanceof NetworkError) {
        console.error('网络错误:', error.message);
    }
}
```

## 拦截器支持

```typescript
// 请求拦截器
client.interceptors.request.use((config) => {
    console.log('发送请求:', config.url);
    return config;
});

// 响应拦截器
client.interceptors.response.use(
    (response) => {
        console.log('收到响应:', response.status);
        return response;
    },
    (error) => {
        console.error('请求失败:', error.message);
        return Promise.reject(error);
    }
);
```

## @sttot/axios-api 特性

本SDK基于@sttot/axios-api构建，享受以下特性：

- **逻辑分离**: API定义和使用分离，无需手动处理axios调用
- **规范化**: 简洁的API定义方式，完整的TypeScript类型推断
- **可扩展性**: 支持自定义axios实例、请求预处理等操作
- **错误处理**: 统一的错误处理机制
- **请求重试**: 内置请求重试支持

## 配置选项

| 参数          | 类型    | 默认值 | 说明                 |
| ------------- | ------- | ------ | -------------------- |
| token         | string  | 必填   | API认证Token         |
| baseUrl       | string  | 必填   | API服务器地址        |
| timeout       | number  | 30000  | 请求超时时间(毫秒)   |
| enableLogging | boolean | false  | 是否启用HTTP请求日志 |

## TypeScript支持

SDK提供完整的TypeScript类型定义：

```typescript
// 完整的类型推断
const servers: ListServersResponse = await client.listServers();
const server: ServerRegistry = servers.servers[0];
const serverType: ServerType = server.serverType;

// 请求参数类型检查
const request: CreatePlayerRequest = {
    name: 'PlayerName',
    qq: '123456789',
    inQqGroup: true
};
```

## 项目结构

```
src/
├── types/          # 类型定义
├── errors/         # 异常类定义
├── apis/           # API定义层
├── examples/       # 使用示例
├── client.ts       # 主客户端类
└── index.ts        # 入口文件
```

## 开发

```bash
# 安装依赖
npm install

# 构建
npm run build

# 测试
npm run test

# 类型检查
npm run type-check

# 代码检查
npm run lint
```

## 许可证

MIT License
