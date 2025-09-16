"""Town management service."""

from typing import Optional

from ..http_client import HttpClient
from ..models import (
    CreateTownRequest,
    Town,
    TownsListData,
    TownDetailResponse,
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
        params: dict[str, int | str] = {
            "page": page or 1,
            "page_size": page_size or 20,
        }

        # 只添加非None的可选参数
        if search is not None:
            params["search"] = search
        if min_level is not None:
            params["min_level"] = min_level
        if max_level is not None:
            params["max_level"] = max_level

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

    async def get_town(self, town_id: int, detail: bool = False) -> TownDetailResponse:
        """获取城镇详情.

        Args:
            town_id: 城镇ID
            detail: 是否返回详细信息（包括镇长和成员）

        Returns:
            城镇详细信息
        """
        params = {"detail": detail} if detail else None
        return await self._http.get(
            f"/api/v1/towns/{town_id}",
            params=params,
            response_model=TownDetailResponse,
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
