"""Town management service."""

from typing import Optional

from ..http_client import HttpClient
from ..models import (
    CreateTownRequest,
    ManageTownMemberRequest,
    Town,
    TownMembersData,
    TownsListData,
    UpdateTownRequest,
)


class TownService:
    """城镇管理服务."""

    def __init__(self, http_client: HttpClient) -> None:
        """初始化城镇服务.

        Args:
            http_client: HTTP客户端
        """
        self._http = http_client

    async def list_towns(
        self,
        page: Optional[int] = None,
        page_size: Optional[int] = None,
        search: Optional[str] = None,
        min_level: Optional[int] = None,
        max_level: Optional[int] = None,
    ) -> TownsListData:
        """获取城镇列表.

        Args:
            page: 页码
            page_size: 每页大小
            search: 搜索关键词
            min_level: 最小等级
            max_level: 最大等级

        Returns:
            城镇列表数据
        """
        params = {
            "page": page,
            "page_size": page_size,
            "search": search,
            "min_level": min_level,
            "max_level": max_level,
        }

        return await self._http.get(
            "/api/v1/towns",
            params=params,
            response_model=TownsListData,
        )

    async def create_town(
        self,
        request: CreateTownRequest,
    ) -> Town:
        """创建城镇.

        Args:
            request: 创建城镇请求

        Returns:
            创建的城镇信息
        """
        return await self._http.post(
            "/api/v1/towns",
            json_data=request,
            response_model=Town,
        )

    async def get_town(self, town_id: int) -> Town:
        """获取城镇详情.

        Args:
            town_id: 城镇ID

        Returns:
            城镇信息
        """
        return await self._http.get(
            f"/api/v1/towns/{town_id}",
            response_model=Town,
        )

    async def update_town(
        self,
        town_id: int,
        request: UpdateTownRequest,
    ) -> Town:
        """更新城镇信息.

        Args:
            town_id: 城镇ID
            request: 更新请求

        Returns:
            更新后的城镇信息
        """
        return await self._http.put(
            f"/api/v1/towns/{town_id}",
            json_data=request,
            response_model=Town,
        )

    async def delete_town(self, town_id: int) -> None:
        """删除城镇.

        Args:
            town_id: 城镇ID
        """
        await self._http.delete(f"/api/v1/towns/{town_id}")

    async def get_town_members(
        self,
        town_id: int,
        page: Optional[int] = None,
        page_size: Optional[int] = None,
    ) -> TownMembersData:
        """获取城镇成员列表.

        Args:
            town_id: 城镇ID
            page: 页码
            page_size: 每页大小

        Returns:
            城镇成员数据
        """
        params = {
            "page": page,
            "page_size": page_size,
        }

        return await self._http.get(
            f"/api/v1/towns/{town_id}/members",
            params=params,
            response_model=TownMembersData,
        )

    async def manage_town_member(
        self,
        town_id: int,
        request: ManageTownMemberRequest,
    ) -> None:
        """管理城镇成员.

        Args:
            town_id: 城镇ID
            request: 成员管理请求
        """
        await self._http.post(
            f"/api/v1/towns/{town_id}/members",
            json_data=request,
        )
