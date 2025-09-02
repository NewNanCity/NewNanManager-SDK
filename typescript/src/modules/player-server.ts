/**
 * 玩家服务器关系管理服务模块
 * 基于 @sttot/axios-api 的标准化API定义
 */

import { apiBase } from '@sttot/axios-api';
import {
  GetOnlinePlayersRequest,
  ServerPlayersResponse,
  EmptyResponse,
  SetPlayersOfflineRequest
} from '../types';
import { commonErrorHandler } from '../utils/errorHandler';

export const initPlayerServerService = (apiFactory: ReturnType<typeof apiBase>) => {
  class PlayerServerService {


    // 获取全局在线玩家列表
    public getOnlinePlayers = apiFactory<GetOnlinePlayersRequest, ServerPlayersResponse>(
      (request) => ({
        method: 'GET',
        url: '/api/v1/online-players',
        params: this.buildParams({
          page: request.page,
          page_size: request.pageSize,
          search: request.search,
          server_id: request.serverId
        })
      }),
      ({ data }) => data as ServerPlayersResponse,
      commonErrorHandler
    );

    // 获取玩家的服务器列表
    public getPlayerServers = apiFactory<{ playerId: number; page?: number; pageSize?: number }, ServerPlayersResponse>(
      (request) => ({
        method: 'GET',
        url: '/api/v1/player-servers/servers',
        params: this.buildParams({
          player_id: request.playerId,
          page: request.page,
          page_size: request.pageSize
        })
      }),
      ({ data }) => data as ServerPlayersResponse,
      commonErrorHandler
    );

    // 获取服务器的玩家列表
    public getServerPlayers = apiFactory<{ serverId: number; page?: number; pageSize?: number }, ServerPlayersResponse>(
      (request) => ({
        method: 'GET',
        url: '/api/v1/player-servers/players',
        params: this.buildParams({
          server_id: request.serverId,
          page: request.page,
          page_size: request.pageSize
        })
      }),
      ({ data }) => data as ServerPlayersResponse,
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
