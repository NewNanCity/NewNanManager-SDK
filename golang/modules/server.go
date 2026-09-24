package modules

import (
	"context"
	"strconv"

	"github.com/NewNanCity/NewNanManager-SDK/clients/golang/utils"
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
	return s.ListServersWithContext(context.Background(), page, pageSize, search, onlineOnly)
}

// ListServersWithContext executes ListServers with cancellation scoped to this request.
func (s *ServerService) ListServersWithContext(ctx context.Context, page, pageSize *int32, search *string, onlineOnly *bool) (*ServersListData, error) {
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
	return s.CreateServerWithContext(context.Background(), request)
}

// CreateServerWithContext executes CreateServer with cancellation scoped to this request.
func (s *ServerService) CreateServerWithContext(ctx context.Context, request CreateServerRequest) (*ServerRegistry, error) {
	resp, err := s.client.R().SetContext(ctx).
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
func (s *ServerService) GetServer(id int32, detail bool) (*ServerDetailData, error) {
	return s.GetServerWithContext(context.Background(), id, detail)
}

// GetServerWithContext executes GetServer with cancellation scoped to this request.
func (s *ServerService) GetServerWithContext(ctx context.Context, id int32, detail bool) (*ServerDetailData, error) {
	req := s.client.R().SetContext(ctx)
	if detail {
		req.SetQueryParam("detail", "true")
	}
	resp, err := req.Get("/api/v1/servers/" + strconv.Itoa(int(id)))

	var result ServerDetailData
	err = utils.HandleResponse(resp, err, &result)
	if err != nil {
		return nil, err
	}

	return &result, nil
}

// UpdateServer 更新服务器信息
func (s *ServerService) UpdateServer(id int32, request UpdateServerRequest) (*ServerRegistry, error) {
	return s.UpdateServerWithContext(context.Background(), id, request)
}

// UpdateServerWithContext executes UpdateServer with cancellation scoped to this request.
func (s *ServerService) UpdateServerWithContext(ctx context.Context, id int32, request UpdateServerRequest) (*ServerRegistry, error) {
	resp, err := s.client.R().SetContext(ctx).
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
	return s.DeleteServerWithContext(context.Background(), id)
}

// DeleteServerWithContext executes DeleteServer with cancellation scoped to this request.
func (s *ServerService) DeleteServerWithContext(ctx context.Context, id int32) error {
	resp, err := s.client.R().SetContext(ctx).
		Delete("/api/v1/servers/" + strconv.Itoa(int(id)))

	return utils.HandleResponse(resp, err, nil)
}
