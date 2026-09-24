package modules

import (
	"context"
	"strconv"

	"github.com/NewNanCity/NewNanManager-SDK/clients/golang/utils"
	"github.com/go-resty/resty/v2"
)

// TokenService Token管理服务
type TokenService struct {
	client *resty.Client
}

// NewTokenService 创建Token服务实例
func NewTokenService(client *resty.Client) *TokenService {
	return &TokenService{client: client}
}

// ListApiTokens 获取API Token列表
func (s *TokenService) ListApiTokens() (*ListApiTokensData, error) {
	return s.ListApiTokensWithContext(context.Background())
}

// ListApiTokensWithContext executes ListApiTokens with cancellation scoped to this request.
func (s *TokenService) ListApiTokensWithContext(ctx context.Context) (*ListApiTokensData, error) {
	return s.listApiTokens(ctx, nil)
}

// ListApiTokensPage 获取指定页的API Token列表，Page从1开始，PageSize范围1-100。
func (s *TokenService) ListApiTokensPage(request ListApiTokensRequest) (*ListApiTokensData, error) {
	return s.ListApiTokensPageWithContext(context.Background(), request)
}

// ListApiTokensPageWithContext 在调用方上下文中获取指定页的API Token列表。
func (s *TokenService) ListApiTokensPageWithContext(ctx context.Context, request ListApiTokensRequest) (*ListApiTokensData, error) {
	return s.listApiTokens(ctx, &request)
}

func (s *TokenService) listApiTokens(ctx context.Context, request *ListApiTokensRequest) (*ListApiTokensData, error) {
	req := s.client.R().SetContext(ctx)
	if request != nil {
		req.SetQueryParam("page", strconv.Itoa(int(request.Page)))
		req.SetQueryParam("page_size", strconv.Itoa(int(request.PageSize)))
	}
	resp, err := req.Get("/api/v1/tokens")

	var result ListApiTokensData
	err = utils.HandleResponse(resp, err, &result)
	if err != nil {
		return nil, err
	}

	return &result, nil
}

// CreateApiToken 创建API Token
func (s *TokenService) CreateApiToken(request CreateApiTokenRequest) (*CreateApiTokenData, error) {
	return s.CreateApiTokenWithContext(context.Background(), request)
}

// CreateApiTokenWithContext executes CreateApiToken with cancellation scoped to this request.
func (s *TokenService) CreateApiTokenWithContext(ctx context.Context, request CreateApiTokenRequest) (*CreateApiTokenData, error) {
	resp, err := s.client.R().SetContext(ctx).
		SetBody(request).
		Post("/api/v1/tokens")

	var result CreateApiTokenData
	err = utils.HandleResponse(resp, err, &result)
	if err != nil {
		return nil, err
	}

	return &result, nil
}

// GetApiToken 获取API Token详情
func (s *TokenService) GetApiToken(id int32) (*ApiToken, error) {
	return s.GetApiTokenWithContext(context.Background(), id)
}

// GetApiTokenWithContext executes GetApiToken with cancellation scoped to this request.
func (s *TokenService) GetApiTokenWithContext(ctx context.Context, id int32) (*ApiToken, error) {
	resp, err := s.client.R().SetContext(ctx).
		Get("/api/v1/tokens/" + strconv.Itoa(int(id)))

	var result ApiToken
	err = utils.HandleResponse(resp, err, &result)
	if err != nil {
		return nil, err
	}

	return &result, nil
}

// UpdateApiToken 更新API Token
func (s *TokenService) UpdateApiToken(id int32, request UpdateApiTokenRequest) (*ApiToken, error) {
	return s.UpdateApiTokenWithContext(context.Background(), id, request)
}

// UpdateApiTokenWithContext executes UpdateApiToken with cancellation scoped to this request.
func (s *TokenService) UpdateApiTokenWithContext(ctx context.Context, id int32, request UpdateApiTokenRequest) (*ApiToken, error) {
	resp, err := s.client.R().SetContext(ctx).
		SetBody(request).
		Put("/api/v1/tokens/" + strconv.Itoa(int(id)))

	var result ApiToken
	err = utils.HandleResponse(resp, err, &result)
	if err != nil {
		return nil, err
	}

	return &result, nil
}

// DeleteApiToken 删除API Token
func (s *TokenService) DeleteApiToken(id int32) error {
	return s.DeleteApiTokenWithContext(context.Background(), id)
}

// DeleteApiTokenWithContext executes DeleteApiToken with cancellation scoped to this request.
func (s *TokenService) DeleteApiTokenWithContext(ctx context.Context, id int32) error {
	resp, err := s.client.R().SetContext(ctx).
		Delete("/api/v1/tokens/" + strconv.Itoa(int(id)))

	return utils.HandleResponse(resp, err, nil)
}
