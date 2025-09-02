"""Server-related data models."""

from typing import Optional

from pydantic import BaseModel, Field

from .common import PagedData


class ServerRegistry(BaseModel):
    """服务器注册信息."""

    id: int = Field(description="服务器ID")
    name: str = Field(description="服务器名称")
    address: str = Field(description="服务器地址")
    description: Optional[str] = Field(default=None, description="服务器描述")
    active: bool = Field(description="是否激活")
    created_at: str = Field(description="创建时间(ISO8601格式)")
    updated_at: str = Field(description="更新时间(ISO8601格式)")


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
    expire_at: str = Field(description="状态失效时间(ISO8601格式)")
    last_heartbeat: str = Field(description="最后心跳时间(ISO8601格式)")


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
    last_updated: str = Field(description="最后更新时间(ISO8601格式)")


class HeartbeatData(BaseModel):
    """心跳响应数据."""

    received_at: int = Field(description="服务端接收时间戳(毫秒)")
    response_at: int = Field(description="服务端响应时间戳(毫秒)")
    expire_duration_ms: int = Field(description="状态过期时间(毫秒)")


class MonitorStatRecord(BaseModel):
    """监控统计记录."""

    timestamp: int = Field(description="统计时间戳")
    current_players: int = Field(description="当前在线人数")
    tps: Optional[float] = Field(default=None, description="服务器TPS")
    latency_ms: Optional[int] = Field(default=None, description="延迟毫秒")


class MonitorStatsData(BaseModel):
    """监控统计数据."""

    server_id: int = Field(description="服务器ID")
    stats: list[MonitorStatRecord] = Field(
        default_factory=list, description="监控统计信息列表"
    )
