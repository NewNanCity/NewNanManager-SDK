"""Player-server relationship data models."""

from pydantic import BaseModel, Field

from .common import PagedData


class PlayerServer(BaseModel):
    """玩家服务器关系信息."""

    player_id: int = Field(description="玩家ID")
    server_id: int = Field(description="服务器ID")
    online: bool = Field(description="是否在线")
    joined_at: str = Field(description="加入时间(ISO8601格式)")
    created_at: str = Field(description="创建时间(ISO8601格式)")
    updated_at: str = Field(description="更新时间(ISO8601格式)")


class OnlinePlayer(BaseModel):
    """在线玩家信息."""

    player_id: int = Field(description="玩家ID")
    player_name: str = Field(description="玩家名")
    server_id: int = Field(description="服务器ID")
    server_name: str = Field(description="服务器名")
    joined_at: str = Field(description="加入时间(ISO8601格式)")


class PlayerServersData(BaseModel):
    """玩家服务器关系列表数据."""

    servers: list[PlayerServer] = Field(default_factory=list, description="服务器关系列表")
    total: int = Field(description="总数")


class ServerPlayersData(PagedData[OnlinePlayer]):
    """服务器在线玩家数据."""

    players: list[OnlinePlayer] = Field(default_factory=list, description="在线玩家列表")

    @property
    def items(self) -> list[OnlinePlayer]:
        """获取数据项列表."""
        return self.players

    @items.setter
    def items(self, value: list[OnlinePlayer]) -> None:
        """设置数据项列表."""
        self.players = value
