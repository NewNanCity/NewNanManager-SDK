"""Data models for NewNanManager API."""

from .common import *
from .enums import *
from .ip import *
from .player import *
from .player_server import *
from .requests import *
from .server import *
from .token import *
from .town import *

__all__ = [
    # Enums
    "BanMode",
    "LoginAction",
    "ThreatLevel",
    "QueryStatus",
    # Common
    "ApiResponse",
    "ErrorResponse",
    "ErrorData",
    "PagedData",
    # Player
    "Player",
    "PlayersListData",
    "PlayerLoginInfo",
    "PlayerValidateInfo",
    "PlayerValidateResult",
    "ValidateData",
    # IP
    "IPInfo",
    "IPRiskInfo",
    "IPStatistics",
    "IPsListData",
    # Player-Server
    "PlayerServer",
    "OnlinePlayer",
    "PlayerServersData",
    "ServerPlayersData",
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
    "TownDetailResponse",
    # Token
    "ApiToken",
    "CreateApiTokenData",
    "ListApiTokensData",
    # Requests
    "CreatePlayerRequest",
    "UpdatePlayerRequest",
    "BanPlayerRequest",
    "ValidateRequest",
    "RegisterServerRequest",
    "UpdateServerRequest",
    "HeartbeatRequest",
    "CreateTownRequest",
    "UpdateTownRequest",
    "ManageTownMemberRequest",
    "CreateApiTokenRequest",
    "UpdateApiTokenRequest",
    "BanIPRequest",
    "UnbanIPRequest",
    "ListIPsRequest",
]
