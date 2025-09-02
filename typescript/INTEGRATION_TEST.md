# NewNanManager 集成测试指南

## 概述

本文档介绍如何运行 NewNanManager TypeScript SDK 的集成测试。集成测试基于 `docs/integration_test_plan.md` 中的测试计划实现，覆盖所有核心API接口和业务流程。

## 测试环境要求

### 1. 服务器环境
- **服务器地址**: `localhost:8000`
- **认证Token**: `7p9piy2NagtMAryeyBBY7vzUKK1qDJOq`
- **服务状态**: 确保 NewNanManager 服务正在运行

### 2. 依赖环境
```bash
# 确保已安装依赖
npm install

# 或使用 yarn
yarn install
```

## 运行集成测试

### 快速开始
```bash
# 运行完整集成测试
npm run test:integration

# 或使用 yarn
yarn test:integration
```

### 测试内容

集成测试包含以下模块：

#### 1. 玩家服务测试 (PlayerService)
- ✅ 创建玩家 (CreatePlayer)
- ✅ 获取玩家详情 (GetPlayer)
- ✅ 更新玩家信息 (UpdatePlayer)
- ✅ 列出玩家 (ListPlayers)
- ✅ 封禁玩家 (BanPlayer)
- ✅ 解封玩家 (UnbanPlayer)
- ✅ 玩家验证 (Validate) - 批量验证

#### 2. 服务器服务测试 (ServerService)
- ✅ 注册服务器 (RegisterServer)
- ✅ 获取服务器详情 (GetServerDetail)
- ✅ 列出服务器 (ListServers)
- ✅ 更新服务器 (UpdateServer)

#### 3. 城镇服务测试 (TownService)
- ✅ 创建城镇 (CreateTown)
- ✅ 获取城镇详情 (GetTownDetail)
- ✅ 列出城镇 (ListTowns)
- ✅ 更新城镇 (UpdateTown)

#### 4. 监控服务测试 (MonitorService)
- ✅ 发送心跳 (Heartbeat)
- ✅ 获取服务器状态 (GetServerStatus)
- ✅ 获取延迟统计 (GetLatencyStats)

#### 5. IP服务测试 (IPService)
- ✅ 获取IP信息 (GetIPInfo)
- ✅ 获取IP风险信息 (GetIPRisk)
- ✅ 封禁IP (BanIP)
- ✅ 解封IP (UnbanIP)
- ✅ 批量封禁IP (BatchBanIPs)
- ✅ 获取IP列表 (ListIPs)
- ✅ 获取被封禁的IP列表 (GetBannedIPs)
- ✅ 获取IP统计信息 (GetIPStatistics)

#### 6. 业务流程集成测试
- ✅ 玩家生命周期完整流程
  - 创建玩家 → 验证登录 → 封禁 → 验证拒绝 → 解封
- ✅ 城镇成员管理流程
  - 添加成员 → 获取成员列表 → 验证成员关系

## 测试数据

### 数据命名规范
测试使用时间戳确保数据唯一性：
- 玩家: `TestPlayer_YYYYMMDDHHMMSS_001`
- 服务器: `TestServer_YYYYMMDDHHMMSS_001`
- 城镇: `TestTown_YYYYMMDDHHMMSS_001`

### 测试数据保留
- ⚠️ **重要**: 测试完成后，所有创建的数据都会保留在数据库中
- 测试数据包括：玩家、服务器、城镇、IP记录
- 建议定期清理测试数据以避免数据库膨胀

## 测试报告

测试完成后会生成详细报告，包含：
- ✅ 通过/失败统计
- ⏱️ 执行时间统计
- 📈 成功率百分比
- 📋 创建的测试数据清单
- ❌ 失败测试的错误详情

### 示例输出
```
🚀 NewNanManager 集成测试开始
📅 测试时间戳: 20250829143022
🔗 测试服务器: http://localhost:8000
============================================================

📋 1. 玩家服务完整测试
🧪 Running: PlayerService.CreatePlayer
✅ PASSED: PlayerService.CreatePlayer (245ms)
   Created player: TestPlayer_20250829143022_001 (ID: 123)

...

============================================================
📊 集成测试报告
============================================================
✅ 通过: 25/25
❌ 失败: 0/25
⏱️  总耗时: 5432ms
📈 成功率: 100.0%

📋 创建的测试数据:
   玩家: 2 个
     - TestPlayer_20250829143022_001 (ID: 123)
     - LifecyclePlayer_20250829143022_001 (ID: 124)
   服务器: 1 个
     - TestServer_20250829143022_001 (ID: 5)
   城镇: 1 个
     - TestTown_20250829143022_001 (ID: 8)
   测试IP: 4 个
     - 203.208.60.1
     - 192.168.1.1
     - 192.168.1.2
     - 192.168.1.3

✨ 集成测试完成！测试数据已保留。
```

## 故障排除

### 常见问题

1. **连接失败**
   ```
   Error: connect ECONNREFUSED 127.0.0.1:8000
   ```
   - 确保 NewNanManager 服务正在运行
   - 检查服务器地址和端口是否正确

2. **认证失败**
   ```
   Error: 401 Unauthorized
   ```
   - 检查 Token 是否正确
   - 确认 Token 具有足够的权限

3. **测试部分失败**
   - 查看详细错误信息
   - 检查服务器日志
   - 确认数据库连接正常

### 调试模式
如需更详细的调试信息，可以修改测试脚本中的日志级别或添加额外的控制台输出。

## 扩展测试

如需添加新的测试用例：

1. 在 `IntegrationTestRunner` 类中添加新的测试方法
2. 在 `runAllTests()` 方法中调用新的测试方法
3. 确保遵循现有的测试模式和错误处理

## 相关文档

- [集成测试计划](../../docs/integration_test_plan.md) - 详细的测试计划和策略
- [API文档](../../docs/) - 完整的API接口文档
- [SDK使用指南](./README.md) - TypeScript SDK 使用说明
