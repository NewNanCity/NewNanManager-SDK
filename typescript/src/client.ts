import { ClientConfig } from './types';
import { PlayerApi, TownApi, ServerApi, MonitorApi, TokenApi } from './apis';

// 重新导出所有类型和错误
export * from './types';
export * from './errors';
export * from './apis';

/**
 * NewNanManager客户端SDK
 * 
 * 使用示例:
 * ```typescript
 * const client = new NewNanManagerClient({
 *     token: 'your-api-token',
 *     baseUrl: 'https://your-server.com'
 * });
 * 
 * const servers = await client.listServers();
 * const players = await client.listPlayers();
 * ```
 */
export class NewNanManagerClient {
  private readonly playerApi: PlayerApi;
  private readonly townApi: TownApi;
  private readonly serverApi: ServerApi;
  private readonly monitorApi: MonitorApi;
  private readonly tokenApi: TokenApi;

  constructor(config: ClientConfig) {
    // 创建各个API实例
    this.playerApi = new PlayerApi(config);
    this.townApi = new TownApi(config);
    this.serverApi = new ServerApi(config);
    this.monitorApi = new MonitorApi(config);
    this.tokenApi = new TokenApi(config);
  }

  /**
   * 获取axios实例的拦截器，用于自定义请求/响应处理
   */
  get interceptors() {
    return this.playerApi.interceptors;
  }

  // ========== 玩家管理 ==========
  
  /**
   * 创建玩家
   */
  createPlayer = this.playerApi.createPlayer;

  /**
   * 获取玩家详情
   */
  getPlayer = this.playerApi.getPlayer;

  /**
   * 更新玩家信息
   */
  updatePlayer = (id: number, request: Parameters<typeof this.playerApi.updatePlayer>[0]['request']) =>
    this.playerApi.updatePlayer({ id, request });

  /**
   * 删除玩家
   */
  deletePlayer = this.playerApi.deletePlayer;

  /**
   * 获取玩家列表
   */
  listPlayers = this.playerApi.listPlayers;

  /**
   * 封禁玩家
   */
  banPlayer = (playerId: number, request: Parameters<typeof this.playerApi.banPlayer>[0]['request']) =>
    this.playerApi.banPlayer({ playerId, request });

  /**
   * 解封玩家
   */
  unbanPlayer = this.playerApi.unbanPlayer;

  /**
   * 玩家登录验证
   */
  validateLogin = this.playerApi.validateLogin;

  // ========== 城镇管理 ==========
  
  /**
   * 创建城镇
   */
  createTown = this.townApi.createTown;

  /**
   * 获取城镇详情
   */
  getTown = this.townApi.getTown;

  /**
   * 更新城镇信息
   */
  updateTown = (id: number, request: Parameters<typeof this.townApi.updateTown>[0]['request']) =>
    this.townApi.updateTown({ id, request });

  /**
   * 删除城镇
   */
  deleteTown = this.townApi.deleteTown;

  /**
   * 获取城镇列表
   */
  listTowns = this.townApi.listTowns;

  /**
   * 管理城镇成员
   */
  manageTownMember = (townId: number, request: Parameters<typeof this.townApi.manageTownMember>[0]['request']) =>
    this.townApi.manageTownMember({ townId, request });

  /**
   * 获取城镇成员列表
   */
  getTownMembers = (townId: number, request: Parameters<typeof this.townApi.getTownMembers>[0]['request'] = {}) =>
    this.townApi.getTownMembers({ townId, request });

  // ========== 服务器管理 ==========
  
  /**
   * 注册服务器
   */
  registerServer = this.serverApi.registerServer;

  /**
   * 获取服务器信息
   */
  getServer = this.serverApi.getServer;

  /**
   * 更新服务器信息
   */
  updateServer = (id: number, request: Parameters<typeof this.serverApi.updateServer>[0]['request']) =>
    this.serverApi.updateServer({ id, request });

  /**
   * 删除服务器
   */
  deleteServer = this.serverApi.deleteServer;

  /**
   * 获取服务器列表
   */
  listServers = this.serverApi.listServers;

  /**
   * 获取服务器详细信息
   */
  getServerDetail = this.serverApi.getServerDetail;

  // ========== 服务器监控 ==========
  
  /**
   * 服务器心跳
   */
  heartbeat = (serverId: number, request: Parameters<typeof this.monitorApi.heartbeat>[0]['request']) =>
    this.monitorApi.heartbeat({ serverId, request });

  /**
   * 获取服务器状态
   */
  getServerStatus = this.monitorApi.getServerStatus;

  /**
   * 获取延迟统计
   */
  getLatencyStats = this.monitorApi.getLatencyStats;

  // ========== API Token管理 ==========
  
  /**
   * 创建API Token
   */
  createApiToken = this.tokenApi.createApiToken;

  /**
   * 获取API Token详情
   */
  getApiToken = this.tokenApi.getApiToken;

  /**
   * 更新API Token
   */
  updateApiToken = (id: number, request: Parameters<typeof this.tokenApi.updateApiToken>[0]['request']) =>
    this.tokenApi.updateApiToken({ id, request });

  /**
   * 删除API Token
   */
  deleteApiToken = this.tokenApi.deleteApiToken;

  /**
   * 获取API Token列表
   */
  listApiTokens = () => this.tokenApi.listApiTokens();
}
