# NewNanManager TypeScript SDK 项目结构

```
clients/typescript/
├── README.md                           # 项目说明文档
├── QUICKSTART.md                       # 快速开始指南
├── PROJECT_STRUCTURE.md               # 项目结构说明（本文件）
├── package.json                        # NPM包配置
├── tsconfig.json                       # TypeScript配置
├── jest.config.js                      # Jest测试配置
├── rollup.config.js                    # Rollup构建配置
├── .eslintrc.js                        # ESLint配置
├── src/
│   ├── types/                          # 类型定义
│   │   ├── enums.ts                   # 枚举定义
│   │   ├── common.ts                  # 通用类型
│   │   ├── player.ts                  # 玩家相关类型
│   │   ├── town.ts                    # 城镇相关类型
│   │   ├── server.ts                  # 服务器相关类型
│   │   ├── token.ts                   # Token相关类型
│   │   └── index.ts                   # 类型导出
│   ├── errors/                         # 异常定义
│   │   └── index.ts                   # 异常类
│   ├── apis/                           # API定义层
│   │   ├── base.ts                    # 基础API工厂
│   │   ├── player.ts                  # 玩家管理API
│   │   ├── town.ts                    # 城镇管理API
│   │   ├── server.ts                  # 服务器管理API
│   │   ├── monitor.ts                 # 服务器监控API
│   │   ├── token.ts                   # Token管理API
│   │   └── index.ts                   # API导出
│   ├── examples/                       # 使用示例
│   │   └── basic-usage.ts             # 基本使用示例
│   ├── __tests__/                      # 测试文件
│   │   ├── setup.ts                   # 测试环境设置
│   │   └── client.test.ts             # 客户端测试
│   ├── client.ts                       # 主客户端类
│   └── index.ts                        # 入口文件
└── dist/                               # 构建输出目录
    ├── index.js                       # CommonJS构建
    ├── index.esm.js                   # ES模块构建
    └── index.d.ts                     # 类型定义文件
```

## 核心组件说明

### 1. client.ts
- **作用**: 主要的客户端入口类
- **特性**: 
  - 基于@sttot/axios-api的标准化API调用
  - 统一的错误处理
  - 完整的TypeScript类型支持
  - 拦截器支持

### 2. types/ 目录
包含所有API数据模型，与IDL文件中的结构体一一对应：

- **enums.ts**: 所有枚举类型（BanMode, LoginAction, ServerType, ClientType）
- **common.ts**: 通用类型定义（配置、分页、错误响应等）
- **player.ts**: 玩家相关的数据模型和请求/响应类
- **town.ts**: 城镇相关的数据模型和请求/响应类
- **server.ts**: 服务器相关的数据模型和请求/响应类
- **token.ts**: API Token相关的数据模型和请求/响应类

### 3. apis/ 目录
基于@sttot/axios-api的API定义层：

- **base.ts**: 基础API工厂类，提供统一的错误处理和配置
- **player.ts**: 玩家管理相关API定义
- **town.ts**: 城镇管理相关API定义
- **server.ts**: 服务器管理相关API定义
- **monitor.ts**: 服务器监控相关API定义
- **token.ts**: API Token管理相关API定义

### 4. errors/ 目录
- **index.ts**: 定义所有异常类型
  - `NanManagerError`: API业务异常
  - `NetworkError`: 网络异常
  - `AuthenticationError`: 认证异常
  - `SerializationError`: 序列化异常
  - `ValidationError`: 验证异常
  - `TimeoutError`: 超时异常

### 5. examples/ 目录
- **basic-usage.ts**: 完整的使用示例，展示所有API的用法

## 设计原则

### 1. 基于@sttot/axios-api
- 使用标准化的API定义模式
- 享受逻辑分离、规范化、可扩展性等特性
- 完整的TypeScript类型推断

### 2. 类型安全
- 使用TypeScript数据类和接口确保类型安全
- 枚举类型防止无效值
- 可选类型明确标识

### 3. 统一错误处理
- 基于@sttot/axios-api的错误处理机制
- 详细的错误信息分类
- 支持错误重试

### 4. 模块化设计
- 清晰的分层架构
- API定义与使用分离
- 易于扩展和维护

### 5. 开发体验
- 完整的TypeScript支持
- 详细的JSDoc注释
- 丰富的使用示例

## @sttot/axios-api 特性

### API定义模式
每个API使用以下模式定义：

```typescript
const apiMethod = this.createApi<RequestType, ResponseType>(
  // CallHandler: 将请求参数转化为AxiosRequestConfig
  (request) => ({
    method: 'POST',
    url: '/api/endpoint',
    data: request
  }),
  // ResultHandler: 将响应转化为返回数据
  ({ data }) => data
);
```

### 错误处理
- 统一的错误处理机制
- 自动错误类型转换
- 支持错误重试

### 拦截器支持
- 请求前处理
- 响应后处理
- 错误处理

## 技术栈

- **TypeScript**: 5.2.0+
- **@sttot/axios-api**: 1.0.0+ (API定义框架)
- **Axios**: 1.6.0+ (HTTP客户端)
- **Jest**: 29.5.0+ (单元测试)
- **Rollup**: 4.0.0+ (构建工具)
- **ESLint**: 8.50.0+ (代码检查)

## 构建和测试

```bash
# 安装依赖
npm install

# 构建项目
npm run build

# 运行测试
npm run test

# 类型检查
npm run type-check

# 代码检查
npm run lint

# 修复代码风格
npm run lint:fix
```

## 版本兼容性

- **Node.js**: 16.0.0+
- **TypeScript**: 5.0.0+
- **Axios**: 1.0.0+
- **@sttot/axios-api**: 1.0.0+

## 未来扩展

1. **请求重试**: 基于@sttot/axios-api的重试机制
2. **缓存支持**: 添加响应缓存功能
3. **批量操作**: 支持批量API调用
4. **WebSocket支持**: 实时通信功能
5. **React Hooks**: 配合@sttot/axios-api-hooks使用

## 与Kotlin SDK的对比

| 特性 | TypeScript SDK | Kotlin SDK |
|------|----------------|------------|
| HTTP客户端 | @sttot/axios-api + Axios | Ktor |
| API调用方式 | 异步Promise | 同步runBlocking |
| 错误处理 | 统一异常类 | 统一异常类 |
| 类型安全 | TypeScript接口 | Kotlin数据类 |
| 拦截器 | Axios拦截器 | Ktor拦截器 |
| 构建工具 | Rollup | Gradle |
