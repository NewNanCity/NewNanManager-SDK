package nanmanager

import (
	"time"

	"github.com/NewNanCity/NewNanManager-SDK/clients/golang/modules"
	"github.com/go-resty/resty/v2"
)

// NanCityManagerClient NewNanManager API客户端
type NanCityManagerClient struct {
	client *resty.Client

	// 所有服务模块
	Players       *modules.PlayerService
	Servers       *modules.ServerService
	Towns         *modules.TownService
	Monitor       *modules.MonitorService
	Tokens        *modules.TokenService
	IPs           *modules.IPService
	PlayerServers *modules.PlayerServerService
}

// NewNanCityManager 创建新的API客户端
func NewNanCityManager(baseURL, token string) *NanCityManagerClient {
	return NewNanCityManagerWithAuthScheme(baseURL, token, AuthSchemeBearer)
}

// NewNanCityManagerWithAuthScheme creates a client using exactly one supported
// credential header. The legacy constructor remains Bearer-compatible.
func NewNanCityManagerWithAuthScheme(baseURL, token string, scheme AuthScheme) *NanCityManagerClient {
	client := resty.New()
	client.SetBaseURL(baseURL)
	client.SetTimeout(30 * time.Second)
	client.SetRedirectPolicy(resty.NoRedirectPolicy())
	switch scheme {
	case AuthSchemeAPIToken:
		client.SetHeader("X-API-Token", token)
	default:
		client.SetHeader("Authorization", "Bearer "+token)
	}
	client.SetHeader("Content-Type", "application/json")

	return &NanCityManagerClient{
		client:        client,
		Players:       modules.NewPlayerService(client),
		Servers:       modules.NewServerService(client),
		Towns:         modules.NewTownService(client),
		Monitor:       modules.NewMonitorService(client),
		Tokens:        modules.NewTokenService(client),
		IPs:           modules.NewIPService(client),
		PlayerServers: modules.NewPlayerServerService(client),
	}
}

// SetTimeout sets the deadline for subsequent requests. Configure before concurrent use.
func (c *NanCityManagerClient) SetTimeout(timeout time.Duration) *NanCityManagerClient {
	c.client.SetTimeout(timeout)
	return c
}
