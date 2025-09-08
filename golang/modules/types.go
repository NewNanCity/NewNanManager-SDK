package modules

import "time"

// 重新导出主包中的类型定义，供各个模块使用

// 枚举类型定义
type BanMode int

const (
	BanModeNormal    BanMode = 0
	BanModeTemporary BanMode = 1
	BanModePermanent BanMode = 2
)

type LoginAction string

const (
	LoginActionLogin  LoginAction = "LOGIN"
	LoginActionLogout LoginAction = "LOGOUT"
)

type ThreatLevel int

const (
	ThreatLevelLow      ThreatLevel = 0
	ThreatLevelMedium   ThreatLevel = 1
	ThreatLevelHigh     ThreatLevel = 2
	ThreatLevelCritical ThreatLevel = 3
)

type QueryStatus int

const (
	QueryStatusPending   QueryStatus = 0
	QueryStatusCompleted QueryStatus = 1
	QueryStatusFailed    QueryStatus = 2
)

type ServerType string

const (
	ServerTypeMinecraft ServerType = "MINECRAFT"
	ServerTypeProxy     ServerType = "PROXY"
	ServerTypeLobby     ServerType = "LOBBY"
)

// 核心实体模型
type Player struct {
	ID        int32   `json:"id"`
	Name      string  `json:"name"`
	TownID    *int32  `json:"town_id,omitempty"`
	QQ        *string `json:"qq,omitempty"`
	QQGuild   *string `json:"qqguild,omitempty"`
	Discord   *string `json:"discord,omitempty"`
	InQQGroup bool    `json:"in_qq_group"`
	InQQGuild bool    `json:"in_qq_guild"`
	InDiscord bool    `json:"in_discord"`
	BanMode   BanMode `json:"ban_mode"`
	BanExpire *string `json:"ban_expire,omitempty"`
	BanReason *string `json:"ban_reason,omitempty"`
	CreatedAt string  `json:"created_at"`
	UpdatedAt string  `json:"updated_at"`
}

type Town struct {
	ID          int32     `json:"id"`
	Name        string    `json:"name"`
	Description *string   `json:"description,omitempty"`
	Level       int32     `json:"level"`
	QQGroup     *string   `json:"qq_group,omitempty"`
	LeaderID    *int32    `json:"leader_id,omitempty"`
	CreatedAt   time.Time `json:"created_at"`
	UpdatedAt   time.Time `json:"updated_at"`
}

type ServerRegistry struct {
	ID          int32   `json:"id"`                    // 服务器ID
	Name        string  `json:"name"`                  // 服务器名称
	Address     string  `json:"address"`               // 服务器地址（可包含端口）
	Description *string `json:"description,omitempty"` // 服务器描述
	Active      bool    `json:"active"`                // 是否激活
	CreatedAt   string  `json:"created_at"`            // 创建时间(ISO8601格式)
	UpdatedAt   string  `json:"updated_at"`            // 更新时间(ISO8601格式)
}

type ServerStatus struct {
	ID             int32     `json:"id"`
	ServerID       int32     `json:"server_id"`
	IsOnline       bool      `json:"is_online"`
	CurrentPlayers int32     `json:"current_players"`
	MaxPlayers     int32     `json:"max_players"`
	TPS            *float64  `json:"tps,omitempty"`
	LastHeartbeat  time.Time `json:"last_heartbeat"`
	CreatedAt      time.Time `json:"created_at"`
	UpdatedAt      time.Time `json:"updated_at"`
}

type ApiToken struct {
	ID          int32      `json:"id"`
	Name        string     `json:"name"`
	Role        string     `json:"role"`
	Description *string    `json:"description,omitempty"`
	Active      bool       `json:"active"`
	ExpireAt    *time.Time `json:"expire_at,omitempty"`
	LastUsedAt  *time.Time `json:"last_used_at,omitempty"`
	LastUsedIP  *string    `json:"last_used_ip,omitempty"`
	CreatedAt   time.Time  `json:"created_at"`
	UpdatedAt   time.Time  `json:"updated_at"`
}

