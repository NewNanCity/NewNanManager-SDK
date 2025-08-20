"""Player-related data models."""

from datetime import datetime
from typing import Optional

from pydantic import BaseModel, Field

from .common import PagedData
from .enums import BanMode


class Player(BaseModel):
    """玩家实体."""

    id: int = Field(description="玩家ID")
    name: str = Field(description="玩家名称")
    town_id: Optional[int] = Field(default=None, description="所属城镇ID")
    qq: Optional[str] = Field(default=None, description="QQ号")
    qqguild: Optional[str] = Field(default=None, description="QQ频道")
    discord: Optional[str] = Field(default=None, description="Discord")
    in_qq_group: bool = Field(default=False, description="是否在QQ群中")
    in_qq_guild: bool = Field(default=False, description="是否在QQ频道中")
    in_discord: bool = Field(default=False, description="是否在Discord中")
    ban_mode: BanMode = Field(default=BanMode.NORMAL, description="封禁模式")
    ban_expire: Optional[datetime] = Field(default=None, description="封禁过期时间")
    ban_reason: Optional[str] = Field(default=None, description="封禁原因")
    created_at: datetime = Field(description="创建时间")
    updated_at: datetime = Field(description="更新时间")


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

    player_id: Optional[int] = Field(default=None, description="玩家ID")
    name: Optional[str] = Field(default=None, description="玩家名称")
    ip: Optional[str] = Field(default=None, description="IP地址")


class ValidateLoginData(BaseModel):
    """登录验证数据."""

    allowed: bool = Field(description="是否允许登录")
    player_id: Optional[int] = Field(default=None, description="玩家ID")
    reason: Optional[str] = Field(default=None, description="拒绝原因")
