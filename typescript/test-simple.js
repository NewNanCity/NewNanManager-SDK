const axios = require('axios');

// 简单的API客户端测试
class SimpleApiTester {
  constructor(baseUrl, token) {
    this.baseUrl = baseUrl;
    this.token = token;
    this.axios = axios.create({
      baseURL: baseUrl,
      headers: {
        'Authorization': `Bearer ${token}`,
        'X-API-Token': token,
        'Content-Type': 'application/json'
      }
    });
    this.results = [];
  }

  async testApi(name, description, apiCall) {
    const start = Date.now();
    try {
      const result = await apiCall();
      const duration = Date.now() - start;
      this.results.push({ name, description, success: true, duration, error: null });
      console.log(`✓ ${description}成功 (${duration}ms)`);
      return result;
    } catch (error) {
      const duration = Date.now() - start;
      this.results.push({ name, description, success: false, duration, error: error.message });
      console.log(`✗ ${description}失败: ${error.response?.data?.message || error.message} (${duration}ms)`);
      throw error;
    }
  }

  async testPlayerManagement() {
    console.log('\n=== 1. 测试玩家管理功能 ===');
    let createdPlayerId;

    // 1.1 获取玩家列表
    const players = await this.testApi('ListPlayers', '获取玩家列表', async () => {
      const response = await this.axios.get('/api/v1/players');
      return response.data.data;
    });
    console.log(`   共 ${players.total} 个玩家`);

    // 1.2 创建玩家
    const testPlayerName = `TestPlayer_${Date.now()}`;
    const player = await this.testApi('CreatePlayer', '创建玩家', async () => {
      const response = await this.axios.post('/api/v1/players', {
        name: testPlayerName,
        in_qq_group: true,
        in_qq_guild: false,
        in_discord: false
      });
      return response.data.data;
    });
    createdPlayerId = player.id;
    console.log(`   ID: ${player.id}, 名称: ${player.name}`);

    // 1.3 获取玩家详情
    await this.testApi('GetPlayer', '获取玩家详情', async () => {
      const response = await this.axios.get(`/api/v1/players/${createdPlayerId}`);
      return response.data.data;
    });

    // 1.4 更新玩家信息
    const newName = testPlayerName + '_Updated';
    await this.testApi('UpdatePlayer', '更新玩家信息', async () => {
      const response = await this.axios.put(`/api/v1/players/${createdPlayerId}`, {
        name: newName
      });
      return response.data.data;
    });

    // 1.5 封禁玩家
    await this.testApi('BanPlayer', '封禁玩家', async () => {
      const response = await this.axios.post(`/api/v1/players/${createdPlayerId}/ban`, {
        ban_mode: 1, // TEMPORARY
        reason: '测试封禁',
        duration_seconds: 60
      });
      return response.data;
    });

    // 1.6 解封玩家
    await this.testApi('UnbanPlayer', '解封玩家', async () => {
      const response = await this.axios.post(`/api/v1/players/${createdPlayerId}/unban`);
      return response.data;
    });

    // 1.7 验证登录
    await this.testApi('ValidateLogin', '验证玩家登录', async () => {
      const response = await this.axios.post('/api/v1/players/validate-login', {
        player_name: newName,
        server_id: 1
      });
      return response.data.data;
    });

    // 1.8 删除玩家
    await this.testApi('DeletePlayer', '删除玩家', async () => {
      const response = await this.axios.delete(`/api/v1/players/${createdPlayerId}`);
      return response.data;
    });
  }

  async testServerManagement() {
    console.log('\n=== 2. 测试服务器管理功能 ===');
    let createdServerId;

    // 2.1 获取服务器列表
    const servers = await this.testApi('ListServers', '获取服务器列表', async () => {
      const response = await this.axios.get('/api/v1/servers');
      return response.data.data;
    });
    console.log(`   共 ${servers.total} 个服务器`);

    // 2.2 注册服务器
    const testServerName = `TestServer_${Date.now()}`;
    const testServerAddress = `test-${Date.now()}.example.com:25565`;
    const server = await this.testApi('RegisterServer', '注册服务器', async () => {
      const response = await this.axios.post('/api/v1/servers', {
        name: testServerName,
        address: testServerAddress,
        server_type: 'MINECRAFT'
      });
      return response.data.data;
    });
    createdServerId = server.id;
    console.log(`   ID: ${server.id}, 名称: ${server.name}`);

    // 2.3 获取服务器信息
    await this.testApi('GetServer', '获取服务器信息', async () => {
      const response = await this.axios.get(`/api/v1/servers/${createdServerId}`);
      return response.data.data;
    });

    // 2.4 更新服务器信息
    const newServerName = testServerName + '_Updated';
    const newAddress = `updated-${Date.now()}.example.com:25565`;
    await this.testApi('UpdateServer', '更新服务器信息', async () => {
      const response = await this.axios.put(`/api/v1/servers/${createdServerId}`, {
        name: newServerName,
        address: newAddress
      });
      return response.data.data;
    });

    // 2.5 获取服务器详细信息
    await this.testApi('GetServerDetail', '获取服务器详细信息', async () => {
      const response = await this.axios.get(`/api/v1/servers/${createdServerId}/detail`);
      return response.data.data;
    });

    // 2.6 删除服务器
    await this.testApi('DeleteServer', '删除服务器', async () => {
      const response = await this.axios.delete(`/api/v1/servers/${createdServerId}`);
      return response.data;
    });
  }

