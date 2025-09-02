/**
 * 玩家管理服务模块
 * 基于 @sttot/axios-api 的标准化API定义
 */

import { apiBase } from '@sttot/axios-api';
import {
  Player,
  CreatePlayerRequest,
  GetPlayerRequest,
  UpdatePlayerRequest,
  DeletePlayerRequest,
  BanPlayerRequest,
  UnbanPlayerRequest,
  ListPlayersRequest,
  ListPlayersResponse,
  ValidateRequest,
  ValidateResponse,
  EmptyResponse
} from '../types';
import { commonErrorHandler } from '../utils/errorHandler';

export const initPlayerService = (apiFactory: ReturnType<typeof apiBase>) => {
  class PlayerService {
    // 创建玩家
    public createPlayer = apiFactory<CreatePlayerRequest, Player>(
      // 生成 RequestConfig
      (request) => ({
        method: 'POST',
        url: '/api/v1/players',
        data: {
          name: request.name,
          town_id: request.townId,
          qq: request.qq,
          qqguild: request.qqguild,
          discord: request.discord,
          in_qq_group: request.inQqGroup,
          in_qq_guild: request.inQqGuild,
          in_discord: request.inDiscord
        }
      }),
      // 处理响应：新的响应格式直接返回数据
      ({ data }) => data as Player,
      // 处理错误：解析新的错误格式 {"detail": "..."}
      commonErrorHandler
    );

    // 获取玩家列表
    public listPlayers = apiFactory<ListPlayersRequest, ListPlayersResponse>(
      (request = {}) => ({
        method: 'GET',
        url: '/api/v1/players',
        params: this.buildParams({
          page: request.page || 1,
          page_size: request.pageSize || 20,
          search: request.search,
          town_id: request.townId,
          ban_mode: request.banMode,
          name: request.name,
          qq: request.qq,
          qqguild: request.qqguild,
          discord: request.discord
        })
      }),
      ({ data }) => data as ListPlayersResponse,
      commonErrorHandler
    );

    // 获取玩家详情
    public getPlayer = apiFactory<GetPlayerRequest, Player>(
      (request) => ({
        method: 'GET',
        url: `/api/v1/players/${request.id}`
      }),
      ({ data }) => data as Player,
      commonErrorHandler
    );

    // 更新玩家信息
    public updatePlayer = apiFactory<UpdatePlayerRequest, Player>(
      (request) => ({
        method: 'PUT',
        url: `/api/v1/players/${request.id}`,
        data: {
          name: request.name,
          town_id: request.townId,
          qq: request.qq,
          qqguild: request.qqguild,
          discord: request.discord,
          in_qq_group: request.inQqGroup,
          in_qq_guild: request.inQqGuild,
          in_discord: request.inDiscord
        }
      }),
      ({ data }) => data as Player,
      commonErrorHandler
    );

    // 删除玩家
    public deletePlayer = apiFactory<DeletePlayerRequest, EmptyResponse>(
      (request) => ({
        method: 'DELETE',
        url: `/api/v1/players/${request.id}`
      }),
      ({ data }) => data as EmptyResponse,
      commonErrorHandler
    );

    // 封禁玩家
    public banPlayer = apiFactory<BanPlayerRequest, EmptyResponse>(
      (request) => ({
        method: 'POST',
        url: `/api/v1/players/${request.playerId}/ban`,
        data: {
          ban_mode: request.banMode,
          duration_seconds: request.durationSeconds,
          reason: request.reason
        }
      }),
      ({ data }) => data as EmptyResponse,
      commonErrorHandler
    );

    // 解封玩家
    public unbanPlayer = apiFactory<UnbanPlayerRequest, EmptyResponse>(
      (request) => ({
        method: 'POST',
        url: `/api/v1/players/${request.playerId}/unban`,
        data: {}
      }),
      ({ data }) => data as EmptyResponse,
      commonErrorHandler
    );

    // 批量玩家验证
    public validate = apiFactory<ValidateRequest, ValidateResponse>(
      (request) => ({
        method: 'POST',
        url: '/api/v1/players/validate',
        data: {
          players: request.players.map(p => ({
            player_name: p.playerName,
            ip: p.ip,
            client_version: p.clientVersion,
            protocol_version: p.protocolVersion
          })),
          server_id: request.serverId,
          login: request.login,
          timestamp: request.timestamp || Date.now()
        }
      }),
      ({ data }) => data as ValidateResponse,
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

  return new PlayerService();
};
