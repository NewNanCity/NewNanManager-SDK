package com.nanmanager.client.services

import com.nanmanager.client.models.*
import io.ktor.client.*

/**
 * 服务器监控服务模块
 */
class MonitorService(
    httpClient: HttpClient,
    baseUrl: String,
    token: String
) : BaseService(httpClient, baseUrl, token) {

    /**
     * 服务器心跳
     */
    suspend fun heartbeat(serverId: Int, request: HeartbeatRequest): HeartbeatResponse {
        return post("/api/v1/servers/$serverId/heartbeat", request)
    }

    /**
     * 获取服务器状态
     */
    suspend fun getServerStatus(serverId: Int): ServerStatus {
        return get("/api/v1/servers/$serverId/status")
    }

    /**
     * 获取延迟统计
     */
    suspend fun getLatencyStats(serverId: Int): LatencyStatsResponse {
        return get("/api/v1/servers/$serverId/latency")
    }

    /**
     * 获取监控统计信息
     */
    suspend fun getMonitorStats(
        serverId: Int,
        since: Long? = null,
        duration: Long? = null
    ): MonitorStatsResponse {
        val params = mutableMapOf<String, String>()
        since?.let { params["since"] = it.toString() }
        duration?.let { params["duration"] = it.toString() }

        val queryString = if (params.isNotEmpty()) {
            "?" + params.map { "${it.key}=${it.value}" }.joinToString("&")
        } else ""

        return get("/api/v1/servers/$serverId/monitor$queryString")
    }
}
