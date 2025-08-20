"""Server management service."""

from typing import Optional

from ..http_client import HttpClient
from ..models import (
    RegisterServerRequest,
    ServerDetailData,
    ServerRegistry,
    ServersListData,
    UpdateServerRequest,
)


class ServerService:
    """服务器管理服务."""

    def __init__(self, http_client: HttpClient) -> None:
        """初始化服务器服务.

        Args:
            http_client: HTTP客户端
        """
        self._http = http_client

    async def list_servers(
        self,
        page: Optional[int] = None,
        page_size: Optional[int] = None,
        search: Optional[str] = None,
        online_only: Optional[bool] = None,
    ) -> ServersListData:
        """获取服务器列表.

        Args:
            page: 页码
            page_size: 每页大小
            search: 搜索关键词
            online_only: 仅显示在线服务器

        Returns:
            服务器列表数据
        """
        params = {
            "page": page,
            "page_size": page_size,
            "search": search,
            "online_only": online_only,
        }

        return await self._http.get(
            "/api/v1/servers",
            params=params,
            response_model=ServersListData,
        )

    async def register_server(
        self,
        request: RegisterServerRequest,
    ) -> ServerRegistry:
        """注册服务器.

        Args:
            request: 注册服务器请求

        Returns:
            注册的服务器信息
        """
        return await self._http.post(
            "/api/v1/servers",
            json_data=request,
            response_model=ServerRegistry,
        )

    async def get_server(self, server_id: int) -> ServerRegistry:
        """获取服务器信息.

        Args:
            server_id: 服务器ID

        Returns:
            服务器信息
        """
        return await self._http.get(
            f"/api/v1/servers/{server_id}",
            response_model=ServerRegistry,
        )

    async def update_server(
        self,
        server_id: int,
        request: UpdateServerRequest,
    ) -> ServerRegistry:
        """更新服务器信息.

        Args:
            server_id: 服务器ID
            request: 更新请求

        Returns:
            更新后的服务器信息
        """
        return await self._http.put(
            f"/api/v1/servers/{server_id}",
            json_data=request,
            response_model=ServerRegistry,
        )

    async def delete_server(self, server_id: int) -> None:
        """删除服务器.

        Args:
            server_id: 服务器ID
        """
        await self._http.delete(f"/api/v1/servers/{server_id}")

    async def get_server_detail(self, server_id: int) -> ServerDetailData:
        """获取服务器详细信息.

        Args:
            server_id: 服务器ID

        Returns:
            服务器详细信息
        """
        return await self._http.get(
            f"/api/v1/servers/{server_id}/detail",
            response_model=ServerDetailData,
        )
