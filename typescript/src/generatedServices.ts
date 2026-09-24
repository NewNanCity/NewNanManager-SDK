import type { AxiosInstance, AxiosPromise, RawAxiosRequestConfig } from 'axios';
import {
  Configuration,
  HealthApi,
  IPServiceApi,
  MonitorServiceApi,
  PlayerServerServiceApi,
  PlayerServiceApi,
  ServerServiceApi,
  TokenServiceApi,
  TownServiceApi
} from './generated';
import type {
  ApiToken as GeneratedApiToken,
  BanIPRequest as GeneratedBanIPRequest,
  BanPlayerRequest as GeneratedBanPlayerRequest,
  CreateApiTokenRequest as GeneratedCreateApiTokenRequest,
  CreateApiTokenResponse as GeneratedCreateApiTokenResponse,
  CreatePlayerRequest as GeneratedCreatePlayerRequest,
  CreateServerRequest as GeneratedCreateServerRequest,
  CreateTownRequest as GeneratedCreateTownRequest,
  GetMonitorStatsResponse as GeneratedGetMonitorStatsResponse,
  HeartbeatRequest as GeneratedHeartbeatRequest,
  HeartbeatResponse as GeneratedHeartbeatResponse,
  IPInfo as GeneratedIPInfo,
  IPStatistics as GeneratedIPStatistics,
  ListApiTokensResponse as GeneratedListApiTokensResponse,
  ListIPsResponse as GeneratedListIPsResponse,
  ListPlayersBanModeEnum,
  ListPlayersResponse as GeneratedListPlayersResponse,
  ListServersResponse as GeneratedListServersResponse,
  ListTownsResponse as GeneratedListTownsResponse,
  Player as GeneratedPlayer,
  PlayerServersResponse as GeneratedPlayerServersResponse,
  PlayerValidateResult as GeneratedPlayerValidateResult,
  ServerDetailResponse as GeneratedServerDetailResponse,
  ServerPlayersResponse as GeneratedServerPlayersResponse,
  ServerRegistry as GeneratedServerRegistry,
  SetPlayersOfflineRequest as GeneratedSetPlayersOfflineRequest,
  Town as GeneratedTown,
  TownDetailResponse as GeneratedTownDetailResponse,
  UnbanIPRequest as GeneratedUnbanIPRequest,
  UpdateApiTokenRequest as GeneratedUpdateApiTokenRequest,
  UpdatePlayerRequest as GeneratedUpdatePlayerRequest,
  UpdateServerRequest as GeneratedUpdateServerRequest,
  UpdateTownRequest as GeneratedUpdateTownRequest,
  ValidateRequest as GeneratedValidateRequest,
  ValidateResponse as GeneratedValidateResponse,
  GetBannedIPsMinThreatLevelEnum,
  GetHighRiskIPsMinThreatLevelEnum,
  GetSuspiciousIPsMinThreatLevelEnum,
  ListIPsMinThreatLevelEnum
} from './generated';
import type {
  ApiToken,
  BanIPRequest,
  BanPlayerRequest,
  CreateApiTokenRequest,
  CreateApiTokenResponse,
  CreatePlayerRequest,
  CreateServerRequest,
  CreateTownRequest,
  EmptyResponse,
  GetMonitorStatsRequest,
  MonitorStatsResponse,
  GetPlayerRequest,
  GetServerPlayersRequest,
  GetServerRequest,
  GetTownRequest,
  HeartbeatRequest,
  HeartbeatResponse,
  IPInfo,
  IPStatistics,
  ListApiTokensRequest,
  ListApiTokensResponse,
  ListBannedIPsRequest,
  ListBannedIPsResponse,
  ListIPsRequest,
  ListIPsResponse,
  ListPlayersRequest,
  ListPlayersResponse,
  ListServersRequest,
  ListServersResponse,
  ListTownsRequest,
  ListTownsResponse,
  Player,
  PlayerServersResponse,
  PlayerValidateInfo,
  PlayerValidateResult,
  ServerDetailResponse,
  ServerPlayersResponse,
  ServerRegistry,
  SessionContext,
  SetPlayersOfflineRequest,
  Town,
  TownDetailResponse,
  UnbanIPRequest,
  UnbanPlayerRequest,
  UpdateApiTokenRequest,
  UpdatePlayerRequest,
  UpdateServerRequest,
  UpdateTownRequest,
  ValidateRequest,
  ValidateResponse,
  DeleteApiTokenRequest,
  DeletePlayerRequest,
  DeleteServerRequest,
  DeleteTownRequest
} from './types';
import { commonErrorHandler } from './utils/errorHandler';

