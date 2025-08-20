/**
 * NewNanManager TypeScript SDK 基本使用示例
 */

import {
  NewNanManagerClient,
  BanMode,
  NanManagerError,
  AuthenticationError,
  NetworkError
} from '../index';

async function main() {
  // 创建客户端实例
  const client = new NewNanManagerClient({
    token: 'your-api-token-here',
    baseUrl: 'https://your-nanmanager-server.com',
    enableLogging: true,
    timeout: 30000
  });

  try {
    // ========== 服务器管理示例 ==========
    console.log('=== 服务器管理示例 ===');

    // 获取服务器列表
    const serversResponse = await client.listServers({
      page: 1,
      pageSize: 10,
      onlineOnly: true
    });
    console.log(`找到 ${serversResponse.total} 个服务器`);
    serversResponse.servers.forEach(server => {
      console.log(`服务器: ${server.name} (${server.address})`);
    });

    // 注册新服务器
    const newServer = await client.registerServer({
      name: '测试服务器',
      address: 'test.example.com:25565',
      description: '这是一个测试服务器'
    });
    console.log(`注册服务器成功: ${newServer.name}`);

    // 获取服务器详细信息
    const serverDetail = await client.getServerDetail(newServer.id);
    console.log(`服务器详情: ${serverDetail.server.name}`);
    if (serverDetail.status) {
      console.log(`在线状态: ${serverDetail.status.online ? '在线' : '离线'}`);
      console.log(`当前玩家: ${serverDetail.status.currentPlayers}/${serverDetail.status.maxPlayers}`);
    }

    // ========== 玩家管理示例 ==========
    console.log('\n=== 玩家管理示例 ===');

    // 获取玩家列表
    const playersResponse = await client.listPlayers({
      page: 1,
      pageSize: 10,
      banMode: BanMode.NORMAL
    });
    console.log(`找到 ${playersResponse.total} 个玩家`);
    playersResponse.players.forEach(player => {
      console.log(`玩家: ${player.name} (ID: ${player.id})`);
    });

    // 创建新玩家
    const newPlayer = await client.createPlayer({
      name: 'TestPlayer',
      qq: '123456789',
      inQqGroup: true,
      inQqGuild: false,
      inDiscord: false
    });
    console.log(`创建玩家成功: ${newPlayer.name}`);

    // 玩家登录验证
    const loginValidation = await client.validateLogin({
      playerName: 'TestPlayer',
      serverId: newServer.id,
      clientVersion: '1.20.1',
      protocolVersion: '763'
    });
    console.log(`登录验证结果: ${loginValidation.allowed ? '允许' : '拒绝'}`);
    if (!loginValidation.allowed) {
      console.log(`拒绝原因: ${loginValidation.reason}`);
    }

    // ========== 城镇管理示例 ==========
    console.log('\n=== 城镇管理示例 ===');

    // 获取城镇列表
    const townsResponse = await client.listTowns({
      page: 1,
      pageSize: 10
    });
    console.log(`找到 ${townsResponse.total} 个城镇`);
    townsResponse.towns.forEach(town => {
      console.log(`城镇: ${town.name} (等级: ${town.level})`);
    });

    // 创建新城镇
    const newTown = await client.createTown({
      name: '测试城镇',
      level: 1,
      leaderId: newPlayer.id,
      description: '这是一个测试城镇'
    });
    console.log(`创建城镇成功: ${newTown.name}`);

    // 管理城镇成员
    await client.manageTownMember(newTown.id, {
      playerId: newPlayer.id,
      action: 'add'
    });
    console.log('添加玩家到城镇成功');

    // 获取城镇成员
    const townMembers = await client.getTownMembers(newTown.id);
    console.log(`城镇成员数量: ${townMembers.total}`);

    // ========== 服务器监控示例 ==========
    console.log('\n=== 服务器监控示例 ===');

    // 发送心跳
    const heartbeatResponse = await client.heartbeat(newServer.id, {
      timestamp: Date.now(),
      sequenceId: 1,
      currentPlayers: 5,
      maxPlayers: 100,
      tps: 20.0,
      version: '1.20.1',
      motd: '欢迎来到测试服务器'
    });
    console.log(`心跳发送成功，下次心跳时间: ${heartbeatResponse.nextHeartbeat}`);

    // 获取服务器状态
    const serverStatus = await client.getServerStatus(newServer.id);
    console.log(`服务器状态: ${serverStatus.online ? '在线' : '离线'}`);

    // 获取延迟统计
    const latencyStats = await client.getLatencyStats(newServer.id);
    console.log(`延迟统计 - 平均: ${latencyStats.average}ms, 最小: ${latencyStats.min}ms, 最大: ${latencyStats.max}ms`);

    // ========== API Token管理示例 ==========
    console.log('\n=== API Token管理示例 ===');

    // 获取Token列表
    const tokensResponse = await client.listApiTokens();
    console.log(`找到 ${tokensResponse.tokens.length} 个Token`);
    tokensResponse.tokens.forEach(token => {
      console.log(`Token: ${token.name} (角色: ${token.role})`);
    });

    // 创建新Token
    const newTokenResponse = await client.createApiToken({
      name: '测试Token',
      role: 'admin',
      description: '这是一个测试Token',
      expireDays: 30
    });
    console.log(`创建Token成功: ${newTokenResponse.tokenInfo.name}`);
    console.log(`Token值: ${newTokenResponse.tokenValue}`);

  } catch (error) {
    // 详细的错误处理
    if (error instanceof AuthenticationError) {
      console.error('认证失败:', error.message);
    } else if (error instanceof NanManagerError) {
      console.error(`API错误 [${error.code}]:`, error.message);
    } else if (error instanceof NetworkError) {
      console.error('网络错误:', error.message);
    } else {
      console.error('未知错误:', error);
    }
  }
}

// 运行示例
if (require.main === module) {
  main().catch(console.error);
}

export { main };
