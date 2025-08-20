/**
 * NewNanManager API 异常类定义
 */

/**
 * NewNanManager API异常基类
 */
export class NanManagerError extends Error {
  public readonly code: number;

  constructor(code: number, message: string, cause?: Error) {
    super(message);
    this.name = 'NanManagerError';
    this.code = code;
    
    // 保持错误堆栈
    if (cause && cause.stack) {
      this.stack = cause.stack;
    }
    
    // 确保instanceof正常工作
    Object.setPrototypeOf(this, NanManagerError.prototype);
  }

  toString(): string {
    return `${this.name}(code=${this.code}, message='${this.message}')`;
  }
}

/**
 * 网络异常
 */
export class NetworkError extends Error {
  constructor(message: string, cause?: Error) {
    super(message);
    this.name = 'NetworkError';
    
    if (cause && cause.stack) {
      this.stack = cause.stack;
    }
    
    Object.setPrototypeOf(this, NetworkError.prototype);
  }
}

/**
 * 认证异常
 */
export class AuthenticationError extends Error {
  constructor(message: string = 'Authentication failed', cause?: Error) {
    super(message);
    this.name = 'AuthenticationError';
    
    if (cause && cause.stack) {
      this.stack = cause.stack;
    }
    
    Object.setPrototypeOf(this, AuthenticationError.prototype);
  }
}

/**
 * 序列化异常
 */
export class SerializationError extends Error {
  constructor(message: string, cause?: Error) {
    super(message);
    this.name = 'SerializationError';
    
    if (cause && cause.stack) {
      this.stack = cause.stack;
    }
    
    Object.setPrototypeOf(this, SerializationError.prototype);
  }
}

/**
 * 验证异常
 */
export class ValidationError extends Error {
  public readonly field?: string;

  constructor(message: string, field?: string, cause?: Error) {
    super(message);
    this.name = 'ValidationError';
    this.field = field;
    
    if (cause && cause.stack) {
      this.stack = cause.stack;
    }
    
    Object.setPrototypeOf(this, ValidationError.prototype);
  }
}

/**
 * 超时异常
 */
export class TimeoutError extends Error {
  public readonly timeout: number;

  constructor(timeout: number, cause?: Error) {
    super(`Request timeout after ${timeout}ms`);
    this.name = 'TimeoutError';
    this.timeout = timeout;
    
    if (cause && cause.stack) {
      this.stack = cause.stack;
    }
    
    Object.setPrototypeOf(this, TimeoutError.prototype);
  }
}