type GeneratedCall<T> = () => AxiosPromise<T>;

export type GeneratedOperation<Request, Response> = {
  (request: Request): Promise<Response>;
  setDefaultAxiosConfig(config: RawAxiosRequestConfig): void;
};

export type OptionalGeneratedOperation<Request, Response> = {
  (request?: Request): Promise<Response>;
  setDefaultAxiosConfig(config: RawAxiosRequestConfig): void;
};

export type SessionGeneratedOperation<Request, Response> = {
  (request: Request, session?: SessionContext): Promise<Response>;
  setDefaultAxiosConfig(config: RawAxiosRequestConfig): void;
};

function operation<Request, Response>(
  run: (request: Request, options: RawAxiosRequestConfig) => Promise<Response>
): GeneratedOperation<Request, Response> {
  let defaultOptions: RawAxiosRequestConfig = {};
  const callable = ((request: Request) => run(request, defaultOptions)) as GeneratedOperation<Request, Response>;
  callable.setDefaultAxiosConfig = (config) => {
    defaultOptions = config;
  };
  return callable;
}

function optionalOperation<Request, Response>(
  run: (request: Request, options: RawAxiosRequestConfig) => Promise<Response>
): OptionalGeneratedOperation<Request, Response> {
  let defaultOptions: RawAxiosRequestConfig = {};
  const callable = ((request?: Request) => run((request ?? {}) as Request, defaultOptions)) as OptionalGeneratedOperation<Request, Response>;
  callable.setDefaultAxiosConfig = (config) => {
    defaultOptions = config;
  };
  return callable;
}

function sessionOperation<Request, Response>(
  run: (request: Request, session: SessionContext | undefined, options: RawAxiosRequestConfig) => Promise<Response>
): SessionGeneratedOperation<Request, Response> {
  let defaultOptions: RawAxiosRequestConfig = {};
  const callable = ((request: Request, session?: SessionContext) => run(request, session, defaultOptions)) as SessionGeneratedOperation<Request, Response>;
  callable.setDefaultAxiosConfig = (config) => {
    defaultOptions = config;
  };
  return callable;
}

function withParams(options: RawAxiosRequestConfig, params: Record<string, unknown>): RawAxiosRequestConfig {
  return {
    ...options,
    params: Object.fromEntries(Object.entries(params).filter(([, value]) => value !== undefined && value !== null))
  };
}

function normalizeSession(session: SessionContext): SessionContext {
  if (typeof session.id !== 'string' || !session.id.trim()) {
    throw new Error('session id must be a non-empty string');
  }
  if (session.id.length < 32 || session.id.length > 64) {
    throw new Error('session id length must be between 32 and 64 characters');
  }
  if (!Number.isInteger(session.epoch) || session.epoch <= 0) {
    throw new Error('session epoch must be a positive integer');
  }
  return session;
}

async function request<T>(call: GeneratedCall<T>): Promise<T> {
  try {
    return (await call()).data;
  } catch (error: unknown) {
    return commonErrorHandler({ error });
  }
}

async function requestEmpty(call: GeneratedCall<unknown>): Promise<EmptyResponse> {
  await request(call);
  return {};
}