// IP相关类型
type IPInfo struct {
	IP           string      `json:"ip"`                      // IP地址
	IPType       string      `json:"ip_type"`                 // IP类型：ipv4/ipv6
	Country      *string     `json:"country,omitempty"`       // 国家/地区
	CountryCode  *string     `json:"country_code,omitempty"`  // 国家代码
	Region       *string     `json:"region,omitempty"`        // 省份/州
	City         *string     `json:"city,omitempty"`          // 城市
	Latitude     *float64    `json:"latitude,omitempty"`      // 纬度
	Longitude    *float64    `json:"longitude,omitempty"`     // 经度
	Timezone     *string     `json:"timezone,omitempty"`      // 时区
	ISP          *string     `json:"isp,omitempty"`           // 网络服务提供商
	Organization *string     `json:"organization,omitempty"`  // 组织名称
	ASN          *string     `json:"asn,omitempty"`           // ASN号码
	IsBogon      bool        `json:"is_bogon"`                // 是否为Bogon IP
	IsMobile     bool        `json:"is_mobile"`               // 是否为移动网络
	IsSatellite  bool        `json:"is_satellite"`            // 是否为卫星网络
	IsCrawler    bool        `json:"is_crawler"`              // 是否为爬虫
	IsDatacenter bool        `json:"is_datacenter"`           // 是否为数据中心IP
	IsTor        bool        `json:"is_tor"`                  // 是否为Tor出口节点
	IsProxy      bool        `json:"is_proxy"`                // 是否为代理IP
	IsVPN        bool        `json:"is_vpn"`                  // 是否为VPN
	IsAbuser     bool        `json:"is_abuser"`               // 是否为滥用者
	Banned       bool        `json:"banned"`                  // 是否被封禁
	BanReason    *string     `json:"ban_reason,omitempty"`    // 封禁原因
	ThreatLevel  ThreatLevel `json:"threat_level"`            // 威胁等级（枚举值）
	RiskScore    int         `json:"risk_score"`              // 风险评分（0-100）
	QueryStatus  QueryStatus `json:"query_status"`            // 查询状态（枚举值）
	LastQueryAt  *string     `json:"last_query_at,omitempty"` // 最后查询时间(ISO8601格式)
	CreatedAt    string      `json:"created_at"`              // 创建时间(ISO8601格式)
	UpdatedAt    string      `json:"updated_at"`              // 更新时间(ISO8601格式)
	// 风险信息字段
	RiskLevel       string `json:"risk_level"`       // 风险等级
	RiskDescription string `json:"risk_description"` // 风险描述
}

// 请求类型
type CreatePlayerRequest struct {
	Name      string  `json:"name"`
	TownID    *int32  `json:"town_id,omitempty"`
	QQ        *string `json:"qq,omitempty"`
	QQGuild   *string `json:"qqguild,omitempty"`
	Discord   *string `json:"discord,omitempty"`
	InQQGroup bool    `json:"in_qq_group"`
	InQQGuild bool    `json:"in_qq_guild"`
	InDiscord bool    `json:"in_discord"`
}

type UpdatePlayerRequest struct {
	Name      *string `json:"name,omitempty"`
	TownID    *int32  `json:"town_id,omitempty"`
	QQ        *string `json:"qq,omitempty"`
	QQGuild   *string `json:"qqguild,omitempty"`
	Discord   *string `json:"discord,omitempty"`
	InQQGroup *bool   `json:"in_qq_group,omitempty"`
	InQQGuild *bool   `json:"in_qq_guild,omitempty"`
	InDiscord *bool   `json:"in_discord,omitempty"`
}

type BanPlayerRequest struct {
	BanMode         BanMode `json:"ban_mode"`
	DurationSeconds *int64  `json:"duration_seconds,omitempty"`
	Reason          string  `json:"reason"`
}

// 单个玩家验证信息
type PlayerValidateInfo struct {
	PlayerName      string  `json:"player_name"`
	IP              string  `json:"ip"`
	ClientVersion   *string `json:"client_version,omitempty"`
	ProtocolVersion *string `json:"protocol_version,omitempty"`
}

// 玩家验证请求（支持批处理）
type ValidateRequest struct {
	Players  []PlayerValidateInfo `json:"players"`   // 玩家列表（1-100个）
	ServerID int32                `json:"server_id"` // 服务器ID：1-999999
	Login    bool                 `json:"login"`     // 是否为登录验证（true=登录需记录日志，false=定期检查不记录日志）
}

// 单个玩家验证结果 - 包含IP风险信息
type PlayerValidateResult struct {
	PlayerName string   `json:"player_name"` // 玩家名
	Allowed    bool     `json:"allowed"`     // 是否允许登录
	PlayerID   *int32   `json:"player_id"`   // 玩家ID
	Reason     *string  `json:"reason"`      // 拒绝原因
	Newbie     bool     `json:"newbie"`      // 是否为新玩家
	BanMode    *BanMode `json:"ban_mode"`    // 封禁模式
	BanExpire  *string  `json:"ban_expire"`  // 封禁到期时间(ISO8601格式)
	BanReason  *string  `json:"ban_reason"`  // 封禁原因
}

// 玩家验证响应（支持批处理）
type ValidateResponse struct {
	Results     []PlayerValidateResult `json:"results"`
	ProcessedAt int64                  `json:"processed_at"`
}

type ValidateLoginRequest struct {
	Name string `json:"name"`
	IP   string `json:"ip"`
}

