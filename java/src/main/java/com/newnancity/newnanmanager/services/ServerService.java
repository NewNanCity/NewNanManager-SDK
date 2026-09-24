package com.newnancity.newnanmanager.services;

import com.newnancity.newnanmanager.exceptions.NewNanManagerException;
import com.newnancity.newnanmanager.generated.api.ServerServiceApi;
import com.newnancity.newnanmanager.generated.model.CreateServerRequest;
import com.newnancity.newnanmanager.generated.model.ListServersResponse;
import com.newnancity.newnanmanager.generated.model.ServerDetailResponse;
import com.newnancity.newnanmanager.generated.model.ServerRegistry;
import com.newnancity.newnanmanager.generated.model.UpdateServerRequest;

/** Server registration and lookup service. */
public final class ServerService extends AbstractService {
    private final ServerServiceApi api;

    public ServerService(ServerServiceApi api) {
        this.api = api;
    }

    public ServerRegistry createServer(CreateServerRequest request) throws NewNanManagerException {
        return execute(() -> api.createServer(request).execute());
    }

    public ServerDetailResponse getServer(int id) throws NewNanManagerException {
        return getServer(id, null);
    }

    public ServerDetailResponse getServer(int id, Boolean detail) throws NewNanManagerException {
        return execute(() -> api.getServer(id).detail(detail).execute());
    }

    public ServerRegistry updateServer(int id, UpdateServerRequest request) throws NewNanManagerException {
        return execute(() -> api.updateServer(id, request).execute());
    }

    public void deleteServer(int id) throws NewNanManagerException {
        executeVoid(() -> api.deleteServer(id).execute());
    }

    public ListServersResponse listServers() throws NewNanManagerException {
        return listServers(null, null, null, null);
    }

    public ListServersResponse listServers(
            Integer page,
            Integer pageSize,
            String search,
            Boolean onlineOnly) throws NewNanManagerException {
        return execute(() -> api.listServers()
                .page(page)
                .pageSize(pageSize)
                .search(search)
                .onlineOnly(onlineOnly)
                .execute());
    }

    public ServerServiceApi raw() {
        return api;
    }
}
