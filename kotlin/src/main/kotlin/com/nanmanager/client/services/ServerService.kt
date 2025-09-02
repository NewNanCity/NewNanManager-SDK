package com.nanmanager.client.services

import com.nanmanager.client.models.*
import io.ktor.client.*

/**
 * 服务器管理服务模块
 */
class ServerService(
    httpClient: HttpClient,
    baseUrl: String,
    token: String
) : BaseService(httpClient, baseUrl, token) {

    /**
     * 创建服务器
     */
    suspend fun createServer(request: CreateServerRequest): ServerRegistry {
        return post("/api/v1/servers", request)
    }

    /**
     * 获取服务器信息
     */
    suspend fun getServer(id: Int): ServerRegistry {
        return get("/api/v1/servers/$id")
    }

    /**
     * 更新服务器信息
     */
    suspend fun updateServer(id: Int, request: UpdateServerRequest): ServerRegistry {
        return put("/api/v1/servers/$id", request)
    }

    /**
     * 删除服务器
     */
    suspend fun deleteServer(id: Int) {
        delete("/api/v1/servers/$id")
    }

    /**
     * 获取服务器列表
     */
    suspend fun listServers(request: ListServersRequest = ListServersRequest()): ListServersResponse {
        val params = buildParams(mapOf(
            "page" to request.page,
            "page_size" to request.pageSize,
            "search" to request.search,
            "online_only" to request.onlineOnly
        ))
        return get("/api/v1/servers", params)
    }

    /**
     * 获取服务器详细信息
     */
    suspend fun getServerDetail(id: Int): ServerDetailResponse {
        return get("/api/v1/servers/$id/detail")
    }
}
