package com.nanmanager.bukkit.exceptions

/**
 * NewNanManager API异常基类
 */
open class NewNanManagerException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause) {
    var requestId: String? = null
        internal set
    var retryAfter: String? = null
        internal set
    var traceId: String? = null
        internal set
}

/**
 * HTTP请求异常
 */
class HttpException(
    val statusCode: Int,
    message: String,
    cause: Throwable? = null
) : NewNanManagerException("HTTP $statusCode: $message", cause)

/**
 * API错误异常
 */
class ApiException(
    val errorDetail: String,
    cause: Throwable? = null,
    val apiCode: Int? = null,
    val errorCategory: String? = null,
    val machineCode: String? = null
) : NewNanManagerException("API Error: $errorDetail", cause) {
    var statusCode: Int? = null
        internal set
}

/**
 * 网络连接异常
 */
class NetworkException(
    message: String,
    cause: Throwable? = null
) : NewNanManagerException("Network Error: $message", cause)

/**
 * JSON解析异常
 */
class JsonParseException(
    message: String,
    cause: Throwable? = null
) : NewNanManagerException("JSON Parse Error: $message", cause)

/**
 * 配置异常
 */
class ConfigurationException(
    message: String,
    cause: Throwable? = null
) : NewNanManagerException("Configuration Error: $message", cause)
