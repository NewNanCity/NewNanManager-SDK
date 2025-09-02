package modules

import (
	"strconv"

	"github.com/Gk0Wk/NewNanManager/sdk/golang/utils"
	"github.com/go-resty/resty/v2"
)

// PlayerServerService 玩家服务器关系管理服务
type PlayerServerService struct {
	client *resty.Client
}

// NewPlayerServerService 创建玩家服务器关系服务实例
func NewPlayerServerService(client *resty.Client) *PlayerServerService {
	return &PlayerServerService{client: client}
}

// GetPlayerServers 获取玩家的服务器列表
func (s *PlayerServerService) GetPlayerServers(playerID int32, page, pageSize *int32) (*PlayerServersData, error) {
	req := s.client.R()

	if page != nil {
		req.SetQueryParam("page", strconv.Itoa(int(*page)))
	}
	if pageSize != nil {
		req.SetQueryParam("page_size", strconv.Itoa(int(*pageSize)))
	}

	resp, err := req.Get("/api/v1/players/" + strconv.Itoa(int(playerID)) + "/servers")

	var result PlayerServersData
	err = utils.HandleResponse(resp, err, &result)
	if err != nil {
		return nil, err
	}

	return &result, nil
}

// GetServerPlayers 获取服务器的玩家列表
func (s *PlayerServerService) GetServerPlayers(serverID int32, page, pageSize *int32, onlineOnly *bool) (*ServerPlayersData, error) {
	req := s.client.R()

	if page != nil {
		req.SetQueryParam("page", strconv.Itoa(int(*page)))
	}
	if pageSize != nil {
		req.SetQueryParam("page_size", strconv.Itoa(int(*pageSize)))
	}
	if onlineOnly != nil {
		req.SetQueryParam("online_only", strconv.FormatBool(*onlineOnly))
	}

	resp, err := req.Get("/api/v1/servers/" + strconv.Itoa(int(serverID)) + "/players")

	var result ServerPlayersData
	err = utils.HandleResponse(resp, err, &result)
	if err != nil {
		return nil, err
	}

	return &result, nil
}

// GetOnlinePlayers 获取在线玩家列表
func (s *PlayerServerService) GetOnlinePlayers(page, pageSize *int32, serverID *int32) (*OnlinePlayersData, error) {
	req := s.client.R()

	if page != nil {
		req.SetQueryParam("page", strconv.Itoa(int(*page)))
	}
	if pageSize != nil {
		req.SetQueryParam("page_size", strconv.Itoa(int(*pageSize)))
	}
	if serverID != nil {
		req.SetQueryParam("server_id", strconv.Itoa(int(*serverID)))
	}

	resp, err := req.Get("/api/v1/online-players")

	var result OnlinePlayersData
	err = utils.HandleResponse(resp, err, &result)
	if err != nil {
		return nil, err
	}

	return &result, nil
}

// SetPlayersOffline 设置玩家离线状态 - 在玩家退出时调用
func (s *PlayerServerService) SetPlayersOffline(serverID int32, playerIDs []int32) error {
	req := s.client.R().
		SetBody(map[string]interface{}{
			"server_id":  serverID,
			"player_ids": playerIDs,
		})

	resp, err := req.Post("/api/v1/servers/players/offline")

	return utils.HandleResponse(resp, err, nil)
}
