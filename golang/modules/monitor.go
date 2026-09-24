package modules

import (
	"context"
	"strconv"

	"github.com/NewNanCity/NewNanManager-SDK/clients/golang/utils"
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

// CreateServerSession issues a new fencing epoch for a server instance.
func (s *MonitorService) CreateServerSession(serverID int32) (*SessionContext, error) {
	return s.CreateServerSessionWithContext(context.Background(), serverID)
}

// CreateServerSessionWithContext issues a session while honoring cancellation.
func (s *MonitorService) CreateServerSessionWithContext(ctx context.Context, serverID int32) (*SessionContext, error) {
	resp, err := s.client.R().SetContext(ctx).
		SetBody(map[string]any{}).
		Post("/api/v1/monitor/" + strconv.Itoa(int(serverID)) + "/session")

	var result ServerSessionResponse
	err = utils.HandleResponse(resp, err, &result)
	if err != nil {
		return nil, err
	}
	session := SessionContext{ID: result.SessionID, Epoch: result.SessionEpoch}
	if err := session.Validate(); err != nil {
		return nil, err
	}
	return &session, nil
}

// Heartbeat 服务器心跳
func (s *MonitorService) Heartbeat(serverID int32, request HeartbeatRequest) (*HeartbeatData, error) {
	return s.HeartbeatWithContext(context.Background(), serverID, request)
}

// HeartbeatWithContext executes Heartbeat with cancellation scoped to this request.
func (s *MonitorService) HeartbeatWithContext(ctx context.Context, serverID int32, request HeartbeatRequest) (*HeartbeatData, error) {
	return s.heartbeatWithContext(ctx, serverID, request, nil)
}

// HeartbeatWithSession sends the server session fencing headers.
func (s *MonitorService) HeartbeatWithSession(serverID int32, request HeartbeatRequest, session SessionContext) (*HeartbeatData, error) {
	return s.HeartbeatWithContextAndSession(context.Background(), serverID, request, session)
}

// HeartbeatWithContextAndSession scopes cancellation and sends session fencing headers.
func (s *MonitorService) HeartbeatWithContextAndSession(ctx context.Context, serverID int32, request HeartbeatRequest, session SessionContext) (*HeartbeatData, error) {
	return s.heartbeatWithContext(ctx, serverID, request, &session)
}

func (s *MonitorService) heartbeatWithContext(ctx context.Context, serverID int32, request HeartbeatRequest, session *SessionContext) (*HeartbeatData, error) {
	req := s.client.R().SetContext(ctx)
	if session != nil {
		if err := applySessionHeaders(req, *session); err != nil {
			return nil, err
		}
	}
	resp, err := req.
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
	return s.GetMonitorStatsWithContext(context.Background(), serverID, since, duration)
}

// GetMonitorStatsWithContext executes GetMonitorStats with cancellation scoped to this request.
func (s *MonitorService) GetMonitorStatsWithContext(ctx context.Context, serverID int32, since *int64, duration *int64) (*MonitorStatsData, error) {
	return s.GetMonitorStatsPageWithContext(ctx, serverID, MonitorStatsQuery{Since: since, Duration: duration})
}

// GetMonitorStatsPage returns one page; a non-nil NextCursor indicates more records.
func (s *MonitorService) GetMonitorStatsPage(serverID int32, query MonitorStatsQuery) (*MonitorStatsData, error) {
	return s.GetMonitorStatsPageWithContext(context.Background(), serverID, query)
}

// GetMonitorStatsPageWithContext scopes cancellation to this page request.
func (s *MonitorService) GetMonitorStatsPageWithContext(ctx context.Context, serverID int32, query MonitorStatsQuery) (*MonitorStatsData, error) {
	req := s.client.R().SetContext(ctx)

	if query.Since != nil {
		req.SetQueryParam("since", strconv.FormatInt(*query.Since, 10))
	}
	if query.Duration != nil {
		req.SetQueryParam("duration", strconv.FormatInt(*query.Duration, 10))
	}
	if query.Limit != nil {
		req.SetQueryParam("limit", strconv.FormatInt(int64(*query.Limit), 10))
	}
	if query.Cursor != nil {
		req.SetQueryParam("cursor", *query.Cursor)
	}

	resp, err := req.Get("/api/v1/monitor/" + strconv.Itoa(int(serverID)) + "/stats")

	var result MonitorStatsData
	err = utils.HandleResponse(resp, err, &result)
	if err != nil {
		return nil, err
	}

	return &result, nil
}
