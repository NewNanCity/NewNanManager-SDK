/**
 * IP管理服务模块
 * 基于 @sttot/axios-api 的标准化API定义
 */

import { apiBase } from '@sttot/axios-api';
import {
  IPInfo,
  BanIPRequest,
  UnbanIPRequest,
  ListBannedIPsRequest,
  ListBannedIPsResponse,
  EmptyResponse
} from '../types';
import { commonErrorHandler } from '../utils/errorHandler';

export const initIPService = (apiFactory: ReturnType<typeof apiBase>) => {
  class IPService {
    // 获取IP信息
    public getIPInfo = apiFactory<{ ip: string }, IPInfo>(
      (request) => ({
        method: 'GET',
        url: `/api/v1/ips/${request.ip}`
      }),
      ({ data }) => data as IPInfo,
      commonErrorHandler
    );

    // 封禁IP（支持批量）
    public banIP = apiFactory<BanIPRequest, EmptyResponse>(
      (request) => ({
        method: 'POST',
        url: '/api/v1/ips/ban',
        data: {
          ips: request.ips,
          reason: request.reason
        }
      }),
      ({ data }) => data as EmptyResponse,
      commonErrorHandler
    );

    // 解封IP（支持批量）
    public unbanIP = apiFactory<UnbanIPRequest, EmptyResponse>(
      (request) => ({
        method: 'POST',
        url: '/api/v1/ips/unban',
        data: {
          ips: request.ips
        }
      }),
      ({ data }) => data as EmptyResponse,
      commonErrorHandler
    );

    // 获取被封禁的IP列表
    public listBannedIPs = apiFactory<ListBannedIPsRequest, ListBannedIPsResponse>(
      (request = {}) => ({
        method: 'GET',
        url: '/api/v1/ips/banned',
        params: this.buildParams({
          page: request.page,
          page_size: request.pageSize,
          active_only: request.activeOnly
        })
      }),
      ({ data }) => data as ListBannedIPsResponse,
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

  return new IPService();
};
