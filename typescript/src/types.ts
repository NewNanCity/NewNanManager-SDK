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
  pageSize: number; // 匹配服务端的字段名
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
  banMode?: BanMode;
  banExpire?: string;
  banReason?: string;
}

export interface ValidateRequest {
  players: PlayerValidateInfo[];
  serverId: number;
  login: boolean;
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
  active: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface ServerStatus {
  serverId: number;
  online: boolean;
  currentPlayers: number;
  maxPlayers: number;
  latencyMs?: number;
  tps?: number;
  version?: string;
  motd?: string;
  expireAt: string;
  lastHeartbeat: string;
}

export interface ServerDetailResponse {
  server: ServerRegistry;
  status?: ServerStatus;
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
  serverId: number;
  online: boolean;
  joinedAt: string;
  createdAt: string;
  updatedAt: string;
}

export interface OnlinePlayer {
  playerId: number;
  playerName: string;
  serverId: number;
  serverName: string;
  joinedAt: string;
}

export interface GetPlayerServersRequest {
  playerId: number;
  onlineOnly?: boolean;
}

export interface PlayerServersResponse extends PaginationResponse {
  servers: PlayerServer[];
}

export interface ServerPlayersResponse extends PaginationResponse {
  players: OnlinePlayer[];
}

// ========== IP管理（新功能）==========
export interface IPInfo {
  ip: string;                      // IP地址
  ipType: string;                  // IP类型：ipv4/ipv6
  country?: string;                // 国家/地区
  countryCode?: string;            // 国家代码
  region?: string;                 // 省份/州
  city?: string;                   // 城市
  latitude?: number;               // 纬度
  longitude?: number;              // 经度
  timezone?: string;               // 时区
  isp?: string;                    // 网络服务提供商
  organization?: string;           // 组织名称
  asn?: string;                    // ASN号码
  isBogon: boolean;                // 是否为Bogon IP
  isMobile: boolean;               // 是否为移动网络
  isSatellite: boolean;            // 是否为卫星网络
  isCrawler: boolean;              // 是否为爬虫
  isDatacenter: boolean;           // 是否为数据中心IP
  isTor: boolean;                  // 是否为Tor出口节点
  isProxy: boolean;                // 是否为代理IP
  isVpn: boolean;                  // 是否为VPN
  isAbuser: boolean;               // 是否为滥用者
  banned: boolean;                 // 是否被封禁
  banReason?: string;              // 封禁原因
  threatLevel: ThreatLevel;        // 威胁等级
  riskScore: number;               // 风险评分（0-100）
  queryStatus: QueryStatus;        // 查询状态
  lastQueryAt?: string;            // 最后查询时间(ISO8601格式)
  createdAt: string;               // 创建时间(ISO8601格式)
  updatedAt: string;               // 更新时间(ISO8601格式)
  riskLevel: string;               // 风险等级
  riskDescription: string;         // 风险描述
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

export interface ListIPsResponse extends PaginationResponse {
  ips: IPInfo[];
}

export interface IPStatistics {
  totalIps: number;
  completedIps: number;
  pendingIps: number;
  failedIps: number;
  bannedIps: number;
  proxyIps: number;
  vpnIps: number;
  torIps: number;
  datacenterIps: number;
  highRiskIps: number;
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
  name?: string;
  search?: string;
  minLevel?: number;
  maxLevel?: number;
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
  leader?: number;  // 镇长ID，符合IDL中的 optional i32 leader 定义
  members: Player[];
}

// ========== 更多服务器相关接口 ==========
export interface GetServerRequest {
  id: number;
  detail?: boolean;
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
  onlineOnly?: boolean;
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
  receivedAt: number;           // 服务端接收时间戳(毫秒)
  responseAt: number;           // 服务端响应时间戳(毫秒)
  expireDurationMs: number;    // 状态过期时间(毫秒)
}

export interface MonitorStatRecord {
  timestamp: number;             // 统计时间戳
  currentPlayers: number;       // 当前在线人数
  tps?: number;                  // 服务器TPS
  latencyMs?: number;           // 延迟毫秒
}

export interface MonitorStatsResponse {
  serverId: number;             // 服务器ID
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
  tokenInfo: ApiToken;
  tokenValue: string;
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
  active: boolean;
  expireAt?: string;
  lastUsedAt?: string;
  lastUsedIp?: string;
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
