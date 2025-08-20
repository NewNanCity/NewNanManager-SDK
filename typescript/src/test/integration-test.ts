import { NewNanManagerClient } from '../client';
import { BanMode, ServerType } from '../types';

interface TestResult {
  name: string;
  success: boolean;
  error?: Error;
  duration: number;
  description: string;
}

interface TestReport {
  startTime: Date;
  endTime?: Date;
  tests: TestResult[];
}

class IntegrationTester {
  private client: NewNanManagerClient;
  private report: TestReport;

  constructor(baseUrl: string, token: string) {
    this.client = new NewNanManagerClient({
      baseUrl,
      token
    });
    this.report = {
      startTime: new Date(),
      tests: []
    };
  }

  private addTestResult(name: string, description: string, success: boolean, error?: Error, duration: number = 0) {
    this.report.tests.push({
      name,
      success,
      error,
      duration,
      description
    });
  }

  async testPlayerManagement(): Promise<void> {
    console.log('=== 1. 测试玩家管理功能 ===');
    let createdPlayerId: number | undefined;

    // 1.1 获取玩家列表
    const start1 = Date.now();
    try {
      const players = await this.client.listPlayers({});
      const duration = Date.now() - start1;
      this.addTestResult('ListPlayers', '获取玩家列表', true, undefined, duration);
      console.log(`✓ 获取玩家列表成功，共 ${players.total} 个玩家`);
    } catch (error) {
      const duration = Date.now() - start1;
      this.addTestResult('ListPlayers', '获取玩家列表', false, error as Error, duration);
      console.log(`✗ 获取玩家列表失败: ${error}`);
    }

    // 1.2 创建玩家
    const start2 = Date.now();
    const testPlayerName = `TestPlayer_${Date.now()}`;
    try {
      const player = await this.client.createPlayer({
        name: testPlayerName,
        inQqGroup: true,
        inQqGuild: false,
        inDiscord: false
      });
      const duration = Date.now() - start2;
      createdPlayerId = player.id;
      this.addTestResult('CreatePlayer', '创建玩家', true, undefined, duration);
      console.log(`✓ 创建玩家成功，ID: ${player.id}, 名称: ${player.name}`);
    } catch (error) {
      const duration = Date.now() - start2;
      this.addTestResult('CreatePlayer', '创建玩家', false, error as Error, duration);
      console.log(`✗ 创建玩家失败: ${error}`);
      return; // 如果创建失败，后续测试无法进行
    }

    if (!createdPlayerId) return;

    // 1.3 获取玩家详情
    const start3 = Date.now();
    try {
      const player = await this.client.players.getPlayer(createdPlayerId);
      const duration = Date.now() - start3;
      this.addTestResult('GetPlayer', '获取玩家详情', true, undefined, duration);
      console.log(`✓ 获取玩家详情成功，名称: ${player.name}, 创建时间: ${player.createdAt}`);
    } catch (error) {
      const duration = Date.now() - start3;
      this.addTestResult('GetPlayer', '获取玩家详情', false, error as Error, duration);
      console.log(`✗ 获取玩家详情失败: ${error}`);
    }

    // 1.4 更新玩家信息
    const start4 = Date.now();
    const newName = testPlayerName + '_Updated';
    try {
      const updatedPlayer = await this.client.players.updatePlayer(createdPlayerId, {
        name: newName
      });
      const duration = Date.now() - start4;
      this.addTestResult('UpdatePlayer', '更新玩家信息', true, undefined, duration);
      console.log(`✓ 更新玩家信息成功，新名称: ${updatedPlayer.name}`);
    } catch (error) {
      const duration = Date.now() - start4;
      this.addTestResult('UpdatePlayer', '更新玩家信息', false, error as Error, duration);
      console.log(`✗ 更新玩家信息失败: ${error}`);
    }

    // 1.5 封禁玩家
    const start5 = Date.now();
    try {
      await this.client.players.banPlayer(createdPlayerId, {
        banMode: BanMode.TEMPORARY,
        reason: '测试封禁',
        durationSeconds: 60
      });
      const duration = Date.now() - start5;
      this.addTestResult('BanPlayer', '封禁玩家', true, undefined, duration);
      console.log('✓ 封禁玩家成功');
    } catch (error) {
      const duration = Date.now() - start5;
      this.addTestResult('BanPlayer', '封禁玩家', false, error as Error, duration);
      console.log(`✗ 封禁玩家失败: ${error}`);
    }

    // 1.6 解封玩家
    const start6 = Date.now();
    try {
      await this.client.players.unbanPlayer(createdPlayerId);
      const duration = Date.now() - start6;
      this.addTestResult('UnbanPlayer', '解封玩家', true, undefined, duration);
      console.log('✓ 解封玩家成功');
    } catch (error) {
      const duration = Date.now() - start6;
      this.addTestResult('UnbanPlayer', '解封玩家', false, error as Error, duration);
      console.log(`✗ 解封玩家失败: ${error}`);
    }

    // 1.7 验证登录
    const start7 = Date.now();
    try {
      const validateResult = await this.client.players.validateLogin({
        playerName: newName,
        serverId: 1
      });
      const duration = Date.now() - start7;
      this.addTestResult('ValidateLogin', '验证玩家登录', true, undefined, duration);
      console.log(`✓ 验证登录成功，允许登录: ${validateResult.allowed}`);
    } catch (error) {
      const duration = Date.now() - start7;
      this.addTestResult('ValidateLogin', '验证玩家登录', false, error as Error, duration);
      console.log(`✗ 验证登录失败: ${error}`);
    }

    // 1.8 删除玩家
    const start8 = Date.now();
    try {
      await this.client.players.deletePlayer(createdPlayerId);
      const duration = Date.now() - start8;
      this.addTestResult('DeletePlayer', '删除玩家', true, undefined, duration);
      console.log('✓ 删除玩家成功');
    } catch (error) {
      const duration = Date.now() - start8;
      this.addTestResult('DeletePlayer', '删除玩家', false, error as Error, duration);
      console.log(`✗ 删除玩家失败: ${error}`);
    }
  }