type CreateServerRequest struct {
	Name        string  `json:"name"`                  // 服务器名称：1-100字符
	Address     string  `json:"address"`               // 服务器地址：1-255字符
	Description *string `json:"description,omitempty"` // 服务器描述：nil、空或1-1000字符
}

type UpdateServerRequest struct {
	Name        *string `json:"name,omitempty"`        // 服务器名称：nil、空或1-100字符
	Address     *string `json:"address,omitempty"`     // 服务器地址：nil、空或1-255字符
	Description *string `json:"description,omitempty"` // 服务器描述：nil、空或1-1000字符
}

type HeartbeatRequest struct {
	ServerID       int32    `json:"server_id"`
	CurrentPlayers int32    `json:"current_players"`
	MaxPlayers     int32    `json:"max_players"`
	TPS            *float64 `json:"tps,omitempty"`
	Version        *string  `json:"version,omitempty"`
	MOTD           *string  `json:"motd,omitempty"`
	RttMs          *int64   `json:"rtt_ms,omitempty"`
}

type CreateTownRequest struct {
	Name        string  `json:"name"`
	Description *string `json:"description,omitempty"`
	Level       int32   `json:"level"`
	QQGroup     *string `json:"qq_group,omitempty"`
	LeaderID    *int32  `json:"leader_id,omitempty"`
}

type ListTownsRequest struct {
	Page     int32   `json:"page"`
	PageSize int32   `json:"page_size"`
	Name     *string `json:"name,omitempty"`
	Search   *string `json:"search,omitempty"`
	MinLevel *int32  `json:"min_level,omitempty"`
	MaxLevel *int32  `json:"max_level,omitempty"`
}

// Token相关请求结构体
type CreateApiTokenRequest struct {
	Name        string  `json:"name"`                  // Token名称：1-100字符
	Role        string  `json:"role"`                  // 角色：1-50字符
	Description *string `json:"description,omitempty"` // Token描述：nil、空或1-500字符
	ExpireDays  *int64  `json:"expire_days,omitempty"` // 过期天数：0表示永不过期，或1-3650天
}

type UpdateApiTokenRequest struct {
	Name        *string `json:"name,omitempty"`        // Token名称：nil、空或1-100字符
	Role        *string `json:"role,omitempty"`        // 角色：nil、空或1-50字符
	Description *string `json:"description,omitempty"` // Token描述：nil、空或1-500字符
	Active      *bool   `json:"active,omitempty"`      // 是否激活
}

type ListApiTokensRequest struct {
	Page     int32 `json:"page"`
	PageSize int32 `json:"page_size"`
}

type UpdateTownRequest struct {
	Name          *string  `json:"name,omitempty"`
	Description   *string  `json:"description,omitempty"`
	Level         *int32   `json:"level,omitempty"`
	QQGroup       *string  `json:"qq_group,omitempty"`
	LeaderID      *int32   `json:"leader_id,omitempty"`
	AddPlayers    *[]int32 `json:"add_players,omitempty"`
	RemovePlayers *[]int32 `json:"remove_players,omitempty"`
}

type BanIPRequest struct {
	IPs    []string `json:"ips"`
	Reason string   `json:"reason"`
}

type UnbanIPRequest struct {
	IPs []string `json:"ips"`
}

// 响应类型
type PlayersListData struct {
	Players  []Player `json:"players"`
	Total    int32    `json:"total"`
	Page     int32    `json:"page"`
	PageSize int32    `json:"page_size"` // 符合IDL规范的字段命名
}

type ValidateLoginData struct {
	Valid     bool    `json:"valid"`
	Player    *Player `json:"player,omitempty"`
	IPInfo    *IPInfo `json:"ip_info,omitempty"`
	BanReason *string `json:"ban_reason,omitempty"`
}

type ServersListData struct {
	Servers  []ServerRegistry `json:"servers"`
	Total    int32            `json:"total"`
	Page     int32            `json:"page"`
	PageSize int32            `json:"page_size"` // 符合IDL规范的字段命名
}

type ServerDetailData struct {
	Server ServerRegistry `json:"server"`
	Status *ServerStatus  `json:"status,omitempty"`
}

type HeartbeatData struct {
	ReceivedAt       int64 `json:"received_at"`        // 服务端接收时间戳(毫秒)
	ResponseAt       int64 `json:"response_at"`        // 服务端响应时间戳(毫秒)
	ExpireDurationMs int64 `json:"expire_duration_ms"` // 状态过期时间(毫秒)
}

type MonitorStatRecord struct {
	Timestamp      int64    `json:"timestamp"`            // 统计时间戳
	CurrentPlayers int32    `json:"current_players"`      // 当前在线人数
	TPS            *float64 `json:"tps,omitempty"`        // 服务器TPS
	LatencyMs      *int64   `json:"latency_ms,omitempty"` // 延迟毫秒
}

