"""Enumeration types for NewNanManager API."""

from enum import Enum, IntEnum


class BanMode(IntEnum):
    """封禁模式枚举."""

    NORMAL = 0  # 正常状态（未封禁）
    TEMPORARY = 1  # 临时封禁
    PERMANENT = 2  # 永久封禁


class LoginAction(str, Enum):
    """登录动作枚举."""

    LOGIN = "login"  # 登录
    LOGOUT = "logout"  # 登出


class ServerType(str, Enum):
    """服务器类型枚举."""

    MINECRAFT = "minecraft"  # Minecraft游戏服务器
    PROXY = "proxy"  # 代理服务器
    LOBBY = "lobby"  # 大厅服务器
