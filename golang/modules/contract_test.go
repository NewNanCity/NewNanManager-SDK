package modules

import (
	"context"
	"encoding/json"
	"fmt"
	"net/http"
	"net/http/httptest"
	"net/url"
	"sync/atomic"
	"testing"

	"github.com/go-resty/resty/v2"
)

func TestNewContractFieldsSurviveJSONRoundTrip(t *testing.T) {
	tests := []struct {
		name  string
		body  string
		model interface{}
		field string
		want  string
	}{
		{"token binding", `{"server_id":7}`, &ApiToken{}, "server_id", "7"},
		{"create binding", `{"server_id":7}`, &CreateApiTokenRequest{}, "server_id", "7"},
		{"update binding", `{"server_id":7}`, &UpdateApiTokenRequest{}, "server_id", "7"},
		{"status source", `{"measurement_type":"pull"}`, &ServerStatus{}, "measurement_type", `"pull"`},
		{"status metric", `{"latency_metric":"rtt"}`, &ServerStatus{}, "latency_metric", `"rtt"`},
		{"history source", `{"measurement_type":"unknown"}`, &MonitorStatRecord{}, "measurement_type", `"unknown"`},
		{"history metric", `{"latency_metric":"legacy"}`, &MonitorStatRecord{}, "latency_metric", `"legacy"`},
		{"next cursor", `{"next_cursor":"opaque&+/#"}`, &MonitorStatsData{}, "next_cursor", `"opaque&+/#"`},
	}
	for _, test := range tests {
		t.Run(test.name, func(t *testing.T) {
			if err := json.Unmarshal([]byte(test.body), test.model); err != nil {
				t.Fatal(err)
			}
			encoded, err := json.Marshal(test.model)
			if err != nil {
				t.Fatal(err)
			}
			var fields map[string]json.RawMessage
			if err := json.Unmarshal(encoded, &fields); err != nil {
				t.Fatal(err)
			}
			var got interface{}
			if err := json.Unmarshal(fields[test.field], &got); err != nil {
				t.Fatalf("lost %s: %v", test.field, err)
			}
			var want interface{}
			if err := json.Unmarshal([]byte(test.want), &want); err != nil {
				t.Fatal(err)
			}
			if got != want {
				t.Fatalf("%s = %v, want %v", test.field, got, want)
			}
		})
	}
}

func TestMonitorPagePreservesQueryAndUnknownMetadata(t *testing.T) {
	queries := make(chan url.Values, 2)
	server := httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		queries <- r.URL.Query()
		w.Header().Set("Content-Type", "application/json")
		_, _ = w.Write([]byte(`{"server_id":7,"stats":[{"timestamp":1,"current_players":0},{"timestamp":2,"current_players":0,"measurement_type":"pull","latency_metric":"rtt"}],"next_cursor":"opaque&+/#"}`))
	}))
	defer server.Close()
	service := NewMonitorService(resty.New().SetBaseURL(server.URL))
	since, duration, limit, cursor := int64(0), int64(86400), int32(10000), "opaque&+/#"
	result, err := service.GetMonitorStatsPageWithContext(context.Background(), 7, MonitorStatsQuery{
		Since: &since, Duration: &duration, Limit: &limit, Cursor: &cursor,
	})
	if err != nil {
		t.Fatal(err)
	}
	query := <-queries
	if len(query) != 4 || query.Get("since") != "0" || query.Get("duration") != "86400" || query.Get("limit") != "10000" || query.Get("cursor") != cursor {
		t.Fatalf("pagination query changed: %v", query)
	}
	if result.NextCursor == nil || *result.NextCursor != cursor {
		t.Fatal("next cursor lost")
	}
	if result.Stats[0].MeasurementType != nil || result.Stats[0].LatencyMetric != nil {
		t.Fatal("unknown metadata was fabricated")
	}
	if *result.Stats[1].MeasurementType != "pull" || *result.Stats[1].LatencyMetric != "rtt" {
		t.Fatal("measurement metadata lost")
	}
	if _, err := service.GetMonitorStats(7, nil, nil); err != nil {
		t.Fatal(err)
	}
	if query := <-queries; len(query) != 0 {
		t.Fatalf("legacy call added query defaults: %v", query)
	}
}

func TestTokenBindingsAreSentAndDecoded(t *testing.T) {
	bodies := make(chan map[string]interface{}, 2)
	server := httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		var body map[string]interface{}
		if err := json.NewDecoder(r.Body).Decode(&body); err != nil {
			t.Error(err)
		}
		bodies <- body
		w.Header().Set("Content-Type", "application/json")
		if r.Method == http.MethodPost {
			_, _ = w.Write([]byte(`{"token_info":{"server_id":7},"token_value":"synthetic"}`))
		} else {
			_, _ = w.Write([]byte(`{"server_id":7}`))
		}
	}))
	defer server.Close()
	service := NewTokenService(resty.New().SetBaseURL(server.URL))
	serverID, active := int32(7), false
	created, err := service.CreateApiToken(CreateApiTokenRequest{Name: "audit", Role: "server", ServerID: &serverID})
	if err != nil {
		t.Fatal(err)
	}
	if created.TokenInfo.ServerID == nil || *created.TokenInfo.ServerID != 7 {
		t.Fatal("created token binding lost")
	}
	if body := <-bodies; len(body) != 3 || body["server_id"] != float64(7) {
		t.Fatalf("create binding lost: %v", body)
	}
	updated, err := service.UpdateApiToken(1, UpdateApiTokenRequest{ServerID: &serverID, Active: &active})
	if err != nil {
		t.Fatal(err)
	}
	if updated.ServerID == nil || *updated.ServerID != 7 {
		t.Fatal("updated token binding lost")
	}
	if body := <-bodies; len(body) != 2 || body["server_id"] != float64(7) || body["active"] != false {
		t.Fatalf("update changed optional fields: %v", body)
	}
}

func TestPeriodicSnapshotsAreSentOnceWithoutChangingNames(t *testing.T) {
	for _, count := range []int{0, 1000} {
		t.Run(fmt.Sprint(count), func(t *testing.T) {
			var calls atomic.Int32
			server := httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
				calls.Add(1)
				var body ValidateRequest
				if err := json.NewDecoder(r.Body).Decode(&body); err != nil {
					t.Error(err)
				}
				if body.Login || body.Players == nil || len(body.Players) != count {
					t.Errorf("snapshot shape changed: login=%t count=%d", body.Login, len(body.Players))
				}
				if count > 0 && body.Players[0].PlayerName != "MiXeD0" {
					t.Error("player name changed")
				}
				w.Header().Set("Content-Type", "application/json")
				_, _ = w.Write([]byte(`{"results":[],"processed_at":1}`))
			}))
			defer server.Close()
			players := make([]PlayerValidateInfo, count)
			for index := range players {
				players[index] = PlayerValidateInfo{PlayerName: fmt.Sprintf("MiXeD%d", index), IP: "192.0.2.1"}
			}
			_, err := NewPlayerService(resty.New().SetBaseURL(server.URL)).Validate(ValidateRequest{Players: players, ServerID: 7, Login: false})
			if err != nil {
				t.Fatal(err)
			}
			if calls.Load() != 1 {
				t.Fatalf("snapshot split into %d calls", calls.Load())
			}
		})
	}
}