function mapPlayer(value: GeneratedPlayer): Player {
  return {
    id: value.id,
    name: value.name,
    townId: value.town_id,
    qq: value.qq,
    qqguild: value.qqguild,
    discord: value.discord,
    inQqGroup: value.in_qq_group,
    inQqGuild: value.in_qq_guild,
    inDiscord: value.in_discord,
    banMode: value.ban_mode,
    banExpire: value.ban_expire,
    banReason: value.ban_reason,
    createdAt: value.created_at,
    updatedAt: value.updated_at
  };
}

function mapServer(value: GeneratedServerRegistry): ServerRegistry {
  return {
    id: value.id,
    name: value.name,
    address: value.address,
    description: value.description,
    active: value.active,
    createdAt: value.created_at,
    updatedAt: value.updated_at
  };
}

function mapServerDetail(value: GeneratedServerDetailResponse): ServerDetailResponse {
  return {
    server: mapServer(value.server),
    status: value.status ? {
      serverId: value.status.server_id,
      online: value.status.online,
      currentPlayers: value.status.current_players,
      maxPlayers: value.status.max_players,
      latencyMs: value.status.latency_ms,
      tps: value.status.tps,
      version: value.status.version,
      motd: value.status.motd,
      expireAt: value.status.expire_at,
      lastHeartbeat: value.status.last_heartbeat,
      measurementType: value.status.measurement_type,
      latencyMetric: value.status.latency_metric
    } : undefined
  };
}

function mapTown(value: GeneratedTown): Town {
  return {
    id: value.id,
    name: value.name,
    level: value.level,
    leaderId: value.leader_id,
    qqGroup: value.qq_group,
    description: value.description,
    createdAt: value.created_at,
    updatedAt: value.updated_at
  };
}

function mapTownDetail(value: GeneratedTownDetailResponse): TownDetailResponse {
  return {
    town: mapTown(value.town),
    leader: value.leader,
    members: value.members.map(mapPlayer)
  };
}

function mapToken(value: GeneratedApiToken): ApiToken {
  return {
    id: value.id,
    name: value.name,
    role: value.role,
    serverId: value.server_id,
    description: value.description,
    active: value.active,
    expireAt: value.expire_at,
    lastUsedAt: value.last_used_at,
    lastUsedIp: value.last_used_ip,
    createdAt: value.created_at,
    updatedAt: value.updated_at
  };
}

function mapIP(value: GeneratedIPInfo): IPInfo {
  return {
    ip: value.ip,
    ipType: value.ip_type,
    country: value.country,
    countryCode: value.country_code,
    region: value.region,
    city: value.city,
    latitude: value.latitude,
    longitude: value.longitude,
    timezone: value.timezone,
    isp: value.isp,
    organization: value.organization,
    asn: value.asn,
    isBogon: value.is_bogon,
    isMobile: value.is_mobile,
    isSatellite: value.is_satellite,
    isCrawler: value.is_crawler,
    isDatacenter: value.is_datacenter,
    isTor: value.is_tor,
    isProxy: value.is_proxy,
    isVpn: value.is_vpn,
    isAbuser: value.is_abuser,
    banned: value.banned,
    banReason: value.ban_reason,
    threatLevel: value.threat_level,
    riskScore: value.risk_score,
    queryStatus: value.query_status,
    lastQueryAt: value.last_query_at,
    createdAt: value.created_at,
    updatedAt: value.updated_at,
    riskLevel: value.risk_level,
    riskDescription: value.risk_description
  };
}

function mapMonitor(value: GeneratedGetMonitorStatsResponse): MonitorStatsResponse {
  return {
    serverId: value.server_id,
    stats: value.stats.map((stat) => ({
      timestamp: stat.timestamp,
      currentPlayers: stat.current_players,
      tps: stat.tps,
      latencyMs: stat.latency_ms,
      measurementType: stat.measurement_type,
      latencyMetric: stat.latency_metric
    })),
    nextCursor: value.next_cursor
  };
}

