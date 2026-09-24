package com.newnancity.newnanmanager.services;

import com.newnancity.newnanmanager.SessionContext;
import com.newnancity.newnanmanager.exceptions.NewNanManagerException;
import com.newnancity.newnanmanager.generated.api.PlayerServerServiceApi;
import com.newnancity.newnanmanager.generated.model.PlayerServersResponse;
import com.newnancity.newnanmanager.generated.model.ServerPlayersResponse;
import com.newnancity.newnanmanager.generated.model.SetPlayersOfflineRequest;

/** Player/server relationship and online snapshot service. */
public final class PlayerServerService extends AbstractService {
    private final PlayerServerServiceApi api;

    public PlayerServerService(PlayerServerServiceApi api) {
        this.api = api;
    }

    public PlayerServersResponse getPlayerServers(int playerId) throws NewNanManagerException {
        return getPlayerServers(playerId, null);
    }

    public PlayerServersResponse getPlayerServers(int playerId, Boolean onlineOnly)
            throws NewNanManagerException {
        return execute(() -> api.getPlayerServers(playerId).onlineOnly(onlineOnly).execute());
    }

    public ServerPlayersResponse getServerPlayers() throws NewNanManagerException {
        return getServerPlayers(null, null, null, null, null);
    }

    public ServerPlayersResponse getServerPlayers(
            Integer page,
            Integer pageSize,
            String search,
            Integer serverId,
            Boolean onlineOnly) throws NewNanManagerException {
        return execute(() -> api.getServerPlayers()
                .page(page)
                .pageSize(pageSize)
                .search(search)
                .serverId(serverId)
                .onlineOnly(onlineOnly)
                .execute());
    }

    public void setPlayersOffline(
            String sessionId,
            long sessionEpoch,
            SetPlayersOfflineRequest request) throws NewNanManagerException {
        SessionContext session = new SessionContext(sessionId, sessionEpoch);
        executeVoid(() -> api.setPlayersOffline(session.getId(), session.getEpoch(), request).execute());
    }

    public void setPlayersOffline(
            SetPlayersOfflineRequest request,
            SessionContext session) throws NewNanManagerException {
        setPlayersOffline(session.getId(), session.getEpoch(), request);
    }

    public PlayerServerServiceApi raw() {
        return api;
    }
}
