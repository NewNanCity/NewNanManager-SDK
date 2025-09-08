/**
 * 服务器管理服务模块
 * 基于 @sttot/axios-api 的标准化API定义
 */

import { apiBase } from '@sttot/axios-api';
import {
  ServerRegistry,
  ServerDetailResponse,
  CreateServerRequest,
  GetServerRequest,
  UpdateServerRequest,
  DeleteServerRequest,
  ListServersRequest,
  ListServersResponse,
  EmptyResponse
} from '../types';
import { commonErrorHandler } from '../utils/errorHandler';

export const initServerService = (apiFactory: ReturnType<typeof apiBase>) => {
  class ServerService {
    // 创建服务器
    public createServer = apiFactory<CreateServerRequest, ServerRegistry>(
      (request) => ({
        method: 'POST',
        url: '/api/v1/servers',
        data: {
          name: request.name,
          address: request.address,
          description: request.description
        }
      }),
      ({ data }) => ({
        id: data.id,
        name: data.name,
        address: data.address,
        description: data.description,
        active: data.active,
        createdAt: data.created_at,
        updatedAt: data.updated_at
      }),
      commonErrorHandler
    );

    // 获取服务器列表
    public listServers = apiFactory<ListServersRequest, ListServersResponse>(
      (request = {}) => ({
        method: 'GET',
        url: '/api/v1/servers',
        params: this.buildParams({
          page: request.page || 1,
          page_size: request.pageSize || 20,
          search: request.search,
          online_only: request.onlineOnly
        })
      }),
      ({ data }) => ({
        servers: data.servers.map((server: any) => ({
          id: server.id,
          name: server.name,
          address: server.address,
          description: server.description,
          active: server.active,
          createdAt: server.created_at,
          updatedAt: server.updated_at
        })),
        total: data.total,
        page: data.page,
        pageSize: data.page_size
      }),
      commonErrorHandler
    );

    // 获取服务器详情
    public getServer = apiFactory<GetServerRequest, ServerDetailResponse>(
      (request) => ({
        method: 'GET',
        url: `/api/v1/servers/${request.id}`,
        params: this.buildParams({
          detail: request.detail
        })
      }),
      ({ data }) => ({
        server: {
          id: data.server.id,
          name: data.server.name,
          address: data.server.address,
          description: data.server.description,
          active: data.server.active,
          createdAt: data.server.created_at,
          updatedAt: data.server.updated_at
        },
        status: data.status ? {
          serverId: data.status.server_id,
          online: data.status.online,
          currentPlayers: data.status.current_players,
          maxPlayers: data.status.max_players,
          latencyMs: data.status.latency_ms,
          tps: data.status.tps,
          version: data.status.version,
          motd: data.status.motd,
          expireAt: data.status.expire_at,
          lastHeartbeat: data.status.last_heartbeat
        } : undefined
      }),
      commonErrorHandler
    );

    // 更新服务器信息
    public updateServer = apiFactory<UpdateServerRequest, ServerRegistry>(
      (request) => ({
        method: 'PUT',
        url: `/api/v1/servers/${request.id}`,
        data: {
          name: request.name,
          address: request.address,
          description: request.description
        }
      }),
      ({ data }) => ({
        id: data.id,
        name: data.name,
        address: data.address,
        description: data.description,
        active: data.active,
        createdAt: data.created_at,
        updatedAt: data.updated_at
      }),
      commonErrorHandler
    );

    // 删除服务器
    public deleteServer = apiFactory<DeleteServerRequest, EmptyResponse>(
      (request) => ({
        method: 'DELETE',
        url: `/api/v1/servers/${request.id}`
      }),
      ({ data }) => data as EmptyResponse,
      commonErrorHandler
    );

    // 辅助方法：构建查询参数，过滤掉undefined值
    public buildParams(params: Record<string, any>): Record<string, any> {
      const result: Record<string, any> = {};
      for (const [key, value] of Object.entries(params)) {
        if (value !== undefined && value !== null) {
          result[key] = value;
        }
      }
      return result;
    }
  }

  return new ServerService();
};