function mapValidateResult(value: GeneratedPlayerValidateResult): PlayerValidateResult {
  return {
    playerName: value.player_name,
    allowed: value.allowed,
    playerId: value.player_id,
    reason: value.reason,
    newbie: value.newbie,
    banMode: value.ban_mode,
    banExpire: value.ban_expire,
    banReason: value.ban_reason
  };
}

function mapPlayerServers(value: GeneratedPlayerServersResponse): PlayerServersResponse {
  return {
    servers: value.servers.map((server) => ({
      playerId: server.player_id,
      serverId: server.server_id,
      online: server.online,
      joinedAt: server.joined_at,
      createdAt: server.created_at,
      updatedAt: server.updated_at
    })),
    total: value.total
  };
}

function mapServerPlayers(value: GeneratedServerPlayersResponse): ServerPlayersResponse {
  return {
    players: value.players.map((player) => ({
      playerId: player.player_id,
      playerName: player.player_name,
      serverId: player.server_id,
      serverName: player.server_name,
      joinedAt: player.joined_at
    })),
    total: value.total,
    page: value.page,
    pageSize: value.page_size
  };
}

function generatedPlayer(value: PlayerValidateInfo): GeneratedPlayerValidateInfo {
  return {
    player_name: value.playerName,
    ip: value.ip,
    client_version: value.clientVersion,
    protocol_version: value.protocolVersion
  };
}

export interface GeneratedServices {
  health: {
    ping: GeneratedOperation<void, { ping: string; version: string }>;
  };
  players: {
    createPlayer: GeneratedOperation<CreatePlayerRequest, Player>;
    listPlayers: OptionalGeneratedOperation<ListPlayersRequest, ListPlayersResponse>;
    getPlayer: GeneratedOperation<GetPlayerRequest, Player>;
    updatePlayer: GeneratedOperation<UpdatePlayerRequest, Player>;
    deletePlayer: GeneratedOperation<DeletePlayerRequest, EmptyResponse>;
    banPlayer: GeneratedOperation<BanPlayerRequest, EmptyResponse>;
    unbanPlayer: GeneratedOperation<UnbanPlayerRequest, EmptyResponse>;
    validate: SessionGeneratedOperation<ValidateRequest, ValidateResponse>;
  };
  servers: {
    createServer: GeneratedOperation<CreateServerRequest, ServerRegistry>;
    listServers: OptionalGeneratedOperation<ListServersRequest, ListServersResponse>;
    getServer: GeneratedOperation<GetServerRequest, ServerDetailResponse>;
    updateServer: GeneratedOperation<UpdateServerRequest, ServerRegistry>;
    deleteServer: GeneratedOperation<DeleteServerRequest, EmptyResponse>;
  };
  towns: {
    createTown: GeneratedOperation<CreateTownRequest, Town>;
    listTowns: OptionalGeneratedOperation<ListTownsRequest, ListTownsResponse>;
    getTown: GeneratedOperation<GetTownRequest, TownDetailResponse>;
    updateTown: GeneratedOperation<UpdateTownRequest, Town>;
    deleteTown: GeneratedOperation<DeleteTownRequest, EmptyResponse>;
  };
  tokens: {
    createApiToken: GeneratedOperation<CreateApiTokenRequest, CreateApiTokenResponse>;
    listApiTokens: OptionalGeneratedOperation<ListApiTokensRequest, ListApiTokensResponse>;
    getApiToken: GeneratedOperation<{ id: number }, ApiToken>;
    updateApiToken: GeneratedOperation<UpdateApiTokenRequest, ApiToken>;
    deleteApiToken: GeneratedOperation<DeleteApiTokenRequest, EmptyResponse>;
  };
  ips: {
    getIPInfo: GeneratedOperation<string | { ip: string }, IPInfo>;
    banIP: GeneratedOperation<BanIPRequest, EmptyResponse>;
    unbanIP: GeneratedOperation<UnbanIPRequest, EmptyResponse>;
    listBannedIPs: OptionalGeneratedOperation<ListBannedIPsRequest, ListBannedIPsResponse>;
    listIPs: OptionalGeneratedOperation<ListIPsRequest, ListIPsResponse>;
    getSuspiciousIPs: OptionalGeneratedOperation<ListIPsRequest, ListIPsResponse>;
    getHighRiskIPs: OptionalGeneratedOperation<ListIPsRequest, ListIPsResponse>;
    getIPStatistics: GeneratedOperation<void, IPStatistics>;
  };
  playerServers: {
    getPlayerServers: GeneratedOperation<{ playerId: number; onlineOnly?: boolean }, PlayerServersResponse>;
    getServerPlayers: OptionalGeneratedOperation<GetServerPlayersRequest, ServerPlayersResponse>;
    setPlayersOffline: SessionGeneratedOperation<SetPlayersOfflineRequest, EmptyResponse>;
  };
  monitor: {
    createServerSession: GeneratedOperation<number, SessionContext>;
    heartbeat: SessionGeneratedOperation<HeartbeatRequest, HeartbeatResponse>;
    getMonitorStats: GeneratedOperation<GetMonitorStatsRequest, MonitorStatsResponse>;
  };
}

