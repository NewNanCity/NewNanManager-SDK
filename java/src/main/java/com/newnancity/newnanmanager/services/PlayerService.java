package com.newnancity.newnanmanager.services;

import com.newnancity.newnanmanager.SessionContext;
import com.newnancity.newnanmanager.exceptions.NewNanManagerException;
import com.newnancity.newnanmanager.generated.api.PlayerServiceApi;
import com.newnancity.newnanmanager.generated.model.BanPlayerRequest;
import com.newnancity.newnanmanager.generated.model.CreatePlayerRequest;
import com.newnancity.newnanmanager.generated.model.ListPlayersResponse;
import com.newnancity.newnanmanager.generated.model.Player;
import com.newnancity.newnanmanager.generated.model.UpdatePlayerRequest;
import com.newnancity.newnanmanager.generated.model.ValidateRequest;
import com.newnancity.newnanmanager.generated.model.ValidateResponse;

/** Player management service using generated request and model types. */
public final class PlayerService extends AbstractService {
    private final PlayerServiceApi api;

    public PlayerService(PlayerServiceApi api) {
        this.api = api;
    }

    public Player createPlayer(CreatePlayerRequest request) throws NewNanManagerException {
        return execute(() -> api.createPlayer(request).execute());
    }

    public Player getPlayer(int id) throws NewNanManagerException {
        return execute(() -> api.getPlayer(id).execute());
    }

    public Player updatePlayer(int id, UpdatePlayerRequest request) throws NewNanManagerException {
        return execute(() -> api.updatePlayer(id, request).execute());
    }

    public void deletePlayer(int id) throws NewNanManagerException {
        executeVoid(() -> api.deletePlayer(id).execute());
    }

    public ListPlayersResponse listPlayers() throws NewNanManagerException {
        return listPlayers(null, null, null, null, null, null, null, null, null);
    }

    public ListPlayersResponse listPlayers(
            Integer page,
            Integer pageSize,
            String search,
            Integer townId,
            Long banMode,
            String name,
            String qq,
            String qqguild,
            String discord) throws NewNanManagerException {
        return execute(() -> api.listPlayers()
                .page(page)
                .pageSize(pageSize)
                .search(search)
                .townId(townId)
                .banMode(banMode)
                .name(name)
                .qq(qq)
                .qqguild(qqguild)
                .discord(discord)
                .execute());
    }

    public void banPlayer(int playerId, BanPlayerRequest request) throws NewNanManagerException {
        executeVoid(() -> api.banPlayer(playerId, request).execute());
    }

    public void unbanPlayer(int playerId) throws NewNanManagerException {
        executeVoid(() -> api.unbanPlayer(playerId).execute());
    }

    public ValidateResponse validate(
            String sessionId,
            long sessionEpoch,
            ValidateRequest request) throws NewNanManagerException {
        SessionContext session = new SessionContext(sessionId, sessionEpoch);
        return execute(() -> api.validate(session.getId(), session.getEpoch(), request).execute());
    }

    public ValidateResponse validate(
            ValidateRequest request,
            SessionContext session) throws NewNanManagerException {
        return validate(session.getId(), session.getEpoch(), request);
    }

    public PlayerServiceApi raw() {
        return api;
    }
}
