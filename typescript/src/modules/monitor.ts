/**
 * 服务器监控服务模块
 * 基于 @sttot/axios-api 的标准化API定义
 */

import { apiBase } from '@sttot/axios-api';
import {
  HeartbeatRequest,
  HeartbeatResponse,
  GetMonitorStatsRequest,
  MonitorStatsResponse
} from '../types';
import { commonErrorHandler } from '../utils/errorHandler';

export const initMonitorService = (apiFactory: ReturnType<typeof apiBase>) => {
  class MonitorService {
    // 服务器心跳
    public heartbeat = apiFactory<HeartbeatRequest, HeartbeatResponse>(
      (request) => ({
        method: 'POST',
        url: `/api/v1/monitor/${request.serverId}/heartbeat`,
        data: {
          current_players: request.currentPlayers,
          max_players: request.maxPlayers,
          tps: request.tps,
          version: request.version,
          motd: request.motd,
          rtt_ms: request.rttMs
        }
      }),
      ({ data }) => ({
        receivedAt: data.received_at,
        responseAt: data.response_at,
        expireDurationMs: data.expire_duration_ms
      }),
      commonErrorHandler
    );

    // 获取监控统计信息
    public getMonitorStats = apiFactory<GetMonitorStatsRequest, MonitorStatsResponse>(
      (request) => ({
        method: 'GET',
        url: `/api/v1/monitor/${request.serverId}/stats`,
        params: {
          since: request.since,
          duration: request.duration
        }
      }),
      ({ data }) => ({
        serverId: data.server_id,
        stats: data.stats.map((stat: any) => ({
          timestamp: stat.timestamp,
          currentPlayers: stat.current_players,
          tps: stat.tps,
          latencyMs: stat.latency_ms
        }))
      }),
      commonErrorHandler
    );
  }

  return new MonitorService();
};
