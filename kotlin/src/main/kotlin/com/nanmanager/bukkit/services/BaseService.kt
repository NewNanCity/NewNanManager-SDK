package com.nanmanager.bukkit.services

import com.nanmanager.bukkit.http.HttpClient

/**
 * 基础服务类，提供通用的HTTP请求功能
 */
abstract class BaseService(
    protected val httpClient: HttpClient
) {

    /** Convert a facade model into the corresponding generated OpenAPI model. */
    protected inline fun <reified T : Any> toGenerated(value: Any): T {
        return httpClient.toGenerated(value)
    }

    /** Execute a generated request and convert its response back to the facade model. */
    protected inline fun <reified T : Any, reified G : Any> generated(
        noinline request: () -> com.newnanmanager.generated.infrastructure.ApiResponse<G?>
    ): T {
        return httpClient.convertGenerated(httpClient.executeGenerated(request)
            ?: throw com.nanmanager.bukkit.exceptions.JsonParseException(
                "Generated API returned an empty response"
            ))
    }

    /** Execute a generated request whose successful response has no public body. */
    protected fun generatedVoid(
        request: () -> com.newnanmanager.generated.infrastructure.ApiResponse<Any?>
    ) {
        httpClient.generatedVoid(request)
    }

    /**
     * 执行GET请求
     */
    protected inline fun <reified T> get(
        path: String,
        params: Map<String, Any?> = emptyMap()
    ): T {
        return httpClient.get<T>(path, params)
    }

    /**
     * 执行POST请求
     */
    protected inline fun <reified T> post(
        path: String,
        body: Any? = null
    ): T {
        return httpClient.post<T>(path, body)
    }

    /**
     * 执行PUT请求
     */
    protected inline fun <reified T> put(
        path: String,
        body: Any? = null
    ): T {
        return httpClient.put<T>(path, body)
    }

    /**
     * 执行DELETE请求
     */
    protected fun delete(path: String) {
        httpClient.delete(path)
    }

    /**
     * 构建查询参数，过滤掉null值
     */
    protected fun buildParams(params: Map<String, Any?>): Map<String, Any?> {
        return params.filterValues { it != null }
    }
}
