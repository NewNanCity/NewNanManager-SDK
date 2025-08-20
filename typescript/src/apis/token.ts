import { BaseApiFactory } from './base';
import {
  ApiToken,
  CreateApiTokenRequest,
  CreateApiTokenResponse,
  UpdateApiTokenRequest,
  ListApiTokensResponse,
  EmptyResponse,
  ClientConfig
} from '../types';

/**
 * API Token管理API
 */
export class TokenApi extends BaseApiFactory {
  constructor(config: ClientConfig) {
    super(config);
  }

  /**
   * 创建API Token
   */
  createApiToken = this.createApi<CreateApiTokenRequest, CreateApiTokenResponse>(
    (request) => ({
      method: 'POST',
      url: '/api/v1/tokens',
      data: {
        name: request.name,
        role: request.role,
        description: request.description,
        expire_days: request.expireDays
      }
    }),
    ({ data }) => data
  );

  /**
   * 获取API Token详情
   */
  getApiToken = this.createApi<number, ApiToken>(
    (id) => ({
      method: 'GET',
      url: `/api/v1/tokens/${id}`
    }),
    ({ data }) => data
  );

  /**
   * 更新API Token
   */
  updateApiToken = this.createApi<{ id: number; request: UpdateApiTokenRequest }, ApiToken>(
    ({ id, request }) => ({
      method: 'PUT',
      url: `/api/v1/tokens/${id}`,
      data: {
        name: request.name,
        role: request.role,
        description: request.description,
        active: request.active
      }
    }),
    ({ data }) => data
  );

  /**
   * 删除API Token
   */
  deleteApiToken = this.createApi<number, EmptyResponse>(
    (id) => ({
      method: 'DELETE',
      url: `/api/v1/tokens/${id}`
    }),
    ({ data }) => data
  );

  /**
   * 获取API Token列表
   */
  listApiTokens = this.createApi<void, ListApiTokensResponse>(
    () => ({
      method: 'GET',
      url: '/api/v1/tokens'
    }),
    ({ data }) => data
  );
}
