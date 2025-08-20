"""Data models for NewNanManager API."""

from .common import *
from .enums import *
from .player import *
from .requests import *
from .server import *
from .token import *
from .town import *

__all__ = [
    # Enums
    "BanMode",
    "LoginAction",
    "ServerType",
    # Common
    "ApiResponse",
    "ErrorResponse",
    "ErrorData",
    "PagedData",
    # Player
    "Player",
    "PlayersListData",
    "PlayerLoginInfo",
    "ValidateLoginData",
    # Server
    "ServerRegistry",
    "ServerStatus",
    "ServersListData",
    "ServerDetailData",
    "LatencyStatsData",
    "HeartbeatData",
    # Town
    "Town",
    "TownsListData",
    "TownMembersData",
    # Token
    "ApiToken",
    "CreateApiTokenData",
    "ListApiTokensData",
    # Requests
    "CreatePlayerRequest",
    "UpdatePlayerRequest",
    "BanPlayerRequest",
    "ValidateLoginRequest",
    "RegisterServerRequest",
    "UpdateServerRequest",
    "HeartbeatRequest",
    "CreateTownRequest",
    "UpdateTownRequest",
    "ManageTownMemberRequest",
    "CreateApiTokenRequest",
    "UpdateApiTokenRequest",
]
