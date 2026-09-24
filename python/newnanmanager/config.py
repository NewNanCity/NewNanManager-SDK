"""Configuration classes for NewNanManager SDK."""

from enum import Enum

from pydantic import BaseModel, ConfigDict, Field

from ._version import USER_AGENT


class AuthScheme(str, Enum):
    """Credential header selected for API requests."""

    BEARER = "bearer"
    API_TOKEN = "api-token"


class ClientConfig(BaseModel):
    """NewNanManager客户端配置."""

    model_config = ConfigDict(frozen=True)  # 使配置不可变

    base_url: str = Field(description="API基础URL")
    token: str = Field(description="API Token")
    auth_scheme: AuthScheme = Field(
        default=AuthScheme.BEARER,
        description="认证方案；每个请求只发送一种凭证头",
    )
    timeout: float = Field(default=30.0, description="HTTP请求超时时间（秒）")
    user_agent: str = Field(default=USER_AGENT, description="用户代理字符串")
    max_retries: int = Field(default=3, description="最大重试次数")
    retry_delay: float = Field(default=1.0, description="重试延迟（秒）")

    # 高级配置
    verify_ssl: bool = Field(default=True, description="是否验证SSL证书")
    connection_pool_size: int = Field(default=100, description="连接池大小")
    connection_pool_size_per_host: int = Field(
        default=30, description="每个主机的连接池大小"
    )
