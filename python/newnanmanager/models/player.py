"""Player-related data models."""

from typing import Optional

from pydantic import BaseModel, Field

from .common import PagedData
from .enums import BanMode


class Player(BaseModel):
    """玩家实体."""

    id: int = Field(description="玩家ID")
    name: str = Field(description="游戏ID")
    town_id: Optional[int] = Field(default=None, description="所属城镇ID")
    qq: Optional[str] = Field(default=None, description="QQ号")
    qqguild: Optional[str] = Field(default=None, description="QQ频道ID")
    discord: Optional[str] = Field(default=None, description="Discord ID")
    in_qq_group: bool = Field(description="是否在QQ群")
    in_qq_guild: bool = Field(description="是否在QQ频道")
    in_discord: bool = Field(description="是否在Discord群")
    ban_mode: BanMode = Field(description="封禁模式")
    ban_expire: Optional[str] = Field(
        default=None, description="封禁到期时间(ISO8601格式)"
    )
    ban_reason: Optional[str] = Field(default=None, description="封禁原因")
    created_at: str = Field(description="创建时间(ISO8601格式)")
    updated_at: str = Field(description="更新时间(ISO8601格式)")


class PlayersListData(PagedData[Player]):
    """玩家列表数据."""

    players: list[Player] = Field(default_factory=list, description="玩家列表")

    @property
    def items(self) -> list[Player]:
        """获取数据项列表."""
        return self.players

    @items.setter
    def items(self, value: list[Player]) -> None:
        """设置数据项列表."""
        self.players = value


class PlayerLoginInfo(BaseModel):
    """玩家登录信息."""

    player_id: int = Field(description="玩家ID")
    name: str = Field(description="玩家名")
    ip: str = Field(description="登录IP")


class PlayerValidateInfo(BaseModel):
    """单个玩家验证信息."""

    player_name: str = Field(description="玩家名")
    ip: str = Field(description="IP地址")
    client_version: Optional[str] = Field(default=None, description="客户端版本")
    protocol_version: Optional[str] = Field(default=None, description="协议版本")


class PlayerValidateResult(BaseModel):
    """单个玩家验证结果."""

    player_name: str = Field(description="玩家名")
    allowed: bool = Field(description="是否允许登录")
    player_id: Optional[int] = Field(default=None, description="玩家ID")
    reason: Optional[str] = Field(default=None, description="拒绝原因")
    newbie: bool = Field(default=False, description="是否为新玩家")
    ban_mode: Optional[BanMode] = Field(default=None, description="封禁模式")
    ban_expire: Optional[str] = Field(
        default=None, description="封禁到期时间(ISO8601格式)"
    )
    ban_reason: Optional[str] = Field(default=None, description="封禁原因")


class ValidateData(BaseModel):
    """玩家验证响应数据."""

    results: list[PlayerValidateResult] = Field(description="验证结果列表")
    processed_at: int = Field(description="处理时间戳")
