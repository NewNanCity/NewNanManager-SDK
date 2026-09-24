package modules

import (
	"context"
	"io"
	"net/http"
	"net/http/httptest"
	"sync/atomic"
	"testing"

	"github.com/go-resty/resty/v2"
)

const validSessionID = "0123456789abcdef0123456789abcdef"

func TestSessionOperationsSendFencingHeaders(t *testing.T) {
	var calls atomic.Int32
	server := httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		calls.Add(1)
		if got := r.Header.Get("X-NNM-Session-ID"); got != validSessionID {
			t.Errorf("session id = %q", got)
		}
		if got := r.Header.Get("X-NNM-Session-Epoch"); got != "7" {
			t.Errorf("session epoch = %q", got)
		}
		w.Header().Set("Content-Type", "application/json")
		switch r.URL.Path {
		case "/api/v1/players/validate":
			_, _ = w.Write([]byte(`{"results":[],"processed_at":1}`))
		case "/api/v1/monitor/7/heartbeat":
			_, _ = w.Write([]byte(`{"received_at":1,"response_at":2,"expire_duration_ms":30000}`))
		default:
			_, _ = w.Write([]byte(`{}`))
		}
	}))
	defer server.Close()

	client := resty.New().SetBaseURL(server.URL)
	session := SessionContext{ID: validSessionID, Epoch: 7}
	if _, err := NewPlayerService(client).ValidateWithSession(ValidateRequest{Players: []PlayerValidateInfo{}, ServerID: 7}, session); err != nil {
		t.Fatal(err)
	}
	if _, err := NewMonitorService(client).HeartbeatWithSession(7, HeartbeatRequest{CurrentPlayers: 0, MaxPlayers: 20}, session); err != nil {
		t.Fatal(err)
	}
	if err := NewPlayerServerService(client).SetPlayersOfflineWithSession(7, []int32{1}, session); err != nil {
		t.Fatal(err)
	}
	if calls.Load() != 3 {
		t.Fatalf("session operations made %d calls", calls.Load())
	}
}

func TestInvalidSessionIsRejectedBeforeNetwork(t *testing.T) {
	var calls atomic.Int32
	server := httptest.NewServer(http.HandlerFunc(func(http.ResponseWriter, *http.Request) {
		calls.Add(1)
	}))
	defer server.Close()
	client := resty.New().SetBaseURL(server.URL)

	_, err := NewPlayerService(client).ValidateWithContextAndSession(
		context.Background(),
		ValidateRequest{Players: []PlayerValidateInfo{}, ServerID: 7},
		SessionContext{ID: " ", Epoch: 1},
	)
	if err == nil {
		t.Fatal("blank session id was accepted")
	}
	if calls.Load() != 0 {
		t.Fatal("invalid session reached the network")
	}

	_, err = NewPlayerService(client).ValidateWithContextAndSession(
		context.Background(),
		ValidateRequest{Players: []PlayerValidateInfo{}, ServerID: 7},
		SessionContext{ID: "too-short", Epoch: 1},
	)
	if err == nil {
		t.Fatal("short session id was accepted")
	}
	if calls.Load() != 0 {
		t.Fatal("short session id reached the network")
	}
}

func TestCreateServerSessionValidatesResponse(t *testing.T) {
	server := httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		if r.URL.Path != "/api/v1/monitor/7/session" {
			t.Fatalf("unexpected path: %s", r.URL.Path)
		}
		body, err := io.ReadAll(r.Body)
		if err != nil {
			t.Fatalf("read request body: %v", err)
		}
		if string(body) != "{}" {
			t.Fatalf("session request body = %q, want {}", body)
		}
		w.Header().Set("Content-Type", "application/json")
		_, _ = w.Write([]byte(`{"session_id":"0123456789abcdef0123456789abcdef","session_epoch":8}`))
	}))
	defer server.Close()

	session, err := NewMonitorService(resty.New().SetBaseURL(server.URL)).CreateServerSession(7)
	if err != nil {
		t.Fatal(err)
	}
	if session.ID != validSessionID || session.Epoch != 8 {
		t.Fatalf("unexpected session: %+v", session)
	}
}
