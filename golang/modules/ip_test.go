package modules

import (
	"encoding/json"
	"fmt"
	"math"
	"net/http"
	"net/http/httptest"
	"testing"

	"github.com/go-resty/resty/v2"
)

func TestIPStatisticsPreservesQueryStateCounts(t *testing.T) {
	server := httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		w.Header().Set("Content-Type", "application/json")
		_, _ = w.Write([]byte(`{"total_ips":10,"completed_ips":6,"pending_ips":3,"failed_ips":1,"datacenter_ips":2}`))
	}))
	defer server.Close()
	result, err := NewIPService(resty.New().SetBaseURL(server.URL)).GetIPStatistics()
	if err != nil {
		t.Fatal(err)
	}
	encoded, err := json.Marshal(result)
	if err != nil {
		t.Fatal(err)
	}
	var counts map[string]int64
	if err := json.Unmarshal(encoded, &counts); err != nil {
		t.Fatal(err)
	}
	for field, want := range map[string]int64{"completed_ips": 6, "pending_ips": 3, "failed_ips": 1, "datacenter_ips": 2} {
		if got, exists := counts[field]; !exists || got != want {
			t.Errorf("%s lost in SDK response: got %d, want %d", field, got, want)
		}
	}
}

func TestIPStatisticsPreservesCountsBeyondInt32(t *testing.T) {
	for _, count := range []int64{math.MaxInt32 + 1, math.MaxInt64} {
		t.Run(fmt.Sprint(count), func(t *testing.T) {
			want := map[string]int64{
				"total_ips": count, "banned_ips": count, "high_risk_ips": count,
				"proxy_ips": count, "vpn_ips": count, "tor_ips": count,
			}
			server := httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
				w.Header().Set("Content-Type", "application/json")
				_ = json.NewEncoder(w).Encode(want)
			}))
			defer server.Close()
			result, err := NewIPService(resty.New().SetBaseURL(server.URL)).GetIPStatistics()
			if err != nil {
				t.Fatalf("valid i64 statistics failed to decode: %v", err)
			}
			got := map[string]int64{
				"total_ips": int64(result.TotalIPs), "banned_ips": int64(result.BannedIPs),
				"high_risk_ips": int64(result.HighRiskIPs), "proxy_ips": int64(result.ProxyIPs),
				"vpn_ips": int64(result.VPNIPs), "tor_ips": int64(result.TorIPs),
			}
			for field, expected := range want {
				if got[field] != expected {
					t.Errorf("%s: got %d, want %d", field, got[field], expected)
				}
			}
		})
	}
}
