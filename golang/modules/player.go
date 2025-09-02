package modules

import (
	"strconv"

	"github.com/Gk0Wk/NewNanManager/sdk/golang/utils"
	"github.com/go-resty/resty/v2"
)

// PlayerService 玩家管理服务
type PlayerService struct {
	client *resty.Client
}

// NewPlayerService 创建玩家服务实例
func NewPlayerService(client *resty.Client) *PlayerService {
	return &PlayerService{client: client}
}

// ListPlayers 获取玩家列表
func (s *PlayerService) ListPlayers(page, pageSize *int32, search *string, townID *int32, banMode *BanMode, name, qq, qqguild, discord *string) (*PlayersListData, error) {
	req := s.client.R()

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
	if name != nil {
		req.SetQueryParam("name", *name)
	}
	if qq != nil {
		req.SetQueryParam("qq", *qq)
	}
	if qqguild != nil {
		req.SetQueryParam("qqguild", *qqguild)
	}
	if discord != nil {
		req.SetQueryParam("discord", *discord)
	}

	resp, err := req.Get("/api/v1/players")

	var result PlayersListData
	err = utils.HandleResponse(resp, err, &result)
	if err != nil {
		return nil, err
	}

	return &result, nil
}

// CreatePlayer 创建玩家
func (s *PlayerService) CreatePlayer(request CreatePlayerRequest) (*Player, error) {
	resp, err := s.client.R().
		SetBody(request).
		Post("/api/v1/players")

	var result Player
	err = utils.HandleResponse(resp, err, &result)
	if err != nil {
		return nil, err
	}

	return &result, nil
}

// Validate 玩家验证（支持批处理）
func (s *PlayerService) Validate(request ValidateRequest) (*ValidateResponse, error) {
	resp, err := s.client.R().
		SetBody(request).
		Post("/api/v1/players/validate")

	var result ValidateResponse
	err = utils.HandleResponse(resp, err, &result)
	if err != nil {
		return nil, err
	}

	return &result, nil
}

// ValidateLogin 玩家登录验证（向后兼容）
// Deprecated: Please use Validate method for batch validation
func (s *PlayerService) ValidateLogin(request ValidateLoginRequest) (*ValidateLoginData, error) {
	resp, err := s.client.R().
		SetBody(request).
		Post("/api/v1/players/validate-login")

	var result ValidateLoginData
	err = utils.HandleResponse(resp, err, &result)
	if err != nil {
		return nil, err
	}

	return &result, nil
}

// GetPlayer 获取玩家详情
func (s *PlayerService) GetPlayer(id int32) (*Player, error) {
	resp, err := s.client.R().
		Get("/api/v1/players/" + strconv.Itoa(int(id)))

	var result Player
	err = utils.HandleResponse(resp, err, &result)
	if err != nil {
		return nil, err
	}

	return &result, nil
}

// UpdatePlayer 更新玩家信息
func (s *PlayerService) UpdatePlayer(id int32, request UpdatePlayerRequest) (*Player, error) {
	resp, err := s.client.R().
		SetBody(request).
		Put("/api/v1/players/" + strconv.Itoa(int(id)))

	var result Player
	err = utils.HandleResponse(resp, err, &result)
	if err != nil {
		return nil, err
	}

	return &result, nil
}

// DeletePlayer 删除玩家
func (s *PlayerService) DeletePlayer(id int32) error {
	resp, err := s.client.R().
		Delete("/api/v1/players/" + strconv.Itoa(int(id)))

	return utils.HandleResponse(resp, err, nil)
}

// BanPlayer 封禁玩家
func (s *PlayerService) BanPlayer(playerID int32, request BanPlayerRequest) error {
	resp, err := s.client.R().
		SetBody(request).
		Post("/api/v1/players/" + strconv.Itoa(int(playerID)) + "/ban")

	return utils.HandleResponse(resp, err, nil)
}

// UnbanPlayer 解封玩家
func (s *PlayerService) UnbanPlayer(playerID int32) error {
	resp, err := s.client.R().
		Post("/api/v1/players/" + strconv.Itoa(int(playerID)) + "/unban")

	return utils.HandleResponse(resp, err, nil)
}
