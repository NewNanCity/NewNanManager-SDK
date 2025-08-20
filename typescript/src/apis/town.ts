import { BaseApiFactory } from './base';
import {
  Town,
  CreateTownRequest,
  UpdateTownRequest,
  ListTownsRequest,
  ListTownsResponse,
  ManageTownMemberRequest,
  GetTownMembersRequest,
  TownMembersResponse,
  EmptyResponse,
  ClientConfig
} from '../types';

/**
 * 城镇管理API
 */
export class TownApi extends BaseApiFactory {
  constructor(config: ClientConfig) {
    super(config);
  }

  /**
   * 创建城镇
   */
  createTown = this.createApi<CreateTownRequest, Town>(
    (request) => ({
      method: 'POST',
      url: '/api/v1/towns',
      data: {
        name: request.name,
        level: request.level ?? 0,
        leader_id: request.leaderId,
        qq_group: request.qqGroup,
        description: request.description
      }
    }),
    ({ data }) => data
  );

  /**
   * 获取城镇详情
   */
  getTown = this.createApi<number, Town>(
    (id) => ({
      method: 'GET',
      url: `/api/v1/towns/${id}`
    }),
    ({ data }) => data
  );

  /**
   * 更新城镇信息
   */
  updateTown = this.createApi<{ id: number; request: UpdateTownRequest }, Town>(
    ({ id, request }) => ({
      method: 'PUT',
      url: `/api/v1/towns/${id}`,
      data: {
        name: request.name,
        level: request.level,
        leader_id: request.leaderId,
        qq_group: request.qqGroup,
        description: request.description
      }
    }),
    ({ data }) => data
  );

  /**
   * 删除城镇
   */
  deleteTown = this.createApi<number, EmptyResponse>(
    (id) => ({
      method: 'DELETE',
      url: `/api/v1/towns/${id}`
    }),
    ({ data }) => data
  );

  /**
   * 获取城镇列表
   */
  listTowns = this.createApi<ListTownsRequest, ListTownsResponse>(
    (request) => {
      const params = this.buildQueryParams({
        page: request.page ?? 1,
        page_size: request.pageSize ?? 20,
        search: request.search,
        min_level: request.minLevel,
        max_level: request.maxLevel
      });

      return {
        method: 'GET',
        url: '/api/v1/towns',
        params
      };
    },
    ({ data }) => data
  );

  /**
   * 管理城镇成员
   */
  manageTownMember = this.createApi<{ townId: number; request: ManageTownMemberRequest }, EmptyResponse>(
    ({ townId, request }) => ({
      method: 'POST',
      url: `/api/v1/towns/${townId}/members`,
      data: {
        player_id: request.playerId,
        action: request.action
      }
    }),
    ({ data }) => data
  );

  /**
   * 获取城镇成员列表
   */
  getTownMembers = this.createApi<{ townId: number; request: GetTownMembersRequest }, TownMembersResponse>(
    ({ townId, request }) => {
      const params = this.buildQueryParams({
        page: request.page ?? 1,
        page_size: request.pageSize ?? 50
      });

      return {
        method: 'GET',
        url: `/api/v1/towns/${townId}/members`,
        params
      };
    },
    ({ data }) => data
  );
}
