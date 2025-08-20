"""Exception classes for NewNanManager SDK."""

from typing import Optional


class NewNanManagerException(Exception):
    """NewNanManager API异常基类."""

    def __init__(
        self,
        message: str,
        error_code: Optional[int] = None,
        request_id: Optional[str] = None,
        details: Optional[str] = None,
    ) -> None:
        super().__init__(message)
        self.message = message
        self.error_code = error_code
        self.request_id = request_id
        self.details = details

    def __str__(self) -> str:
        result = self.message
        if self.request_id:
            result += f" (Request ID: {self.request_id})"
        if self.details:
            result += f" - Details: {self.details}"
        return result


class HttpException(NewNanManagerException):
    """HTTP请求异常."""

    def __init__(
        self,
        message: str,
        status_code: int,
        request_id: Optional[str] = None,
        details: Optional[str] = None,
    ) -> None:
        super().__init__(message, request_id=request_id, details=details)
        self.status_code = status_code


class ApiErrorException(NewNanManagerException):
    """API错误异常."""

    def __init__(
        self,
        message: str,
        error_code: int,
        request_id: Optional[str] = None,
        details: Optional[str] = None,
    ) -> None:
        super().__init__(message, error_code, request_id, details)


class TimeoutException(NewNanManagerException):
    """请求超时异常."""

    def __init__(self, message: str = "Request timeout") -> None:
        super().__init__(message)


class ConnectionException(NewNanManagerException):
    """连接异常."""

    def __init__(self, message: str = "Connection error") -> None:
        super().__init__(message)
