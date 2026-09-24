package com.newnancity.newnanmanager.services;

import com.newnancity.newnanmanager.exceptions.NewNanManagerException;
import com.newnancity.newnanmanager.generated.api.IpServiceApi;
import com.newnancity.newnanmanager.generated.model.BanIPRequest;
import com.newnancity.newnanmanager.generated.model.IPInfo;
import com.newnancity.newnanmanager.generated.model.IPStatistics;
import com.newnancity.newnanmanager.generated.model.ListIPsResponse;
import com.newnancity.newnanmanager.generated.model.UnbanIPRequest;

/** IP lookup, risk and ban management service. */
public final class IpService extends AbstractService {
    private final IpServiceApi api;

    public IpService(IpServiceApi api) {
        this.api = api;
    }

    public void banIP(BanIPRequest request) throws NewNanManagerException {
        executeVoid(() -> api.banIP(request).execute());
    }

    public void unbanIP(UnbanIPRequest request) throws NewNanManagerException {
        executeVoid(() -> api.unbanIP(request).execute());
    }

    public IPInfo getIPInfo(String ip) throws NewNanManagerException {
        return execute(() -> api.getIPInfo(ip).execute());
    }

    public IPStatistics getIPStatistics() throws NewNanManagerException {
        return execute(() -> api.getIPStatistics().execute());
    }

    public ListIPsResponse listIPs() throws NewNanManagerException {
        return listIPs(null, null, null, null, null);
    }

    public ListIPsResponse listIPs(
            Integer page,
            Integer pageSize,
            Boolean bannedOnly,
            Long minThreatLevel,
            Integer minRiskScore) throws NewNanManagerException {
        return execute(() -> api.listIPs()
                .page(page)
                .pageSize(pageSize)
                .bannedOnly(bannedOnly)
                .minThreatLevel(minThreatLevel)
                .minRiskScore(minRiskScore)
                .execute());
    }

    public ListIPsResponse getBannedIPs(
            Integer page,
            Integer pageSize,
            Boolean bannedOnly,
            Long minThreatLevel,
            Integer minRiskScore) throws NewNanManagerException {
        return execute(() -> api.getBannedIPs()
                .page(page)
                .pageSize(pageSize)
                .bannedOnly(bannedOnly)
                .minThreatLevel(minThreatLevel)
                .minRiskScore(minRiskScore)
                .execute());
    }

    public ListIPsResponse getHighRiskIPs(
            Integer page,
            Integer pageSize,
            Boolean bannedOnly,
            Long minThreatLevel,
            Integer minRiskScore) throws NewNanManagerException {
        return execute(() -> api.getHighRiskIPs()
                .page(page)
                .pageSize(pageSize)
                .bannedOnly(bannedOnly)
                .minThreatLevel(minThreatLevel)
                .minRiskScore(minRiskScore)
                .execute());
    }

    public ListIPsResponse getSuspiciousIPs(
            Integer page,
            Integer pageSize,
            Boolean bannedOnly,
            Long minThreatLevel,
            Integer minRiskScore) throws NewNanManagerException {
        return execute(() -> api.getSuspiciousIPs()
                .page(page)
                .pageSize(pageSize)
                .bannedOnly(bannedOnly)
                .minThreatLevel(minThreatLevel)
                .minRiskScore(minRiskScore)
                .execute());
    }

    public IpServiceApi raw() {
        return api;
    }
}
