package modules

import (
	"context"
	"errors"
	"io"
	"net/http"
	"net/url"
	"reflect"
	"strings"
	"testing"
	"time"

	"github.com/NewNanCity/NewNanManager-SDK/clients/golang/utils"
	"github.com/go-resty/resty/v2"
)

type tokenPaginationTransport func(*http.Request) (*http.Response, error)

func (transport tokenPaginationTransport) RoundTrip(request *http.Request) (*http.Response, error) {
	return transport(request)
}

func TestTokenPaginationQueryAndLegacyCalls(t *testing.T) {
	var query url.Values
	client := resty.New().SetBaseURL("https://audit.invalid").SetTransport(tokenPaginationTransport(func(request *http.Request) (*http.Response, error) {
		if request.Method != http.MethodGet || request.URL.Path != "/api/v1/tokens" {
			t.Fatalf("unexpected request: %s %s", request.Method, request.URL.Path)
		}
		query = request.URL.Query()
		return &http.Response{StatusCode: http.StatusOK, Header: http.Header{"Content-Type": {"application/json"}},
			Body: io.NopCloser(strings.NewReader(`{"tokens":[],"total":4294967296,"page":2,"page_size":100}`))}, nil
	}))
	service := NewTokenService(client)
	page := ListApiTokensRequest{Page: 2, PageSize: 100}
	tests := []struct {
		name string
		call func() (*ListApiTokensData, error)
		want url.Values
	}{
		{"legacy", service.ListApiTokens, url.Values{}},
		{"legacy context", func() (*ListApiTokensData, error) { return service.ListApiTokensWithContext(context.Background()) }, url.Values{}},
		{"page", func() (*ListApiTokensData, error) { return service.ListApiTokensPage(page) }, url.Values{"page": {"2"}, "page_size": {"100"}}},
		{"page context", func() (*ListApiTokensData, error) {
			return service.ListApiTokensPageWithContext(context.Background(), page)
		}, url.Values{"page": {"2"}, "page_size": {"100"}}},
	}
	for _, test := range tests {
		t.Run(test.name, func(t *testing.T) {
			result, err := test.call()
			if err != nil {
				t.Fatal(err)
			}
			if !reflect.DeepEqual(query, test.want) {
				t.Fatalf("query = %v, want %v", query, test.want)
			}
			if len(result.Tokens) != 0 || result.Total != 4294967296 || result.Page != 2 || result.PageSize != 100 {
				t.Fatalf("pagination metadata lost: %+v", result)
			}
		})
	}
}

func TestTokenPaginationPreservesServiceValidationError(t *testing.T) {
	client := resty.New().SetBaseURL("https://audit.invalid").SetTransport(tokenPaginationTransport(func(request *http.Request) (*http.Response, error) {
		if query := request.URL.Query(); query.Get("page") != "0" || query.Get("page_size") != "101" {
			t.Fatalf("explicit pagination values were changed: %v", query)
		}
		return &http.Response{StatusCode: http.StatusBadRequest, Header: http.Header{"Content-Type": {"application/json"}, "X-Request-Id": {"pagination-test"}},
			Body: io.NopCloser(strings.NewReader(`{"detail":"invalid pagination"}`))}, nil
	}))
	result, err := NewTokenService(client).ListApiTokensPage(ListApiTokensRequest{Page: 0, PageSize: 101})
	var responseError *utils.HTTPError
	if result != nil || !errors.As(err, &responseError) || responseError.StatusCode != http.StatusBadRequest || responseError.RequestID != "pagination-test" {
		t.Fatalf("validation error lost: result=%+v error=%v", result, err)
	}
}

func TestTokenPaginationCancelsInFlightRequest(t *testing.T) {
	started := make(chan struct{})
	client := resty.New().SetBaseURL("https://audit.invalid").SetTransport(tokenPaginationTransport(func(request *http.Request) (*http.Response, error) {
		close(started)
		<-request.Context().Done()
		return nil, request.Context().Err()
	}))
	ctx, cancel := context.WithCancel(context.Background())
	defer cancel()
	done := make(chan error, 1)
	go func() {
		_, err := NewTokenService(client).ListApiTokensPageWithContext(ctx, ListApiTokensRequest{Page: 2, PageSize: 100})
		done <- err
	}()
	select {
	case <-started:
	case <-time.After(2 * time.Second):
		t.Fatal("request did not start")
	}
	cancel()
	select {
	case err := <-done:
		if !errors.Is(err, context.Canceled) {
			t.Fatalf("cancellation lost: %v", err)
		}
	case <-time.After(2 * time.Second):
		t.Fatal("request did not stop after cancellation")
	}
}
