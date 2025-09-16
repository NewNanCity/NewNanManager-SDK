"""Common data models for NewNanManager API."""

from typing import Generic, Optional, TypeVar

from pydantic import BaseModel, Field

T = TypeVar("T")


class ApiResponse(BaseModel, Generic[T]):
    """API通用响应格式."""

    code: int = Field(description="响应代码，0表示成功")
    message: str = Field(description="响应消息")
    data: Optional[T] = Field(default=None, description="响应数据")
    request_id: str = Field(description="请求ID")


class ErrorData(BaseModel):
    """错误详情数据."""

    details: Optional[str] = Field(default=None, description="错误详情")


class ErrorResponse(ApiResponse[ErrorData]):
    """API错误响应."""

    pass


class PagedData(BaseModel, Generic[T]):
    """分页数据基类."""

    items: list[T] = Field(default_factory=list, description="数据列表")
    total: int = Field(description="总数量")
    page: int = Field(description="当前页码")
    page_size: int = Field(description="每页大小")
