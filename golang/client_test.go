package nanmanager

import (
	"context"
	"errors"
	"net/http"
	"net/http/httptest"
	"sync"
	"sync/atomic"
	"testing"
	"time"
)

func TestConfiguredDeadlineCancelsStalledResponse(t *testing.T) {
	server := httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		<-r.Context().Done()
	}))
	defer server.Close()
	client := NewNanCityManager(server.URL, "synthetic").SetTimeout(50 * time.Millisecond)
	_, err := client.Players.GetPlayer(1)
	if !errors.Is(err, context.DeadlineExceeded) {
		t.Fatalf("stalled request did not report its deadline: %v", err)
	}
}

func TestDefaultRequestsHaveDeadline(t *testing.T) {
	client := NewNanCityManager("http://127.0.0.1", "synthetic")
	if timeout := client.client.GetClient().Timeout; timeout <= 0 || timeout > time.Minute {
		t.Fatalf("request deadline must be bounded, got %s", timeout)
	}
}

func TestConcurrentRequestCancellationDoesNotAffectOtherRequestsOrLegacyCalls(t *testing.T) {
	entered := make(chan string, 2)
	release := make(chan struct{})
	var releaseOnce sync.Once
	server := httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		if r.URL.Path != "/api/v1/players/3" {
			entered <- r.URL.Path
			select {
			case <-r.Context().Done():
				return
			case <-release:
			}
		}
		w.Header().Set("Content-Type", "application/json")
		_, _ = w.Write([]byte(`{"id":3,"name":"audit"}`))
	}))
	defer server.Close()
	defer releaseOnce.Do(func() { close(release) })
	client := NewNanCityManager(server.URL, "synthetic").SetTimeout(2 * time.Second)
	firstContext, cancelFirst := context.WithCancel(context.Background())
	defer cancelFirst()
	secondContext, cancelSecond := context.WithCancel(context.Background())
	defer cancelSecond()
	firstResult, secondResult := make(chan error, 1), make(chan error, 1)
	go func() {
		_, err := client.Players.GetPlayerWithContext(firstContext, 1)
		firstResult <- err
	}()
	go func() {
		_, err := client.Players.GetPlayerWithContext(secondContext, 2)
		secondResult <- err
	}()
	for count := 0; count < 2; count++ {
		select {
		case <-entered:
		case <-time.After(time.Second):
			t.Fatal("concurrent requests did not reach loopback server")
		}
	}
	cancelFirst()
	select {
	case err := <-firstResult:
		if !errors.Is(err, context.Canceled) {
			t.Fatalf("request cancellation lost: %v", err)
		}
	case <-time.After(time.Second):
		t.Fatal("request did not honor cancellation")
	}
	select {
	case err := <-secondResult:
		t.Fatalf("canceling first request completed the independent request: %v", err)
	default:
	}
	releaseOnce.Do(func() { close(release) })
	if err := <-secondResult; err != nil {
		t.Fatalf("independent request failed: %v", err)
	}
	player, err := client.Players.GetPlayer(3)
	if err != nil || player.ID != 3 {
		t.Fatalf("legacy entry point inherited another request's cancellation: %v", err)
	}
}

func TestRequestContextDeadlineOverridesClientTimeout(t *testing.T) {
	server := httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		<-r.Context().Done()
	}))
	defer server.Close()
	client := NewNanCityManager(server.URL, "synthetic").SetTimeout(2 * time.Second)
	ctx, cancel := context.WithTimeout(context.Background(), 50*time.Millisecond)
	defer cancel()
	started := time.Now()
	_, err := client.Players.GetPlayerWithContext(ctx, 1)
	if !errors.Is(err, context.DeadlineExceeded) {
		t.Fatalf("request-specific deadline lost: %v", err)
	}
	if elapsed := time.Since(started); elapsed >= time.Second {
		t.Fatalf("request waited for the shared client timeout: %s", elapsed)
	}
}

func TestAuthorizationIsNotSentToRedirectTarget(t *testing.T) {
	var targetCalls atomic.Int32
	target := httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		targetCalls.Add(1)
		w.Header().Set("Content-Type", "application/json")
		_, _ = w.Write([]byte(`{"id":1,"name":"audit"}`))
	}))
	defer target.Close()
	sourceHeaders := make(chan http.Header, 1)
	source := httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		sourceHeaders <- r.Header.Clone()
		http.Redirect(w, r, target.URL, http.StatusFound)
	}))
	defer source.Close()
	client := NewNanCityManager(source.URL, "synthetic")
	if _, err := client.Players.GetPlayer(1); err == nil {
		t.Error("authenticated API redirect must be rejected")
	}
	headers := <-sourceHeaders
	if headers.Get("Authorization") != "Bearer synthetic" || headers.Get("X-API-Token") != "" {
		t.Error("requests must use Authorization only")
	}
	if targetCalls.Load() != 0 {
		t.Error("redirect target was contacted")
	}
}

func TestAPITokenSchemeSendsOnlyItsCredentialHeader(t *testing.T) {
	headers := make(chan http.Header, 1)
	server := httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		headers <- r.Header.Clone()
		w.Header().Set("Content-Type", "application/json")
		_, _ = w.Write([]byte(`{"id":1,"name":"audit"}`))
	}))
	defer server.Close()

	client := NewNanCityManagerWithAuthScheme(server.URL, "api-secret", AuthSchemeAPIToken)
	if _, err := client.Players.GetPlayer(1); err != nil {
		t.Fatal(err)
	}
	requestHeaders := <-headers
	if requestHeaders.Get("X-API-Token") != "api-secret" {
		t.Fatalf("API token header missing: %v", requestHeaders)
	}
	if requestHeaders.Get("Authorization") != "" {
		t.Fatalf("Bearer header must not be sent with API token scheme: %v", requestHeaders)
	}
}
