package com.nanmanager.bukkit.services

import com.nanmanager.bukkit.http.HttpClient
import com.nanmanager.bukkit.models.*

/** API Token 管理服务；传输和模型解析由 OpenAPI 生成客户端负责。 */
class TokenService(httpClient: HttpClient) : BaseService(httpClient) {
    private val api get() = httpClient.generatedApis.tokens

    fun createApiToken(request: CreateApiTokenRequest): CreateApiTokenResponse {
        val body = toGenerated<com.newnanmanager.generated.models.CreateApiTokenRequest>(request)
        return generated<CreateApiTokenResponse, com.newnanmanager.generated.models.CreateApiTokenResponse> {
            api.createApiTokenWithHttpInfo(body)
        }
    }

    fun getApiToken(request: GetApiTokenRequest): ApiToken {
        return generated<ApiToken, com.newnanmanager.generated.models.ApiToken> {
            api.getApiTokenWithHttpInfo(request.id)
        }
    }

    fun updateApiToken(request: UpdateApiTokenRequest): ApiToken {
        val body = toGenerated<com.newnanmanager.generated.models.UpdateApiTokenRequest>(request)
        return generated<ApiToken, com.newnanmanager.generated.models.ApiToken> {
            api.updateApiTokenWithHttpInfo(request.id, body)
        }
    }

    fun deleteApiToken(request: DeleteApiTokenRequest) {
        generatedVoid { api.deleteApiTokenWithHttpInfo(request.id) }
    }

    fun listApiTokens(request: ListApiTokensRequest): ListApiTokensResponse {
        return generated<ListApiTokensResponse, com.newnanmanager.generated.models.ListApiTokensResponse> {
            api.listApiTokensWithHttpInfo(request.page, request.pageSize)
        }
    }
}
