package modules

import (
	"strconv"

	"github.com/Gk0Wk/NewNanManager/sdk/golang/utils"
	"github.com/go-resty/resty/v2"
)

// ServerService 服务器管理服务
type ServerService struct {
	client *resty.Client
}

// NewServerService 创建服务器服务实例
func NewServerService(client *resty.Client) *ServerService {
	return &ServerService{client: client}
}

// ListServers 获取服务器列表
func (s *ServerService) ListServers(page, pageSize *int32, search *string, onlineOnly *bool) (*ServersListData, error) {
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
	if onlineOnly != nil {
		req.SetQueryParam("online_only", strconv.FormatBool(*onlineOnly))
	}

	resp, err := req.Get("/api/v1/servers")

	var result ServersListData
	err = utils.HandleResponse(resp, err, &result)
	if err != nil {
		return nil, err
	}

	return &result, nil
}

// CreateServer 创建服务器
func (s *ServerService) CreateServer(request CreateServerRequest) (*ServerRegistry, error) {
	resp, err := s.client.R().
		SetBody(request).
		Post("/api/v1/servers")

	var result ServerRegistry
	err = utils.HandleResponse(resp, err, &result)
	if err != nil {
		return nil, err
	}

	return &result, nil
}

// GetServer 获取服务器信息
func (s *ServerService) GetServer(id int32) (*ServerRegistry, error) {
	resp, err := s.client.R().
		Get("/api/v1/servers/" + strconv.Itoa(int(id)))

	var result ServerRegistry
	err = utils.HandleResponse(resp, err, &result)
	if err != nil {
		return nil, err
	}

	return &result, nil
}

// UpdateServer 更新服务器信息
func (s *ServerService) UpdateServer(id int32, request UpdateServerRequest) (*ServerRegistry, error) {
	resp, err := s.client.R().
		SetBody(request).
		Put("/api/v1/servers/" + strconv.Itoa(int(id)))

	var result ServerRegistry
	err = utils.HandleResponse(resp, err, &result)
	if err != nil {
		return nil, err
	}

	return &result, nil
}

// DeleteServer 删除服务器
func (s *ServerService) DeleteServer(id int32) error {
	resp, err := s.client.R().
		Delete("/api/v1/servers/" + strconv.Itoa(int(id)))

	return utils.HandleResponse(resp, err, nil)
}

// GetServerDetail 获取服务器详细信息
func (s *ServerService) GetServerDetail(id int32) (*ServerDetailData, error) {
	resp, err := s.client.R().
		Get("/api/v1/servers/" + strconv.Itoa(int(id)) + "/detail")

	var result ServerDetailData
	err = utils.HandleResponse(resp, err, &result)
	if err != nil {
		return nil, err
	}

	return &result, nil
}
