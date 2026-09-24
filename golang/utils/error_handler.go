package utils

import (
	"encoding/json"
	"fmt"
	"net/http"

	"github.com/go-resty/resty/v2"
)

// ErrorDetails is the stable machine-readable error registry entry.
type ErrorDetails struct {
	Category string `json:"category"`
	Code     string `json:"code"`
}

// ErrorResponse is the template error response. Detail remains a migration
// fallback for servers that still emit the legacy body.
type ErrorResponse struct {
	Code      int          `json:"code"`
	Message   string       `json:"message"`
	Detail    string       `json:"detail"`
	RequestID string       `json:"request_id"`
	TraceID   string       `json:"trace_id"`
	Error     ErrorDetails `json:"error"`
}

// HTTPError preserves response metadata for errors.As and caller retry decisions.
type HTTPError struct {
	StatusCode  int
	Detail      string
	Code        int
	Category    string
	MachineCode string
	RequestID   string
	TraceID     string
	RetryAfter  string
}

func (e *HTTPError) Error() string {
	return fmt.Sprintf("HTTP %d: %s", e.StatusCode, e.Detail)
}

// HandleResponse 统一的API响应处理函数
// HandleResponse parses the template error body and accepts legacy detail.
func HandleResponse(resp *resty.Response, err error, result interface{}) error {
	if err != nil {
		return fmt.Errorf("request failed: %w", err)
	}

	if resp.StatusCode() >= 300 {
		// Prefer the template message/error fields and fall back to detail.
		var errorData ErrorResponse
		if err := json.Unmarshal(resp.Body(), &errorData); err != nil {
			errorData = ErrorResponse{}
		}
		message := errorData.Message
		if message == "" {
			message = errorData.Detail
		}
		if message == "" {
			message = http.StatusText(resp.StatusCode())
		}
		requestID := resp.Header().Get("X-Request-ID")
		if requestID == "" {
			requestID = errorData.RequestID
		}
		return &HTTPError{
			StatusCode:  resp.StatusCode(),
			Detail:      message,
			Code:        errorData.Code,
			Category:    errorData.Error.Category,
			MachineCode: errorData.Error.Code,
			RequestID:   requestID,
			TraceID:     errorData.TraceID,
			RetryAfter:  resp.Header().Get("Retry-After"),
		}
	}

	if result != nil {
		if err := json.Unmarshal(resp.Body(), result); err != nil {
			return fmt.Errorf("failed to parse response: %w", err)
		}
	}

	return nil
}
