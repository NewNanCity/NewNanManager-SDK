package com.nanmanager.client.services

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.Json

/**
 * 基础服务类，提供通用的HTTP请求功能
 */
abstract class BaseService(
    protected val httpClient: HttpClient,
    protected val baseUrl: String,
    protected val token: String
) {
    
    /**
     * 执行GET请求
     */
    protected suspend inline fun <reified T> get(
        path: String,
        params: Map<String, Any?> = emptyMap()
    ): T {
        val response = httpClient.get("$baseUrl$path") {
            headers {
                append("Authorization", "Bearer $token")
                append("X-API-Token", token)
            }
            params.forEach { (key, value) ->
                value?.let { parameter(key, it) }
            }
        }
        return response.body<T>()
    }
    
    /**
     * 执行POST请求
     */
    protected suspend inline fun <reified T> post(
        path: String,
        body: Any? = null
    ): T {
        val response = httpClient.post("$baseUrl$path") {
            headers {
                append("Authorization", "Bearer $token")
                append("X-API-Token", token)
            }
            if (body != null) {
                contentType(ContentType.Application.Json)
                setBody(body)
            }
        }
        return response.body<T>()
    }
    
    /**
     * 执行PUT请求
     */
    protected suspend inline fun <reified T> put(
        path: String,
        body: Any? = null
    ): T {
        val response = httpClient.put("$baseUrl$path") {
            headers {
                append("Authorization", "Bearer $token")
                append("X-API-Token", token)
            }
            if (body != null) {
                contentType(ContentType.Application.Json)
                setBody(body)
            }
        }
        return response.body<T>()
    }
    
    /**
     * 执行DELETE请求
     */
    protected suspend fun delete(path: String) {
        httpClient.delete("$baseUrl$path") {
            headers {
                append("Authorization", "Bearer $token")
                append("X-API-Token", token)
            }
        }
    }
    
    /**
     * 构建查询参数，过滤掉null值
     */
    protected fun buildParams(params: Map<String, Any?>): Map<String, Any?> {
        return params.filterValues { it != null }
    }
}
