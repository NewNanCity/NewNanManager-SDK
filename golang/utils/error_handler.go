package utils

import (
	"encoding/json"
	"fmt"

	"github.com/go-resty/resty/v2"
)

// ErrorResponse 统一错误响应格式
type ErrorResponse struct {
	Detail string `json:"detail"`
}

// HandleResponse 统一的API响应处理函数
// 处理服务端返回的 {"detail": "xxx"} 格式错误响应
func HandleResponse(resp *resty.Response, err error, result interface{}) error {
	if err != nil {
		return fmt.Errorf("request failed: %w", err)
	}

	if resp.StatusCode() >= 400 {
		// 尝试解析新的错误响应格式 {"detail": "..."}
		var errorData ErrorResponse
		if err := json.Unmarshal(resp.Body(), &errorData); err != nil {
			return fmt.Errorf("HTTP %d: %s", resp.StatusCode(), string(resp.Body()))
		}
		if errorData.Detail != "" {
			return fmt.Errorf("API error: %s", errorData.Detail)
		}
		return fmt.Errorf("HTTP %d: %s", resp.StatusCode(), string(resp.Body()))
	}

	if result != nil {
		if err := json.Unmarshal(resp.Body(), result); err != nil {
			return fmt.Errorf("failed to parse response: %w", err)
		}
	}

	return nil
}
