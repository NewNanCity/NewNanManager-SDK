package nanmanager

import (
	"os"
	"testing"
	"time"
)

// Test configuration
const (
	testBaseURL = "https://your-api-server.com"
	testToken   = "your-test-token-here"
)

func getTestClient(t *testing.T) *NanCityManagerClient {
	baseURL := os.Getenv("NANMANAGER_TEST_BASE_URL")
	if baseURL == "" {
		baseURL = testBaseURL
	}

	token := os.Getenv("NANMANAGER_TEST_TOKEN")
	if token == "" {
		token = testToken
	}

	client := NewNanCityManager(baseURL, token)
	if client == nil {
		t.Fatal("Failed to create test client")
	}

	return client
}

func TestClientCreation(t *testing.T) {
	client := NewNanCityManager(testBaseURL, testToken)
	if client == nil {
		t.Fatal("Expected client to be created, got nil")
	}

	if client.baseURL != testBaseURL {
		t.Errorf("Expected BaseURL to be %s, got %s", testBaseURL, client.baseURL)
	}

	if client.token != testToken {
		t.Errorf("Expected Token to be %s, got %s", testToken, client.token)
	}
}

func TestConfigValidation(t *testing.T) {
	tests := []struct {
		name      string
		config    *Config
		expectErr bool
	}{
		{
			name: "valid config",
			config: &Config{
				BaseURL: "https://example.com",
				Token:   "test-token",
				Timeout: 30 * time.Second,
			},
			expectErr: false,
		},
		{
			name: "missing base URL",
			config: &Config{
				Token:   "test-token",
				Timeout: 30 * time.Second,
			},
			expectErr: true,
		},
		{
			name: "missing token",
			config: &Config{
				BaseURL: "https://example.com",
				Timeout: 30 * time.Second,
			},
			expectErr: true,
		},
		{
			name: "invalid timeout",
			config: &Config{
				BaseURL: "https://example.com",
				Token:   "test-token",
				Timeout: -1 * time.Second,
			},
			expectErr: true,
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			err := tt.config.Validate()
			if tt.expectErr && err == nil {
				t.Error("Expected error but got none")
			}
			if !tt.expectErr && err != nil {
				t.Errorf("Expected no error but got: %v", err)
			}
		})
	}
}

func TestAPIErrorHandling(t *testing.T) {
	apiErr := NewAPIError(404, "Not found", "req-123", "Resource does not exist")

	if apiErr.Code != 404 {
		t.Errorf("Expected code 404, got %d", apiErr.Code)
	}

	if apiErr.Message != "Not found" {
		t.Errorf("Expected message 'Not found', got %s", apiErr.Message)
	}

	expectedError := "API error 404: Not found (Request ID: req-123)"
	if apiErr.Error() != expectedError {
		t.Errorf("Expected error message '%s', got '%s'", expectedError, apiErr.Error())
	}

	// Test IsAPIError
	if _, ok := IsAPIError(apiErr); !ok {
		t.Error("Expected IsAPIError to return true for APIError")
	}
}

func TestBuildInfo(t *testing.T) {
	info := GetBuildInfo()

	if info.Version == "" {
		t.Error("Expected version to be set")
	}

	if info.UserAgent == "" {
		t.Error("Expected user agent to be set")
	}

	if info.APIVersion == "" {
		t.Error("Expected API version to be set")
	}
}

// Integration tests - these require a real API server
func TestIntegrationPlayerList(t *testing.T) {
	if testing.Short() {
		t.Skip("Skipping integration test in short mode")
	}

	client := getTestClient(t)

	players, err := client.ListPlayers(nil, nil, nil, nil, nil)
	if err != nil {
		t.Fatalf("Failed to list players: %v", err)
	}

	if players == nil {
		t.Fatal("Expected players response, got nil")
	}

	t.Logf("Found %d players", players.Total)
}

func TestIntegrationServerList(t *testing.T) {
	if testing.Short() {
		t.Skip("Skipping integration test in short mode")
	}

	client := getTestClient(t)

	servers, err := client.ListServers(nil, nil, nil, nil)
	if err != nil {
		t.Fatalf("Failed to list servers: %v", err)
	}

	if servers == nil {
		t.Fatal("Expected servers response, got nil")
	}

	t.Logf("Found %d servers", servers.Total)
}

func TestIntegrationTownList(t *testing.T) {
	if testing.Short() {
		t.Skip("Skipping integration test in short mode")
	}

	client := getTestClient(t)

	towns, err := client.ListTowns(nil, nil, nil, nil, nil)
	if err != nil {
		t.Fatalf("Failed to list towns: %v", err)
	}

	if towns == nil {
		t.Fatal("Expected towns response, got nil")
	}

	t.Logf("Found %d towns", towns.Total)
}

func TestIntegrationTokenList(t *testing.T) {
	if testing.Short() {
		t.Skip("Skipping integration test in short mode")
	}

	client := getTestClient(t)

	tokens, err := client.ListApiTokens()
	if err != nil {
		t.Fatalf("Failed to list tokens: %v", err)
	}

	if tokens == nil {
		t.Fatal("Expected tokens response, got nil")
	}

	t.Logf("Found %d tokens", len(tokens.Tokens))
}

// Benchmark tests
func BenchmarkClientCreation(b *testing.B) {
	for i := 0; i < b.N; i++ {
		client := NewNanCityManager(testBaseURL, testToken)
		if client == nil {
			b.Fatal("Failed to create client")
		}
	}
}

func BenchmarkConfigValidation(b *testing.B) {
	config := &Config{
		BaseURL: "https://example.com",
		Token:   "test-token",
		Timeout: 30 * time.Second,
	}

	b.ResetTimer()
	for i := 0; i < b.N; i++ {
		_ = config.Validate()
	}
}
