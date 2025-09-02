/**
 * NewNanManager TypeScript SDK - 类型定义
 */

// ========== 枚举类型 ==========
export enum BanMode {
  NORMAL = 0,
  TEMPORARY = 1,
  PERMANENT = 2
}

export enum LoginAction {
  LOGIN = 1,
  LOGOUT = 2
}

export enum ThreatLevel {
  LOW = 0,
  MEDIUM = 1,
  HIGH = 2,
  CRITICAL = 3
}

export enum QueryStatus {
  PENDING = 0,
  COMPLETED = 1,
  FAILED = 2
}

// ========== 基础类型 ==========
export interface ClientConfig {
  baseUrl: string;
  token: string;
  timeout?: number;
}

export interface PaginationRequest {
  page?: number;
  pageSize?: number;
}

export interface PaginationResponse {
  total: number;
  page: number;
  page_size: number; // 匹配服务端的字段名
}

export interface EmptyResponse {}

// ========== 玩家相关 ==========
export interface Player {
  id: number;
  name: string;
  townId?: number;
  qq?: string;
  qqguild?: string;
  discord?: string;
  inQqGroup: boolean;
  inQqGuild: boolean;
  inDiscord: boolean;
  banMode: BanMode;
  banExpire?: string;
  banReason?: string;
  createdAt: string;
  updatedAt: string;
}

export interface CreatePlayerRequest {
  name: string;
  townId?: number;
  qq?: string;
  qqguild?: string;
  discord?: string;
  inQqGroup?: boolean;
  inQqGuild?: boolean;
  inDiscord?: boolean;
}

export interface ListPlayersRequest extends PaginationRequest {
  search?: string;
  townId?: number;
  banMode?: BanMode;
  name?: string;
  qq?: string;
  qqguild?: string;
  discord?: string;
}

export interface ListPlayersResponse extends PaginationResponse {
  players: Player[];
}

// ========== 批量验证（新功能）==========
export interface PlayerValidateInfo {
  playerName: string;
  ip: string;
  clientVersion?: string;
  protocolVersion?: string;
}

export interface PlayerValidateResult {
  playerName: string;
  allowed: boolean;
  playerId?: number;
  reason?: string;
  newbie: boolean;
  ipRisk?: Record<string, any>;
}

export interface ValidateRequest {
  players: PlayerValidateInfo[];
  serverId: number;
  login: boolean;
  timestamp?: number;
}

export interface ValidateResponse {
  results: PlayerValidateResult[];
  processedAt: number;
}

// ========== 服务器相关 ==========
export interface ServerRegistry {
  id: number;
  name: string;
  address: string;
  description?: string;
  createdAt: string;
  updatedAt: string;
}

export interface CreateServerRequest {
  name: string;
  address: string;
  description?: string;
}

// ========== 城镇相关 ==========
export interface Town {
  id: number;
  name: string;
  level: number;
  leaderId?: number;
  qqGroup?: string;
  description?: string;
  createdAt: string;
  updatedAt: string;
}

export interface CreateTownRequest {
  name: string;
  level: number;
  leaderId?: number;
  qqGroup?: string;
  description?: string;
}

// ========== 玩家服务器关系（新功能）==========

export interface PlayerServer {
  playerId: number;
  playerName: string;
  serverId: number;
  serverName: string;
  online: boolean;
  lastOnline?: string;
  firstJoined: string;
  totalPlayTime: number;
}

export interface GetOnlinePlayersRequest extends PaginationRequest {
  search?: string;
  serverId?: number;
}

export interface ServerPlayersResponse extends PaginationResponse {
  players: PlayerServer[];
}

// ========== IP管理（新功能）==========
export interface IPInfo {
  ip: string;
  country?: string;
  region?: string;
  city?: string;
  isp?: string;
  org?: string;
  timezone?: string;
  lat?: number;
  lon?: number;
  mobile?: boolean;
  proxy?: boolean;
  hosting?: boolean;
  queryStatus: string;
  queryAttempts: number;
  lastQueryAt?: string;
  createdAt: string;
  updatedAt: string;
  // 风险信息字段
  riskLevel: string;
  riskDescription: string;
}

export interface BanIPRequest {
  ips: string[];
  reason: string;
}

export interface UnbanIPRequest {
  ips: string[];
}

export interface ListIPsRequest extends PaginationRequest {
  bannedOnly?: boolean;
  minThreatLevel?: ThreatLevel;
  minRiskScore?: number;
}

export interface IPBan {
  ip: string;
  reason: string;
  bannedAt: string;
  unbannedAt?: string;
  unbanReason?: string;
  active: boolean;
}

