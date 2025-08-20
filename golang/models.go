package nanmanager

import "time"

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

// 通用响应类型
type ApiResponse struct {
	Code      int32       `json:"code"`
	Message   string      `json:"message"`
	Data      interface{} `json:"data,omitempty"`
	RequestID string      `json:"request_id"`
}

type ErrorResponse struct {
	ApiResponse
	Data *ErrorData `json:"data,omitempty"`
}

type ErrorData struct {
	Details string `json:"details,omitempty"`
}

// 核心实体模型
type Player struct {
	ID        int32      `json:"id"`
	Name      string     `json:"name"`
	TownID    *int32     `json:"town_id,omitempty"`
	QQ        *string    `json:"qq,omitempty"`
	QQGuild   *string    `json:"qqguild,omitempty"`
	Discord   *string    `json:"discord,omitempty"`
	InQQGroup bool       `json:"in_qq_group"`
	InQQGuild bool       `json:"in_qq_guild"`
	InDiscord bool       `json:"in_discord"`
	BanMode   BanMode    `json:"ban_mode"`
	BanExpire *time.Time `json:"ban_expire,omitempty"`
	BanReason *string    `json:"ban_reason,omitempty"`
	CreatedAt time.Time  `json:"created_at"`
	UpdatedAt time.Time  `json:"updated_at"`
}

type Town struct {
	ID          int32     `json:"id"`
	Name        string    `json:"name"`
	Level       int32     `json:"level"`
	LeaderID    *int32    `json:"leader_id,omitempty"`
	QQGroup     *string   `json:"qq_group,omitempty"`
	Description *string   `json:"description,omitempty"`
	CreatedAt   time.Time `json:"created_at"`
	UpdatedAt   time.Time `json:"updated_at"`
}

type ServerRegistry struct {
	ID          int32      `json:"id"`
	Name        string     `json:"name"`
	Address     string     `json:"address"`
	ServerType  ServerType `json:"server_type"`
	Description *string    `json:"description,omitempty"`
	CreatedAt   time.Time  `json:"created_at"`
	UpdatedAt   time.Time  `json:"updated_at"`
}

