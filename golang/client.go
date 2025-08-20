package nanmanager

import (
	"encoding/json"
	"fmt"
	"strconv"

	"github.com/go-resty/resty/v2"
)

// NanCityManagerClient NewNanManager API客户端
type NanCityManagerClient struct {
	client  *resty.Client
	baseURL string
	token   string
}

// NewNanCityManager 创建新的API客户端
func NewNanCityManager(baseURL, token string) *NanCityManagerClient {
	client := resty.New()
	client.SetBaseURL(baseURL)
	client.SetHeader("Authorization", "Bearer "+token)
	client.SetHeader("X-API-Token", token)
	client.SetHeader("Content-Type", "application/json")

	return &NanCityManagerClient{
		client:  client,
		baseURL: baseURL,
		token:   token,
	}
}

// 辅助函数：处理API响应
func handleResponse(resp *resty.Response, err error, result interface{}) error {
	if err != nil {
		return fmt.Errorf("request failed: %w", err)
	}

	if resp.StatusCode() >= 400 {
		var errorResp ErrorResponse
		if err := json.Unmarshal(resp.Body(), &errorResp); err != nil {
			return fmt.Errorf("HTTP %d: %s", resp.StatusCode(), string(resp.Body()))
		}
		return fmt.Errorf("API error (code %d): %s", errorResp.Code, errorResp.Message)
	}

	if result != nil {
		var apiResp ApiResponse
		if err := json.Unmarshal(resp.Body(), &apiResp); err != nil {
			return fmt.Errorf("failed to parse response: %w", err)
		}

		if apiResp.Code != 0 {
			return fmt.Errorf("API error (code %d): %s", apiResp.Code, apiResp.Message)
		}

		// 将data部分重新序列化然后反序列化到目标结构
		if apiResp.Data != nil {
			dataBytes, err := json.Marshal(apiResp.Data)
			if err != nil {
				return fmt.Errorf("failed to marshal data: %w", err)
			}
			if err := json.Unmarshal(dataBytes, result); err != nil {
				return fmt.Errorf("failed to unmarshal data: %w", err)
			}
		}
	}

	return nil
}

// ========== 玩家管理服务 ==========

// ListPlayers 获取玩家列表
func (c *NanCityManagerClient) ListPlayers(page, pageSize *int32, search *string, townID *int32, banMode *BanMode) (*PlayersListData, error) {
	req := c.client.R()

	if page != nil {
		req.SetQueryParam("page", strconv.Itoa(int(*page)))
	}
	if pageSize != nil {
		req.SetQueryParam("page_size", strconv.Itoa(int(*pageSize)))
	}
	if search != nil {
		req.SetQueryParam("search", *search)
	}
	if townID != nil {
		req.SetQueryParam("town_id", strconv.Itoa(int(*townID)))
	}
	if banMode != nil {
		req.SetQueryParam("ban_mode", strconv.Itoa(int(*banMode)))
	}

	resp, err := req.Get("/api/v1/players")

	var result PlayersListData
	err = handleResponse(resp, err, &result)
	if err != nil {
		return nil, err
	}

	return &result, nil
}

// CreatePlayer 创建玩家
func (c *NanCityManagerClient) CreatePlayer(request CreatePlayerRequest) (*Player, error) {
	resp, err := c.client.R().
		SetBody(request).
		Post("/api/v1/players")

	var result Player
	err = handleResponse(resp, err, &result)
	if err != nil {
		return nil, err
	}

	return &result, nil
}

// ValidateLogin 玩家登录验证
func (c *NanCityManagerClient) ValidateLogin(request ValidateLoginRequest) (*ValidateLoginData, error) {
	resp, err := c.client.R().
		SetBody(request).
		Post("/api/v1/players/validate-login")

	var result ValidateLoginData
	err = handleResponse(resp, err, &result)
	if err != nil {
		return nil, err
	}

	return &result, nil
}

// GetPlayer 获取玩家详情
func (c *NanCityManagerClient) GetPlayer(id int32) (*Player, error) {
	resp, err := c.client.R().
		Get("/api/v1/players/" + strconv.Itoa(int(id)))

	var result Player
	err = handleResponse(resp, err, &result)
	if err != nil {
		return nil, err
	}

	return &result, nil
}

// UpdatePlayer 更新玩家信息
func (c *NanCityManagerClient) UpdatePlayer(id int32, request UpdatePlayerRequest) (*Player, error) {
	resp, err := c.client.R().
		SetBody(request).
		Put("/api/v1/players/" + strconv.Itoa(int(id)))

	var result Player
	err = handleResponse(resp, err, &result)
	if err != nil {
		return nil, err
	}

	return &result, nil
}

// DeletePlayer 删除玩家
func (c *NanCityManagerClient) DeletePlayer(id int32) error {
	resp, err := c.client.R().
		Delete("/api/v1/players/" + strconv.Itoa(int(id)))

	return handleResponse(resp, err, nil)
}

