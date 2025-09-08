package modules

import (
	"strconv"

	"github.com/Gk0Wk/NewNanManager/sdk/golang/utils"
	"github.com/go-resty/resty/v2"
)

// MonitorService 监控服务
type MonitorService struct {
	client *resty.Client
}

// NewMonitorService 创建监控服务实例
func NewMonitorService(client *resty.Client) *MonitorService {
	return &MonitorService{client: client}
}

// Heartbeat 服务器心跳
func (s *MonitorService) Heartbeat(serverID int32, request HeartbeatRequest) (*HeartbeatData, error) {
	resp, err := s.client.R().
		SetBody(request).
		Post("/api/v1/monitor/" + strconv.Itoa(int(serverID)) + "/heartbeat")

	var result HeartbeatData
	err = utils.HandleResponse(resp, err, &result)
	if err != nil {
		return nil, err
	}

	return &result, nil
}

// GetMonitorStats 获取监控统计信息
func (s *MonitorService) GetMonitorStats(serverID int32, since *int64, duration *int64) (*MonitorStatsData, error) {
	req := s.client.R()

	if since != nil {
		req.SetQueryParam("since", strconv.FormatInt(*since, 10))
	}
	if duration != nil {
		req.SetQueryParam("duration", strconv.FormatInt(*duration, 10))
	}

	resp, err := req.Get("/api/v1/monitor/" + strconv.Itoa(int(serverID)) + "/stats")

	var result MonitorStatsData
	err = utils.HandleResponse(resp, err, &result)
	if err != nil {
		return nil, err
	}

	return &result, nil
}
