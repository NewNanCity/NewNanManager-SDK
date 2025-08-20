"""Town-related data models."""

from datetime import datetime
from typing import Optional

from pydantic import BaseModel, Field

from .common import PagedData
from .player import Player


class Town(BaseModel):
    """城镇实体."""

    id: int = Field(description="城镇ID")
    name: str = Field(description="城镇名称")
    level: int = Field(description="城镇等级")
    leader_id: Optional[int] = Field(default=None, description="城主ID")
    qq_group: Optional[str] = Field(default=None, description="QQ群号")
    description: Optional[str] = Field(default=None, description="城镇描述")
    created_at: datetime = Field(description="创建时间")
    updated_at: datetime = Field(description="更新时间")


class TownsListData(PagedData[Town]):
    """城镇列表数据."""

    towns: list[Town] = Field(default_factory=list, description="城镇列表")

    @property
    def items(self) -> list[Town]:
        """获取数据项列表."""
        return self.towns

    @items.setter
    def items(self, value: list[Town]) -> None:
        """设置数据项列表."""
        self.towns = value


class TownMembersData(PagedData[Player]):
    """城镇成员数据."""

    members: list[Player] = Field(default_factory=list, description="成员列表")

    @property
    def items(self) -> list[Player]:
        """获取数据项列表."""
        return self.members

    @items.setter
    def items(self, value: list[Player]) -> None:
        """设置数据项列表."""
        self.members = value
