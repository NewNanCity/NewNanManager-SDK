package com.nanmanager.bukkit.services

import com.nanmanager.bukkit.http.HttpClient
import com.nanmanager.bukkit.models.*

/**
 * 监控服务 - 处理服务器心跳和监控统计
 */
class MonitorService(httpClient: HttpClient) : BaseService(httpClient) {

    /**
     * 发送心跳
     */
    fun heartbeat(serverId: Int, request: HeartbeatRequest): HeartbeatResponse {
        return post("/api/v1/monitor/${serverId}/heartbeat", request)
    }

    /**
     * 获取监控统计
     */
    fun getMonitorStats(request: GetMonitorStatsRequest): GetMonitorStatsResponse {
        val params = buildParams(mapOf(
            "since" to request.since,
            "duration" to request.duration
        ))
        return get("/api/v1/monitor/${request.serverId}/stats", params)
    }
}
