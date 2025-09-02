"""Request data models for NewNanManager API."""

from typing import Optional

from pydantic import BaseModel, Field

from .enums import BanMode, ThreatLevel
from .player import PlayerLoginInfo, PlayerValidateInfo


class CreatePlayerRequest(BaseModel):
    """创建玩家请求."""

    name: str = Field(description="游戏ID")
    town_id: Optional[int] = Field(default=None, description="所属城镇ID")
    qq: Optional[str] = Field(default=None, description="QQ号")
    qqguild: Optional[str] = Field(default=None, description="QQ频道ID")
    discord: Optional[str] = Field(default=None, description="Discord ID")
    in_qq_group: bool = Field(default=False, description="是否在QQ群")
    in_qq_guild: bool = Field(default=False, description="是否在QQ频道")
    in_discord: bool = Field(default=False, description="是否在Discord群")


class UpdatePlayerRequest(BaseModel):
    """更新玩家请求."""

    name: Optional[str] = Field(default=None, description="玩家名称")
    town_id: Optional[int] = Field(default=None, description="所属城镇ID")
    qq: Optional[str] = Field(default=None, description="QQ号")
    qqguild: Optional[str] = Field(default=None, description="QQ频道")
    discord: Optional[str] = Field(default=None, description="Discord")
    in_qq_group: Optional[bool] = Field(default=None, description="是否在QQ群中")
    in_qq_guild: Optional[bool] = Field(default=None, description="是否在QQ频道中")
    in_discord: Optional[bool] = Field(default=None, description="是否在Discord中")


class BanPlayerRequest(BaseModel):
    """封禁玩家请求."""

    ban_mode: BanMode = Field(description="封禁模式")
    duration_seconds: Optional[int] = Field(
        default=None, description="封禁持续时间（秒）"
    )
    reason: str = Field(description="封禁原因")


class ValidateRequest(BaseModel):
    """玩家验证请求（支持批处理）."""

    players: list[PlayerValidateInfo] = Field(description="玩家列表（1-100个）")
    server_id: int = Field(description="服务器ID")
    login: bool = Field(description="是否为登录验证")
    timestamp: int = Field(default=0, description="请求时间戳")


class RegisterServerRequest(BaseModel):
    """注册服务器请求."""

    name: str = Field(description="服务器名称")
    address: str = Field(description="服务器地址")
    description: Optional[str] = Field(default=None, description="服务器描述")


class UpdateServerRequest(BaseModel):
    """更新服务器请求."""

    name: Optional[str] = Field(default=None, description="服务器名称")
    address: Optional[str] = Field(default=None, description="服务器地址")
    description: Optional[str] = Field(default=None, description="服务器描述")


class HeartbeatRequest(BaseModel):
    """心跳请求."""

    timestamp: Optional[int] = Field(default=None, description="时间戳")
    sequence_id: Optional[int] = Field(default=None, description="序列ID")
    current_players: Optional[int] = Field(default=None, description="当前玩家数")
    max_players: Optional[int] = Field(default=None, description="最大玩家数")
    tps: Optional[float] = Field(default=None, description="TPS")
    version: Optional[str] = Field(default=None, description="版本")
    motd: Optional[str] = Field(default=None, description="MOTD")
    last_rtt_ms: Optional[int] = Field(default=None, description="最后RTT（毫秒）")
    player_list: Optional[list[PlayerLoginInfo]] = Field(
        default=None, description="玩家列表"
    )


class CreateTownRequest(BaseModel):
    """创建城镇请求."""

    name: Optional[str] = Field(default=None, description="城镇名称")
    level: Optional[int] = Field(default=None, description="城镇等级")
    leader_id: Optional[int] = Field(default=None, description="城主ID")
    qq_group: Optional[str] = Field(default=None, description="QQ群号")
    description: Optional[str] = Field(default=None, description="城镇描述")


class UpdateTownRequest(BaseModel):
    """更新城镇请求."""

    name: Optional[str] = Field(default=None, description="城镇名称")
    level: Optional[int] = Field(default=None, description="城镇等级")
    leader_id: Optional[int] = Field(default=None, description="城主ID")
    qq_group: Optional[str] = Field(default=None, description="QQ群号")
    description: Optional[str] = Field(default=None, description="城镇描述")


class ManageTownMemberRequest(BaseModel):
    """管理城镇成员请求."""

    player_id: Optional[int] = Field(default=None, description="玩家ID")
    action: Optional[str] = Field(default=None, description="操作类型")


class CreateApiTokenRequest(BaseModel):
    """创建API Token请求."""

    name: Optional[str] = Field(default=None, description="Token名称")
    role: Optional[str] = Field(default=None, description="Token角色")
    description: Optional[str] = Field(default=None, description="Token描述")
    expire_days: Optional[int] = Field(default=None, description="过期天数")


class UpdateApiTokenRequest(BaseModel):
    """更新API Token请求."""

    name: Optional[str] = Field(default=None, description="Token名称")
    role: Optional[str] = Field(default=None, description="Token角色")
    description: Optional[str] = Field(default=None, description="Token描述")
    active: Optional[bool] = Field(default=None, description="是否激活")


# IP相关请求
class BanIPRequest(BaseModel):
    """封禁IP请求（支持批量）."""

    ips: list[str] = Field(description="IP地址列表")
    reason: str = Field(description="封禁原因")


class UnbanIPRequest(BaseModel):
    """解封IP请求（支持批量）."""

    ips: list[str] = Field(description="IP地址列表")


class ListIPsRequest(BaseModel):
    """IP列表请求."""

    page: int = Field(default=1, description="页码")
    page_size: int = Field(default=20, description="每页数量")
    banned_only: bool = Field(default=False, description="仅显示被封禁的IP")
    min_threat_level: Optional[ThreatLevel] = Field(
        default=None, description="最小威胁等级"
    )
    min_risk_score: Optional[int] = Field(default=None, description="最小风险评分")


# 玩家服务器关系相关请求
class SetPlayerOnlineRequest(BaseModel):
    """设置玩家在线状态请求."""

    player_id: int = Field(description="玩家ID")
    server_id: int = Field(description="服务器ID")
    online: bool = Field(description="是否在线")


class SetPlayersOfflineRequest(BaseModel):
    """设置玩家离线状态请求."""

    server_id: int = Field(description="服务器ID")
    player_ids: list[int] = Field(description="玩家ID列表（1-1000个）")
