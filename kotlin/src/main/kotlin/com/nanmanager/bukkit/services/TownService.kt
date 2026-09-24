package com.nanmanager.bukkit.services

import com.nanmanager.bukkit.http.HttpClient
import com.nanmanager.bukkit.models.*

/** 城镇管理服务；传输和模型解析由 OpenAPI 生成客户端负责。 */
class TownService(httpClient: HttpClient) : BaseService(httpClient) {
    private val api get() = httpClient.generatedApis.towns

    fun createTown(request: CreateTownRequest): Town {
        val body = toGenerated<com.newnanmanager.generated.models.CreateTownRequest>(request)
        return generated<Town, com.newnanmanager.generated.models.Town> {
            api.createTownWithHttpInfo(body)
        }
    }

    fun getTown(request: GetTownRequest): TownDetailResponse {
        return generated<TownDetailResponse, com.newnanmanager.generated.models.TownDetailResponse> {
            api.getTownWithHttpInfo(request.id, request.detail)
        }
    }

    fun updateTown(request: UpdateTownRequest): Town {
        val body = toGenerated<com.newnanmanager.generated.models.UpdateTownRequest>(request)
        return generated<Town, com.newnanmanager.generated.models.Town> {
            api.updateTownWithHttpInfo(request.id, body)
        }
    }

    fun deleteTown(request: DeleteTownRequest) {
        generatedVoid { api.deleteTownWithHttpInfo(request.id) }
    }

    fun listTowns(request: ListTownsRequest): ListTownsResponse {
        return generated<ListTownsResponse, com.newnanmanager.generated.models.ListTownsResponse> {
            api.listTownsWithHttpInfo(
                page = request.page,
                pageSize = request.pageSize,
                name = request.name,
                search = request.search,
                minLevel = request.minLevel,
                maxLevel = request.maxLevel
            )
        }
    }
}
