/**
 * 玩家服务器关系管理服务模块
 * 基于 @sttot/axios-api 的标准化API定义
 */

import { apiBase } from '@sttot/axios-api';
import {
  PlayerServersResponse,
  ServerPlayersResponse,
  EmptyResponse,
  SetPlayersOfflineRequest
} from '../types';
import { commonErrorHandler } from '../utils/errorHandler';

export const initPlayerServerService = (apiFactory: ReturnType<typeof apiBase>) => {
  class PlayerServerService {

    // 获取玩家的服务器关系
    public getPlayerServers = apiFactory<{ playerId: number; onlineOnly?: boolean }, PlayerServersResponse>(
      (request) => ({
        method: 'GET',
        url: `/api/v1/players/${request.playerId}/servers`,
        params: this.buildParams({
          online_only: request.onlineOnly
        })
      }),
      ({ data }) => ({
        servers: data.servers.map((server: any) => ({
          playerId: server.player_id,
          serverId: server.server_id,
          online: server.online,
          joinedAt: server.joined_at,
          createdAt: server.created_at,
          updatedAt: server.updated_at
        })),
        total: data.total,
        page: data.page,
        pageSize: data.page_size
      }),
      commonErrorHandler
    );

    // 获取全局在线玩家
    public getServerPlayers = apiFactory<{
      page?: number;
      pageSize?: number;
      search?: string;
      serverId?: number;
      onlineOnly?: boolean
    }, ServerPlayersResponse>(
      (request) => ({
        method: 'GET',
        url: '/api/v1/server-players',
        params: this.buildParams({
          page: request.page,
          page_size: request.pageSize,
          search: request.search,
          server_id: request.serverId,
          online_only: request.onlineOnly
        })
      }),
      ({ data }) => ({
        players: data.players.map((player: any) => ({
          playerId: player.player_id,
          playerName: player.player_name,
          serverId: player.server_id,
          serverName: player.server_name,
          joinedAt: player.joined_at
        })),
        total: data.total,
        page: data.page,
        pageSize: data.page_size
      }),
      commonErrorHandler
    );

    // 设置玩家离线状态 - 在玩家退出时调用
    public setPlayersOffline = apiFactory<SetPlayersOfflineRequest, EmptyResponse>(
      (request) => ({
        method: 'POST',
        url: '/api/v1/servers/players/offline',
        data: {
          server_id: request.serverId,
          player_ids: request.playerIds
        }
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

  return new PlayerServerService();
};
