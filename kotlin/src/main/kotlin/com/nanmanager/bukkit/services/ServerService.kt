package com.nanmanager.bukkit.services

import com.nanmanager.bukkit.http.HttpClient
import com.nanmanager.bukkit.models.*

/** 服务器管理服务；传输和模型解析由 OpenAPI 生成客户端负责。 */
class ServerService(httpClient: HttpClient) : BaseService(httpClient) {
    private val api get() = httpClient.generatedApis.servers

    fun createServer(request: CreateServerRequest): ServerRegistry {
        val body = toGenerated<com.newnanmanager.generated.models.CreateServerRequest>(request)
        return generated<ServerRegistry, com.newnanmanager.generated.models.ServerRegistry> {
            api.createServerWithHttpInfo(body)
        }
    }

    fun getServer(request: GetServerRequest): ServerDetailResponse {
        return generated<ServerDetailResponse, com.newnanmanager.generated.models.ServerDetailResponse> {
            api.getServerWithHttpInfo(request.id, request.detail)
        }
    }

    fun updateServer(request: UpdateServerRequest): ServerRegistry {
        val body = toGenerated<com.newnanmanager.generated.models.UpdateServerRequest>(request)
        return generated<ServerRegistry, com.newnanmanager.generated.models.ServerRegistry> {
            api.updateServerWithHttpInfo(request.id, body)
        }
    }

    fun deleteServer(request: DeleteServerRequest) {
        generatedVoid { api.deleteServerWithHttpInfo(request.id) }
    }

    fun listServers(request: ListServersRequest): ListServersResponse {
        return generated<ListServersResponse, com.newnanmanager.generated.models.ListServersResponse> {
            api.listServersWithHttpInfo(
                page = request.page,
                pageSize = request.pageSize,
                search = request.search,
                onlineOnly = request.onlineOnly
            )
        }
    }
}