  async testServerManagement(): Promise<void> {
    console.log('\n=== 2. 测试服务器管理功能 ===');
    let createdServerId: number | undefined;

    // 2.1 获取服务器列表
    const start1 = Date.now();
    try {
      const servers = await this.client.servers.listServers({});
      const duration = Date.now() - start1;
      this.addTestResult('ListServers', '获取服务器列表', true, undefined, duration);
      console.log(`✓ 获取服务器列表成功，共 ${servers.total} 个服务器`);
    } catch (error) {
      const duration = Date.now() - start1;
      this.addTestResult('ListServers', '获取服务器列表', false, error as Error, duration);
      console.log(`✗ 获取服务器列表失败: ${error}`);
    }

    // 2.2 注册服务器
    const start2 = Date.now();
    const testServerName = `TestServer_${Date.now()}`;
    const testServerAddress = `test-${Date.now()}.example.com:25565`;
    try {
      const server = await this.client.servers.registerServer({
        name: testServerName,
        address: testServerAddress,
        serverType: ServerType.MINECRAFT
      });
      const duration = Date.now() - start2;
      createdServerId = server.id;
      this.addTestResult('RegisterServer', '注册服务器', true, undefined, duration);
      console.log(`✓ 注册服务器成功，ID: ${server.id}, 名称: ${server.name}`);
    } catch (error) {
      const duration = Date.now() - start2;
      this.addTestResult('RegisterServer', '注册服务器', false, error as Error, duration);
      console.log(`✗ 注册服务器失败: ${error}`);
      return;
    }

    if (!createdServerId) return;

    // 2.3 获取服务器信息
    const start3 = Date.now();
    try {
      const server = await this.client.servers.getServer(createdServerId);
      const duration = Date.now() - start3;
      this.addTestResult('GetServer', '获取服务器信息', true, undefined, duration);
      console.log(`✓ 获取服务器信息成功，名称: ${server.name}, 地址: ${server.address}`);
    } catch (error) {
      const duration = Date.now() - start3;
      this.addTestResult('GetServer', '获取服务器信息', false, error as Error, duration);
      console.log(`✗ 获取服务器信息失败: ${error}`);
    }

    // 2.4 更新服务器信息
    const start4 = Date.now();
    const newServerName = testServerName + '_Updated';
    const newAddress = `updated-${Date.now()}.example.com:25565`;
    try {
      const updatedServer = await this.client.servers.updateServer(createdServerId, {
        name: newServerName,
        address: newAddress
      });
      const duration = Date.now() - start4;
      this.addTestResult('UpdateServer', '更新服务器信息', true, undefined, duration);
      console.log(`✓ 更新服务器信息成功，新名称: ${updatedServer.name}`);
    } catch (error) {
      const duration = Date.now() - start4;
      this.addTestResult('UpdateServer', '更新服务器信息', false, error as Error, duration);
      console.log(`✗ 更新服务器信息失败: ${error}`);
    }

    // 2.5 获取服务器详细信息
    const start5 = Date.now();
    try {
      const serverDetail = await this.client.servers.getServerDetail(createdServerId);
      const duration = Date.now() - start5;
      this.addTestResult('GetServerDetail', '获取服务器详细信息', true, undefined, duration);
      console.log(`✓ 获取服务器详细信息成功，服务器: ${serverDetail.server.name}`);
      if (serverDetail.status) {
        console.log(`  状态: 在线=${serverDetail.status.online}, 当前玩家=${serverDetail.status.currentPlayers}`);
      }
    } catch (error) {
      const duration = Date.now() - start5;
      this.addTestResult('GetServerDetail', '获取服务器详细信息', false, error as Error, duration);
      console.log(`✗ 获取服务器详细信息失败: ${error}`);
    }

    // 2.6 删除服务器
    const start6 = Date.now();
    try {
      await this.client.servers.deleteServer(createdServerId);
      const duration = Date.now() - start6;
      this.addTestResult('DeleteServer', '删除服务器', true, undefined, duration);
      console.log('✓ 删除服务器成功');
    } catch (error) {
      const duration = Date.now() - start6;
      this.addTestResult('DeleteServer', '删除服务器', false, error as Error, duration);
      console.log(`✗ 删除服务器失败: ${error}`);
    }
  }

