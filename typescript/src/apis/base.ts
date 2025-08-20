import axios, { AxiosInstance, AxiosRequestConfig, AxiosResponse } from 'axios';
import { apiBase } from '@sttot/axios-api';
import { ClientConfig, ApiErrorResponse } from '../types';
import { 
  NanManagerError, 
  NetworkError, 
  AuthenticationError, 
  TimeoutError 
} from '../errors';

/**
 * 基础API工厂类
 */
export class BaseApiFactory {
  protected readonly axiosInstance: AxiosInstance;
  protected readonly config: Required<ClientConfig>;
  protected readonly api: ReturnType<typeof apiBase>;

  constructor(config: ClientConfig) {
    // 设置默认配置
    this.config = {
      timeout: 30000,
      enableLogging: false,
      ...config
    };

    // 创建axios实例
    this.axiosInstance = axios.create({
      baseURL: this.config.baseUrl,
      timeout: this.config.timeout,
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${this.config.token}`
      }
    });

    // 创建api工厂
    this.api = apiBase(this.axiosInstance);

    // 设置请求拦截器
    this.axiosInstance.interceptors.request.use(
      (config) => {
        if (this.config.enableLogging) {
          console.log(`[NewNanManager] Request: ${config.method?.toUpperCase()} ${config.url}`);
          if (config.data) {
            console.log(`[NewNanManager] Request Data:`, config.data);
          }
        }
        return config;
      },
      (error) => {
        if (this.config.enableLogging) {
          console.error('[NewNanManager] Request Error:', error);
        }
        return Promise.reject(this.handleError(error));
      }
    );

    // 设置响应拦截器
    this.axiosInstance.interceptors.response.use(
      (response) => {
        if (this.config.enableLogging) {
          console.log(`[NewNanManager] Response: ${response.status} ${response.statusText}`);
          if (response.data) {
            console.log(`[NewNanManager] Response Data:`, response.data);
          }
        }
        return response;
      },
      (error) => {
        if (this.config.enableLogging) {
          console.error('[NewNanManager] Response Error:', error);
        }
        return Promise.reject(this.handleError(error));
      }
    );
  }

  /**
   * 获取axios实例的拦截器
   */
  get interceptors() {
    return this.axiosInstance.interceptors;
  }

  /**
   * 统一错误处理
   */
  protected handleError(error: any): Error {
    // 网络错误或请求被取消
    if (!error.response) {
      if (error.code === 'ECONNABORTED' || error.message?.includes('timeout')) {
        return new TimeoutError(this.config.timeout, error);
      }
      return new NetworkError(`Network error: ${error.message}`, error);
    }

    const response: AxiosResponse = error.response;
    const status = response.status;

    // 认证错误
    if (status === 401) {
      return new AuthenticationError('Invalid token or unauthorized access', error);
    }

    // API业务错误
    if (status >= 400 && status < 500) {
      try {
        const errorData = response.data as ApiErrorResponse;
        if (errorData && typeof errorData.code === 'number' && typeof errorData.message === 'string') {
          return new NanManagerError(errorData.code, errorData.message, error);
        }
      } catch (e) {
        // 解析错误响应失败，使用默认错误信息
      }
      
      return new NanManagerError(status, response.statusText || 'Client error', error);
    }

    // 服务器错误
    if (status >= 500) {
      return new NanManagerError(status, `Server error: ${response.statusText}`, error);
    }

    // 其他错误
    return new NanManagerError(status, `HTTP error: ${response.statusText}`, error);
  }

  /**
   * 构建查询参数
   */
  protected buildQueryParams(params: Record<string, any>): Record<string, string> {
    const queryParams: Record<string, string> = {};
    
    Object.entries(params).forEach(([key, value]) => {
      if (value !== undefined && value !== null) {
        queryParams[key] = String(value);
      }
    });

    return queryParams;
  }

  /**
   * 创建基础API定义
   */
  protected createApi<TProps, TResult>(
    callHandler: (props: TProps) => AxiosRequestConfig,
    resultHandler: (response: AxiosResponse) => TResult
  ) {
    return this.api<TProps, TResult>(
      callHandler,
      resultHandler,
      ({ error }) => {
        throw this.handleError(error);
      }
    );
  }
}
