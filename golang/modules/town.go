package modules

import (
	"strconv"

	"github.com/Gk0Wk/NewNanManager/sdk/golang/utils"
	"github.com/go-resty/resty/v2"
)

// TownService 城镇管理服务
type TownService struct {
	client *resty.Client
}

// NewTownService 创建城镇服务实例
func NewTownService(client *resty.Client) *TownService {
	return &TownService{client: client}
}

// ListTowns 获取城镇列表
func (s *TownService) ListTowns(page, pageSize *int32, search *string, minLevel, maxLevel *int32) (*TownsListData, error) {
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
	if minLevel != nil {
		req.SetQueryParam("min_level", strconv.Itoa(int(*minLevel)))
	}
	if maxLevel != nil {
		req.SetQueryParam("max_level", strconv.Itoa(int(*maxLevel)))
	}

	resp, err := req.Get("/api/v1/towns")

	var result TownsListData
	err = utils.HandleResponse(resp, err, &result)
	if err != nil {
		return nil, err
	}

	return &result, nil
}

// CreateTown 创建城镇
func (s *TownService) CreateTown(request CreateTownRequest) (*Town, error) {
	resp, err := s.client.R().
		SetBody(request).
		Post("/api/v1/towns")

	var result Town
	err = utils.HandleResponse(resp, err, &result)
	if err != nil {
		return nil, err
	}

	return &result, nil
}

// GetTown 获取城镇详情
func (s *TownService) GetTown(id int32, detail bool) (*TownDetailResponse, error) {
	req := s.client.R()

	if detail {
		req.SetQueryParam("detail", "true")
	}

	resp, err := req.Get("/api/v1/towns/" + strconv.Itoa(int(id)))

	var result TownDetailResponse
	err = utils.HandleResponse(resp, err, &result)
	if err != nil {
		return nil, err
	}

	return &result, nil
}

// UpdateTown 更新城镇信息
func (s *TownService) UpdateTown(id int32, request UpdateTownRequest) (*Town, error) {
	resp, err := s.client.R().
		SetBody(request).
		Put("/api/v1/towns/" + strconv.Itoa(int(id)))

	var result Town
	err = utils.HandleResponse(resp, err, &result)
	if err != nil {
		return nil, err
	}

	return &result, nil
}

// DeleteTown 删除城镇
func (s *TownService) DeleteTown(id int32) error {
	resp, err := s.client.R().
		Delete("/api/v1/towns/" + strconv.Itoa(int(id)))

	return utils.HandleResponse(resp, err, nil)
}

// GetTownMembers 获取城镇成员列表
func (s *TownService) GetTownMembers(townID int32, page, pageSize *int32) (*TownMembersData, error) {
	req := s.client.R()

	if page != nil {
		req.SetQueryParam("page", strconv.Itoa(int(*page)))
	}
	if pageSize != nil {
		req.SetQueryParam("page_size", strconv.Itoa(int(*pageSize)))
	}

	resp, err := req.Get("/api/v1/towns/" + strconv.Itoa(int(townID)) + "/members")

	var result TownMembersData
	err = utils.HandleResponse(resp, err, &result)
	if err != nil {
		return nil, err
	}

	return &result, nil
}
