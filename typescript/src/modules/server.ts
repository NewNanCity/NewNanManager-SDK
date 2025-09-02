/**
 * 服务器管理服务模块
 * 基于 @sttot/axios-api 的标准化API定义
 */

import { apiBase } from '@sttot/axios-api';
import {
  ServerRegistry,
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
      ({ data }) => data as ServerRegistry,
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
          search: request.search
        })
      }),
      ({ data }) => data as ListServersResponse,
      commonErrorHandler
    );

    // 获取服务器详情
    public getServer = apiFactory<GetServerRequest, ServerRegistry>(
      (request) => ({
        method: 'GET',
        url: `/api/v1/servers/${request.id}`
      }),
      ({ data }) => data as ServerRegistry,
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
      ({ data }) => data as ServerRegistry,
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

    // 获取服务器详细信息
    public getServerDetail = apiFactory<GetServerRequest, ServerRegistry>(
      (request) => ({
        method: 'GET',
        url: `/api/v1/servers/${request.id}/detail`
      }),
      ({ data }) => data as ServerRegistry,
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
