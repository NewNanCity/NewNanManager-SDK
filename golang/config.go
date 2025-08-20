package nanmanager

import (
	"fmt"
	"net/url"
	"time"
)

// Config holds the configuration for the NewNanManager client
type Config struct {
	// BaseURL is the base URL of the NewNanManager API
	BaseURL string
	
	// Token is the API token for authentication
	Token string
	
	// Timeout is the request timeout duration
	Timeout time.Duration
	
	// UserAgent is the user agent string to use for requests
	UserAgent string
	
	// MaxRetries is the maximum number of retries for failed requests
	MaxRetries int
	
	// RetryDelay is the delay between retries
	RetryDelay time.Duration
	
	// Debug enables debug logging
	Debug bool
	
	// InsecureSkipVerify skips TLS certificate verification
	InsecureSkipVerify bool
}

// DefaultConfig returns a default configuration
func DefaultConfig() *Config {
	return &Config{
		Timeout:    30 * time.Second,
		UserAgent:  UserAgent,
		MaxRetries: 3,
		RetryDelay: 1 * time.Second,
		Debug:      false,
	}
}

// NewConfig creates a new configuration with the given base URL and token
func NewConfig(baseURL, token string) *Config {
	config := DefaultConfig()
	config.BaseURL = baseURL
	config.Token = token
	return config
}

// Validate validates the configuration
func (c *Config) Validate() error {
	if c.BaseURL == "" {
		return fmt.Errorf("%w: base URL is required", ErrInvalidConfig)
	}
	
	if c.Token == "" {
		return fmt.Errorf("%w: token is required", ErrInvalidConfig)
	}
	
	// Validate base URL format
	if _, err := url.Parse(c.BaseURL); err != nil {
		return fmt.Errorf("%w: invalid base URL format: %v", ErrInvalidConfig, err)
	}
	
	if c.Timeout <= 0 {
		return fmt.Errorf("%w: timeout must be positive", ErrInvalidConfig)
	}
	
	if c.MaxRetries < 0 {
		return fmt.Errorf("%w: max retries cannot be negative", ErrInvalidConfig)
	}
	
	if c.RetryDelay < 0 {
		return fmt.Errorf("%w: retry delay cannot be negative", ErrInvalidConfig)
	}
	
	return nil
}

// Clone creates a copy of the configuration
func (c *Config) Clone() *Config {
	return &Config{
		BaseURL:            c.BaseURL,
		Token:              c.Token,
		Timeout:            c.Timeout,
		UserAgent:          c.UserAgent,
		MaxRetries:         c.MaxRetries,
		RetryDelay:         c.RetryDelay,
		Debug:              c.Debug,
		InsecureSkipVerify: c.InsecureSkipVerify,
	}
}
