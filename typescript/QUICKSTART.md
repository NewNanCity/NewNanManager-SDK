# NewNanManager TypeScript SDK 快速开始

## 1. 安装依赖

```bash
# 使用npm
npm install axios @sttot/axios-api

# 使用yarn
yarn add axios @sttot/axios-api

# 使用pnpm
pnpm add axios @sttot/axios-api
```

## 2. 创建客户端

```typescript
import { NewNanManagerClient } from '@nanmanager/typescript-sdk';

const client = new NewNanManagerClient({
    token: 'your-api-token-here',
    baseUrl: 'https://your-nanmanager-server.com'
});
```

## 3. 基本使用

### 服务器管理

```typescript
import { ServerType } from '@nanmanager/typescript-sdk';

// 获取服务器列表
const servers = await client.listServers();
console.log(`找到 ${servers.total} 个服务器`);

// 注册新服务器
const newServer = await client.registerServer({
    name: '我的服务器',
    address: 'mc.example.com:25565',
    serverType: ServerType.MINECRAFT
});
```

### 玩家管理

```typescript
import { BanMode } from '@nanmanager/typescript-sdk';

// 获取玩家列表
const players = await client.listPlayers();
console.log(`找到 ${players.total} 个玩家`);

// 创建新玩家
const newPlayer = await client.createPlayer({
    name: 'PlayerName',
    qq: '123456789'
});
```

### 城镇管理

```typescript
// 获取城镇列表
const towns = await client.listTowns();
console.log(`找到 ${towns.total} 个城镇`);

// 创建新城镇
const newTown = await client.createTown({
    name: '美丽小镇',
    level: 1
});
```

## 4. 异常处理

```typescript
import { 
    NanManagerError, 
    AuthenticationError, 
    NetworkError 
} from '@nanmanager/typescript-sdk';

try {
    const servers = await client.listServers();
    // 处理成功响应
} catch (error) {
    if (error instanceof AuthenticationError) {
        console.error('认证失败，请检查Token');
    } else if (error instanceof NanManagerError) {
        console.error(`API错误 [${error.code}]:`, error.message);
    } else if (error instanceof NetworkError) {
        console.error('网络错误:', error.message);
    }
}
```

## 5. 高级配置

```typescript
const client = new NewNanManagerClient({
    token: 'your-token',
    baseUrl: 'https://your-server.com',
    timeout: 30000,        // 请求超时时间
    enableLogging: true    // 启用请求日志
});

// 添加请求拦截器
client.interceptors.request.use((config) => {
    console.log('发送请求:', config.url);
    return config;
});

// 添加响应拦截器
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

## 6. 完整示例

```typescript
import { 
    NewNanManagerClient, 
    ServerType, 
    BanMode 
} from '@nanmanager/typescript-sdk';

async function main() {
    const client = new NewNanManagerClient({
        token: 'your-token',
        baseUrl: 'https://your-server.com'
    });
    
    try {
        // 获取服务器列表
        const servers = await client.listServers();
        console.log('服务器数量:', servers.total);
        
        // 获取玩家列表
        const players = await client.listPlayers();
        console.log('玩家数量:', players.total);
        
        // 获取城镇列表
        const towns = await client.listTowns();
        console.log('城镇数量:', towns.total);
        
    } catch (error) {
        console.error('发生错误:', error.message);
    }
}

main();
```

## 7. @sttot/axios-api 特性

本SDK基于@sttot/axios-api构建，具有以下优势：

- **标准化API定义**: 使用统一的API定义模式
- **类型安全**: 完整的TypeScript类型推断
- **错误处理**: 统一的错误处理机制
- **可扩展性**: 支持自定义拦截器和配置

## 8. TypeScript支持

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

## 9. API文档

所有可用的API方法请参考：

- **玩家管理**: `createPlayer`, `getPlayer`, `updatePlayer`, `deletePlayer`, `listPlayers`, `banPlayer`, `unbanPlayer`, `validateLogin`
- **城镇管理**: `createTown`, `getTown`, `updateTown`, `deleteTown`, `listTowns`, `manageTownMember`, `getTownMembers`
- **服务器管理**: `registerServer`, `getServer`, `updateServer`, `deleteServer`, `listServers`, `getServerDetail`
- **服务器监控**: `heartbeat`, `getServerStatus`, `getLatencyStats`
- **Token管理**: `createApiToken`, `getApiToken`, `updateApiToken`, `deleteApiToken`, `listApiTokens`

## 10. 获取帮助

如果遇到问题，请：

1. 检查API Token是否正确
2. 确认服务器地址是否可访问
3. 查看详细的错误信息
4. 参考完整的使用示例

更多详细信息请查看 `README.md` 文件。
