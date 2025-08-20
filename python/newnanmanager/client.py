"""Main client class for NewNanManager SDK."""

from typing import Optional

from .config import ClientConfig
from .http_client import HttpClient
from .services import (
    MonitorService,
    PlayerService,
    ServerService,
    TokenService,
    TownService,
)


class NewNanManagerClient:
    """NewNanManager API客户端."""

    def __init__(
        self,
        base_url: Optional[str] = None,
        token: Optional[str] = None,
        *,
        config: Optional[ClientConfig] = None,
        timeout: float = 30.0,
        user_agent: str = "NewNanManager-Python-SDK/1.0.0",
        max_retries: int = 3,
        retry_delay: float = 1.0,
    ) -> None:
        """初始化NewNanManager客户端.

        Args:
            base_url: API基础URL
            token: API Token
            config: 客户端配置（如果提供，将忽略其他参数）
            timeout: HTTP请求超时时间（秒）
            user_agent: 用户代理字符串
            max_retries: 最大重试次数
            retry_delay: 重试延迟（秒）
        """
        if config is not None:
            self._config = config
        else:
            if base_url is None or token is None:
                raise ValueError(
                    "base_url and token are required when config is not provided"
                )

            self._config = ClientConfig(
                base_url=base_url,
                token=token,
                timeout=timeout,
                user_agent=user_agent,
                max_retries=max_retries,
                retry_delay=retry_delay,
            )

        self._http_client = HttpClient(self._config)

        # 初始化各个服务
        self.players = PlayerService(self._http_client)
        self.servers = ServerService(self._http_client)
        self.towns = TownService(self._http_client)
        self.monitor = MonitorService(self._http_client)
        self.tokens = TokenService(self._http_client)

    @classmethod
    def from_config(cls, config: ClientConfig) -> "NewNanManagerClient":
        """从配置对象创建客户端.

        Args:
            config: 客户端配置

        Returns:
            NewNanManager客户端实例
        """
        return cls(config=config)

    async def __aenter__(self) -> "NewNanManagerClient":
        """异步上下文管理器入口."""
        await self._http_client.__aenter__()
        return self

    async def __aexit__(self, exc_type, exc_val, exc_tb) -> None:
        """异步上下文管理器出口."""
        await self._http_client.__aexit__(exc_type, exc_val, exc_tb)

    async def close(self) -> None:
        """关闭客户端连接."""
        await self._http_client.close()

    @property
    def config(self) -> ClientConfig:
        """获取客户端配置."""
        return self._config