interface GeneratedPlayerValidateInfo {
  player_name: string;
  ip: string;
  client_version?: string;
  protocol_version?: string;
}

export function createGeneratedServices(axiosInstance: AxiosInstance, baseUrl: string): GeneratedServices {
  const configuration = new Configuration({ basePath: baseUrl });
  const health = new HealthApi(configuration, baseUrl, axiosInstance);
  const players = new PlayerServiceApi(configuration, baseUrl, axiosInstance);
  const servers = new ServerServiceApi(configuration, baseUrl, axiosInstance);
  const towns = new TownServiceApi(configuration, baseUrl, axiosInstance);
  const tokens = new TokenServiceApi(configuration, baseUrl, axiosInstance);
  const ips = new IPServiceApi(configuration, baseUrl, axiosInstance);
  const playerServers = new PlayerServerServiceApi(configuration, baseUrl, axiosInstance);
  const monitor = new MonitorServiceApi(configuration, baseUrl, axiosInstance);

  return {
    health: {
      ping: operation((_value: void, options) => request(() => health.ping(options)))
    },
    players: {
      createPlayer: operation((value: CreatePlayerRequest, options) => request(() => players.createPlayer(({
        name: value.name,
        town_id: value.townId,
        qq: value.qq,
        qqguild: value.qqguild,
        discord: value.discord,
        in_qq_group: value.inQqGroup,
        in_qq_guild: value.inQqGuild,
        in_discord: value.inDiscord
      } satisfies GeneratedCreatePlayerRequest), options)).then(mapPlayer)),
      listPlayers: optionalOperation(async (value: ListPlayersRequest, options) => {
        const response = await request(() => players.listPlayers(
          undefined, undefined, undefined, undefined, undefined, undefined, undefined, undefined, undefined,
          withParams(options, {
            page: value.page || 1, page_size: value.pageSize || 20, search: value.search, town_id: value.townId,
            ban_mode: value.banMode as ListPlayersBanModeEnum | undefined, name: value.name, qq: value.qq,
            qqguild: value.qqguild, discord: value.discord
          })
        ));
        return {
          players: response.players.map(mapPlayer),
          total: response.total,
          page: response.page,
          pageSize: response.page_size
        };
      }),
      getPlayer: operation((value: GetPlayerRequest, options) => request(() => players.getPlayer(value.id, options)).then(mapPlayer)),
      updatePlayer: operation((value: UpdatePlayerRequest, options) => request(() => players.updatePlayer(value.id, ({
        name: value.name,
        town_id: value.townId,
        qq: value.qq,
        qqguild: value.qqguild,
        discord: value.discord,
        in_qq_group: value.inQqGroup,
        in_qq_guild: value.inQqGuild,
        in_discord: value.inDiscord
      } satisfies GeneratedUpdatePlayerRequest), options)).then(mapPlayer)),
      deletePlayer: operation((value: DeletePlayerRequest, options) => requestEmpty(() => players.deletePlayer(value.id, options))),
      banPlayer: operation((value: BanPlayerRequest, options) => requestEmpty(() => players.banPlayer(value.playerId, ({
        ban_mode: value.banMode,
        duration_seconds: value.durationSeconds,
        reason: value.reason
      } satisfies GeneratedBanPlayerRequest), options))),
      unbanPlayer: operation((value: UnbanPlayerRequest, options) => requestEmpty(() => players.unbanPlayer(value.playerId, options))),
      validate: sessionOperation(async (value: ValidateRequest, session, options) => {
        if (session) {
          const validSession = normalizeSession(session);
          const response = await request(() => players.validate(validSession.id, validSession.epoch, ({
            players: value.players.map(generatedPlayer),
            server_id: value.serverId,
            login: value.login
          } satisfies GeneratedValidateRequest), options));
          return {
            results: response.results.map(mapValidateResult),
            processedAt: response.processed_at
          };
        }
        return legacyRequest<ValidateResponse>(axiosInstance, 'POST', '/api/v1/players/validate', {
          players: value.players.map(generatedPlayer), server_id: value.serverId, login: value.login
        }, options);
      })
    },
    servers: {
      createServer: operation((value: CreateServerRequest, options) => request(() => servers.createServer(({
        name: value.name, address: value.address, description: value.description
      } satisfies GeneratedCreateServerRequest), options)).then(mapServer)),
      listServers: optionalOperation(async (value: ListServersRequest, options) => {
        const response = await request(() => servers.listServers(undefined, undefined, undefined, undefined, withParams(options, {
          page: value.page, page_size: value.pageSize, search: value.search, online_only: value.onlineOnly
        })));
        return { servers: response.servers.map(mapServer), total: response.total, page: response.page, pageSize: response.page_size };
      }),
      getServer: operation((value: GetServerRequest, options) => request(() => servers.getServer(value.id, undefined, withParams(options, { detail: value.detail }))).then(mapServerDetail)),
      updateServer: operation((value: UpdateServerRequest, options) => request(() => servers.updateServer(value.id, ({
        name: value.name, address: value.address, description: value.description
      } satisfies GeneratedUpdateServerRequest), options)).then(mapServer)),
      deleteServer: operation((value: DeleteServerRequest, options) => requestEmpty(() => servers.deleteServer(value.id, options)))
    },
    towns: {
      createTown: operation((value: CreateTownRequest, options) => request(() => towns.createTown(({
        name: value.name, level: value.level, leader_id: value.leaderId, qq_group: value.qqGroup, description: value.description
      } satisfies GeneratedCreateTownRequest), options)).then(mapTown)),
      listTowns: optionalOperation(async (value: ListTownsRequest, options) => {
        const response = await request(() => towns.listTowns(undefined, undefined, undefined, undefined, undefined, undefined, withParams(options, {
          page: value.page, page_size: value.pageSize, name: value.name, search: value.search,
          min_level: value.minLevel, max_level: value.maxLevel
        })));
        return { towns: response.towns.map(mapTown), total: response.total, page: response.page, pageSize: response.page_size };
      }),
      getTown: operation((value: GetTownRequest, options) => request(() => towns.getTown(value.id, undefined, withParams(options, { detail: value.detail }))).then(mapTownDetail)),
      updateTown: operation((value: UpdateTownRequest, options) => request(() => towns.updateTown(value.id, ({
        name: value.name, level: value.level, leader_id: value.leaderId, qq_group: value.qqGroup,
        description: value.description, add_players: value.addPlayers, remove_players: value.removePlayers
      } satisfies GeneratedUpdateTownRequest), options)).then(mapTown)),
      deleteTown: operation((value: DeleteTownRequest, options) => requestEmpty(() => towns.deleteTown(value.id, options)))
    },
    tokens: {
      createApiToken: operation(async (value: CreateApiTokenRequest, options) => {
        const response = await request(() => tokens.createApiToken(({
          name: value.name, role: value.role, description: value.description,
          expire_days: value.expireDays, server_id: value.serverId
        } satisfies GeneratedCreateApiTokenRequest), options));
        return { tokenInfo: mapToken(response.token_info), tokenValue: response.token_value };
      }),
      listApiTokens: optionalOperation(async (value: ListApiTokensRequest, options) => {
        const response = await request(() => tokens.listApiTokens(undefined, undefined, withParams(options, {
          page: value.page, page_size: value.pageSize
        })));
        return { tokens: response.tokens.map(mapToken), total: response.total, page: response.page, pageSize: response.page_size };
      }),
      getApiToken: operation((value: { id: number }, options) => request(() => tokens.getApiToken(value.id, options)).then(mapToken)),
      updateApiToken: operation((value: UpdateApiTokenRequest, options) => request(() => tokens.updateApiToken(value.id, ({
        name: value.name, role: value.role, description: value.description, active: value.active, server_id: value.serverId
      } satisfies GeneratedUpdateApiTokenRequest), options)).then(mapToken)),
      deleteApiToken: operation((value: DeleteApiTokenRequest, options) => requestEmpty(() => tokens.deleteApiToken(value.id, options)))
    },
    ips: {
      getIPInfo: operation((value: string | { ip: string }, options) => request(() => ips.getIPInfo(
        typeof value === 'string' ? value : value.ip,
        options
      )).then(mapIP)),
      banIP: operation((value: BanIPRequest, options) => requestEmpty(() => ips.banIP(({ ips: value.ips, reason: value.reason } satisfies GeneratedBanIPRequest), options))),
      unbanIP: operation((value: UnbanIPRequest, options) => requestEmpty(() => ips.unbanIP(({ ips: value.ips } satisfies GeneratedUnbanIPRequest), options))),
      listBannedIPs: optionalOperation(async (value: ListBannedIPsRequest, options) => {
        const response = await request(() => ips.getBannedIPs(undefined, undefined, undefined, undefined, undefined, withParams(options, { page: value.page, page_size: value.pageSize })));
        return { bans: response.ips.map((ip) => ({ ip: ip.ip, reason: ip.ban_reason || '', active: ip.banned })), total: response.total, page: response.page, pageSize: response.page_size };
      }),
      listIPs: optionalOperation(async (value: ListIPsRequest, options) => mapIPList(await request(() => ips.listIPs(undefined, undefined, undefined, undefined, undefined, withParams(options, { page: value.page, page_size: value.pageSize, banned_only: value.bannedOnly, min_threat_level: value.minThreatLevel as ListIPsMinThreatLevelEnum | undefined, min_risk_score: value.minRiskScore }))))),
      getSuspiciousIPs: optionalOperation(async (value: ListIPsRequest, options) => mapIPList(await request(() => ips.getSuspiciousIPs(undefined, undefined, undefined, undefined, undefined, withParams(options, { page: value.page, page_size: value.pageSize, banned_only: value.bannedOnly, min_threat_level: value.minThreatLevel as GetSuspiciousIPsMinThreatLevelEnum | undefined, min_risk_score: value.minRiskScore }))))),
      getHighRiskIPs: optionalOperation(async (value: ListIPsRequest, options) => mapIPList(await request(() => ips.getHighRiskIPs(undefined, undefined, undefined, undefined, undefined, withParams(options, { page: value.page, page_size: value.pageSize, banned_only: value.bannedOnly, min_threat_level: value.minThreatLevel as GetHighRiskIPsMinThreatLevelEnum | undefined, min_risk_score: value.minRiskScore }))))),
      getIPStatistics: operation((_value: void, options) => request(() => ips.getIPStatistics(options)).then(mapIPStatistics))
    },
    playerServers: {
      getPlayerServers: operation((value: { playerId: number; onlineOnly?: boolean }, options) => request(() => playerServers.getPlayerServers(value.playerId, undefined, withParams(options, { online_only: value.onlineOnly }))).then(mapPlayerServers)),
      getServerPlayers: optionalOperation(async (value: GetServerPlayersRequest, options) => mapServerPlayers(await request(() => playerServers.getServerPlayers(undefined, undefined, undefined, undefined, undefined, withParams(options, { page: value.page, page_size: value.pageSize, search: value.search, server_id: value.serverId, online_only: value.onlineOnly }))))),
      setPlayersOffline: sessionOperation(async (value: SetPlayersOfflineRequest, session, options) => {
        const body: GeneratedSetPlayersOfflineRequest = { server_id: value.serverId, player_ids: value.playerIds };
        if (session) {
          const validSession = normalizeSession(session);
          return requestEmpty(() => playerServers.setPlayersOffline(validSession.id, validSession.epoch, body, options));
        }
        return legacyRequest<EmptyResponse>(axiosInstance, 'POST', '/api/v1/servers/players/offline', body, options).then(() => ({}));
      })
    },
    monitor: {
      createServerSession: operation(async (serverId: number, options) => {
        const response = await request(() => monitor.createServerSession(serverId, {}, options));
        return normalizeSession({ id: response.session_id, epoch: response.session_epoch });
      }),
      heartbeat: sessionOperation(async (value: HeartbeatRequest, session, options) => {
        const body: GeneratedHeartbeatRequest = {
          current_players: value.currentPlayers, max_players: value.maxPlayers, tps: value.tps,
          version: value.version, motd: value.motd, rtt_ms: value.rttMs
        };
        if (session) {
          const validSession = normalizeSession(session);
          return mapHeartbeat(await request(() => monitor.heartbeat(value.serverId, validSession.id, validSession.epoch, body, options)));
        }
        return legacyRequest<HeartbeatResponse>(axiosInstance, 'POST', `/api/v1/monitor/${value.serverId}/heartbeat`, body, options);
      }),
      getMonitorStats: operation((value: GetMonitorStatsRequest, options) => request(() => monitor.getMonitorStats(value.serverId, undefined, undefined, undefined, undefined, withParams(options, { since: value.since, duration: value.duration, limit: value.limit, cursor: value.cursor }))).then(mapMonitor))
    }
  };
}

function mapIPList(value: GeneratedListIPsResponse): ListIPsResponse {
  return { ips: value.ips.map(mapIP), total: value.total, page: value.page, pageSize: value.page_size };
}

function mapIPStatistics(value: GeneratedIPStatistics): IPStatistics {
  return {
    totalIps: value.total_ips, completedIps: value.completed_ips, pendingIps: value.pending_ips,
    failedIps: value.failed_ips, bannedIps: value.banned_ips, proxyIps: value.proxy_ips,
    vpnIps: value.vpn_ips, torIps: value.tor_ips, datacenterIps: value.datacenter_ips,
    highRiskIps: value.high_risk_ips
  };
}

function mapHeartbeat(value: GeneratedHeartbeatResponse): HeartbeatResponse {
  return { receivedAt: value.received_at, responseAt: value.response_at, expireDurationMs: value.expire_duration_ms };
}

async function legacyRequest<T>(
  axiosInstance: AxiosInstance,
  method: 'POST',
  url: string,
  data: unknown,
  options: RawAxiosRequestConfig = {}
): Promise<T> {
  try {
    return (await axiosInstance.request<T>({ ...options, method, url, data })).data;
  } catch (error: unknown) {
    return commonErrorHandler({ error });
  }
}
