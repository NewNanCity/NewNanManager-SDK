package com.nanmanager.client.exceptions

/**
 * NewNanManager API异常基类
 */
open class NanManagerException(
    val code: Int,
    message: String,
    cause: Throwable? = null
) : Exception(message, cause)

/**
 * 认证异常
 */
class AuthenticationException(
    message: String,
    cause: Throwable? = null
) : NanManagerException(401, message, cause)

/**
 * 网络异常
 */
class NetworkException(
    message: String,
    cause: Throwable? = null
) : NanManagerException(0, message, cause)

/**
 * 服务器异常
 */
class ServerException(
    code: Int,
    message: String,
    cause: Throwable? = null
) : NanManagerException(code, message, cause)

/**
 * 客户端异常
 */
class ClientException(
    code: Int,
    message: String,
    cause: Throwable? = null
) : NanManagerException(code, message, cause)