export interface ListBannedIPsRequest extends PaginationRequest {
  search?: string;
  activeOnly?: boolean;
}

export interface ListBannedIPsResponse extends PaginationResponse {
  bans: IPBan[];
}

// ========== 更多玩家相关接口 ==========
export interface GetPlayerRequest {
  id: number;
}

export interface UpdatePlayerRequest {
  id: number;
  name?: string;
  townId?: number;
  qq?: string;
  qqguild?: string;
  discord?: string;
  inQqGroup?: boolean;
  inQqGuild?: boolean;
  inDiscord?: boolean;
}

export interface DeletePlayerRequest {
  id: number;
}

export interface BanPlayerRequest {
  playerId: number;
  banMode: BanMode;
  durationSeconds?: number;
  reason: string;
}

export interface UnbanPlayerRequest {
  playerId: number;
}

// ========== 更多城镇相关接口 ==========
export interface GetTownRequest {
  id: number;
  detail?: boolean;
}

export interface UpdateTownRequest {
  id: number;
  name?: string;
  level?: number;
  leaderId?: number;
  qqGroup?: string;
  description?: string;
  addPlayers?: number[];
  removePlayers?: number[];
}

export interface DeleteTownRequest {
  id: number;
}

export interface ListTownsRequest extends PaginationRequest {
  search?: string;
  level?: number;
}

export interface ListTownsResponse extends PaginationResponse {
  towns: Town[];
}



export interface GetTownMembersRequest extends PaginationRequest {
  townId: number;
}

export interface TownMember {
  playerId: number;
  playerName: string;
  joinedAt: string;
  role: string;
}

export interface TownMembersResponse extends PaginationResponse {
  members: TownMember[];
}

export interface TownDetailResponse {
  town: Town;
  leader?: Player;
  members: Player[];
  memberCount: number;
}

// ========== 更多服务器相关接口 ==========
export interface GetServerRequest {
  id: number;
}

export interface UpdateServerRequest {
  id: number;
  name?: string;
  address?: string;
  description?: string;
}

export interface DeleteServerRequest {
  id: number;
}

export interface ListServersRequest extends PaginationRequest {
  search?: string;
}

export interface ListServersResponse extends PaginationResponse {
  servers: ServerRegistry[];
}

// ========== 监控相关接口 ==========
export interface PlayerLoginInfo {
  playerId: number;
  name: string;
  ip: string;
}

export interface HeartbeatRequest {
  serverId: number;
  currentPlayers: number;        // 当前在线人数
  maxPlayers: number;            // 最大玩家数
  tps?: number;                  // 服务器TPS
  version?: string;              // 服务器版本
  motd?: string;                 // 服务器描述
  rttMs?: number;                // RTT延迟(毫秒)
}

export interface HeartbeatResponse {
  received_at: number;           // 服务端接收时间戳(毫秒)
  response_at: number;           // 服务端响应时间戳(毫秒)
  expire_duration_ms: number;    // 状态过期时间(毫秒)
}

export interface MonitorStatRecord {
  timestamp: number;             // 统计时间戳
  current_players: number;       // 当前在线人数
  tps?: number;                  // 服务器TPS
  latency_ms?: number;           // 延迟毫秒
}

export interface MonitorStatsResponse {
  server_id: number;             // 服务器ID
  stats: MonitorStatRecord[];    // 监控统计信息列表
}

export interface GetMonitorStatsRequest {
  serverId: number;
  since?: number;                // 起始时间戳(Unix时间戳，0表示当前时间-duration)
  duration?: number;             // 持续时间(秒，默认3600秒)
}

export interface LatencyRequest {
  serverId: number;
  latency: number;
}

// ========== Token管理相关接口 ==========
export interface CreateApiTokenRequest {
  name: string;
  role: string;
  description?: string;
  expireDays?: number;
}

export interface CreateApiTokenResponse {
  id: number;
  name: string;
  token: string;
  role: string;
  description?: string;
  createdAt: string;
}

export interface UpdateApiTokenRequest {
  id: number;
  name?: string;
  role?: string;
  description?: string;
  active?: boolean;
}

export interface DeleteApiTokenRequest {
  id: number;
}

export interface ListApiTokensRequest extends PaginationRequest {
  search?: string;
}

export interface ApiToken {
  id: number;
  name: string;
  role: string;
  description?: string;
  lastUsedAt?: string;
  createdAt: string;
  updatedAt: string;
}

export interface ListApiTokensResponse extends PaginationResponse {
  tokens: ApiToken[];
}

// ========== 玩家服务器关系相关 ==========
export interface SetPlayersOfflineRequest {
  serverId: number;
  playerIds: number[];
}
