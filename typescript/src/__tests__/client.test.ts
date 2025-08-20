/**
 * NewNanManagerClient 单元测试
 */

import { NewNanManagerClient } from '../client';
import { BanMode } from '../types';
import { NanManagerError, AuthenticationError, NetworkError } from '../errors';

describe('NewNanManagerClient', () => {
  let client: NewNanManagerClient;

  beforeEach(() => {
    client = new NewNanManagerClient({
      token: 'test-token',
      baseUrl: 'https://test.example.com',
      timeout: 5000,
      enableLogging: false
    });
  });

  describe('客户端创建', () => {
    it('应该成功创建客户端实例', () => {
      expect(client).toBeInstanceOf(NewNanManagerClient);
    });

    it('应该有拦截器属性', () => {
      expect(client.interceptors).toBeDefined();
      expect(client.interceptors.request).toBeDefined();
      expect(client.interceptors.response).toBeDefined();
    });
  });

  describe('类型定义测试', () => {
    it('BanMode枚举应该有正确的值', () => {
      expect(BanMode.NORMAL).toBe(0);
      expect(BanMode.TEMPORARY).toBe(1);
      expect(BanMode.PERMANENT).toBe(2);
    });


  });

  describe('错误类测试', () => {
    it('NanManagerError应该正确创建', () => {
      const error = new NanManagerError(400, 'Test error');
      expect(error).toBeInstanceOf(Error);
      expect(error).toBeInstanceOf(NanManagerError);
      expect(error.code).toBe(400);
      expect(error.message).toBe('Test error');
      expect(error.name).toBe('NanManagerError');
    });

    it('AuthenticationError应该正确创建', () => {
      const error = new AuthenticationError('Auth failed');
      expect(error).toBeInstanceOf(Error);
      expect(error).toBeInstanceOf(AuthenticationError);
      expect(error.message).toBe('Auth failed');
      expect(error.name).toBe('AuthenticationError');
    });

    it('NetworkError应该正确创建', () => {
      const error = new NetworkError('Network failed');
      expect(error).toBeInstanceOf(Error);
      expect(error).toBeInstanceOf(NetworkError);
      expect(error.message).toBe('Network failed');
      expect(error.name).toBe('NetworkError');
    });
  });

  describe('API方法存在性测试', () => {
    it('应该有所有玩家管理方法', () => {
      expect(typeof client.createPlayer).toBe('function');
      expect(typeof client.getPlayer).toBe('function');
      expect(typeof client.updatePlayer).toBe('function');
      expect(typeof client.deletePlayer).toBe('function');
      expect(typeof client.listPlayers).toBe('function');
      expect(typeof client.banPlayer).toBe('function');
      expect(typeof client.unbanPlayer).toBe('function');
      expect(typeof client.validateLogin).toBe('function');
    });

    it('应该有所有城镇管理方法', () => {
      expect(typeof client.createTown).toBe('function');
      expect(typeof client.getTown).toBe('function');
      expect(typeof client.updateTown).toBe('function');
      expect(typeof client.deleteTown).toBe('function');
      expect(typeof client.listTowns).toBe('function');
      expect(typeof client.manageTownMember).toBe('function');
      expect(typeof client.getTownMembers).toBe('function');
    });

    it('应该有所有服务器管理方法', () => {
      expect(typeof client.registerServer).toBe('function');
      expect(typeof client.getServer).toBe('function');
      expect(typeof client.updateServer).toBe('function');
      expect(typeof client.deleteServer).toBe('function');
      expect(typeof client.listServers).toBe('function');
      expect(typeof client.getServerDetail).toBe('function');
    });

    it('应该有所有服务器监控方法', () => {
      expect(typeof client.heartbeat).toBe('function');
      expect(typeof client.getServerStatus).toBe('function');
      expect(typeof client.getLatencyStats).toBe('function');
    });

    it('应该有所有Token管理方法', () => {
      expect(typeof client.createApiToken).toBe('function');
      expect(typeof client.getApiToken).toBe('function');
      expect(typeof client.updateApiToken).toBe('function');
      expect(typeof client.deleteApiToken).toBe('function');
      expect(typeof client.listApiTokens).toBe('function');
    });
  });
});
