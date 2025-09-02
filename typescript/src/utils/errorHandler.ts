/**
 * 统一的错误处理器
 * 处理服务端返回的 {"detail": "xxx"} 格式错误响应
 */

/**
 * 通用错误处理函数
 * 用作所有API的第三个参数（ErrorHandler）
 * 与现有的player模块中的错误处理逻辑保持一致
 */
export const commonErrorHandler = ({ error }: { error: any }) => {
  const message = (error as any).response?.data?.detail || error.message || (error ? String(error) : 'Unkown error');
  throw new Error(message);
};
