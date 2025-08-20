import { BaseApiFactory } from './base';
import {
  ServerRegistry,
  RegisterServerRequest,
  UpdateServerRequest,
  ListServersRequest,
  ListServersResponse,
  ServerDetailResponse,
  EmptyResponse,
  ClientConfig
} from '../types';

/**
 * 服务器管理API
 */
export class ServerApi extends BaseApiFactory {
  constructor(config: ClientConfig) {
    super(config);
  }

  /**
   * 注册服务器
   */
  registerServer = this.createApi<RegisterServerRequest, ServerRegistry>(
    (request) => ({
      method: 'POST',
      url: '/api/v1/servers',
      data: {
        name: request.name,
        address: request.address,
        description: request.description
      }
    }),
    ({ data }) => data
  );

  /**
   * 获取服务器信息
   */
  getServer = this.createApi<number, ServerRegistry>(
    (id) => ({
      method: 'GET',
      url: `/api/v1/servers/${id}`
    }),
    ({ data }) => data
  );

  /**
   * 更新服务器信息
   */
  updateServer = this.createApi<{ id: number; request: UpdateServerRequest }, ServerRegistry>(
    ({ id, request }) => ({
      method: 'PUT',
      url: `/api/v1/servers/${id}`,
      data: {
        name: request.name,
        address: request.address,
        description: request.description
      }
    }),
    ({ data }) => data
  );

  /**
   * 删除服务器
   */
  deleteServer = this.createApi<number, EmptyResponse>(
    (id) => ({
      method: 'DELETE',
      url: `/api/v1/servers/${id}`
    }),
    ({ data }) => data
  );

  /**
   * 获取服务器列表
   */
  listServers = this.createApi<ListServersRequest, ListServersResponse>(
    (request) => {
      const params = this.buildQueryParams({
        page: request.page ?? 1,
        page_size: request.pageSize ?? 20,
        search: request.search,
        online_only: request.onlineOnly ?? false
      });

      return {
        method: 'GET',
        url: '/api/v1/servers',
        params
      };
    },
    ({ data }) => data
  );

  /**
   * 获取服务器详细信息
   */
  getServerDetail = this.createApi<number, ServerDetailResponse>(
    (id) => ({
      method: 'GET',
      url: `/api/v1/servers/${id}/detail`
    }),
    ({ data }) => data
  );
}
