import { PaginationRequest, PaginationResponse, EmptyResponse } from './common';

/**
 * 服务器注册信息
 */
export interface ServerRegistry {
  /** 服务器ID */
  id: number;
  /** 服务器名称 */
  name: string;
  /** 服务器地址（可包含端口，如 "example.com:25565" 或 "example.com" 用于SRV记录） */
  address: string;
  /** 服务器描述 */
  description?: string;
  /** 创建时间(ISO8601格式) */
  createdAt: string;
  /** 更新时间(ISO8601格式) */
  updatedAt: string;
}

/**
 * 服务器状态
 */
export interface ServerStatus {
  /** 服务器ID */
  serverId: number;
  /** 是否在线 */
  online: boolean;
  /** 当前在线人数 */
  currentPlayers: number;
  /** 最大玩家数 */
  maxPlayers: number;
  /** 延迟毫秒 */
  latencyMs?: number;
  /** 服务器TPS */
  tps?: number;
  /** 服务器版本 */
  version?: string;
  /** 服务器描述 */
  motd?: string;
  /** 状态失效时间(ISO8601格式) */
  expireAt: string;
  /** 最后心跳时间(ISO8601格式) */
  lastHeartbeat: string;
}

/**
 * 服务器注册请求
 */
export interface RegisterServerRequest {
  /** 服务器名称 */
  name: string;
  /** 服务器地址（可包含端口，如 "example.com:25565" 或 "example.com" 用于SRV记录） */
  address: string;
  /** 服务器描述 */
  description?: string;
}

/**
 * 更新服务器请求
 */
export interface UpdateServerRequest {
  /** 服务器名称 */
  name?: string;
  /** 服务器地址（可包含端口，如 "example.com:25565" 或 "example.com" 用于SRV记录） */
  address?: string;
  /** 服务器描述 */
  description?: string;
}

/**
 * 服务器列表请求
 */
export interface ListServersRequest extends PaginationRequest {
  /** 搜索关键词 */
  search?: string;
  /** 仅显示在线服务器 */
  onlineOnly?: boolean;
}

/**
 * 服务器列表响应
 */
export interface ListServersResponse extends PaginationResponse {
  /** 服务器列表 */
  servers: ServerRegistry[];
}

/**
 * 服务器详细信息响应
 */
export interface ServerDetailResponse {
  /** 服务器信息 */
  server: ServerRegistry;
  /** 服务器状态 */
  status?: ServerStatus;
}

/**
 * 玩家登录信息
 */
export interface PlayerLoginInfo {
  /** 玩家ID */
  playerId: number;
  /** 玩家名 */
  name: string;
  /** 登录IP */
  ip: string;
}

/**
 * 心跳请求
 */
export interface HeartbeatRequest {
  /** 客户端时间戳 */
  timestamp: number;
  /** 序列号 */
  sequenceId: number;
  /** 当前在线人数 */
  currentPlayers: number;
  /** 最大玩家数 */
  maxPlayers: number;
  /** 服务器TPS */
  tps?: number;
  /** 服务器版本 */
  version?: string;
  /** 服务器描述 */
  motd?: string;
  /** 上次RTT */
  lastRttMs?: number;
  /** 玩家列表 */
  playerList?: PlayerLoginInfo[];
}

/**
 * 心跳响应
 */
export interface HeartbeatResponse {
  /** 服务端接收时间戳 */
  receivedAt: number;
  /** 服务端响应时间戳 */
  responseAt: number;
  /** 序列号 */
  sequenceId: number;
  /** 服务端时间戳 */
  serverTime: number;
  /** 状态信息 */
  status: string;
  /** 下次心跳时间 */
  nextHeartbeat: number;
  /** 状态过期时间 */
  expireAt: number;
}

/**
 * 延迟统计响应
 */
export interface LatencyStatsResponse {
  /** 服务器ID */
  serverId: number;
  /** 统计数量 */
  count: number;
  /** 当前延迟 */
  current: number;
  /** 平均延迟 */
  average: number;
  /** 最小延迟 */
  min: number;
  /** 最大延迟 */
  max: number;
  /** 方差 */
  variance: number;
  /** 最后更新时间(ISO8601格式) */
  lastUpdated: string;
}
