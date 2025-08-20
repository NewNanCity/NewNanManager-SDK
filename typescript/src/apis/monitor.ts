import { BaseApiFactory } from './base';
import {
  HeartbeatRequest,
  HeartbeatResponse,
  ServerStatus,
  LatencyStatsResponse,
  ClientConfig
} from '../types';

/**
 * 服务器监控API
 */
export class MonitorApi extends BaseApiFactory {
  constructor(config: ClientConfig) {
    super(config);
  }

  /**
   * 服务器心跳
   */
  heartbeat = this.createApi<{ serverId: number; request: HeartbeatRequest }, HeartbeatResponse>(
    ({ serverId, request }) => ({
      method: 'POST',
      url: `/api/v1/servers/${serverId}/heartbeat`,
      data: {
        timestamp: request.timestamp,
        sequence_id: request.sequenceId,
        current_players: request.currentPlayers,
        max_players: request.maxPlayers,
        tps: request.tps,
        version: request.version,
        motd: request.motd,
        last_rtt_ms: request.lastRttMs,
        player_list: request.playerList?.map(player => ({
          player_id: player.playerId,
          name: player.name,
          ip: player.ip
        }))
      }
    }),
    ({ data }) => data
  );

  /**
   * 获取服务器状态
   */
  getServerStatus = this.createApi<number, ServerStatus>(
    (serverId) => ({
      method: 'GET',
      url: `/api/v1/servers/${serverId}/status`
    }),
    ({ data }) => data
  );

  /**
   * 获取延迟统计
   */
  getLatencyStats = this.createApi<number, LatencyStatsResponse>(
    (serverId) => ({
      method: 'GET',
      url: `/api/v1/servers/${serverId}/latency`
    }),
    ({ data }) => data
  );
}