type ServerStatus struct {
	ServerID       int32     `json:"server_id"`
	Online         bool      `json:"online"`
	CurrentPlayers int32     `json:"current_players"`
	MaxPlayers     int32     `json:"max_players"`
	LatencyMs      *int32    `json:"latency_ms,omitempty"`
	TPS            *float64  `json:"tps,omitempty"`
	Version        *string   `json:"version,omitempty"`
	MOTD           *string   `json:"motd,omitempty"`
	ExpireAt       time.Time `json:"expire_at"`
	LastHeartbeat  time.Time `json:"last_heartbeat"`
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

// 数据模型（用于包装在ApiResponse.data中）
type PlayersListData struct {
	Players  []Player `json:"players"`
	Total    int64    `json:"total"`
	Page     int32    `json:"page"`
	PageSize int32    `json:"page_size"`
}

type TownsListData struct {
	Towns    []Town `json:"towns"`
	Total    int64  `json:"total"`
	Page     int32  `json:"page"`
	PageSize int32  `json:"page_size"`
}

type ServersListData struct {
	Servers  []ServerRegistry `json:"servers"`
	Total    int64            `json:"total"`
	Page     int32            `json:"page"`
	PageSize int32            `json:"page_size"`
}

type ValidateLoginData struct {
	Allowed  bool    `json:"allowed"`
	PlayerID *int32  `json:"player_id,omitempty"`
	Reason   *string `json:"reason,omitempty"`
}

type ServerDetailData struct {
	Server ServerRegistry `json:"server"`
	Status *ServerStatus  `json:"status,omitempty"`
}

type LatencyStatsData struct {
	ServerID    int32     `json:"server_id"`
	Count       int64     `json:"count"`
	Current     int64     `json:"current"`
	Average     int64     `json:"average"`
	Min         int64     `json:"min"`
	Max         int64     `json:"max"`
	Variance    float64   `json:"variance"`
	LastUpdated time.Time `json:"last_updated"`
}

type HeartbeatData struct {
	ReceivedAt    int64  `json:"received_at"`
	ResponseAt    int64  `json:"response_at"`
	SequenceID    int64  `json:"sequence_id"`
	ServerTime    int64  `json:"server_time"`
	Status        string `json:"status"`
	NextHeartbeat int64  `json:"next_heartbeat"`
	ExpireAt      int64  `json:"expire_at"`
}

type CreateApiTokenData struct {
	TokenInfo  ApiToken `json:"token_info"`
	TokenValue string   `json:"token_value"`
}

type ListApiTokensData struct {
	Tokens []ApiToken `json:"tokens"`
}

type TownMembersData struct {
	Members  []Player `json:"members"`
	Total    int64    `json:"total"`
	Page     int32    `json:"page"`
	PageSize int32    `json:"page_size"`
}

// 请求体模型
type BanPlayerRequest struct {
	BanMode         BanMode `json:"ban_mode"`
	DurationSeconds *int64  `json:"duration_seconds,omitempty"`
	Reason          string  `json:"reason"`
}

type CreateApiTokenRequest struct {
	Name        string  `json:"name,omitempty"`
	Role        string  `json:"role,omitempty"`
	Description *string `json:"description,omitempty"`
	ExpireDays  *int64  `json:"expire_days,omitempty"`
}

type CreatePlayerRequest struct {
	Name      string  `json:"name,omitempty"`
	TownID    *int32  `json:"town_id,omitempty"`
	QQ        *string `json:"qq,omitempty"`
	QQGuild   *string `json:"qqguild,omitempty"`
	Discord   *string `json:"discord,omitempty"`
	InQQGroup *bool   `json:"in_qq_group,omitempty"`
	InQQGuild *bool   `json:"in_qq_guild,omitempty"`
	InDiscord *bool   `json:"in_discord,omitempty"`
}

type CreateTownRequest struct {
	Name        string  `json:"name,omitempty"`
	Level       *int32  `json:"level,omitempty"`
	LeaderID    *int32  `json:"leader_id,omitempty"`
	QQGroup     *string `json:"qq_group,omitempty"`
	Description *string `json:"description,omitempty"`
}

type HeartbeatRequest struct {
	Timestamp      *int64            `json:"timestamp,omitempty"`
	SequenceID     *int64            `json:"sequence_id,omitempty"`
	CurrentPlayers *int32            `json:"current_players,omitempty"`
	MaxPlayers     *int32            `json:"max_players,omitempty"`
	TPS            *float64          `json:"tps,omitempty"`
	Version        *string           `json:"version,omitempty"`
	MOTD           *string           `json:"motd,omitempty"`
	LastRTTMs      *int64            `json:"last_rtt_ms,omitempty"`
	PlayerList     []PlayerLoginInfo `json:"player_list,omitempty"`
}

type ManageTownMemberRequest struct {
	PlayerID *int32  `json:"player_id,omitempty"`
	Action   *string `json:"action,omitempty"`
}

type PlayerLoginInfo struct {
	PlayerID *int32  `json:"player_id,omitempty"`
	Name     *string `json:"name,omitempty"`
	IP       *string `json:"ip,omitempty"`
}

type RegisterServerRequest struct {
	Name        string      `json:"name,omitempty"`
	Address     string      `json:"address,omitempty"`
	ServerType  *ServerType `json:"server_type,omitempty"`
	Description *string     `json:"description,omitempty"`
}

type UpdateApiTokenRequest struct {
	Name        *string `json:"name,omitempty"`
	Role        *string `json:"role,omitempty"`
	Description *string `json:"description,omitempty"`
	Active      *bool   `json:"active,omitempty"`
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

type UpdateServerRequest struct {
	Name        *string     `json:"name,omitempty"`
	Address     *string     `json:"address,omitempty"`
	ServerType  *ServerType `json:"server_type,omitempty"`
	Description *string     `json:"description,omitempty"`
}

type UpdateTownRequest struct {
	Name        *string `json:"name,omitempty"`
	Level       *int32  `json:"level,omitempty"`
	LeaderID    *int32  `json:"leader_id,omitempty"`
	QQGroup     *string `json:"qq_group,omitempty"`
	Description *string `json:"description,omitempty"`
}

type ValidateLoginRequest struct {
	PlayerName      *string `json:"player_name,omitempty"`
	ServerID        *int32  `json:"server_id,omitempty"`
	ClientVersion   *string `json:"client_version,omitempty"`
	ProtocolVersion *string `json:"protocol_version,omitempty"`
}
