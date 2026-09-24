package modules

import (
	"errors"
	"strconv"
	"strings"

	"github.com/go-resty/resty/v2"
)

// SessionContext is the server-instance fencing tuple issued by NewNanManager.
type SessionContext struct {
	ID    string `json:"session_id"`
	Epoch int64  `json:"session_epoch"`
}

// Validate rejects a context that cannot represent an active server session.
func (session SessionContext) Validate() error {
	if strings.TrimSpace(session.ID) == "" {
		return errors.New("session id must not be blank")
	}
	if len(session.ID) < 32 || len(session.ID) > 64 {
		return errors.New("session id must be between 32 and 64 characters")
	}
	if session.Epoch <= 0 {
		return errors.New("session epoch must be greater than zero")
	}
	return nil
}

func applySessionHeaders(request *resty.Request, session SessionContext) error {
	if err := session.Validate(); err != nil {
		return err
	}
	request.SetHeader("X-NNM-Session-ID", session.ID)
	request.SetHeader("X-NNM-Session-Epoch", strconv.FormatInt(session.Epoch, 10))
	return nil
}
