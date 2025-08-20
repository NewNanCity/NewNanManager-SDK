"""Monitoring service."""

from ..http_client import HttpClient
from ..models import (
    HeartbeatData,
    HeartbeatRequest,
    LatencyStatsData,
    ServerStatus,
)


class MonitorService:
    """监控服务."""

    def __init__(self, http_client: HttpClient) -> None:
        """初始化监控服务.

        Args:
            http_client: HTTP客户端
        """
        self._http = http_client

    async def heartbeat(
        self,
        server_id: int,
        request: HeartbeatRequest,
    ) -> HeartbeatData:
        """发送服务器心跳.

        Args:
            server_id: 服务器ID
            request: 心跳请求

        Returns:
            心跳响应数据
        """
        return await self._http.post(
            f"/api/v1/servers/{server_id}/heartbeat",
            json_data=request,
            response_model=HeartbeatData,
        )

    async def get_latency_stats(self, server_id: int) -> LatencyStatsData:
        """获取延迟统计.

        Args:
            server_id: 服务器ID

        Returns:
            延迟统计数据
        """
        return await self._http.get(
            f"/api/v1/servers/{server_id}/latency",
            response_model=LatencyStatsData,
        )

    async def get_server_status(self, server_id: int) -> ServerStatus:
        """获取服务器状态.

        Args:
            server_id: 服务器ID

        Returns:
            服务器状态
        """
        return await self._http.get(
            f"/api/v1/servers/{server_id}/status",
            response_model=ServerStatus,
        )
