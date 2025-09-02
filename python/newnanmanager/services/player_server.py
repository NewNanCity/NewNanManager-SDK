"""Player-server relationship management service."""

from typing import Any, Optional

from ..http_client import HttpClient
from ..models import (
    PlayerServersData,
    ServerPlayersData,
)
from ..models.requests import SetPlayersOfflineRequest


class PlayerServerService:
    """玩家服务器关系管理服务."""

    def __init__(self, http_client: HttpClient) -> None:
        """初始化玩家服务器关系服务.

        Args:
            http_client: HTTP客户端实例
        """
        self._http_client = http_client

    async def get_player_servers(
        self, player_id: int, online_only: bool = False
    ) -> PlayerServersData:
        """获取玩家的服务器关系.

        Args:
            player_id: 玩家ID
            online_only: 仅显示在线服务器

        Returns:
            玩家服务器关系数据
        """
        params = {"online_only": online_only} if online_only else {}
        return await self._http_client.get(
            f"/api/v1/players/{player_id}/servers",
            params=params,
            response_model=PlayerServersData,
        )

    async def get_server_players(
        self,
        page: int = 1,
        page_size: int = 50,
        search: Optional[str] = None,
        server_id: Optional[int] = None,
        online_only: bool = False,
    ) -> ServerPlayersData:
        """获取服务器玩家.

        Args:
            page: 页码
            page_size: 每页数量
            search: 搜索玩家名
            server_id: 服务器ID过滤
            online_only: 仅显示在线玩家

        Returns:
            服务器玩家数据
        """
        params: dict[str, Any] = {"page": page, "page_size": page_size}
        if search:
            params["search"] = search
        if server_id:
            params["server_id"] = server_id
        if online_only:
            params["online_only"] = online_only

        return await self._http_client.get(
            "/api/v1/server-players", params=params, response_model=ServerPlayersData
        )

    async def set_players_offline(
        self, request: SetPlayersOfflineRequest
    ) -> dict[str, Any]:
        """设置玩家离线状态 - 在玩家退出时调用.

        Args:
            request: 设置玩家离线状态请求

        Returns:
            空响应
        """
        return await self._http_client.post(
            "/api/v1/servers/players/offline",
            json_data=request.model_dump(),
        )
