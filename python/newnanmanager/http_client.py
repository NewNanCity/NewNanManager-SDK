"""HTTP client base class for NewNanManager SDK."""

import asyncio
import json
import logging
from typing import Any, Dict, Optional, Type, TypeVar, Union, overload
from urllib.parse import urlencode, urljoin

import aiohttp
from pydantic import BaseModel

from .config import AuthScheme, ClientConfig
from .exceptions import (
    ApiErrorException,
    ConnectionException,
    HttpException,
    NewNanManagerException,
    TimeoutException,
)

# 移除不再使用的统一响应格式导入
# from .models.common import ApiResponse, ErrorResponse

T = TypeVar("T", bound=BaseModel)

logger = logging.getLogger(__name__)


class HttpClient:
    """HTTP客户端基础类."""

    def __init__(self, config: ClientConfig) -> None:
        """初始化HTTP客户端.

        Args:
            config: 客户端配置
        """
        self.config = config
        self._session: Optional[aiohttp.ClientSession] = None

        # 设置默认请求头
        credential_header = (
            {"X-API-Token": config.token}
            if config.auth_scheme is AuthScheme.API_TOKEN
            else {"Authorization": f"Bearer {config.token}"}
        )
        self._headers = {
            **credential_header,
            "User-Agent": config.user_agent,
            "Accept": "application/json",
            "Content-Type": "application/json",
        }

    async def __aenter__(self) -> "HttpClient":
        """异步上下文管理器入口."""
        await self._ensure_session()
        return self

    async def __aexit__(self, exc_type, exc_val, exc_tb) -> None:
        """异步上下文管理器出口."""
        await self.close()

    async def _ensure_session(self) -> None:
        """确保会话已创建."""
        if self._session is None or self._session.closed:
            timeout = aiohttp.ClientTimeout(total=self.config.timeout)
            connector = aiohttp.TCPConnector(
                limit=self.config.connection_pool_size,
                limit_per_host=self.config.connection_pool_size_per_host,
                verify_ssl=self.config.verify_ssl,
            )
            self._session = aiohttp.ClientSession(
                headers=self._headers,
                timeout=timeout,
                connector=connector,
            )

    async def close(self) -> None:
        """关闭HTTP会话."""
        if self._session and not self._session.closed:
            await self._session.close()
            self._session = None

    def _build_url(self, endpoint: str) -> str:
        """构建完整的URL.

        Args:
            endpoint: API端点

        Returns:
            完整的URL
        """
        return urljoin(self.config.base_url.rstrip("/") + "/", endpoint.lstrip("/"))

    def _build_query_params(self, params: Optional[Dict[str, Any]]) -> str:
        """构建查询参数字符串.

        Args:
            params: 查询参数字典

        Returns:
            查询参数字符串
        """
        if not params:
            return ""

        # 过滤掉None值
        filtered_params = {k: v for k, v in params.items() if v is not None}
        if not filtered_params:
            return ""

        return "?" + urlencode(filtered_params)

    async def _make_request(
        self,
        method: str,
        endpoint: str,
        params: Optional[Dict[str, Any]] = None,
        json_data: Optional[Union[Dict[str, Any], BaseModel]] = None,
        response_model: Optional[Type[T]] = None,
        headers: Optional[Dict[str, str]] = None,
    ) -> Union[T, Dict[str, Any]]:
        """发送HTTP请求.

        Args:
            method: HTTP方法
            endpoint: API端点
            params: 查询参数
            json_data: JSON数据
            response_model: 响应模型类

        Returns:
            响应数据

        Raises:
            NewNanManagerException: 各种API异常
        """
        await self._ensure_session()

        url = self._build_url(endpoint)
        query_string = self._build_query_params(params)
        full_url = url + query_string

        # 准备请求数据
        request_json = None
        if json_data is not None:
            if isinstance(json_data, BaseModel):
                request_json = json_data.model_dump(exclude_none=True, by_alias=True)
            else:
                request_json = json_data  # type: ignore

        logger.debug("Making %s request", method)

        # 重试逻辑
        last_exception: Optional[Exception] = None
        attempts = self.config.max_retries + 1 if method in {"GET", "HEAD", "OPTIONS"} else 1
        for attempt in range(attempts):
            try:
                if self._session is None:
                    raise ConnectionException("HTTP session is not initialized")

                request_kwargs: dict[str, Any] = {
                    "json": request_json,
                    "allow_redirects": False,
                }
                if headers:
                    request_kwargs["headers"] = headers
                async with self._session.request(
                    method,
                    full_url,
                    **request_kwargs,
                ) as response:
                    result = await self._handle_response(response, response_model)
                    logger.debug(
                        "Request completed successfully in %d attempt(s)", attempt + 1
                    )
                    return result

            except (aiohttp.ClientError, asyncio.TimeoutError) as e:
                last_exception = e
                if attempt + 1 < attempts:
                    delay = self.config.retry_delay * (2**attempt)  # 指数退避
                    logger.warning(
                        "Request failed (attempt %d), retrying in %ss: %s",
                        attempt + 1, delay, type(e).__name__,
                    )
                    await asyncio.sleep(delay)
                    continue
                break

        # 处理最终失败
        if isinstance(last_exception, asyncio.TimeoutError):
            raise TimeoutException(
                f"Request timeout after {attempts} attempts"
            )
        elif isinstance(last_exception, aiohttp.ClientError):
            raise ConnectionException(f"Connection error: {last_exception}")
        else:
            raise NewNanManagerException(f"Unexpected error: {last_exception}")

    async def _handle_response(
        self,
        response: aiohttp.ClientResponse,
        response_model: Optional[Type[T]] = None,
    ) -> Union[T, Dict[str, Any]]:
        """处理HTTP响应.

        Args:
            response: HTTP响应
            response_model: 响应模型类

        Returns:
            解析后的响应数据

        Raises:
            HttpException: HTTP错误
            ApiErrorException: API错误
        """
        response_text = await response.text()
        logger.debug("Response status: %d", response.status)

        # 处理HTTP错误状态码
        if response.status >= 300:
            await self._handle_http_error(response, response_text)

        # 解析JSON响应
        try:
            response_data = json.loads(response_text) if response_text else {}
        except json.JSONDecodeError as e:
            raise NewNanManagerException(f"Failed to parse JSON response: {e}")

        # 新的响应格式：成功时直接返回数据，不再有统一包装
        # 直接返回响应数据
        if response_model:
            return response_model.model_validate(response_data)
        return response_data

    async def _handle_http_error(
        self,
        response: aiohttp.ClientResponse,
        response_text: str,
    ) -> None:
        """处理HTTP错误.

        Args:
            response: HTTP响应
            response_text: 响应文本

        Raises:
            HttpException: HTTP错误
            ApiErrorException: API错误
        """
        status_code = response.status
        request_id = response.headers.get("X-Request-ID")
        retry_after = response.headers.get("Retry-After")

        # 模板错误体优先使用 message/error；detail 仅作迁移期回退。
        try:
            error_data = json.loads(response_text) if response_text else {}
            if isinstance(error_data, dict):
                message = error_data.get("message") or error_data.get("detail")
                if not isinstance(message, str) or not message:
                    message = None
                nested = error_data.get("error")
                nested = nested if isinstance(nested, dict) else {}
                body_request_id = error_data.get("request_id")
                body_trace_id = error_data.get("trace_id")
                if request_id is None and isinstance(body_request_id, str):
                    request_id = body_request_id
                if message is not None:
                    api_code = error_data.get("code")
                    api_code = api_code if isinstance(api_code, int) and not isinstance(api_code, bool) else None
                    category = nested.get("category")
                    machine_code = nested.get("code")
                    raise ApiErrorException(
                        message=message,
                        error_code=status_code,
                        request_id=request_id,
                        details=error_data.get("detail") if isinstance(error_data.get("detail"), str) else None,
                        retry_after=retry_after,
                        api_code=api_code,
                        error_category=category if isinstance(category, str) else None,
                        machine_code=machine_code if isinstance(machine_code, str) else None,
                        trace_id=body_trace_id if isinstance(body_trace_id, str) else None,
                    )
        except (json.JSONDecodeError, ValueError):
            # 无法解析为JSON错误响应，继续处理为HTTP错误
            pass

        # 生成HTTP错误消息
        error_messages = {
            401: "Unauthorized: Invalid or missing API token",
            403: "Forbidden: Insufficient permissions",
            404: "Not found: The requested resource does not exist",
            429: "Too many requests: Rate limit exceeded",
            500: "Internal server error",
            502: "Bad gateway",
            503: "Service unavailable",
            504: "Gateway timeout",
        }

        message = error_messages.get(
            status_code, f"HTTP {status_code}: {response.reason}"
        )
        raise HttpException(
            message=message, status_code=status_code,
            request_id=request_id, retry_after=retry_after,
        )

    # HTTP方法的便捷函数
    @overload
    async def get(
        self,
        endpoint: str,
        params: Optional[Dict[str, Any]],
        response_model: Type[T],
        *,
        headers: Optional[Dict[str, str]] = None,
    ) -> T: ...

    @overload
    async def get(
        self,
        endpoint: str,
        params: Optional[Dict[str, Any]] = None,
        response_model: None = None,
        *,
        headers: Optional[Dict[str, str]] = None,
    ) -> Dict[str, Any]: ...

    async def get(
        self,
        endpoint: str,
        params: Optional[Dict[str, Any]] = None,
        response_model: Optional[Type[T]] = None,
        *,
        headers: Optional[Dict[str, str]] = None,
    ) -> Union[T, Dict[str, Any]]:
        """发送GET请求."""
        return await self._make_request(
            "GET", endpoint, params=params, response_model=response_model, headers=headers
        )

    @overload
    async def post(
        self,
        endpoint: str,
        json_data: Optional[Union[Dict[str, Any], BaseModel]],
        response_model: Type[T],
        *,
        headers: Optional[Dict[str, str]] = None,
    ) -> T: ...

    @overload
    async def post(
        self,
        endpoint: str,
        json_data: Optional[Union[Dict[str, Any], BaseModel]] = None,
        response_model: None = None,
        *,
        headers: Optional[Dict[str, str]] = None,
    ) -> Dict[str, Any]: ...

    async def post(
        self,
        endpoint: str,
        json_data: Optional[Union[Dict[str, Any], BaseModel]] = None,
        response_model: Optional[Type[T]] = None,
        *,
        headers: Optional[Dict[str, str]] = None,
    ) -> Union[T, Dict[str, Any]]:
        """发送POST请求."""
        return await self._make_request(
            "POST", endpoint, json_data=json_data, response_model=response_model, headers=headers
        )

    @overload
    async def put(
        self,
        endpoint: str,
        json_data: Optional[Union[Dict[str, Any], BaseModel]],
        response_model: Type[T],
        *,
        headers: Optional[Dict[str, str]] = None,
    ) -> T: ...

    @overload
    async def put(
        self,
        endpoint: str,
        json_data: Optional[Union[Dict[str, Any], BaseModel]] = None,
        response_model: None = None,
        *,
        headers: Optional[Dict[str, str]] = None,
    ) -> Dict[str, Any]: ...

    async def put(
        self,
        endpoint: str,
        json_data: Optional[Union[Dict[str, Any], BaseModel]] = None,
        response_model: Optional[Type[T]] = None,
        *,
        headers: Optional[Dict[str, str]] = None,
    ) -> Union[T, Dict[str, Any]]:
        """发送PUT请求."""
        return await self._make_request(
            "PUT", endpoint, json_data=json_data, response_model=response_model, headers=headers
        )

    async def delete(
        self,
        endpoint: str,
        params: Optional[Dict[str, Any]] = None,
        *,
        headers: Optional[Dict[str, str]] = None,
    ) -> Dict[str, Any]:
        """发送DELETE请求."""
        return await self._make_request("DELETE", endpoint, params=params, headers=headers)
