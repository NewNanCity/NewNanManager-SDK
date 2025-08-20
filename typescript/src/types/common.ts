/**
 * 通用类型定义
 */

/**
 * 客户端配置接口
 */
export interface ClientConfig {
  /** API认证Token */
  token: string;
  /** API服务器地址 */
  baseUrl: string;
  /** 请求超时时间(毫秒) */
  timeout?: number;
  /** 是否启用HTTP请求日志 */
  enableLogging?: boolean;
}

/**
 * API错误响应接口
 */
export interface ApiErrorResponse {
  /** 错误代码 */
  code: number;
  /** 错误信息 */
  message: string;
}

/**
 * 空响应
 */
export interface EmptyResponse {
  // 空对象
}

/**
 * 分页请求基础接口
 */
export interface PaginationRequest {
  /** 页码 */
  page?: number;
  /** 每页数量 */
  pageSize?: number;
}

/**
 * 分页响应基础接口
 */
export interface PaginationResponse {
  /** 总数 */
  total: number;
  /** 当前页码 */
  page: number;
  /** 每页数量 */
  pageSize: number;
}
