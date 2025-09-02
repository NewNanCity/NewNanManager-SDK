package com.nanmanager.client.services

import com.nanmanager.client.models.*
import io.ktor.client.*

/**
 * IP管理服务模块
 */
class IPService(
    httpClient: HttpClient,
    baseUrl: String,
    token: String
) : BaseService(httpClient, baseUrl, token) {

    /**
     * 获取IP信息（包含风险信息）
     */
    suspend fun getIPInfo(ip: String): IPInfo {
        return get("/api/v1/ips/$ip")
    }



    /**
     * 封禁IP（支持批量）
     */
    suspend fun banIP(request: BanIPRequest) {
        post<EmptyResponse>("/api/v1/ips/ban", request)
    }

    /**
     * 解封IP（支持批量）
     */
    suspend fun unbanIP(request: UnbanIPRequest) {
        post<EmptyResponse>("/api/v1/ips/unban", request)
    }

    /**
     * 获取IP列表
     */
    suspend fun listIPs(request: ListIPsRequest = ListIPsRequest()): ListIPsResponse {
        val params = buildParams(mapOf(
            "page" to request.page,
            "page_size" to request.pageSize,
            "banned_only" to request.bannedOnly,
            "min_threat_level" to request.minThreatLevel?.value,
            "min_risk_score" to request.minRiskScore
        ))
        return get("/api/v1/ips", params)
    }

    /**
     * 获取被封禁的IP列表
     */
    suspend fun getBannedIPs(request: ListIPsRequest = ListIPsRequest()): ListIPsResponse {
        val params = buildParams(mapOf(
            "page" to request.page,
            "page_size" to request.pageSize
        ))
        return get("/api/v1/ips/banned", params)
    }

    /**
     * 获取可疑IP列表
     */
    suspend fun getSuspiciousIPs(request: ListIPsRequest = ListIPsRequest()): ListIPsResponse {
        val params = buildParams(mapOf(
            "page" to request.page,
            "page_size" to request.pageSize
        ))
        return get("/api/v1/ips/suspicious", params)
    }

    /**
     * 获取高风险IP列表
     */
    suspend fun getHighRiskIPs(request: ListIPsRequest = ListIPsRequest()): ListIPsResponse {
        val params = buildParams(mapOf(
            "page" to request.page,
            "page_size" to request.pageSize
        ))
        return get("/api/v1/ips/high-risk", params)
    }

    /**
     * 获取IP统计信息
     */
    suspend fun getIPStatistics(): IPStatistics {
        return get("/api/v1/ips/statistics")
    }
}
