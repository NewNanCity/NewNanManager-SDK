package modules

import (
	"strconv"

	"github.com/Gk0Wk/NewNanManager/sdk/golang/utils"
	"github.com/go-resty/resty/v2"
)

// IPService IP管理服务
type IPService struct {
	client *resty.Client
}

// NewIPService 创建IP服务实例
func NewIPService(client *resty.Client) *IPService {
	return &IPService{client: client}
}

// GetIPInfo 获取IP信息（包含风险信息）
func (s *IPService) GetIPInfo(ip string) (*IPInfo, error) {
	resp, err := s.client.R().
		Get("/api/v1/ips/" + ip)

	var result IPInfo
	err = utils.HandleResponse(resp, err, &result)
	if err != nil {
		return nil, err
	}

	return &result, nil
}

// BanIP 封禁IP（支持批量）
func (s *IPService) BanIP(request BanIPRequest) error {
	resp, err := s.client.R().
		SetBody(request).
		Post("/api/v1/ips/ban")

	return utils.HandleResponse(resp, err, nil)
}

// UnbanIP 解封IP（支持批量）
func (s *IPService) UnbanIP(request UnbanIPRequest) error {
	resp, err := s.client.R().
		SetBody(request).
		Post("/api/v1/ips/unban")

	return utils.HandleResponse(resp, err, nil)
}

// GetBannedIPs 获取被封禁的IP列表
func (s *IPService) GetBannedIPs(page, pageSize *int32) (*BannedIPsData, error) {
	req := s.client.R()

	if page != nil {
		req.SetQueryParam("page", strconv.Itoa(int(*page)))
	}
	if pageSize != nil {
		req.SetQueryParam("page_size", strconv.Itoa(int(*pageSize)))
	}

	resp, err := req.Get("/api/v1/ips/banned")

	var result BannedIPsData
	err = utils.HandleResponse(resp, err, &result)
	if err != nil {
		return nil, err
	}

	return &result, nil
}

// ListIPs 获取IP列表
func (s *IPService) ListIPs(page, pageSize *int32, bannedOnly *bool, minThreatLevel *ThreatLevel, minRiskScore *int32) (*BannedIPsData, error) {
	req := s.client.R()

	if page != nil {
		req.SetQueryParam("page", strconv.Itoa(int(*page)))
	}
	if pageSize != nil {
		req.SetQueryParam("page_size", strconv.Itoa(int(*pageSize)))
	}
	if bannedOnly != nil {
		req.SetQueryParam("banned_only", strconv.FormatBool(*bannedOnly))
	}
	if minThreatLevel != nil {
		req.SetQueryParam("min_threat_level", strconv.Itoa(int(*minThreatLevel)))
	}
	if minRiskScore != nil {
		req.SetQueryParam("min_risk_score", strconv.Itoa(int(*minRiskScore)))
	}

	resp, err := req.Get("/api/v1/ips")

	var result BannedIPsData
	err = utils.HandleResponse(resp, err, &result)
	if err != nil {
		return nil, err
	}

	return &result, nil
}

// GetSuspiciousIPs 获取可疑IP列表
func (s *IPService) GetSuspiciousIPs(page, pageSize *int32) (*SuspiciousIPsData, error) {
	req := s.client.R()

	if page != nil {
		req.SetQueryParam("page", strconv.Itoa(int(*page)))
	}
	if pageSize != nil {
		req.SetQueryParam("page_size", strconv.Itoa(int(*pageSize)))
	}

	resp, err := req.Get("/api/v1/ips/suspicious")

	var result SuspiciousIPsData
	err = utils.HandleResponse(resp, err, &result)
	if err != nil {
		return nil, err
	}

	return &result, nil
}

// GetHighRiskIPs 获取高风险IP列表
func (s *IPService) GetHighRiskIPs(page, pageSize *int32) (*HighRiskIPsData, error) {
	req := s.client.R()

	if page != nil {
		req.SetQueryParam("page", strconv.Itoa(int(*page)))
	}
	if pageSize != nil {
		req.SetQueryParam("page_size", strconv.Itoa(int(*pageSize)))
	}

	resp, err := req.Get("/api/v1/ips/high-risk")

	var result HighRiskIPsData
	err = utils.HandleResponse(resp, err, &result)
	if err != nil {
		return nil, err
	}

	return &result, nil
}

// GetIPStatistics 获取IP统计信息
func (s *IPService) GetIPStatistics() (*IPStatistics, error) {
	resp, err := s.client.R().
		Get("/api/v1/ips/statistics")

	var result IPStatistics
	err = utils.HandleResponse(resp, err, &result)
	if err != nil {
		return nil, err
	}

	return &result, nil
}
