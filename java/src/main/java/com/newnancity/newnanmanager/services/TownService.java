package com.newnancity.newnanmanager.services;

import com.newnancity.newnanmanager.exceptions.NewNanManagerException;
import com.newnancity.newnanmanager.generated.api.TownServiceApi;
import com.newnancity.newnanmanager.generated.model.CreateTownRequest;
import com.newnancity.newnanmanager.generated.model.ListTownsResponse;
import com.newnancity.newnanmanager.generated.model.Town;
import com.newnancity.newnanmanager.generated.model.TownDetailResponse;
import com.newnancity.newnanmanager.generated.model.UpdateTownRequest;

/** Town management service. */
public final class TownService extends AbstractService {
    private final TownServiceApi api;

    public TownService(TownServiceApi api) {
        this.api = api;
    }

    public Town createTown(CreateTownRequest request) throws NewNanManagerException {
        return execute(() -> api.createTown(request).execute());
    }

    public TownDetailResponse getTown(int id) throws NewNanManagerException {
        return getTown(id, null);
    }

    public TownDetailResponse getTown(int id, Boolean detail) throws NewNanManagerException {
        return execute(() -> api.getTown(id).detail(detail).execute());
    }

    public Town updateTown(int id, UpdateTownRequest request) throws NewNanManagerException {
        return execute(() -> api.updateTown(id, request).execute());
    }

    public void deleteTown(int id) throws NewNanManagerException {
        executeVoid(() -> api.deleteTown(id).execute());
    }

    public ListTownsResponse listTowns() throws NewNanManagerException {
        return listTowns(null, null, null, null, null, null);
    }

    public ListTownsResponse listTowns(
            Integer page,
            Integer pageSize,
            String name,
            String search,
            Integer minLevel,
            Integer maxLevel) throws NewNanManagerException {
        return execute(() -> api.listTowns()
                .page(page)
                .pageSize(pageSize)
                .name(name)
                .search(search)
                .minLevel(minLevel)
                .maxLevel(maxLevel)
                .execute());
    }

    public TownServiceApi raw() {
        return api;
    }
}
