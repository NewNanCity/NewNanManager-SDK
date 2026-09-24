package com.newnancity.newnanmanager.services;

import com.newnancity.newnanmanager.exceptions.NewNanManagerException;
import com.newnancity.newnanmanager.generated.api.TokenServiceApi;
import com.newnancity.newnanmanager.generated.model.ApiToken;
import com.newnancity.newnanmanager.generated.model.CreateApiTokenRequest;
import com.newnancity.newnanmanager.generated.model.CreateApiTokenResponse;
import com.newnancity.newnanmanager.generated.model.ListApiTokensResponse;
import com.newnancity.newnanmanager.generated.model.UpdateApiTokenRequest;

/** API token management service. */
public final class TokenService extends AbstractService {
    private final TokenServiceApi api;

    public TokenService(TokenServiceApi api) {
        this.api = api;
    }

    public CreateApiTokenResponse createApiToken(CreateApiTokenRequest request) throws NewNanManagerException {
        return execute(() -> api.createApiToken(request).execute());
    }

    public ApiToken getApiToken(int id) throws NewNanManagerException {
        return execute(() -> api.getApiToken(id).execute());
    }

    public ApiToken updateApiToken(int id, UpdateApiTokenRequest request) throws NewNanManagerException {
        return execute(() -> api.updateApiToken(id, request).execute());
    }

    public void deleteApiToken(int id) throws NewNanManagerException {
        executeVoid(() -> api.deleteApiToken(id).execute());
    }

    public ListApiTokensResponse listApiTokens() throws NewNanManagerException {
        return listApiTokens(null, null);
    }

    public ListApiTokensResponse listApiTokens(Integer page, Integer pageSize) throws NewNanManagerException {
        return execute(() -> api.listApiTokens().page(page).pageSize(pageSize).execute());
    }

    public TokenServiceApi raw() {
        return api;
    }
}
