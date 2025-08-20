package nanmanager

import (
	"fmt"
	"net/http"
)

// Error types
var (
	// ErrInvalidConfig indicates invalid configuration
	ErrInvalidConfig = fmt.Errorf("invalid configuration")
	
	// ErrNetworkError indicates network-related errors
	ErrNetworkError = fmt.Errorf("network error")
	
	// ErrTimeout indicates request timeout
	ErrTimeout = fmt.Errorf("request timeout")
	
	// ErrUnauthorized indicates authentication failure
	ErrUnauthorized = fmt.Errorf("unauthorized")
	
	// ErrForbidden indicates insufficient permissions
	ErrForbidden = fmt.Errorf("forbidden")
	
	// ErrNotFound indicates resource not found
	ErrNotFound = fmt.Errorf("resource not found")
	
	// ErrServerError indicates server-side error
	ErrServerError = fmt.Errorf("server error")
)

// APIError represents an API error response
type APIError struct {
	Code      int    `json:"code"`
	Message   string `json:"message"`
	RequestID string `json:"request_id,omitempty"`
	Details   string `json:"details,omitempty"`
	
	// HTTP status code
	StatusCode int `json:"-"`
}

// Error implements the error interface
func (e *APIError) Error() string {
	if e.RequestID != "" {
		return fmt.Sprintf("API error %d: %s (Request ID: %s)", e.Code, e.Message, e.RequestID)
	}
	return fmt.Sprintf("API error %d: %s", e.Code, e.Message)
}

// IsAPIError checks if an error is an APIError
func IsAPIError(err error) (*APIError, bool) {
	if apiErr, ok := err.(*APIError); ok {
		return apiErr, true
	}
	return nil, false
}

// NewAPIError creates a new API error
func NewAPIError(code int, message, requestID, details string) *APIError {
	return &APIError{
		Code:      code,
		Message:   message,
		RequestID: requestID,
		Details:   details,
	}
}

// HTTPError represents an HTTP-level error
type HTTPError struct {
	StatusCode int
	Status     string
	Message    string
	URL        string
}

// Error implements the error interface
func (e *HTTPError) Error() string {
	return fmt.Sprintf("HTTP %d %s: %s (URL: %s)", e.StatusCode, e.Status, e.Message, e.URL)
}

// NewHTTPError creates a new HTTP error
func NewHTTPError(statusCode int, status, message, url string) *HTTPError {
	return &HTTPError{
		StatusCode: statusCode,
		Status:     status,
		Message:    message,
		URL:        url,
	}
}

// MapHTTPStatusToError maps HTTP status codes to appropriate errors
func MapHTTPStatusToError(statusCode int) error {
	switch statusCode {
	case http.StatusUnauthorized:
		return ErrUnauthorized
	case http.StatusForbidden:
		return ErrForbidden
	case http.StatusNotFound:
		return ErrNotFound
	case http.StatusInternalServerError, http.StatusBadGateway, http.StatusServiceUnavailable:
		return ErrServerError
	default:
		if statusCode >= 400 && statusCode < 500 {
			return fmt.Errorf("client error: HTTP %d", statusCode)
		} else if statusCode >= 500 {
			return ErrServerError
		}
		return nil
	}
}
