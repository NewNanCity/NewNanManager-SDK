package com.nanmanager.client.services

import com.nanmanager.client.models.*
import io.ktor.client.*

/**
 * 城镇管理服务模块
 */
class TownService(
    httpClient: HttpClient,
    baseUrl: String,
    token: String
) : BaseService(httpClient, baseUrl, token) {

    /**
     * 创建城镇
     */
    suspend fun createTown(request: CreateTownRequest): Town {
        return post("/api/v1/towns", request)
    }

    /**
     * 获取城镇详情
     */
    suspend fun getTown(id: Int, detail: Boolean = false): TownDetailResponse {
        val params = buildParams(mapOf("detail" to detail))
        return get("/api/v1/towns/$id", params)
    }

    /**
     * 更新城镇信息
     */
    suspend fun updateTown(id: Int, request: UpdateTownRequest): Town {
        return put("/api/v1/towns/$id", request)
    }

    /**
     * 删除城镇
     */
    suspend fun deleteTown(id: Int) {
        delete("/api/v1/towns/$id")
    }

    /**
     * 获取城镇列表
     */
    suspend fun listTowns(request: ListTownsRequest = ListTownsRequest()): ListTownsResponse {
        val params = buildParams(mapOf(
            "page" to request.page,
            "page_size" to request.pageSize,
            "search" to request.search,
            "min_level" to request.minLevel,
            "max_level" to request.maxLevel
        ))
        return get("/api/v1/towns", params)
    }



    /**
     * 获取城镇成员列表
     */
    suspend fun getTownMembers(request: GetTownMembersRequest): TownMembersResponse {
        val params = buildParams(mapOf(
            "page" to request.page,
            "page_size" to request.pageSize
        ))
        return get("/api/v1/towns/${request.townId}/members", params)
    }
}
