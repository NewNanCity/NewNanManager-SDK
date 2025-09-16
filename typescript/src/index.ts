/**
 * NewNanManager TypeScript SDK - 新版模块化架构
 */

// 导出新的模块化客户端
export { NewNanManagerClient } from './client';

// 导出所有服务模块工厂函数
export type { initPlayerService } from './modules/player';
export type { initServerService } from './modules/server';
export type { initTownService } from './modules/town';
export type { initMonitorService } from './modules/monitor';
export type { initTokenService } from './modules/token';
export type { initIPService } from './modules/ip';
export type { initPlayerServerService } from './modules/player-server';

// 导出所有类型定义
export * from './types';
