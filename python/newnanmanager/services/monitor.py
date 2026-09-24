"""Monitoring service."""

from typing import Optional

from ..http_client import HttpClient
from ..session import SessionContext
from ..models import (
    HeartbeatData,
    HeartbeatRequest,
    MonitorStatsData,
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
        session: Optional[SessionContext] = None,
    ) -> HeartbeatData:
        """发送服务器心跳.

        Args:
            server_id: 服务器ID
            request: 心跳请求

        Returns:
            心跳响应数据
        """
        return await self._http.post(
            f"/api/v1/monitor/{server_id}/heartbeat",
            json_data=request,
            response_model=HeartbeatData,
            headers=session.headers() if session is not None else None,
        )

    async def create_server_session(self, server_id: int) -> SessionContext:
        """Issue a server session tuple for subsequent fenced writes."""
        data = await self._http.post(
            f"/api/v1/monitor/{server_id}/session",
            json_data={},
        )
        if not isinstance(data, dict):
            raise TypeError("server session response must be an object")
        return SessionContext(
            id=data["session_id"],
            epoch=data["session_epoch"],
        )

    async def get_monitor_stats(
        self,
        server_id: int,
        since: Optional[int] = None,
        duration: Optional[int] = None,
        *,
        limit: Optional[int] = None,
        cursor: Optional[str] = None,
    ) -> MonitorStatsData:
        """获取监控统计信息.

        Parameters
        ----------
        server_id : int
            服务器ID。
        since : int, optional
            起始Unix时间戳；0表示当前时间减duration。
        duration : int, optional
            持续秒数，服务端默认3600，最多86400。
        limit : int, optional
            每页1..10000条，服务端默认1000。
        cursor : str, optional
            不透明续页游标，最多1024字符；续页保留相同服务器和时间范围。

        Returns
        -------
        MonitorStatsData
            一页监控记录，next_cursor缺失表示结束。
        """
        params: dict[str, object] = {}
        if since is not None:
            params["since"] = since
        if duration is not None:
            params["duration"] = duration
        if limit is not None:
            params["limit"] = limit
        if cursor is not None:
            params["cursor"] = cursor

        return await self._http.get(
            f"/api/v1/monitor/{server_id}/stats",
            params=params,
            response_model=MonitorStatsData,
        )