  async testTownManagement(): Promise<void> {
    console.log('\n=== 3. 测试城镇管理功能 ===');
    let createdTownId: number | undefined;

    // 3.1 获取城镇列表
    const start1 = Date.now();
    try {
      const towns = await this.client.towns.listTowns({});
      const duration = Date.now() - start1;
      this.addTestResult('ListTowns', '获取城镇列表', true, undefined, duration);
      console.log(`✓ 获取城镇列表成功，共 ${towns.total} 个城镇`);
    } catch (error) {
      const duration = Date.now() - start1;
      this.addTestResult('ListTowns', '获取城镇列表', false, error as Error, duration);
      console.log(`✗ 获取城镇列表失败: ${error}`);
    }

    // 3.2 创建城镇
    const start2 = Date.now();
    const testTownName = `TestTown_${Date.now()}`;
    try {
      const town = await this.client.towns.createTown({
        name: testTownName,
        level: 1
      });
      const duration = Date.now() - start2;
      createdTownId = town.id;
      this.addTestResult('CreateTown', '创建城镇', true, undefined, duration);
      console.log(`✓ 创建城镇成功，ID: ${town.id}, 名称: ${town.name}`);
    } catch (error) {
      const duration = Date.now() - start2;
      this.addTestResult('CreateTown', '创建城镇', false, error as Error, duration);
      console.log(`✗ 创建城镇失败: ${error}`);
      return;
    }

    if (!createdTownId) return;

    // 3.3 获取城镇详情
    const start3 = Date.now();
    try {
      const town = await this.client.towns.getTown(createdTownId);
      const duration = Date.now() - start3;
      this.addTestResult('GetTown', '获取城镇详情', true, undefined, duration);
      console.log(`✓ 获取城镇详情成功，名称: ${town.name}, 等级: ${town.level}`);
    } catch (error) {
      const duration = Date.now() - start3;
      this.addTestResult('GetTown', '获取城镇详情', false, error as Error, duration);
      console.log(`✗ 获取城镇详情失败: ${error}`);
    }

    // 3.4 更新城镇信息
    const start4 = Date.now();
    const newTownName = testTownName + '_Updated';
    try {
      const updatedTown = await this.client.towns.updateTown(createdTownId, {
        name: newTownName,
        level: 2
      });
      const duration = Date.now() - start4;
      this.addTestResult('UpdateTown', '更新城镇信息', true, undefined, duration);
      console.log(`✓ 更新城镇信息成功，新名称: ${updatedTown.name}, 新等级: ${updatedTown.level}`);
    } catch (error) {
      const duration = Date.now() - start4;
      this.addTestResult('UpdateTown', '更新城镇信息', false, error as Error, duration);
      console.log(`✗ 更新城镇信息失败: ${error}`);
    }

    // 3.5 获取城镇成员列表
    const start5 = Date.now();
    try {
      const members = await this.client.towns.getTownMembers(createdTownId, {});
      const duration = Date.now() - start5;
      this.addTestResult('GetTownMembers', '获取城镇成员列表', true, undefined, duration);
      console.log(`✓ 获取城镇成员列表成功，共 ${members.total} 个成员`);
    } catch (error) {
      const duration = Date.now() - start5;
      this.addTestResult('GetTownMembers', '获取城镇成员列表', false, error as Error, duration);
      console.log(`✗ 获取城镇成员列表失败: ${error}`);
    }

    // 3.6 删除城镇
    const start6 = Date.now();
    try {
      await this.client.towns.deleteTown(createdTownId);
      const duration = Date.now() - start6;
      this.addTestResult('DeleteTown', '删除城镇', true, undefined, duration);
      console.log('✓ 删除城镇成功');
    } catch (error) {
      const duration = Date.now() - start6;
      this.addTestResult('DeleteTown', '删除城镇', false, error as Error, duration);
      console.log(`✗ 删除城镇失败: ${error}`);
    }
  }

