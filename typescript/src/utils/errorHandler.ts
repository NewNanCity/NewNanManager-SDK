export interface ErrorDetails {
  category?: unknown;
  code?: unknown;
}

export interface ErrorResponse {
  code?: unknown;
  message?: unknown;
  detail?: unknown;
  request_id?: unknown;
  trace_id?: unknown;
  error?: ErrorDetails;
}

/** 统一解析模板错误体，并在迁移期兼容 legacy detail。 */
import { AxiosError, AxiosHeaders, isAxiosError } from 'axios';

export class NewNanManagerHttpError extends Error {
  public readonly statusCode?: number;
  public readonly code?: string;
  public readonly apiCode?: number;
  public readonly errorCategory?: string;
  public readonly machineCode?: string;
  public readonly requestId?: string;
  public readonly traceId?: string;
  public readonly retryAfter?: string;

  constructor(error: AxiosError<ErrorResponse>) {
    const data = error.response?.data;
    const message = typeof data?.message === 'string' && data.message.length > 0
      ? data.message
      : typeof data?.detail === 'string' && data.detail.length > 0
        ? data.detail
        : error.message;
    super(message);
    this.name = 'NewNanManagerHttpError';
    this.statusCode = error.response?.status;
    this.code = error.code;
    this.apiCode = typeof data?.code === 'number' && Number.isInteger(data.code) ? data.code : undefined;
    this.errorCategory = typeof data?.error?.category === 'string' ? data.error.category : undefined;
    this.machineCode = typeof data?.error?.code === 'string' ? data.error.code : undefined;
    const headers = error.response?.headers;
    const requestId = headers instanceof AxiosHeaders ? headers.get('X-Request-ID') : headers?.['x-request-id'];
    const retryAfter = headers instanceof AxiosHeaders ? headers.get('Retry-After') : headers?.['retry-after'];
    this.requestId = typeof requestId === 'string' ? requestId : typeof data?.request_id === 'string' ? data.request_id : undefined;
    this.traceId = typeof data?.trace_id === 'string' ? data.trace_id : undefined;
    this.retryAfter = typeof retryAfter === 'string' ? retryAfter : undefined;
  }
}

/**
 * 通用错误处理函数
 * 用作所有API的第三个参数（ErrorHandler）
 * 与现有的player模块中的错误处理逻辑保持一致
 */
export const commonErrorHandler = ({ error }: { error: unknown }): never => {
  if (isAxiosError<ErrorResponse>(error)) {
    throw new NewNanManagerHttpError(error);
  }
  throw error instanceof Error ? error : new Error(String(error));
};
