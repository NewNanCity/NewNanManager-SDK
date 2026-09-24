package modules

import (
	"context"
	"strconv"

	"github.com/NewNanCity/NewNanManager-SDK/clients/golang/utils"
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
	return s.ListPlayersWithContext(context.Background(), page, pageSize, search, townID, banMode, name, qq, qqguild, discord)
}

// ListPlayersWithContext executes ListPlayers with cancellation scoped to this request.
func (s *PlayerService) ListPlayersWithContext(ctx context.Context, page, pageSize *int32, search *string, townID *int32, banMode *BanMode, name, qq, qqguild, discord *string) (*PlayersListData, error) {
	req := s.client.R().SetContext(ctx)

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
	return s.CreatePlayerWithContext(context.Background(), request)
}

// CreatePlayerWithContext executes CreatePlayer with cancellation scoped to this request.
func (s *PlayerService) CreatePlayerWithContext(ctx context.Context, request CreatePlayerRequest) (*Player, error) {
	resp, err := s.client.R().SetContext(ctx).
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
	return s.ValidateWithContext(context.Background(), request)
}

// ValidateWithContext executes Validate with cancellation scoped to this request.
func (s *PlayerService) ValidateWithContext(ctx context.Context, request ValidateRequest) (*ValidateResponse, error) {
	return s.validateWithContext(ctx, request, nil)
}

// ValidateWithSession sends the server session fencing headers.
func (s *PlayerService) ValidateWithSession(request ValidateRequest, session SessionContext) (*ValidateResponse, error) {
	return s.ValidateWithContextAndSession(context.Background(), request, session)
}

// ValidateWithContextAndSession scopes cancellation and sends session fencing headers.
func (s *PlayerService) ValidateWithContextAndSession(ctx context.Context, request ValidateRequest, session SessionContext) (*ValidateResponse, error) {
	return s.validateWithContext(ctx, request, &session)
}

func (s *PlayerService) validateWithContext(ctx context.Context, request ValidateRequest, session *SessionContext) (*ValidateResponse, error) {
	req := s.client.R().SetContext(ctx)
	if session != nil {
		if err := applySessionHeaders(req, *session); err != nil {
			return nil, err
		}
	}
	resp, err := req.
		SetBody(request).
		Post("/api/v1/players/validate")

	var result ValidateResponse
	err = utils.HandleResponse(resp, err, &result)
	if err != nil {
		return nil, err
	}

	return &result, nil
}

// GetPlayer 获取玩家详情
func (s *PlayerService) GetPlayer(id int32) (*Player, error) {
	return s.GetPlayerWithContext(context.Background(), id)
}

// GetPlayerWithContext executes GetPlayer with cancellation scoped to this request.
func (s *PlayerService) GetPlayerWithContext(ctx context.Context, id int32) (*Player, error) {
	resp, err := s.client.R().SetContext(ctx).
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
	return s.UpdatePlayerWithContext(context.Background(), id, request)
}

// UpdatePlayerWithContext executes UpdatePlayer with cancellation scoped to this request.
func (s *PlayerService) UpdatePlayerWithContext(ctx context.Context, id int32, request UpdatePlayerRequest) (*Player, error) {
	resp, err := s.client.R().SetContext(ctx).
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
	return s.DeletePlayerWithContext(context.Background(), id)
}

// DeletePlayerWithContext executes DeletePlayer with cancellation scoped to this request.
func (s *PlayerService) DeletePlayerWithContext(ctx context.Context, id int32) error {
	resp, err := s.client.R().SetContext(ctx).
		Delete("/api/v1/players/" + strconv.Itoa(int(id)))

	return utils.HandleResponse(resp, err, nil)
}

// BanPlayer 封禁玩家
func (s *PlayerService) BanPlayer(playerID int32, request BanPlayerRequest) error {
	return s.BanPlayerWithContext(context.Background(), playerID, request)
}

// BanPlayerWithContext executes BanPlayer with cancellation scoped to this request.
func (s *PlayerService) BanPlayerWithContext(ctx context.Context, playerID int32, request BanPlayerRequest) error {
	resp, err := s.client.R().SetContext(ctx).
		SetBody(request).
		Post("/api/v1/players/" + strconv.Itoa(int(playerID)) + "/ban")

	return utils.HandleResponse(resp, err, nil)
}

// UnbanPlayer 解封玩家
func (s *PlayerService) UnbanPlayer(playerID int32) error {
	return s.UnbanPlayerWithContext(context.Background(), playerID)
}

// UnbanPlayerWithContext executes UnbanPlayer with cancellation scoped to this request.
func (s *PlayerService) UnbanPlayerWithContext(ctx context.Context, playerID int32) error {
	resp, err := s.client.R().SetContext(ctx).
		Post("/api/v1/players/" + strconv.Itoa(int(playerID)) + "/unban")

	return utils.HandleResponse(resp, err, nil)
}
