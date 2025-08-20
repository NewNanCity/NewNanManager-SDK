import { BanMode } from './enums';
import { PaginationRequest, PaginationResponse, EmptyResponse } from './common';

/**
 * 玩家信息
 */
export interface Player {
  /** 玩家ID */
  id: number;
  /** 游戏ID */
  name: string;
  /** 所属城镇ID */
  townId?: number;
  /** QQ号 */
  qq?: string;
  /** QQ频道ID */
  qqguild?: string;
  /** Discord ID */
  discord?: string;
  /** 是否在QQ群 */
  inQqGroup: boolean;
  /** 是否在QQ频道 */
  inQqGuild: boolean;
  /** 是否在Discord群 */
  inDiscord: boolean;
  /** 封禁模式 */
  banMode: BanMode;
  /** 封禁到期时间(ISO8601格式) */
  banExpire?: string;
  /** 封禁原因 */
  banReason?: string;
  /** 创建时间(ISO8601格式) */
  createdAt: string;
  /** 更新时间(ISO8601格式) */
  updatedAt: string;
}

/**
 * 创建玩家请求
 */
export interface CreatePlayerRequest {
  /** 游戏ID */
  name: string;
  /** 所属城镇ID */
  townId?: number;
  /** QQ号 */
  qq?: string;
  /** QQ频道ID */
  qqguild?: string;
  /** Discord ID */
  discord?: string;
  /** 是否在QQ群 */
  inQqGroup?: boolean;
  /** 是否在QQ频道 */
  inQqGuild?: boolean;
  /** 是否在Discord群 */
  inDiscord?: boolean;
}

/**
 * 更新玩家请求
 */
export interface UpdatePlayerRequest {
  /** 游戏ID */
  name?: string;
  /** 所属城镇ID */
  townId?: number;
  /** QQ号 */
  qq?: string;
  /** QQ频道ID */
  qqguild?: string;
  /** Discord ID */
  discord?: string;
  /** 是否在QQ群 */
  inQqGroup?: boolean;
  /** 是否在QQ频道 */
  inQqGuild?: boolean;
  /** 是否在Discord群 */
  inDiscord?: boolean;
}

/**
 * 封禁玩家请求
 */
export interface BanPlayerRequest {
  /** 封禁模式 */
  banMode: BanMode;
  /** 封禁时长(秒) */
  durationSeconds?: number;
  /** 封禁原因 */
  reason: string;
}

/**
 * 玩家列表请求
 */
export interface ListPlayersRequest extends PaginationRequest {
  /** 搜索关键词 */
  search?: string;
  /** 城镇ID过滤 */
  townId?: number;
  /** 封禁状态过滤 */
  banMode?: BanMode;
}

/**
 * 玩家列表响应
 */
export interface ListPlayersResponse extends PaginationResponse {
  /** 玩家列表 */
  players: Player[];
}

/**
 * 玩家登录验证请求
 */
export interface ValidateLoginRequest {
  /** 玩家名 */
  playerName: string;
  /** 服务器ID */
  serverId: number;
  /** 客户端版本 */
  clientVersion?: string;
  /** 协议版本 */
  protocolVersion?: string;
}

/**
 * 玩家登录验证响应
 */
export interface ValidateLoginResponse {
  /** 是否允许登录 */
  allowed: boolean;
  /** 玩家ID */
  playerId?: number;
  /** 拒绝原因 */
  reason?: string;
}
