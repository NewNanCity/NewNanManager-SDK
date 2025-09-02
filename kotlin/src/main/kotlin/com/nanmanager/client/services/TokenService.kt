package com.nanmanager.client.services

import com.nanmanager.client.models.*
import io.ktor.client.*

/**
 * API Token管理服务模块
 */
class TokenService(
    httpClient: HttpClient,
    baseUrl: String,
    token: String
) : BaseService(httpClient, baseUrl, token) {

    /**
     * 创建API Token
     */
    suspend fun createApiToken(request: CreateApiTokenRequest): CreateApiTokenResponse {
        return post("/api/v1/tokens", request)
    }

    /**
     * 获取API Token详情
     */
    suspend fun getApiToken(id: Int): ApiToken {
        return get("/api/v1/tokens/$id")
    }

    /**
     * 更新API Token
     */
    suspend fun updateApiToken(id: Int, request: UpdateApiTokenRequest): ApiToken {
        return put("/api/v1/tokens/$id", request)
    }

    /**
     * 删除API Token
     */
    suspend fun deleteApiToken(id: Int) {
        delete("/api/v1/tokens/$id")
    }

    /**
     * 获取API Token列表
     */
    suspend fun listApiTokens(): ListApiTokensResponse {
        return get("/api/v1/tokens")
    }
}