  async testTownManagement() {
    console.log('\n=== 3. 测试城镇管理功能 ===');
    let createdTownId;

    // 3.1 获取城镇列表
    const towns = await this.testApi('ListTowns', '获取城镇列表', async () => {
      const response = await this.axios.get('/api/v1/towns');
      return response.data.data;
    });
    console.log(`   共 ${towns.total} 个城镇`);

    // 3.2 创建城镇
    const testTownName = `TestTown_${Date.now()}`;
    const town = await this.testApi('CreateTown', '创建城镇', async () => {
      const response = await this.axios.post('/api/v1/towns', {
        name: testTownName,
        level: 1
      });
      return response.data.data;
    });
    createdTownId = town.id;
    console.log(`   ID: ${town.id}, 名称: ${town.name}`);

    // 3.3 获取城镇详情
    await this.testApi('GetTown', '获取城镇详情', async () => {
      const response = await this.axios.get(`/api/v1/towns/${createdTownId}`);
      return response.data.data;
    });

    // 3.4 更新城镇信息
    const newTownName = testTownName + '_Updated';
    await this.testApi('UpdateTown', '更新城镇信息', async () => {
      const response = await this.axios.put(`/api/v1/towns/${createdTownId}`, {
        name: newTownName,
        level: 2
      });
      return response.data.data;
    });

    // 3.5 获取城镇成员列表
    await this.testApi('GetTownMembers', '获取城镇成员列表', async () => {
      const response = await this.axios.get(`/api/v1/towns/${createdTownId}/members`);
      return response.data.data;
    });

    // 3.6 删除城镇
    await this.testApi('DeleteTown', '删除城镇', async () => {
      const response = await this.axios.delete(`/api/v1/towns/${createdTownId}`);
      return response.data;
    });
  }

  async testTokenManagement() {
    console.log('\n=== 4. 测试Token管理功能 ===');

    // 4.1 获取Token列表
    const tokens = await this.testApi('ListApiTokens', '获取Token列表', async () => {
      const response = await this.axios.get('/api/v1/tokens');
      return response.data.data;
    });
    console.log(`   共 ${tokens.tokens.length} 个Token`);
  }

  async testMonitoringFeatures() {
    console.log('\n=== 5. 测试监控功能 ===');
    console.log('⚠ 监控功能测试需要有效的服务器ID，跳过详细测试');

    // 测试错误处理
    try {
      await this.testApi('GetServerStatus', '获取服务器状态（测试错误处理）', async () => {
        const response = await this.axios.get('/api/v1/servers/999999/status');
        return response.data.data;
      });
      console.log('✗ 错误处理测试失败，应该返回错误但没有');
    } catch (error) {
      console.log(`✓ 错误处理测试成功，正确返回了错误: ${error.response?.status} ${error.response?.data?.message || error.message}`);
      // 修正测试结果
      this.results[this.results.length - 1].success = true;
      this.results[this.results.length - 1].error = null;
    }
  }

  generateReport() {
    const successCount = this.results.filter(r => r.success).length;
    const failCount = this.results.length - successCount;
    const avgDuration = this.results.reduce((sum, r) => sum + r.duration, 0) / this.results.length;

    console.log('\n' + '='.repeat(60));
    console.log('                    测试报告');
    console.log('='.repeat(60));
    console.log(`总测试数: ${this.results.length}`);
    console.log(`成功: ${successCount}, 失败: ${failCount}, 成功率: ${(successCount / this.results.length * 100).toFixed(1)}%`);
    console.log(`平均耗时: ${avgDuration.toFixed(2)}ms`);
    console.log('='.repeat(60));

    if (failCount === 0) {
      console.log('🎉 所有测试都通过了！API客户端工作正常。');
    } else if (successCount > failCount) {
      console.log(`⚠ 大部分测试通过，但有 ${failCount} 个测试失败，请检查相关功能。`);
    } else {
      console.log(`❌ 测试失败较多（${failCount}个），请检查API连接和权限配置。`);
    }

    // 显示失败的测试
    const failedTests = this.results.filter(r => !r.success);
    if (failedTests.length > 0) {
      console.log('\n失败的测试:');
      failedTests.forEach((test, index) => {
        console.log(`${index + 1}. ${test.name}: ${test.error}`);
      });
    }
  }

  async runAllTests() {
    console.log('=== NewNanManager API JavaScript客户端测试 ===');
    console.log('Base URL: https://manager-api.newnan.city');
    console.log('Token: 7p9piy2NagtMAryeyBBY7vzUKK1qDJOq');

    try {
      await this.testPlayerManagement();
    } catch (error) {
      console.log('玩家管理测试中断');
    }

    try {
      await this.testServerManagement();
    } catch (error) {
      console.log('服务器管理测试中断');
    }

    try {
      await this.testTownManagement();
    } catch (error) {
      console.log('城镇管理测试中断');
    }

    try {
      await this.testTokenManagement();
    } catch (error) {
      console.log('Token管理测试中断');
    }

    try {
      await this.testMonitoringFeatures();
    } catch (error) {
      console.log('监控功能测试中断');
    }

    this.generateReport();
  }
}

// 运行测试
async function main() {
  const tester = new SimpleApiTester(
    'https://manager-api.newnan.city',
    '7p9piy2NagtMAryeyBBY7vzUKK1qDJOq'
  );
  
  await tester.runAllTests();
}

main().catch(console.error);
