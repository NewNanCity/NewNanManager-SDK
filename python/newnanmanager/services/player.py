"""Player management service."""

from typing import Optional

from ..http_client import HttpClient
from ..models import (
    BanMode,
    BanPlayerRequest,
    CreatePlayerRequest,
    Player,
    PlayersListData,
    UpdatePlayerRequest,
    ValidateData,
    ValidateRequest,
)


class PlayerService:
    """玩家管理服务."""

    def __init__(self, http_client: HttpClient) -> None:
        """初始化玩家服务.

        Args:
            http_client: HTTP客户端
        """
        self._http = http_client

    async def list_players(
        self,
        page: Optional[int] = None,
        page_size: Optional[int] = None,
        search: Optional[str] = None,
        town_id: Optional[int] = None,
        ban_mode: Optional[BanMode] = None,
        name: Optional[str] = None,
        qq: Optional[str] = None,
        qqguild: Optional[str] = None,
        discord: Optional[str] = None,
    ) -> PlayersListData:
        """获取玩家列表.

        Args:
            page: 页码
            page_size: 每页大小
            search: 搜索关键词（模糊搜索游戏名）
            town_id: 城镇ID
            ban_mode: 封禁模式
            name: 精确游戏名过滤
            qq: QQ号过滤
            qqguild: QQ频道ID过滤
            discord: Discord ID过滤

        Returns:
            玩家列表数据
        """
        params = {
            "page": page,
            "page_size": page_size,
            "search": search,
            "town_id": town_id,
            "ban_mode": ban_mode.value if ban_mode is not None else None,
            "name": name,
            "qq": qq,
            "qqguild": qqguild,
            "discord": discord,
        }

        return await self._http.get(
            "/api/v1/players",
            params=params,
            response_model=PlayersListData,
        )

    async def create_player(
        self,
        request: CreatePlayerRequest,
    ) -> Player:
        """创建玩家.

        Args:
            request: 创建玩家请求

        Returns:
            创建的玩家信息
        """
        return await self._http.post(
            "/api/v1/players",
            json_data=request,
            response_model=Player,
        )

    async def validate(
        self,
        request: ValidateRequest,
    ) -> ValidateData:
        """玩家验证（支持批处理）.

        Args:
            request: 验证请求

        Returns:
            验证结果
        """
        return await self._http.post(
            "/api/v1/players/validate", json_data=request, response_model=ValidateData
        )

    async def get_player(self, player_id: int) -> Player:
        """获取玩家详情.

        Args:
            player_id: 玩家ID

        Returns:
            玩家信息
        """
        return await self._http.get(
            f"/api/v1/players/{player_id}",
            response_model=Player,
        )

    async def update_player(
        self,
        player_id: int,
        request: UpdatePlayerRequest,
    ) -> Player:
        """更新玩家信息.

        Args:
            player_id: 玩家ID
            request: 更新请求

        Returns:
            更新后的玩家信息
        """
        return await self._http.put(
            f"/api/v1/players/{player_id}",
            json_data=request,
            response_model=Player,
        )

    async def delete_player(self, player_id: int) -> None:
        """删除玩家.

        Args:
            player_id: 玩家ID
        """
        await self._http.delete(f"/api/v1/players/{player_id}")

    async def ban_player(
        self,
        player_id: int,
        request: BanPlayerRequest,
    ) -> None:
        """封禁玩家.

        Args:
            player_id: 玩家ID
            request: 封禁请求
        """
        await self._http.post(
            f"/api/v1/players/{player_id}/ban",
            json_data=request,
        )

    async def unban_player(self, player_id: int) -> None:
        """解封玩家.

        Args:
            player_id: 玩家ID
        """
        await self._http.post(f"/api/v1/players/{player_id}/unban")
