package com.nanmanager.bukkit.http

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.nanmanager.bukkit.AuthScheme
import com.nanmanager.bukkit.exceptions.*
import com.nanmanager.bukkit.models.ErrorResponse
import com.newnanmanager.generated.infrastructure.ApiResponse as GeneratedApiResponse
import com.newnanmanager.generated.infrastructure.ClientError as GeneratedClientError
import com.newnanmanager.generated.infrastructure.Informational as GeneratedInformational
import com.newnanmanager.generated.infrastructure.Redirection as GeneratedRedirection
import com.newnanmanager.generated.infrastructure.ServerError as GeneratedServerError
import com.newnanmanager.generated.infrastructure.Success as GeneratedSuccess
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.HttpUrl.Companion.toHttpUrl
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * HTTP客户端基类，提供同步HTTP请求功能
 */
class HttpClient(
    baseUrl: String,
    token: String,
    timeout: Long = 30L,
    private val authScheme: AuthScheme = AuthScheme.BEARER
) : AutoCloseable {

    internal val baseUrl = baseUrl.trimEnd('/')
    internal val token = token.also { require(it.isNotBlank()) { "token must not be blank" } }
    @PublishedApi
    internal val objectMapper: ObjectMapper = jacksonObjectMapper()
    @PublishedApi
    internal val generatedObjectMapper: ObjectMapper = objectMapper.copy()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    private val authHeader: String = when (authScheme) {
        AuthScheme.BEARER -> "Authorization"
        AuthScheme.API_TOKEN -> "X-API-Token"
    }
    private val authValue: String = when (authScheme) {
        AuthScheme.BEARER -> "Bearer $token"
        AuthScheme.API_TOKEN -> token
    }

    internal val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(timeout, TimeUnit.SECONDS)
        .readTimeout(timeout, TimeUnit.SECONDS)
        .writeTimeout(timeout, TimeUnit.SECONDS)
        .callTimeout(timeout, TimeUnit.SECONDS)
        .followRedirects(false)
        .followSslRedirects(false)
        .retryOnConnectionFailure(false)
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .removeHeader("Authorization")
                .removeHeader("X-API-Token")
                .addHeader(authHeader, authValue)
                .build()
            chain.proceed(request)
        }
        .build()

    internal val generatedApis: GeneratedApiSet = GeneratedApiSet(baseUrl, okHttpClient)

    companion object {
        internal val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()
    }

    /**
     * 执行GET请求
     */
    fun <T> get(
        path: String,
        params: Map<String, Any?> = emptyMap(),
        responseType: Class<T>
    ): T {
        val url = "$baseUrl$path".toHttpUrl().newBuilder()
        for ((key, value) in params) {
            if (value != null) url.addQueryParameter(key, value.toString())
        }

        val request = Request.Builder()
            .url(url.build())
            .get()
            .build()

        return executeRequest(request, responseType)
    }

    /**
     * 执行GET请求（reified版本）
     */
    inline fun <reified T> get(
        path: String,
        params: Map<String, Any?> = emptyMap()
    ): T {
        return get(path, params, T::class.java)
    }

    /**
     * 执行POST请求
     */
    fun <T> post(
        path: String,
        body: Any? = null,
        responseType: Class<T>
    ): T {
        val requestBody = if (body != null) {
            objectMapper.writeValueAsString(body).toRequestBody(JSON_MEDIA_TYPE)
        } else {
            "".toRequestBody(JSON_MEDIA_TYPE)
        }

        val request = Request.Builder()
            .url("$baseUrl$path")
            .post(requestBody)
            .build()

        return executeRequest(request, responseType)
    }

    /**
     * 执行POST请求（reified版本）
     */
    inline fun <reified T> post(
        path: String,
        body: Any? = null
    ): T {
        return post(path, body, T::class.java)
    }

    /**
     * 执行PUT请求
     */
    fun <T> put(
        path: String,
        body: Any? = null,
        responseType: Class<T>
    ): T {
        val requestBody = if (body != null) {
            objectMapper.writeValueAsString(body).toRequestBody(JSON_MEDIA_TYPE)
        } else {
            "".toRequestBody(JSON_MEDIA_TYPE)
        }

        val request = Request.Builder()
            .url("$baseUrl$path")
            .put(requestBody)
            .build()

        return executeRequest(request, responseType)
    }

    /**
     * 执行PUT请求（reified版本）
     */
    inline fun <reified T> put(
        path: String,
        body: Any? = null
    ): T {
        return put(path, body, T::class.java)
    }

    /**
     * 执行DELETE请求
     */
    fun delete(path: String) {
        val request = Request.Builder()
            .url("$baseUrl$path")
            .delete()
            .build()

        executeRequest(request, Unit::class.java)
    }

    /**
     * 执行HTTP请求并处理响应
     */
    fun <T> executeRequest(request: Request, responseType: Class<T>): T {
        try {
            okHttpClient.newCall(request).execute().use { response ->
                val responseBody = response.body?.string() ?: ""

                if (!response.isSuccessful) {
                    // 尝试解析错误响应
                    val failure: NewNanManagerException = try {
                        val errorResponse = objectMapper.readValue(responseBody, ErrorResponse::class.java)
                        val message = errorResponse.message?.takeIf { it.isNotEmpty() }
                            ?: errorResponse.detail?.takeIf { it.isNotEmpty() }
                        if (message == null) {
                            HttpException(response.code, response.message)
                        } else {
                            ApiException(
                                errorDetail = message,
                                apiCode = errorResponse.code,
                                errorCategory = errorResponse.error?.category,
                                machineCode = errorResponse.error?.code
                            ).apply {
                                statusCode = response.code
                                requestId = response.header("X-Request-ID") ?: errorResponse.requestId
                                traceId = errorResponse.traceId
                            }
                        }
                    } catch (e: JsonProcessingException) {
                        HttpException(response.code, response.message)
                    }
                    failure.requestId = failure.requestId ?: response.header("X-Request-ID")
                    failure.retryAfter = response.header("Retry-After")
                    throw failure
                }

                // 处理空响应
                if (responseType == Unit::class.java) {
                    @Suppress("UNCHECKED_CAST")
                    return Unit as T
                }

                // 解析JSON响应
                try {
                    return objectMapper.readValue(responseBody, responseType)
                } catch (e: JsonProcessingException) {
                    throw JsonParseException("Failed to parse response (${e.javaClass.simpleName})")
                }
            }
        } catch (e: IOException) {
            throw NetworkException("Network request failed", e)
        }
    }

    /**
     * 执行HTTP请求并处理响应（reified版本）
     */
    inline fun <reified T> executeRequest(request: Request): T {
        return executeRequest(request, T::class.java)
    }

    /** Execute an OpenAPI-generated request and preserve the facade error contract. */
    @PublishedApi
    internal fun <T> executeGenerated(
        request: () -> GeneratedApiResponse<T?>
    ): T? {
        try {
            return when (val response = request()) {
                is GeneratedSuccess -> response.data
                is GeneratedClientError -> throw mapGeneratedError(
                    response.statusCode,
                    response.message,
                    response.body,
                    response.headers
                )
                is GeneratedServerError -> throw mapGeneratedError(
                    response.statusCode,
                    response.message,
                    response.body,
                    response.headers
                )
                is GeneratedRedirection -> throw mapGeneratedError(
                    response.statusCode,
                    "redirect response",
                    null,
                    response.headers
                )
                is GeneratedInformational -> throw mapGeneratedError(
                    response.statusCode,
                    response.statusText,
                    null,
                    response.headers
                )
                else -> throw ConfigurationException("Generated API returned an unknown response type")
            }
        } catch (error: JsonProcessingException) {
            throw JsonParseException(
                "Failed to parse generated API response (${error.javaClass.simpleName})"
            )
        } catch (error: IOException) {
            throw NetworkException("Network request failed", error)
        } catch (error: IllegalStateException) {
            throw ConfigurationException("Generated API request is invalid", error)
        }
    }

    internal inline fun <reified T : Any> generated(
        noinline request: () -> GeneratedApiResponse<T?>
    ): T {
        return convertGenerated(executeGenerated(request) ?: throw JsonParseException(
            "Generated API returned an empty response"
        ))
    }

    @PublishedApi
    internal fun <T> generatedVoid(
        request: () -> GeneratedApiResponse<T?>
    ) {
        executeGenerated(request)
    }

    @PublishedApi
    internal inline fun <reified T : Any> toGenerated(value: Any): T = convertGenerated(value)

    @PublishedApi
    internal inline fun <reified T : Any> convertGenerated(value: Any): T {
        try {
            return generatedObjectMapper.convertValue(value, object : TypeReference<T>() {})
        } catch (error: IllegalArgumentException) {
            throw JsonParseException(
                "Failed to convert generated API model (${error.javaClass.simpleName})"
            )
        }
    }

    private fun mapGeneratedError(
        statusCode: Int,
        fallbackMessage: String?,
        body: Any?,
        headers: Map<String, List<String>>
    ): NewNanManagerException {
        val bodyText = body as? String
        val parsed = bodyText?.let { text ->
            try {
                objectMapper.readValue(text, ErrorResponse::class.java)
            } catch (_: JsonProcessingException) {
                null
            }
        }
        val message = parsed?.message?.takeIf { it.isNotBlank() }
            ?: parsed?.detail?.takeIf { it.isNotBlank() }
            ?: fallbackMessage?.takeIf { it.isNotBlank() }
            ?: "HTTP $statusCode"
        val failure: NewNanManagerException = if (parsed?.message != null || parsed?.detail != null) {
            ApiException(
                errorDetail = message,
                apiCode = parsed.code,
                errorCategory = parsed.error?.category,
                machineCode = parsed.error?.code
            ).apply {
                this.statusCode = statusCode
                requestId = parsed.requestId
                traceId = parsed.traceId
            }
        } else {
            HttpException(statusCode, message)
        }
        failure.requestId = failure.requestId ?: headerValue(headers, "X-Request-ID")
        failure.traceId = failure.traceId ?: headerValue(headers, "X-Trace-ID")
        failure.retryAfter = headerValue(headers, "Retry-After")
        return failure
    }

    private fun headerValue(headers: Map<String, List<String>>, name: String): String? {
        return headers.entries.firstOrNull { it.key.equals(name, ignoreCase = true) }
            ?.value
            ?.firstOrNull()
    }

    override fun close() {
        okHttpClient.connectionPool.evictAll()
        okHttpClient.dispatcher.executorService.shutdown()
        okHttpClient.cache?.let { cache ->
            try {
                cache.close()
            } catch (_: IOException) {
                // Cache cleanup is best effort after all requests have completed.
            }
        }
    }
}
