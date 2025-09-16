/**
 * API Token管理服务模块
 * 基于 @sttot/axios-api 的标准化API定义
 */

import { apiBase } from '@sttot/axios-api';
import { commonErrorHandler } from '../utils/errorHandler';
import {
  CreateApiTokenRequest,
  CreateApiTokenResponse,
  UpdateApiTokenRequest,
  DeleteApiTokenRequest,
  ListApiTokensRequest,
  ListApiTokensResponse,
  ApiToken,
  EmptyResponse
} from '../types';

export const initTokenService = (apiFactory: ReturnType<typeof apiBase>) => {
  class TokenService {
    // 创建API Token
    public createApiToken = apiFactory<CreateApiTokenRequest, CreateApiTokenResponse>(
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
      ({ data }) => ({
        tokenInfo: {
          id: data.token_info.id,
          name: data.token_info.name,
          role: data.token_info.role,
          description: data.token_info.description,
          active: data.token_info.active,
          expireAt: data.token_info.expire_at,
          lastUsedAt: data.token_info.last_used_at,
          lastUsedIp: data.token_info.last_used_ip,
          createdAt: data.token_info.created_at,
          updatedAt: data.token_info.updated_at
        },
        tokenValue: data.token_value
      }),
      commonErrorHandler
    );

    // 获取API Token列表
    public listApiTokens = apiFactory<ListApiTokensRequest, ListApiTokensResponse>(
      (request = {}) => ({
        method: 'GET',
        url: '/api/v1/tokens',
        params: this.buildParams({
          page: request.page,
          page_size: request.pageSize,
          search: request.search
        })
      }),
      ({ data }) => ({
        tokens: data.tokens.map((token: any) => ({
          id: token.id,
          name: token.name,
          role: token.role,
          description: token.description,
          active: token.active,
          expireAt: token.expire_at,
          lastUsedAt: token.last_used_at,
          lastUsedIp: token.last_used_ip,
          createdAt: token.created_at,
          updatedAt: token.updated_at
        })),
        total: data.total,
        page: data.page,
        pageSize: data.page_size
      }),
      commonErrorHandler
    );

    // 获取API Token详情
    public getApiToken = apiFactory<{ id: number }, ApiToken>(
      (request) => ({
        method: 'GET',
        url: `/api/v1/tokens/${request.id}`
      }),
      ({ data }) => ({
        id: data.id,
        name: data.name,
        role: data.role,
        description: data.description,
        active: data.active,
        expireAt: data.expire_at,
        lastUsedAt: data.last_used_at,
        lastUsedIp: data.last_used_ip,
        createdAt: data.created_at,
        updatedAt: data.updated_at
      }),
      commonErrorHandler
    );

    // 更新API Token
    public updateApiToken = apiFactory<UpdateApiTokenRequest, ApiToken>(
      (request) => ({
        method: 'PUT',
        url: `/api/v1/tokens/${request.id}`,
        data: {
          name: request.name,
          role: request.role,
          description: request.description,
          active: request.active
        }
      }),
      ({ data }) => ({
        id: data.id,
        name: data.name,
        role: data.role,
        description: data.description,
        active: data.active,
        expireAt: data.expire_at,
        lastUsedAt: data.last_used_at,
        lastUsedIp: data.last_used_ip,
        createdAt: data.created_at,
        updatedAt: data.updated_at
      }),
      commonErrorHandler
    );

    // 删除API Token
    public deleteApiToken = apiFactory<DeleteApiTokenRequest, EmptyResponse>(
      (request) => ({
        method: 'DELETE',
        url: `/api/v1/tokens/${request.id}`
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

  return new TokenService();
};
