package main

import (
	"github.com/Gk0Wk/NewNanManager/sdk/golang/modules"
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
	client := resty.New()
	client.SetBaseURL(baseURL)
	client.SetHeader("Authorization", "Bearer "+token)
	client.SetHeader("X-API-Token", token)
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
