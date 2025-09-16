"""IP management service."""

from typing import Optional

from ..http_client import HttpClient
from ..models import (
    BanIPRequest,
    UnbanIPRequest,
    IPInfo,
    IPsListData,
    IPStatistics,
    ListIPsRequest,
)


class IPService:
    """IP管理服务."""

    def __init__(self, http_client: HttpClient) -> None:
        """初始化IP服务.

        Args:
            http_client: HTTP客户端实例
        """
        self._http_client = http_client

    async def get_ip_info(self, ip: str) -> IPInfo:
        """获取IP信息（包含风险信息）.

        Args:
            ip: IP地址

        Returns:
            IP信息（包含风险信息）
        """
        return await self._http_client.get(
            f"/api/v1/ips/{ip}", params=None, response_model=IPInfo
        )

    async def ban_ip(self, request: BanIPRequest) -> None:
        """封禁IP（支持批量）.

        Args:
            request: 封禁请求（包含IP列表和原因）
        """
        await self._http_client.post("/api/v1/ips/ban", json_data=request)

    async def unban_ip(self, request: UnbanIPRequest) -> None:
        """解封IP（支持批量）.

        Args:
            request: 解封请求（包含IP列表）
        """
        await self._http_client.post("/api/v1/ips/unban", json_data=request)

    async def list_ips(self, request: Optional[ListIPsRequest] = None) -> IPsListData:
        """获取IP列表.

        Args:
            request: 列表请求参数

        Returns:
            IP列表数据
        """
        params = request.model_dump(exclude_none=True) if request else {}
        return await self._http_client.get(
            "/api/v1/ips", params=params, response_model=IPsListData
        )

    async def get_banned_ips(
        self, request: Optional[ListIPsRequest] = None
    ) -> IPsListData:
        """获取被封禁的IP列表.

        Args:
            request: 列表请求参数

        Returns:
            被封禁IP列表数据
        """
        params = request.model_dump(exclude_none=True) if request else {}
        return await self._http_client.get(
            "/api/v1/ips/banned", params=params, response_model=IPsListData
        )

    async def get_suspicious_ips(
        self, request: Optional[ListIPsRequest] = None
    ) -> IPsListData:
        """获取可疑IP列表.

        Args:
            request: 列表请求参数

        Returns:
            可疑IP列表数据
        """
        params = request.model_dump(exclude_none=True) if request else {}
        return await self._http_client.get(
            "/api/v1/ips/suspicious", params=params, response_model=IPsListData
        )

    async def get_high_risk_ips(
        self, request: Optional[ListIPsRequest] = None
    ) -> IPsListData:
        """获取高风险IP列表.

        Args:
            request: 列表请求参数

        Returns:
            高风险IP列表数据
        """
        params = request.model_dump(exclude_none=True) if request else {}
        return await self._http_client.get(
            "/api/v1/ips/high-risk", params=params, response_model=IPsListData
        )

    async def get_ip_statistics(self) -> IPStatistics:
        """获取IP统计信息.

        Returns:
            IP统计信息
        """
        return await self._http_client.get(
            "/api/v1/ips/statistics", params=None, response_model=IPStatistics
        )
