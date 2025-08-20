"""Server-related data models."""

from datetime import datetime
from typing import Optional

from pydantic import BaseModel, Field

from .common import PagedData
from .enums import ServerType


class ServerRegistry(BaseModel):
    """服务器注册信息."""

    id: int = Field(description="服务器ID")
    name: str = Field(description="服务器名称")
    address: str = Field(description="服务器地址")
    server_type: Optional[ServerType] = Field(default=None, description="服务器类型")
    description: Optional[str] = Field(default=None, description="服务器描述")
    created_at: datetime = Field(description="创建时间")
    updated_at: datetime = Field(description="更新时间")


class ServerStatus(BaseModel):
    """服务器状态."""

    server_id: int = Field(description="服务器ID")
    online: bool = Field(description="是否在线")
    current_players: int = Field(description="当前玩家数")
    max_players: int = Field(description="最大玩家数")
    latency_ms: Optional[int] = Field(default=None, description="延迟（毫秒）")
    tps: Optional[float] = Field(default=None, description="TPS（每秒tick数）")
    version: Optional[str] = Field(default=None, description="服务器版本")
    motd: Optional[str] = Field(default=None, description="服务器MOTD")
    expire_at: datetime = Field(description="状态过期时间")
    last_heartbeat: datetime = Field(description="最后心跳时间")


class ServersListData(PagedData[ServerRegistry]):
    """服务器列表数据."""

    servers: list[ServerRegistry] = Field(
        default_factory=list, description="服务器列表"
    )

    @property
    def items(self) -> list[ServerRegistry]:
        """获取数据项列表."""
        return self.servers

    @items.setter
    def items(self, value: list[ServerRegistry]) -> None:
        """设置数据项列表."""
        self.servers = value


class ServerDetailData(BaseModel):
    """服务器详细信息数据."""

    server: ServerRegistry = Field(description="服务器信息")
    status: Optional[ServerStatus] = Field(default=None, description="服务器状态")


class LatencyStatsData(BaseModel):
    """延迟统计数据."""

    server_id: int = Field(description="服务器ID")
    count: int = Field(description="统计数量")
    current: int = Field(description="当前延迟")
    average: int = Field(description="平均延迟")
    min: int = Field(description="最小延迟")
    max: int = Field(description="最大延迟")
    variance: float = Field(description="方差")
    last_updated: datetime = Field(description="最后更新时间")


class HeartbeatData(BaseModel):
    """心跳响应数据."""

    received_at: int = Field(description="接收时间戳")
    response_at: int = Field(description="响应时间戳")
    sequence_id: int = Field(description="序列ID")
    server_time: int = Field(description="服务器时间")
    status: str = Field(description="状态")
    next_heartbeat: int = Field(description="下次心跳时间")
    expire_at: int = Field(description="过期时间")
