package com.nanmanager.bukkit.services

import com.nanmanager.bukkit.http.HttpClient
import com.nanmanager.bukkit.SessionContext
import com.nanmanager.bukkit.models.*

/** 监控服务；生成客户端负责请求构造，facade 保留原有模型和错误边界。 */
class MonitorService(httpClient: HttpClient) : BaseService(httpClient) {
    private val api get() = httpClient.generatedApis.monitor

    fun createServerSession(serverId: Int): SessionContext {
        return generated<SessionContext, com.newnanmanager.generated.models.ServerSessionResponse> {
            api.createServerSessionWithHttpInfo(serverId, emptyMap<String, Any>())
        }
    }

    fun heartbeat(serverId: Int, request: HeartbeatRequest): HeartbeatResponse {
        return post("/api/v1/monitor/$serverId/heartbeat", request)
    }

    fun heartbeat(serverId: Int, request: HeartbeatRequest, session: SessionContext): HeartbeatResponse {
        val body = toGenerated<com.newnanmanager.generated.models.HeartbeatRequest>(request)
        return generated<HeartbeatResponse, com.newnanmanager.generated.models.HeartbeatResponse> {
            api.heartbeatWithHttpInfo(serverId, session.id, session.epoch, body)
        }
    }

    fun getMonitorStats(request: GetMonitorStatsRequest): GetMonitorStatsResponse {
        return generated<GetMonitorStatsResponse, com.newnanmanager.generated.models.GetMonitorStatsResponse> {
            api.getMonitorStatsWithHttpInfo(
                serverId = request.serverId,
                since = request.since,
                duration = request.duration,
                limit = request.limit,
                cursor = request.cursor
            )
        }
    }
}
