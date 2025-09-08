"""Token management service."""

from ..http_client import HttpClient
from ..models import (
    ApiToken,
    CreateApiTokenData,
    CreateApiTokenRequest,
    ListApiTokensData,
    UpdateApiTokenRequest,
)


class TokenService:
    """Token管理服务."""

    def __init__(self, http_client: HttpClient) -> None:
        """初始化Token服务.

        Args:
            http_client: HTTP客户端
        """
        self._http = http_client

    async def list_api_tokens(
        self, page: int = 1, page_size: int = 20
    ) -> ListApiTokensData:
        """获取API Token列表.

        Args:
            page: 页码
            page_size: 每页数量

        Returns:
            Token列表数据
        """
        params = {"page": page, "page_size": page_size}
        return await self._http.get(
            "/api/v1/tokens",
            params=params,
            response_model=ListApiTokensData,
        )

    async def create_api_token(
        self,
        request: CreateApiTokenRequest,
    ) -> CreateApiTokenData:
        """创建API Token.

        Args:
            request: 创建Token请求

        Returns:
            创建的Token数据（包含Token值）
        """
        return await self._http.post(
            "/api/v1/tokens",
            json_data=request,
            response_model=CreateApiTokenData,
        )

    async def get_api_token(self, token_id: int) -> ApiToken:
        """获取API Token详情.

        Args:
            token_id: Token ID

        Returns:
            Token信息
        """
        return await self._http.get(
            f"/api/v1/tokens/{token_id}",
            params=None,
            response_model=ApiToken,
        )

    async def update_api_token(
        self,
        token_id: int,
        request: UpdateApiTokenRequest,
    ) -> ApiToken:
        """更新API Token.

        Args:
            token_id: Token ID
            request: 更新请求

        Returns:
            更新后的Token信息
        """
        return await self._http.put(
            f"/api/v1/tokens/{token_id}",
            json_data=request,
            response_model=ApiToken,
        )

    async def delete_api_token(self, token_id: int) -> None:
        """删除API Token.

        Args:
            token_id: Token ID
        """
        await self._http.delete(f"/api/v1/tokens/{token_id}")
