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
      ({ data }) => ({
        id: data.id,
        name: data.name,
        level: data.level,
        leaderId: data.leader_id,
        qqGroup: data.qq_group,
        description: data.description,
        createdAt: data.created_at,
        updatedAt: data.updated_at
      }),
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
          name: request.name,
          search: request.search,
          min_level: request.minLevel,
          max_level: request.maxLevel
        })
      }),
      ({ data }) => ({
        towns: data.towns.map((town: any) => ({
          id: town.id,
          name: town.name,
          level: town.level,
          leaderId: town.leader_id,
          qqGroup: town.qq_group,
          description: town.description,
          createdAt: town.created_at,
          updatedAt: town.updated_at
        })),
        total: data.total,
        page: data.page,
        pageSize: data.page_size
      }),
      commonErrorHandler
    );

    // 获取城镇详情（根据IDL规范，返回TownDetailResponse）
    public getTown = apiFactory<GetTownRequest, TownDetailResponse>(
      (request) => ({
        method: 'GET',
        url: `/api/v1/towns/${request.id}`,
        params: request.detail ? { detail: request.detail } : undefined
      }),
      ({ data }) => ({
        town: {
          id: data.town.id,
          name: data.town.name,
          level: data.town.level,
          leaderId: data.town.leader_id,
          qqGroup: data.town.qq_group,
          description: data.town.description,
          createdAt: data.town.created_at,
          updatedAt: data.town.updated_at
        },
        leader: data.leader,  // 直接使用ID，符合IDL中的 optional i32 leader 定义
        members: data.members.map((member: any) => ({
          id: member.id,
          name: member.name,
          townId: member.town_id,
          qq: member.qq,
          qqguild: member.qqguild,
          discord: member.discord,
          inQqGroup: member.in_qq_group,
          inQqGuild: member.in_qq_guild,
          inDiscord: member.in_discord,
          banMode: member.ban_mode,
          banExpire: member.ban_expire,
          banReason: member.ban_reason,
          createdAt: member.created_at,
          updatedAt: member.updated_at
        }))
      }),
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
      ({ data }) => ({
        id: data.id,
        name: data.name,
        level: data.level,
        leaderId: data.leader_id,
        qqGroup: data.qq_group,
        description: data.description,
        createdAt: data.created_at,
        updatedAt: data.updated_at
      }),
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
          pageSize: request.pageSize  // 使用符合IDL规范的字段命名
        })
      }),
      ({ data }) => ({
        members: data.members.map((member: any) => ({
          playerId: member.player_id,
          playerName: member.player_name,
          joinedAt: member.joined_at,
          role: member.role
        })),
        total: data.total,
        page: data.page,
        pageSize: data.page_size
      }),
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
