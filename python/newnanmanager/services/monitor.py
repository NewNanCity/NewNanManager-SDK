"""Monitoring service."""

from typing import Optional

from ..http_client import HttpClient
from ..models import (
    HeartbeatData,
    HeartbeatRequest,
    LatencyStatsData,
    MonitorStatsData,
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

    async def get_monitor_stats(
        self,
        server_id: int,
        since: Optional[int] = None,
        duration: Optional[int] = None,
    ) -> MonitorStatsData:
        """获取监控统计信息.

        Args:
            server_id: 服务器ID
            since: 起始时间戳(Unix时间戳，0表示当前时间-duration)
            duration: 持续时间(秒，默认3600秒)

        Returns:
            监控统计数据
        """
        params = {}
        if since is not None:
            params["since"] = since
        if duration is not None:
            params["duration"] = duration

        return await self._http.get(
            f"/api/v1/servers/{server_id}/monitor",
            params=params,
            response_model=MonitorStatsData,
        )