type MonitorStatsData struct {
	ServerID int32               `json:"server_id"` // 服务器ID
	Stats    []MonitorStatRecord `json:"stats"`     // 监控统计信息列表
}

type LatencyStatsData struct {
	ServerID    int32     `json:"server_id"`
	AvgLatency  float64   `json:"avg_latency"`
	MinLatency  float64   `json:"min_latency"`
	MaxLatency  float64   `json:"max_latency"`
	PacketLoss  float64   `json:"packet_loss"`
	LastUpdated time.Time `json:"last_updated"`
}

type TownsListData struct {
	Towns    []Town `json:"towns"`
	Total    int32  `json:"total"`
	Page     int32  `json:"page"`
	PageSize int32  `json:"page_size"` // 符合IDL规范的字段命名
}

type TownDetailResponse struct {
	Town    Town     `json:"town"`
	Leader  *int32   `json:"leader,omitempty"` // 镇长ID，符合IDL中的 optional i32 leader 定义
	Members []Player `json:"members"`
}

type TownMembersData struct {
	Members  []Player `json:"members"`
	Total    int32    `json:"total"`
	Page     int32    `json:"page"`
	PageSize int32    `json:"page_size"` // 符合IDL规范的字段命名
}

type ListApiTokensData struct {
	Tokens []ApiToken `json:"tokens"`
	Total  int32      `json:"total"`
}

type CreateApiTokenData struct {
	TokenInfo  ApiToken `json:"token_info"`
	TokenValue string   `json:"token_value"`
}

type BannedIPsData struct {
	IPs      []IPInfo `json:"ips"`
	Total    int32    `json:"total"`
	Page     int32    `json:"page"`
	PageSize int32    `json:"page_size"` // 符合IDL规范的字段命名
}

type SuspiciousIPsData struct {
	IPs      []IPInfo `json:"ips"`
	Total    int32    `json:"total"`
	Page     int32    `json:"page"`
	PageSize int32    `json:"page_size"` // 符合IDL规范的字段命名
}

type HighRiskIPsData struct {
	IPs      []IPInfo `json:"ips"`
	Total    int32    `json:"total"`
	Page     int32    `json:"page"`
	PageSize int32    `json:"page_size"` // 符合IDL规范的字段命名
}

type IPStatistics struct {
	TotalIPs      int32 `json:"total_ips"`
	BannedIPs     int32 `json:"banned_ips"`
	SuspiciousIPs int32 `json:"suspicious_ips"`
	HighRiskIPs   int32 `json:"high_risk_ips"`
	ProxyIPs      int32 `json:"proxy_ips"`
	VPNIPs        int32 `json:"vpn_ips"`
	TorIPs        int32 `json:"tor_ips"`
}

// 玩家服务器关系信息
type PlayerServer struct {
	PlayerID  int32  `json:"player_id"`  // 玩家ID
	ServerID  int32  `json:"server_id"`  // 服务器ID
	Online    bool   `json:"online"`     // 是否在线
	JoinedAt  string `json:"joined_at"`  // 加入时间(ISO8601格式)
	CreatedAt string `json:"created_at"` // 创建时间(ISO8601格式)
	UpdatedAt string `json:"updated_at"` // 更新时间(ISO8601格式)
}

// 在线玩家信息
type OnlinePlayer struct {
	PlayerID   int32  `json:"player_id"`   // 玩家ID
	PlayerName string `json:"player_name"` // 玩家名
	ServerID   int32  `json:"server_id"`   // 服务器ID
	ServerName string `json:"server_name"` // 服务器名
	JoinedAt   string `json:"joined_at"`   // 加入时间(ISO8601格式)
}

type BatchBanIPsResponse struct {
	Success []string `json:"success"`
	Failed  []string `json:"failed"`
	Total   int32    `json:"total"`
}

type PlayerServersData struct {
	Servers []PlayerServer `json:"servers"`
	Total   int32          `json:"total"`
}

type ServerPlayersData struct {
	Players  []OnlinePlayer `json:"players"`
	Total    int32          `json:"total"`
	Page     int32          `json:"page"`
	PageSize int32          `json:"page_size"` // 符合IDL规范的字段命名
}

type OnlinePlayersData struct {
	Players  []Player `json:"players"`
	Total    int32    `json:"total"`
	Page     int32    `json:"page"`
	PageSize int32    `json:"page_size"` // 符合IDL规范的字段命名
}

// 设置玩家离线请求
type SetPlayersOfflineRequest struct {
	ServerID  int32   `json:"server_id"`  // 服务器ID
	PlayerIDs []int32 `json:"player_ids"` // 玩家ID列表（1-1000个）
}
