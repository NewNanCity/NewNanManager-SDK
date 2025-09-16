/**
 * 简化的新客户端 - 基于 @sttot/axios-api 的模块化架构
 */

import axios, { AxiosInstance } from 'axios';
import { apiBase } from '@sttot/axios-api';
import { ClientConfig } from './types';
import { initPlayerService } from './modules/player';
import { initServerService } from './modules/server';
import { initTownService } from './modules/town';
import { initMonitorService } from './modules/monitor';
import { initTokenService } from './modules/token';
import { initIPService } from './modules/ip';
import { initPlayerServerService } from './modules/player-server';

export class NewNanManagerClient {
  private axiosInstance: AxiosInstance;

  // 所有服务模块
  public readonly players: ReturnType<typeof initPlayerService>;
  public readonly servers: ReturnType<typeof initServerService>;
  public readonly towns: ReturnType<typeof initTownService>;
  public readonly monitor: ReturnType<typeof initMonitorService>;
  public readonly tokens: ReturnType<typeof initTokenService>;
  public readonly ips: ReturnType<typeof initIPService>;
  public readonly playerServers: ReturnType<typeof initPlayerServerService>;

  constructor(config: ClientConfig) {
    // 创建 axios 实例
    this.axiosInstance = axios.create({
      baseURL: config.baseUrl,
      timeout: config.timeout || 30000,
      headers: {
        'Authorization': `Bearer ${config.token}`,
        'Content-Type': 'application/json',
        'Accept-Encoding': 'gzip, deflate, br'
      },
      decompress: true // 自动解压缩响应
    });

    // 创建 API 工厂
    const apiFactory = apiBase(this.axiosInstance);

    // 初始化所有服务模块
    this.players = initPlayerService(apiFactory);
    this.servers = initServerService(apiFactory);
    this.towns = initTownService(apiFactory);
    this.monitor = initMonitorService(apiFactory);
    this.tokens = initTokenService(apiFactory);
    this.ips = initIPService(apiFactory);
    this.playerServers = initPlayerServerService(apiFactory);
  }
}
