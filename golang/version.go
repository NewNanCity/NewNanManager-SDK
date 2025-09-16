package nanmanager

// Version information
const (
	// Version is the current version of the SDK
	Version = "1.0.0"
	
	// UserAgent is the default user agent string
	UserAgent = "NewNanManager-Go-SDK/" + Version
	
	// APIVersion is the supported API version
	APIVersion = "v1"
)

// BuildInfo contains build information
type BuildInfo struct {
	Version    string `json:"version"`
	UserAgent  string `json:"user_agent"`
	APIVersion string `json:"api_version"`
	GoVersion  string `json:"go_version"`
}

// GetBuildInfo returns build information
func GetBuildInfo() BuildInfo {
	return BuildInfo{
		Version:    Version,
		UserAgent:  UserAgent,
		APIVersion: APIVersion,
		GoVersion:  "go1.21+",
	}
}
