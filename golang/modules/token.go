package modules

import (
	"strconv"

	"github.com/go-resty/resty/v2"
	"github.com/Gk0Wk/NewNanManager/sdk/golang/utils"
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
	resp, err := s.client.R().
		Get("/api/v1/tokens")

	var result ListApiTokensData
	err = utils.HandleResponse(resp, err, &result)
	if err != nil {
		return nil, err
	}

	return &result, nil
}

// CreateApiToken 创建API Token
func (s *TokenService) CreateApiToken(request CreateApiTokenRequest) (*CreateApiTokenData, error) {
	resp, err := s.client.R().
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
	resp, err := s.client.R().
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
	resp, err := s.client.R().
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
	resp, err := s.client.R().
		Delete("/api/v1/tokens/" + strconv.Itoa(int(id)))

	return utils.HandleResponse(resp, err, nil)
}
