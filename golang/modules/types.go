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
	ID          int32      `json:"id"`
	Name        string     `json:"name"`
	Address     string     `json:"address"`
	Port        int32      `json:"port"`
	Type        ServerType `json:"type"`
	Description *string    `json:"description,omitempty"`
	Active      bool       `json:"active"`
	CreatedAt   time.Time  `json:"created_at"`
	UpdatedAt   time.Time  `json:"updated_at"`
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
	ExpiresAt   *time.Time `json:"expires_at,omitempty"`
	CreatedAt   time.Time  `json:"created_at"`
	UpdatedAt   time.Time  `json:"updated_at"`
}

// IP相关类型
type IPInfo struct {
	IP          string    `json:"ip"`
	Country     *string   `json:"country,omitempty"`
	Region      *string   `json:"region,omitempty"`
	City        *string   `json:"city,omitempty"`
	ISP         *string   `json:"isp,omitempty"`
	IsProxy     bool      `json:"is_proxy"`
	IsVPN       bool      `json:"is_vpn"`
	IsTor       bool      `json:"is_tor"`
	ThreatLevel string    `json:"threat_level"`
	IsBanned    bool      `json:"is_banned"`
	BanReason   *string   `json:"ban_reason,omitempty"`
	CreatedAt   time.Time `json:"created_at"`
	UpdatedAt   time.Time `json:"updated_at"`
	// 风险信息字段
	RiskLevel       string `json:"risk_level"`
	RiskDescription string `json:"risk_description"`
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
	Players   []PlayerValidateInfo `json:"players"`
	ServerID  int32                `json:"server_id"`
	Login     bool                 `json:"login"`
	Timestamp int64                `json:"timestamp"`
}

// 单个玩家验证结果
type PlayerValidateResult struct {
	PlayerName string  `json:"player_name"`
	Allowed    bool    `json:"allowed"`
	PlayerID   *int32  `json:"player_id,omitempty"`
	Reason     *string `json:"reason,omitempty"`
	Newbie     bool    `json:"newbie"`
	IPInfo     *IPInfo `json:"ip_info,omitempty"`
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
	Name        string     `json:"name"`
	Address     string     `json:"address"`
	Port        int32      `json:"port"`
	Type        ServerType `json:"type"`
	Description *string    `json:"description,omitempty"`
}

type UpdateServerRequest struct {
	Name        *string     `json:"name,omitempty"`
	Address     *string     `json:"address,omitempty"`
	Port        *int32      `json:"port,omitempty"`
	Type        *ServerType `json:"type,omitempty"`
	Description *string     `json:"description,omitempty"`
	Active      *bool       `json:"active,omitempty"`
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

type UpdateTownRequest struct {
	Name          *string  `json:"name,omitempty"`
	Description   *string  `json:"description,omitempty"`
	Level         *int32   `json:"level,omitempty"`
	QQGroup       *string  `json:"qq_group,omitempty"`
	LeaderID      *int32   `json:"leader_id,omitempty"`
	AddPlayers    *[]int32 `json:"add_players,omitempty"`
	RemovePlayers *[]int32 `json:"remove_players,omitempty"`
}

type CreateApiTokenRequest struct {
	Name        string  `json:"name"`
	Role        string  `json:"role"`
	Description *string `json:"description,omitempty"`
	ExpireDays  *int32  `json:"expire_days,omitempty"`
}

type UpdateApiTokenRequest struct {
	Name        *string `json:"name,omitempty"`
	Description *string `json:"description,omitempty"`
	ExpireDays  *int32  `json:"expire_days,omitempty"`
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
	Players []Player `json:"players"`
	Total   int32    `json:"total"`
	Page    int32    `json:"page"`
	Size    int32    `json:"size"`
}

type ValidateLoginData struct {
	Valid     bool    `json:"valid"`
	Player    *Player `json:"player,omitempty"`
	IPInfo    *IPInfo `json:"ip_info,omitempty"`
	BanReason *string `json:"ban_reason,omitempty"`
}

type ServersListData struct {
	Servers []ServerRegistry `json:"servers"`
	Total   int32            `json:"total"`
	Page    int32            `json:"page"`
	Size    int32            `json:"size"`
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
	Towns []Town `json:"towns"`
	Total int32  `json:"total"`
	Page  int32  `json:"page"`
	Size  int32  `json:"size"`
}

type TownDetailResponse struct {
	Town        Town     `json:"town"`
	Leader      *Player  `json:"leader,omitempty"`
	Members     []Player `json:"members"`
	MemberCount int64    `json:"member_count"`
}

type TownMembersData struct {
	Members []Player `json:"members"`
	Total   int32    `json:"total"`
	Page    int32    `json:"page"`
	Size    int32    `json:"size"`
}

type ListApiTokensData struct {
	Tokens []ApiToken `json:"tokens"`
	Total  int32      `json:"total"`
}

type CreateApiTokenData struct {
	Token      ApiToken `json:"token"`
	TokenValue string   `json:"token_value"`
}

type BannedIPsData struct {
	IPs   []IPInfo `json:"ips"`
	Total int32    `json:"total"`
	Page  int32    `json:"page"`
	Size  int32    `json:"size"`
}

type SuspiciousIPsData struct {
	IPs   []IPInfo `json:"ips"`
	Total int32    `json:"total"`
	Page  int32    `json:"page"`
	Size  int32    `json:"size"`
}

type HighRiskIPsData struct {
	IPs   []IPInfo `json:"ips"`
	Total int32    `json:"total"`
	Page  int32    `json:"page"`
	Size  int32    `json:"size"`
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

type BatchBanIPsResponse struct {
	Success []string `json:"success"`
	Failed  []string `json:"failed"`
	Total   int32    `json:"total"`
}

type PlayerServersData struct {
	Servers []ServerRegistry `json:"servers"`
	Total   int32            `json:"total"`
	Page    int32            `json:"page"`
	Size    int32            `json:"size"`
}

type ServerPlayersData struct {
	Players []Player `json:"players"`
	Total   int32    `json:"total"`
	Page    int32    `json:"page"`
	Size    int32    `json:"size"`
}

type OnlinePlayersData struct {
	Players []Player `json:"players"`
	Total   int32    `json:"total"`
	Page    int32    `json:"page"`
	Size    int32    `json:"size"`
}