// BanPlayer 封禁玩家
func (c *NanCityManagerClient) BanPlayer(playerID int32, request BanPlayerRequest) error {
	resp, err := c.client.R().
		SetBody(request).
		Post("/api/v1/players/" + strconv.Itoa(int(playerID)) + "/ban")

	return handleResponse(resp, err, nil)
}

// UnbanPlayer 解封玩家
func (c *NanCityManagerClient) UnbanPlayer(playerID int32) error {
	resp, err := c.client.R().
		Post("/api/v1/players/" + strconv.Itoa(int(playerID)) + "/unban")

	return handleResponse(resp, err, nil)
}

// ========== 服务器管理服务 ==========

// ListServers 获取服务器列表
func (c *NanCityManagerClient) ListServers(page, pageSize *int32, search *string, onlineOnly *bool) (*ServersListData, error) {
	req := c.client.R()

	if page != nil {
		req.SetQueryParam("page", strconv.Itoa(int(*page)))
	}
	if pageSize != nil {
		req.SetQueryParam("page_size", strconv.Itoa(int(*pageSize)))
	}
	if search != nil {
		req.SetQueryParam("search", *search)
	}
	if onlineOnly != nil {
		req.SetQueryParam("online_only", strconv.FormatBool(*onlineOnly))
	}

	resp, err := req.Get("/api/v1/servers")

	var result ServersListData
	err = handleResponse(resp, err, &result)
	if err != nil {
		return nil, err
	}

	return &result, nil
}

// RegisterServer 注册服务器
func (c *NanCityManagerClient) RegisterServer(request RegisterServerRequest) (*ServerRegistry, error) {
	resp, err := c.client.R().
		SetBody(request).
		Post("/api/v1/servers")

	var result ServerRegistry
	err = handleResponse(resp, err, &result)
	if err != nil {
		return nil, err
	}

	return &result, nil
}

// GetServer 获取服务器信息
func (c *NanCityManagerClient) GetServer(id int32) (*ServerRegistry, error) {
	resp, err := c.client.R().
		Get("/api/v1/servers/" + strconv.Itoa(int(id)))

	var result ServerRegistry
	err = handleResponse(resp, err, &result)
	if err != nil {
		return nil, err
	}

	return &result, nil
}

// UpdateServer 更新服务器信息
func (c *NanCityManagerClient) UpdateServer(id int32, request UpdateServerRequest) (*ServerRegistry, error) {
	resp, err := c.client.R().
		SetBody(request).
		Put("/api/v1/servers/" + strconv.Itoa(int(id)))

	var result ServerRegistry
	err = handleResponse(resp, err, &result)
	if err != nil {
		return nil, err
	}

	return &result, nil
}

// DeleteServer 删除服务器
func (c *NanCityManagerClient) DeleteServer(id int32) error {
	resp, err := c.client.R().
		Delete("/api/v1/servers/" + strconv.Itoa(int(id)))

	return handleResponse(resp, err, nil)
}

// GetServerDetail 获取服务器详细信息
func (c *NanCityManagerClient) GetServerDetail(id int32) (*ServerDetailData, error) {
	resp, err := c.client.R().
		Get("/api/v1/servers/" + strconv.Itoa(int(id)) + "/detail")

	var result ServerDetailData
	err = handleResponse(resp, err, &result)
	if err != nil {
		return nil, err
	}

	return &result, nil
}

// ========== 监控服务 ==========

// Heartbeat 服务器心跳
func (c *NanCityManagerClient) Heartbeat(serverID int32, request HeartbeatRequest) (*HeartbeatData, error) {
	resp, err := c.client.R().
		SetBody(request).
		Post("/api/v1/servers/" + strconv.Itoa(int(serverID)) + "/heartbeat")

	var result HeartbeatData
	err = handleResponse(resp, err, &result)
	if err != nil {
		return nil, err
	}

	return &result, nil
}

// GetLatencyStats 获取延迟统计
func (c *NanCityManagerClient) GetLatencyStats(serverID int32) (*LatencyStatsData, error) {
	resp, err := c.client.R().
		Get("/api/v1/servers/" + strconv.Itoa(int(serverID)) + "/latency")

	var result LatencyStatsData
	err = handleResponse(resp, err, &result)
	if err != nil {
		return nil, err
	}

	return &result, nil
}

// GetServerStatus 获取服务器状态
func (c *NanCityManagerClient) GetServerStatus(serverID int32) (*ServerStatus, error) {
	resp, err := c.client.R().
		Get("/api/v1/servers/" + strconv.Itoa(int(serverID)) + "/status")

	var result ServerStatus
	err = handleResponse(resp, err, &result)
	if err != nil {
		return nil, err
	}

	return &result, nil
}

// ========== Token管理服务 ==========

// ListApiTokens 获取API Token列表
func (c *NanCityManagerClient) ListApiTokens() (*ListApiTokensData, error) {
	resp, err := c.client.R().
		Get("/api/v1/tokens")

	var result ListApiTokensData
	err = handleResponse(resp, err, &result)
	if err != nil {
		return nil, err
	}

	return &result, nil
}

