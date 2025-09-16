"""Enumeration types for NewNanManager API."""

from enum import IntEnum


class BanMode(IntEnum):
    """封禁模式枚举."""

    NORMAL = 0  # 正常状态（未封禁）
    TEMPORARY = 1  # 临时封禁
    PERMANENT = 2  # 永久封禁


class LoginAction(IntEnum):
    """登录动作枚举."""

    LOGIN = 1  # 登录
    LOGOUT = 2  # 登出


class ThreatLevel(IntEnum):
    """IP威胁等级枚举."""

    LOW = 0  # 低威胁
    MEDIUM = 1  # 中等威胁
    HIGH = 2  # 高威胁
    CRITICAL = 3  # 严重威胁


class QueryStatus(IntEnum):
    """IP查询状态枚举."""

    PENDING = 0  # 待查询
    COMPLETED = 1  # 已完成
    FAILED = 2  # 查询失败
