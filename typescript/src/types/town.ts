import { Player } from './player';
import { PaginationRequest, PaginationResponse, EmptyResponse } from './common';

/**
 * 城镇信息
 */
export interface Town {
  /** 城镇ID */
  id: number;
  /** 城镇名称 */
  name: string;
  /** 城镇等级(0-5星级) */
  level: number;
  /** 镇长玩家ID */
  leaderId?: number;
  /** 绑定QQ群号 */
  qqGroup?: string;
  /** 城镇描述 */
  description?: string;
  /** 创建时间(ISO8601格式) */
  createdAt: string;
  /** 更新时间(ISO8601格式) */
  updatedAt: string;
}

/**
 * 创建城镇请求
 */
export interface CreateTownRequest {
  /** 城镇名称 */
  name: string;
  /** 城镇等级 */
  level?: number;
  /** 镇长玩家ID */
  leaderId?: number;
  /** 绑定QQ群号 */
  qqGroup?: string;
  /** 城镇描述 */
  description?: string;
}

/**
 * 更新城镇请求
 */
export interface UpdateTownRequest {
  /** 城镇名称 */
  name?: string;
  /** 城镇等级 */
  level?: number;
  /** 镇长玩家ID */
  leaderId?: number;
  /** 绑定QQ群号 */
  qqGroup?: string;
  /** 城镇描述 */
  description?: string;
}

/**
 * 城镇列表请求
 */
export interface ListTownsRequest extends PaginationRequest {
  /** 搜索关键词 */
  search?: string;
  /** 最小等级 */
  minLevel?: number;
  /** 最大等级 */
  maxLevel?: number;
}

/**
 * 城镇列表响应
 */
export interface ListTownsResponse extends PaginationResponse {
  /** 城镇列表 */
  towns: Town[];
}

/**
 * 城镇成员管理请求
 */
export interface ManageTownMemberRequest {
  /** 玩家ID */
  playerId: number;
  /** 操作类型: add/remove/set_leader */
  action: 'add' | 'remove' | 'set_leader';
}

/**
 * 获取城镇成员请求
 */
export interface GetTownMembersRequest extends PaginationRequest {
  // 继承分页参数
}

/**
 * 城镇成员响应
 */
export interface TownMembersResponse extends PaginationResponse {
  /** 成员列表 */
  members: Player[];
}
