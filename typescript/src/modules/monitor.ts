/**
 * 服务器监控服务模块
 * 基于 @sttot/axios-api 的标准化API定义
 */

import { apiBase } from '@sttot/axios-api';
import {
  HeartbeatRequest,
  HeartbeatResponse,
  GetMonitorStatsRequest,
  MonitorStatsResponse,
  LatencyRequest,
  EmptyResponse
} from '../types';
import { commonErrorHandler } from '../utils/errorHandler';

export const initMonitorService = (apiFactory: ReturnType<typeof apiBase>) => {
  class MonitorService {
    // 服务器心跳
    public heartbeat = apiFactory<HeartbeatRequest, HeartbeatResponse>(
      (request) => ({
        method: 'POST',
        url: `/api/v1/servers/${request.serverId}/heartbeat`,
        data: {
          current_players: request.currentPlayers,
          max_players: request.maxPlayers,
          tps: request.tps,
          version: request.version,
          motd: request.motd,
          rtt_ms: request.rttMs
        }
      }),
      ({ data }) => data as HeartbeatResponse,
      commonErrorHandler
    );

    // 获取延迟统计数据
    public getLatencyStats = apiFactory<{ serverId: number }, any>(
      (request) => ({
        method: 'GET',
        url: `/api/v1/servers/${request.serverId}/latency`
      }),
      ({ data }) => data,
      commonErrorHandler
    );

    // 获取服务器状态
    public getServerStatus = apiFactory<{ serverId: number }, any>(
      (request) => ({
        method: 'GET',
        url: `/api/v1/servers/${request.serverId}/status`
      }),
      ({ data }) => data,
      commonErrorHandler
    );

    // 获取监控统计信息
    public getMonitorStats = apiFactory<GetMonitorStatsRequest, MonitorStatsResponse>(
      (request) => ({
        method: 'GET',
        url: `/api/v1/servers/${request.serverId}/monitor`,
        params: {
          since: request.since,
          duration: request.duration
        }
      }),
      ({ data }) => data as MonitorStatsResponse,
      commonErrorHandler
    );
  }

  return new MonitorService();
};
