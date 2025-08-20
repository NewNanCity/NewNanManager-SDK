"""Token-related data models."""

from datetime import datetime
from typing import Optional

from pydantic import BaseModel, Field


class ApiToken(BaseModel):
    """API Token实体."""

    id: int = Field(description="Token ID")
    name: str = Field(description="Token名称")
    role: str = Field(description="Token角色")
    description: Optional[str] = Field(default=None, description="Token描述")
    active: bool = Field(description="是否激活")
    expire_at: Optional[datetime] = Field(default=None, description="过期时间")
    last_used_at: Optional[datetime] = Field(default=None, description="最后使用时间")
    last_used_ip: Optional[str] = Field(default=None, description="最后使用IP")
    created_at: datetime = Field(description="创建时间")
    updated_at: datetime = Field(description="更新时间")


class CreateApiTokenData(BaseModel):
    """创建Token响应数据."""

    token_info: ApiToken = Field(description="Token信息")
    token_value: str = Field(description="Token值（仅在创建时返回）")


class ListApiTokensData(BaseModel):
    """Token列表数据."""

    tokens: list[ApiToken] = Field(default_factory=list, description="Token列表")
