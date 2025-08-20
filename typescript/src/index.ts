/**
 * NewNanManager TypeScript SDK
 * 
 * 基于@sttot/axios-api的NewNanManager客户端SDK，提供完整的类型安全API接口。
 * 
 * @example
 * ```typescript
 * import { NewNanManagerClient } from '@nanmanager/typescript-sdk';
 * 
 * const client = new NewNanManagerClient({
 *     token: 'your-api-token',
 *     baseUrl: 'https://your-server.com'
 * });
 * 
 * // 使用客户端
 * const servers = await client.listServers();
 * const players = await client.listPlayers();
 * ```
 */

export { NewNanManagerClient } from './client';
export * from './types';
export * from './errors';
export * from './apis';
