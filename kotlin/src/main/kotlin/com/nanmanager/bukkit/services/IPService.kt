package com.nanmanager.bukkit.services

import com.nanmanager.bukkit.http.HttpClient
import com.nanmanager.bukkit.models.*

/** IP 管理服务；传输和模型解析由 OpenAPI 生成客户端负责。 */
class IPService(httpClient: HttpClient) : BaseService(httpClient) {
    private val api get() = httpClient.generatedApis.ips

    fun getIPInfo(request: GetIPInfoRequest): IPInfo {
        return generated<IPInfo, com.newnanmanager.generated.models.IPInfo> {
            api.getIPInfoWithHttpInfo(request.ip)
        }
    }

    fun banIP(request: BanIPRequest) {
        val body = toGenerated<com.newnanmanager.generated.models.BanIPRequest>(request)
        generatedVoid { api.banIPWithHttpInfo(body) }
    }

    fun unbanIP(request: UnbanIPRequest) {
        val body = toGenerated<com.newnanmanager.generated.models.UnbanIPRequest>(request)
        generatedVoid { api.unbanIPWithHttpInfo(body) }
    }

    fun listIPs(request: ListIPsRequest): ListIPsResponse {
        val level = request.minThreatLevel?.let { value ->
            com.newnanmanager.generated.apis.IPServiceApi.MinThreatLevelListIPs.values()
                .first { it.value.toInt() == value.value }
        }
        return generated<ListIPsResponse, com.newnanmanager.generated.models.ListIPsResponse> {
            api.listIPsWithHttpInfo(request.page, request.pageSize, request.bannedOnly, level, request.minRiskScore)
        }
    }

    fun getBannedIPs(request: ListIPsRequest): ListIPsResponse {
        val level = request.minThreatLevel?.let { value ->
            com.newnanmanager.generated.apis.IPServiceApi.MinThreatLevelGetBannedIPs.values()
                .first { it.value.toInt() == value.value }
        }
        return generated<ListIPsResponse, com.newnanmanager.generated.models.ListIPsResponse> {
            api.getBannedIPsWithHttpInfo(request.page, request.pageSize, request.bannedOnly, level, request.minRiskScore)
        }
    }

    fun getSuspiciousIPs(request: ListIPsRequest): ListIPsResponse {
        val level = request.minThreatLevel?.let { value ->
            com.newnanmanager.generated.apis.IPServiceApi.MinThreatLevelGetSuspiciousIPs.values()
                .first { it.value.toInt() == value.value }
        }
        return generated<ListIPsResponse, com.newnanmanager.generated.models.ListIPsResponse> {
            api.getSuspiciousIPsWithHttpInfo(request.page, request.pageSize, request.bannedOnly, level, request.minRiskScore)
        }
    }

    fun getHighRiskIPs(request: ListIPsRequest): ListIPsResponse {
        val level = request.minThreatLevel?.let { value ->
            com.newnanmanager.generated.apis.IPServiceApi.MinThreatLevelGetHighRiskIPs.values()
                .first { it.value.toInt() == value.value }
        }
        return generated<ListIPsResponse, com.newnanmanager.generated.models.ListIPsResponse> {
            api.getHighRiskIPsWithHttpInfo(request.page, request.pageSize, request.bannedOnly, level, request.minRiskScore)
        }
    }

    fun getIPStatistics(): IPStatistics {
        return generated<IPStatistics, com.newnanmanager.generated.models.IPStatistics> {
            api.getIPStatisticsWithHttpInfo()
        }
    }
}
