/**
 * 简化的新客户端 - 基于 @sttot/axios-api 的模块化架构
 */

import axios, { AxiosInstance } from 'axios';
import { ClientConfig, AuthScheme } from './types';
import { createGeneratedServices, GeneratedServices } from './generatedServices';

export class NewNanManagerClient {
  private axiosInstance: AxiosInstance;

  // 所有服务模块
  public readonly players: GeneratedServices['players'];
  public readonly servers: GeneratedServices['servers'];
  public readonly towns: GeneratedServices['towns'];
  public readonly monitor: GeneratedServices['monitor'];
  public readonly tokens: GeneratedServices['tokens'];
  public readonly ips: GeneratedServices['ips'];
  public readonly playerServers: GeneratedServices['playerServers'];
  public readonly health: GeneratedServices['health'];

  constructor(config: ClientConfig) {
    const baseUrl = normalizeBaseUrl(config.baseUrl);
    if (!config.token.trim()) {
      throw new Error('token must not be empty');
    }
    const authScheme = config.authScheme ?? AuthScheme.BEARER;

    this.axiosInstance = axios.create({
      baseURL: baseUrl,
      timeout: config.timeout || 30000,
      maxRedirects: 0,
      headers: {
        'Content-Type': 'application/json',
        'Accept-Encoding': 'gzip, deflate, br'
      },
      decompress: true // 自动解压缩响应
    });

    this.axiosInstance.interceptors.request.use((request) => {
      delete request.headers.Authorization;
      delete request.headers['X-API-Token'];
      if (authScheme === AuthScheme.API_TOKEN) {
        request.headers['X-API-Token'] = config.token;
      } else {
        request.headers.Authorization = `Bearer ${config.token}`;
      }
      return request;
    });

    const services = createGeneratedServices(this.axiosInstance, baseUrl);
    this.players = services.players;
    this.servers = services.servers;
    this.towns = services.towns;
    this.monitor = services.monitor;
    this.tokens = services.tokens;
    this.ips = services.ips;
    this.playerServers = services.playerServers;
    this.health = services.health;
  }
}

function normalizeBaseUrl(value: string): string {
  const normalized = value.trim().replace(/\/+$/, '');
  if (!normalized || !/^https?:\/\/[^/?#]+(?:\/[^?#]*)?$/i.test(normalized)) {
    throw new Error('baseUrl must be an HTTP(S) origin or path without query or fragment');
  }
  return normalized;
}
