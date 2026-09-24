package utils

import (
	"errors"
	"net/http"
	"net/http/httptest"
	"strings"
	"testing"

	"github.com/go-resty/resty/v2"
)

func TestAPIErrorPreservesHTTPStatus(t *testing.T) {
	server := httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		w.Header().Set("Content-Type", "application/json")
		w.Header().Set("Retry-After", "60")
		w.Header().Set("X-Request-ID", "audit-request")
		w.WriteHeader(http.StatusTooManyRequests)
		_, _ = w.Write([]byte(`{"detail":"rate limited"}`))
	}))
	defer server.Close()
	response, requestErr := resty.New().R().Get(server.URL)
	err := HandleResponse(response, requestErr, nil)
	if err == nil || !strings.Contains(err.Error(), "429") || !strings.Contains(err.Error(), "rate limited") {
		t.Fatalf("status or detail missing: %v", err)
	}
	var httpError *HTTPError
	if !errors.As(err, &httpError) || httpError.StatusCode != 429 || httpError.RetryAfter != "60" || httpError.RequestID != "audit-request" {
		t.Fatalf("typed response metadata missing: %v", err)
	}
}

func TestTemplateErrorResponsePreservesMachineMetadata(t *testing.T) {
	server := httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		w.Header().Set("Content-Type", "application/json")
		w.WriteHeader(http.StatusBadRequest)
		_, _ = w.Write([]byte(`{"code":1004000,"message":"invalid request parameters","request_id":"body-request","trace_id":"trace-1","error":{"category":"invalid_argument","code":"common.invalid_argument"}}`))
	}))
	defer server.Close()
	response, requestErr := resty.New().R().Get(server.URL)
	err := HandleResponse(response, requestErr, nil)
	var httpError *HTTPError
	if !errors.As(err, &httpError) || httpError.Detail != "invalid request parameters" || httpError.Code != 1004000 || httpError.Category != "invalid_argument" || httpError.MachineCode != "common.invalid_argument" || httpError.RequestID != "body-request" || httpError.TraceID != "trace-1" {
		t.Fatalf("template error metadata missing: %v", err)
	}
}
