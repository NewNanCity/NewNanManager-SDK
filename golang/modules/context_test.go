package modules

import (
	"context"
	"errors"
	"net/http"
	"reflect"
	"strings"
	"testing"

	"github.com/go-resty/resty/v2"
)

type requestContextKey struct{}

type contextTransport struct {
	t *testing.T
}

func (transport contextTransport) RoundTrip(request *http.Request) (*http.Response, error) {
	if request.Context().Value(requestContextKey{}) != "request-local" {
		transport.t.Error("request lost its caller's context")
		return nil, errors.New("missing request context")
	}
	return nil, request.Context().Err()
}

func TestAllServiceOperationsUseRequestContext(t *testing.T) {
	client := resty.New().SetBaseURL("http://sdk.invalid").SetTransport(contextTransport{t})
	services := []interface{}{
		NewPlayerService(client), NewServerService(client), NewTownService(client),
		NewMonitorService(client), NewTokenService(client), NewIPService(client), NewPlayerServerService(client),
	}
	ctx, cancel := context.WithCancel(context.WithValue(context.Background(), requestContextKey{}, "request-local"))
	cancel()
	for _, service := range services {
		value := reflect.ValueOf(service)
		for index := 0; index < value.NumMethod(); index++ {
			method := value.Type().Method(index)
			if strings.HasSuffix(method.Name, "WithContext") || strings.Contains(method.Name, "Session") {
				continue
			}
			t.Run(value.Type().String()+"/"+method.Name, func(t *testing.T) {
				withContext := value.MethodByName(method.Name + "WithContext")
				if !withContext.IsValid() {
					t.Fatalf("%s has no request-level context entry point", method.Name)
				}
				arguments := make([]reflect.Value, withContext.Type().NumIn())
				arguments[0] = reflect.ValueOf(ctx)
				for argument := 1; argument < len(arguments); argument++ {
					arguments[argument] = reflect.Zero(withContext.Type().In(argument))
				}
				results := withContext.Call(arguments)
				err, ok := results[len(results)-1].Interface().(error)
				if !ok || !errors.Is(err, context.Canceled) {
					t.Fatalf("canceled request did not preserve context.Canceled: %v", err)
				}
			})
		}
	}
}