  async testTokenManagement(): Promise<void> {
    console.log('\n=== 4. 测试Token管理功能 ===');

    // 4.1 获取Token列表
    const start1 = Date.now();
    try {
      const tokens = await this.client.tokens.listApiTokens();
      const duration = Date.now() - start1;
      this.addTestResult('ListApiTokens', '获取Token列表', true, undefined, duration);
      console.log(`✓ 获取Token列表成功，共 ${tokens.tokens.length} 个Token`);
    } catch (error) {
      const duration = Date.now() - start1;
      this.addTestResult('ListApiTokens', '获取Token列表', false, error as Error, duration);
      console.log(`✗ 获取Token列表失败: ${error}`);
    }
  }

  async testMonitoringFeatures(): Promise<void> {
    console.log('\n=== 5. 测试监控功能 ===');
    console.log('⚠ 监控功能测试需要有效的服务器ID，跳过详细测试');

    // 可以尝试获取一个不存在的服务器状态来测试API
    const start1 = Date.now();
    try {
      await this.client.monitor.getServerStatus(999999);
      const duration = Date.now() - start1;
      this.addTestResult('GetServerStatus', '获取服务器状态（测试错误处理）', false, new Error('应该返回错误但没有'), duration);
      console.log('✗ 错误处理测试失败，应该返回错误但没有');
    } catch (error) {
      const duration = Date.now() - start1;
      this.addTestResult('GetServerStatus', '获取服务器状态（测试错误处理）', true, undefined, duration);
      console.log(`✓ 错误处理测试成功，正确返回了错误: ${error}`);
    }
  }

  generateTestReport(): void {
    this.report.endTime = new Date();
    const totalDuration = this.report.endTime.getTime() - this.report.startTime.getTime();

    console.log('\n' + '='.repeat(60));
    console.log('                    测试报告');
    console.log('='.repeat(60));
    console.log(`开始时间: ${this.report.startTime.toLocaleString()}`);
    console.log(`结束时间: ${this.report.endTime.toLocaleString()}`);
    console.log(`总耗时: ${totalDuration}ms`);
    console.log(`总测试数: ${this.report.tests.length}`);

    const successCount = this.report.tests.filter(t => t.success).length;
    const failCount = this.report.tests.length - successCount;
    const avgDuration = this.report.tests.reduce((sum, t) => sum + t.duration, 0) / this.report.tests.length;

    console.log('\n详细结果:');
    console.log('-'.repeat(60));
    this.report.tests.forEach((test, index) => {
      const status = test.success ? '✓ 成功' : '✗ 失败';
      console.log(`${(index + 1).toString().padStart(2)}. ${test.name.padEnd(20)} ${status} (${test.duration}ms)`);
      console.log(`    ${test.description}`);
      if (test.error) {
        console.log(`    错误: ${test.error.message}`);
      }
      console.log();
    });

    console.log('-'.repeat(60));
    console.log(`成功: ${successCount}, 失败: ${failCount}, 成功率: ${(successCount / this.report.tests.length * 100).toFixed(1)}%`);
    console.log(`平均耗时: ${avgDuration.toFixed(2)}ms`);
    console.log('='.repeat(60));

    // 总结
    if (failCount === 0) {
      console.log('🎉 所有测试都通过了！API客户端工作正常。');
    } else if (successCount > failCount) {
      console.log(`⚠ 大部分测试通过，但有 ${failCount} 个测试失败，请检查相关功能。`);
    } else {
      console.log(`❌ 测试失败较多（${failCount}个），请检查API连接和权限配置。`);
    }
  }

  async runAllTests(): Promise<void> {
    console.log('=== NewNanManager API TypeScript客户端测试 ===');
    console.log('Base URL: https://manager-api.newnan.city');
    console.log('Token: 7p9piy2NagtMAryeyBBY7vzUKK1qDJOq');
    console.log();

    await this.testPlayerManagement();
    await this.testServerManagement();
    await this.testTownManagement();
    await this.testTokenManagement();
    await this.testMonitoringFeatures();

    this.generateTestReport();
  }
}

// 运行测试
async function main() {
  const tester = new IntegrationTester(
    'https://manager-api.newnan.city',
    '7p9piy2NagtMAryeyBBY7vzUKK1qDJOq'
  );

  await tester.runAllTests();
}

if (require.main === module) {
  main().catch(console.error);
}

export { IntegrationTester };
