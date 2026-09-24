package com.newnancity.newnanmanager.services;

import com.newnancity.newnanmanager.exceptions.NewNanManagerException;
import com.newnancity.newnanmanager.generated.api.HealthApi;
import com.newnancity.newnanmanager.generated.model.Ping200Response;

/** Unauthenticated service health endpoint. */
public final class HealthService extends AbstractService {
    private final HealthApi api;

    public HealthService(HealthApi api) {
        this.api = api;
    }

    public Ping200Response ping() throws NewNanManagerException {
        return execute(() -> api.ping().execute());
    }

    public HealthApi raw() {
        return api;
    }
}