// CreateApiToken 创建API Token
func (c *NanCityManagerClient) CreateApiToken(request CreateApiTokenRequest) (*CreateApiTokenData, error) {
	resp, err := c.client.R().
		SetBody(request).
		Post("/api/v1/tokens")

	var result CreateApiTokenData
	err = handleResponse(resp, err, &result)
	if err != nil {
		return nil, err
	}

	return &result, nil
}

// GetApiToken 获取API Token详情
func (c *NanCityManagerClient) GetApiToken(id int32) (*ApiToken, error) {
	resp, err := c.client.R().
		Get("/api/v1/tokens/" + strconv.Itoa(int(id)))

	var result ApiToken
	err = handleResponse(resp, err, &result)
	if err != nil {
		return nil, err
	}

	return &result, nil
}

// UpdateApiToken 更新API Token
func (c *NanCityManagerClient) UpdateApiToken(id int32, request UpdateApiTokenRequest) (*ApiToken, error) {
	resp, err := c.client.R().
		SetBody(request).
		Put("/api/v1/tokens/" + strconv.Itoa(int(id)))

	var result ApiToken
	err = handleResponse(resp, err, &result)
	if err != nil {
		return nil, err
	}

	return &result, nil
}

// DeleteApiToken 删除API Token
func (c *NanCityManagerClient) DeleteApiToken(id int32) error {
	resp, err := c.client.R().
		Delete("/api/v1/tokens/" + strconv.Itoa(int(id)))

	return handleResponse(resp, err, nil)
}

// ========== 城镇管理服务 ==========

// ListTowns 获取城镇列表
func (c *NanCityManagerClient) ListTowns(page, pageSize *int32, search *string, minLevel, maxLevel *int32) (*TownsListData, error) {
	req := c.client.R()

	if page != nil {
		req.SetQueryParam("page", strconv.Itoa(int(*page)))
	}
	if pageSize != nil {
		req.SetQueryParam("page_size", strconv.Itoa(int(*pageSize)))
	}
	if search != nil {
		req.SetQueryParam("search", *search)
	}
	if minLevel != nil {
		req.SetQueryParam("min_level", strconv.Itoa(int(*minLevel)))
	}
	if maxLevel != nil {
		req.SetQueryParam("max_level", strconv.Itoa(int(*maxLevel)))
	}

	resp, err := req.Get("/api/v1/towns")

	var result TownsListData
	err = handleResponse(resp, err, &result)
	if err != nil {
		return nil, err
	}

	return &result, nil
}

// CreateTown 创建城镇
func (c *NanCityManagerClient) CreateTown(request CreateTownRequest) (*Town, error) {
	resp, err := c.client.R().
		SetBody(request).
		Post("/api/v1/towns")

	var result Town
	err = handleResponse(resp, err, &result)
	if err != nil {
		return nil, err
	}

	return &result, nil
}

// GetTown 获取城镇详情
func (c *NanCityManagerClient) GetTown(id int32) (*Town, error) {
	resp, err := c.client.R().
		Get("/api/v1/towns/" + strconv.Itoa(int(id)))

	var result Town
	err = handleResponse(resp, err, &result)
	if err != nil {
		return nil, err
	}

	return &result, nil
}

// UpdateTown 更新城镇信息
func (c *NanCityManagerClient) UpdateTown(id int32, request UpdateTownRequest) (*Town, error) {
	resp, err := c.client.R().
		SetBody(request).
		Put("/api/v1/towns/" + strconv.Itoa(int(id)))

	var result Town
	err = handleResponse(resp, err, &result)
	if err != nil {
		return nil, err
	}

	return &result, nil
}

// DeleteTown 删除城镇
func (c *NanCityManagerClient) DeleteTown(id int32) error {
	resp, err := c.client.R().
		Delete("/api/v1/towns/" + strconv.Itoa(int(id)))

	return handleResponse(resp, err, nil)
}

// GetTownMembers 获取城镇成员列表
func (c *NanCityManagerClient) GetTownMembers(townID int32, page, pageSize *int32) (*TownMembersData, error) {
	req := c.client.R()

	if page != nil {
		req.SetQueryParam("page", strconv.Itoa(int(*page)))
	}
	if pageSize != nil {
		req.SetQueryParam("page_size", strconv.Itoa(int(*pageSize)))
	}

	resp, err := req.Get("/api/v1/towns/" + strconv.Itoa(int(townID)) + "/members")

	var result TownMembersData
	err = handleResponse(resp, err, &result)
	if err != nil {
		return nil, err
	}

	return &result, nil
}

// ManageTownMember 管理城镇成员
func (c *NanCityManagerClient) ManageTownMember(townID int32, request ManageTownMemberRequest) error {
	resp, err := c.client.R().
		SetBody(request).
		Post("/api/v1/towns/" + strconv.Itoa(int(townID)) + "/members")

	return handleResponse(resp, err, nil)
}
