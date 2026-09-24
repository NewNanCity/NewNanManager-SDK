package com.newnancity.newnanmanager.services;

import com.newnancity.newnanmanager.SessionContext;
import com.newnancity.newnanmanager.exceptions.NewNanManagerException;
import com.newnancity.newnanmanager.generated.api.MonitorServiceApi;
import com.newnancity.newnanmanager.generated.model.GetMonitorStatsResponse;
import com.newnancity.newnanmanager.generated.model.HeartbeatRequest;
import com.newnancity.newnanmanager.generated.model.HeartbeatResponse;
import com.newnancity.newnanmanager.generated.model.ServerSessionResponse;

/** Server session, heartbeat and monitor statistics service. */
public final class MonitorService extends AbstractService {
    private final MonitorServiceApi api;

    public MonitorService(MonitorServiceApi api) {
        this.api = api;
    }

    public ServerSessionResponse createServerSession(int serverId, Object body) throws NewNanManagerException {
        return execute(() -> api.createServerSession(serverId, body).execute());
    }

    public SessionContext createServerSession(int serverId) throws NewNanManagerException {
        ServerSessionResponse response = createServerSession(serverId, new Object());
        return new SessionContext(response.getSessionId(), response.getSessionEpoch());
    }

    public HeartbeatResponse heartbeat(
            int serverId,
            String sessionId,
            long sessionEpoch,
            HeartbeatRequest request) throws NewNanManagerException {
        SessionContext session = new SessionContext(sessionId, sessionEpoch);
        return execute(() -> api.heartbeat(serverId, session.getId(), session.getEpoch(), request).execute());
    }

    public HeartbeatResponse heartbeat(
            int serverId,
            HeartbeatRequest request,
            SessionContext session) throws NewNanManagerException {
        return heartbeat(serverId, session.getId(), session.getEpoch(), request);
    }

    public GetMonitorStatsResponse getMonitorStats(int serverId) throws NewNanManagerException {
        return getMonitorStats(serverId, null, null, null, null);
    }

    public GetMonitorStatsResponse getMonitorStats(
            int serverId,
            Long since,
            Long duration,
            Integer limit,
            String cursor) throws NewNanManagerException {
        return execute(() -> api.getMonitorStats(serverId)
                .since(since)
                .duration(duration)
                .limit(limit)
                .cursor(cursor)
                .execute());
    }

    public MonitorServiceApi raw() {
        return api;
    }
}
