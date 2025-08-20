import { EmptyResponse } from './common';

/**
 * API Token信息
 */
export interface ApiToken {
  /** Token ID */
  id: number;
  /** Token名称 */
  name: string;
  /** 角色 (admin/manager/server/monitor或自定义) */
  role: string;
  /** Token描述 */
  description?: string;
  /** 是否激活 */
  active: boolean;
  /** 过期时间(ISO8601格式)，NULL表示永不过期 */
  expireAt?: string;
  /** 最后使用时间(ISO8601格式) */
  lastUsedAt?: string;
  /** 最后使用IP */
  lastUsedIp?: string;
  /** 创建时间(ISO8601格式) */
  createdAt: string;
  /** 更新时间(ISO8601格式) */
  updatedAt: string;
}

/**
 * 创建API Token请求
 */
export interface CreateApiTokenRequest {
  /** Token名称 */
  name: string;
  /** 角色 (admin/manager/server/monitor或自定义) */
  role: string;
  /** Token描述 */
  description?: string;
  /** 过期天数 */
  expireDays?: number;
}

/**
 * 创建API Token响应
 */
export interface CreateApiTokenResponse {
  /** Token信息 */
  tokenInfo: ApiToken;
  /** Token明文值(仅创建时返回) */
  tokenValue: string;
}

/**
 * 更新API Token请求
 */
export interface UpdateApiTokenRequest {
  /** Token名称 */
  name?: string;
  /** 角色 (admin/manager/server/monitor或自定义) */
  role?: string;
  /** Token描述 */
  description?: string;
  /** 是否激活 */
  active?: boolean;
}

/**
 * API Token列表响应
 */
export interface ListApiTokensResponse {
  /** Token列表 */
  tokens: ApiToken[];
}
