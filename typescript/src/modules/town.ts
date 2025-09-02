/**
 * 城镇管理服务模块
 * 基于 @sttot/axios-api 的标准化API定义
 */

import { apiBase } from '@sttot/axios-api';
import {
  Town,
  CreateTownRequest,
  GetTownRequest,
  UpdateTownRequest,
  DeleteTownRequest,
  ListTownsRequest,
  ListTownsResponse,
  GetTownMembersRequest,
  TownMembersResponse,
  TownDetailResponse,
  EmptyResponse
} from '../types';
import { commonErrorHandler } from '../utils/errorHandler';

export const initTownService = (apiFactory: ReturnType<typeof apiBase>) => {
  class TownService {

    // 创建城镇
    public createTown = apiFactory<CreateTownRequest, Town>(
      (request) => ({
        method: 'POST',
        url: '/api/v1/towns',
        data: {
          name: request.name,
          level: request.level,
          leader_id: request.leaderId,
          qq_group: request.qqGroup,
          description: request.description
        }
      }),
      ({ data }) => data as Town,
      commonErrorHandler
    );

    // 获取城镇列表
    public listTowns = apiFactory<ListTownsRequest, ListTownsResponse>(
      (request = {}) => ({
        method: 'GET',
        url: '/api/v1/towns',
        params: this.buildParams({
          page: request.page,
          page_size: request.pageSize,
          search: request.search,
          level: request.level
        })
      }),
      ({ data }) => data as ListTownsResponse,
      commonErrorHandler
    );

    // 获取城镇详情
    public getTown = apiFactory<GetTownRequest, Town>(
      (request) => ({
        method: 'GET',
        url: `/api/v1/towns/${request.id}`,
        params: request.detail ? { detail: request.detail } : undefined
      }),
      ({ data }) => data as Town,
      commonErrorHandler
    );

    // 获取城镇详细信息（包含镇长和成员信息）
    public getTownDetail = apiFactory<GetTownRequest, TownDetailResponse>(
      (request) => ({
        method: 'GET',
        url: `/api/v1/towns/${request.id}`,
        params: { detail: true }
      }),
      ({ data }) => data as TownDetailResponse,
      commonErrorHandler
    );

    // 更新城镇信息
    public updateTown = apiFactory<UpdateTownRequest, Town>(
      (request) => ({
        method: 'PUT',
        url: `/api/v1/towns/${request.id}`,
        data: {
          name: request.name,
          level: request.level,
          leader_id: request.leaderId,
          qq_group: request.qqGroup,
          description: request.description
        }
      }),
      ({ data }) => data as Town,
      commonErrorHandler
    );

    // 删除城镇
    public deleteTown = apiFactory<DeleteTownRequest, EmptyResponse>(
      (request) => ({
        method: 'DELETE',
        url: `/api/v1/towns/${request.id}`
      }),
      ({ data }) => data as EmptyResponse,
      commonErrorHandler
    );

    // 获取城镇成员列表
    public getTownMembers = apiFactory<GetTownMembersRequest, TownMembersResponse>(
      (request) => ({
        method: 'GET',
        url: `/api/v1/towns/${request.townId}/members`,
        params: this.buildParams({
          page: request.page,
          page_size: request.pageSize
        })
      }),
      ({ data }) => data as TownMembersResponse,
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

  return new TownService();
};
