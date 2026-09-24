package nanmanager

// AuthScheme selects the single credential header sent by the client.
type AuthScheme string

const (
	// AuthSchemeBearer sends Authorization: Bearer <token>.
	AuthSchemeBearer AuthScheme = "bearer"
	// AuthSchemeAPIToken sends X-API-Token: <token>.
	AuthSchemeAPIToken AuthScheme = "api-token"
)
