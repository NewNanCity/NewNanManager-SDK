"""NewNanManager Python SDK.

Python SDK for NewNanManager API - Minecraft server management system.
"""

from ._version import (
    __author__,
    __description__,
    __email__,
    __license__,
    __url__,
    __version__,
)
from .client import NewNanManagerClient
from .config import ClientConfig
from .exceptions import (
    ApiErrorException,
    ConnectionException,
    HttpException,
    NewNanManagerException,
    TimeoutException,
)
from .models import *

__all__ = [
    # Version info
    "__version__",
    "__author__",
    "__email__",
    "__license__",
    "__description__",
    "__url__",
    # Main classes
    "NewNanManagerClient",
    "ClientConfig",
    # Exceptions
    "NewNanManagerException",
    "ApiErrorException",
    "HttpException",
    "ConnectionException",
    "TimeoutException",
]
