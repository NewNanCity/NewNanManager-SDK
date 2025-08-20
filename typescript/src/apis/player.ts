import { BaseApiFactory } from './base';
import {
  Player,
  CreatePlayerRequest,
  UpdatePlayerRequest,
  BanPlayerRequest,
  ListPlayersRequest,
  ListPlayersResponse,
  ValidateLoginRequest,
  ValidateLoginResponse,
  EmptyResponse,
  ClientConfig
} from '../types';

/**
 * 玩家管理API
 */
export class PlayerApi extends BaseApiFactory {
  constructor(config: ClientConfig) {
    super(config);
  }

  /**
   * 创建玩家
   */
  createPlayer = this.createApi<CreatePlayerRequest, Player>(
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
    ({ data }) => data
  );

  /**
   * 获取玩家详情
   */
  getPlayer = this.createApi<number, Player>(
    (id) => ({
      method: 'GET',
      url: `/api/v1/players/${id}`
    }),
    ({ data }) => data
  );

  /**
   * 更新玩家信息
   */
  updatePlayer = this.createApi<{ id: number; request: UpdatePlayerRequest }, Player>(
    ({ id, request }) => ({
      method: 'PUT',
      url: `/api/v1/players/${id}`,
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
    ({ data }) => data
  );

  /**
   * 删除玩家
   */
  deletePlayer = this.createApi<number, EmptyResponse>(
    (id) => ({
      method: 'DELETE',
      url: `/api/v1/players/${id}`
    }),
    ({ data }) => data
  );

  /**
   * 获取玩家列表
   */
  listPlayers = this.createApi<ListPlayersRequest, ListPlayersResponse>(
    (request) => {
      const params = this.buildQueryParams({
        page: request.page ?? 1,
        page_size: request.pageSize ?? 20,
        search: request.search,
        town_id: request.townId,
        ban_mode: request.banMode
      });

      return {
        method: 'GET',
        url: '/api/v1/players',
        params
      };
    },
    ({ data }) => data
  );

  /**
   * 封禁玩家
   */
  banPlayer = this.createApi<{ playerId: number; request: BanPlayerRequest }, EmptyResponse>(
    ({ playerId, request }) => ({
      method: 'POST',
      url: `/api/v1/players/${playerId}/ban`,
      data: {
        ban_mode: request.banMode,
        duration_seconds: request.durationSeconds,
        reason: request.reason
      }
    }),
    ({ data }) => data
  );

  /**
   * 解封玩家
   */
  unbanPlayer = this.createApi<number, EmptyResponse>(
    (playerId) => ({
      method: 'POST',
      url: `/api/v1/players/${playerId}/unban`
    }),
    ({ data }) => data
  );

  /**
   * 玩家登录验证
   */
  validateLogin = this.createApi<ValidateLoginRequest, ValidateLoginResponse>(
    (request) => ({
      method: 'POST',
      url: '/api/v1/players/validate-login',
      data: {
        player_name: request.playerName,
        server_id: request.serverId,
        client_version: request.clientVersion,
        protocol_version: request.protocolVersion
      }
    }),
    ({ data }) => data
  );
}
