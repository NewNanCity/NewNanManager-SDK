# NewNanManager Python SDK

Python SDK for NewNanManager API - Minecraft server management system.

## 安装

```bash
pip install newnanmanager-client
```

## 快速开始

```python
import asyncio
from newnanmanager import NewNanManagerClient

async def main():
    # 创建客户端
    async with NewNanManagerClient("https://your-server.com", "your-api-token") as client:
        # 获取服务器列表
        servers = await client.servers.list_servers()

        # 获取玩家列表
        players = await client.players.list_players()

        # 创建玩家
        new_player = await client.players.create_player(
            name="PlayerName",
            qq="123456789"
        )

if __name__ == "__main__":
    asyncio.run(main())
```

## 功能特性

- ✅ 玩家管理（创建、查询、更新、删除、封禁）
- ✅ 服务器管理（注册、查询、更新、删除）
- ✅ 城镇管理（创建、查询、更新、删除、成员管理）
- ✅ 监控服务（心跳、延迟统计、状态查询）
- ✅ Token管理（创建、查询、更新、删除）
- ✅ 完整的类型提示支持
- ✅ 异步/等待模式
- ✅ 自动错误处理
- ✅ 基于aiohttp的高性能HTTP客户端
- ✅ Pydantic数据验证
- ✅ 新的响应格式：成功时直接返回数据，错误时返回 `{"detail": "错误信息"}`

## 高级配置

```python
from newnanmanager import NewNanManagerClient, ClientConfig

# 使用配置对象
config = ClientConfig(
    base_url="https://your-server.com",
    token="your-api-token",
    timeout=60.0,
    user_agent="MyApp/1.0.0"
)

async with NewNanManagerClient.from_config(config) as client:
    # 使用客户端
    pass
```

## API使用示例

### 玩家管理

```python
# 获取玩家列表（分页）
players = await client.players.list_players(
    page=1,
    page_size=20,
    search="player_name",
    town_id=1,
    ban_mode=BanMode.NORMAL
)

# 创建玩家
from newnanmanager.models import CreatePlayerRequest

player = await client.players.create_player(
    CreatePlayerRequest(
        name="NewPlayer",
        qq="123456789",
        town_id=1,
        in_qq_group=True
    )
)

# 封禁玩家
from newnanmanager.models import BanPlayerRequest, BanMode

await client.players.ban_player(
    player.id,
    BanPlayerRequest(
        ban_mode=BanMode.TEMPORARY,
        duration_seconds=3600,
        reason="违规行为"
    )
)

# 解封玩家
await client.players.unban_player(player.id)
```

### 服务器管理

```python
from newnanmanager.models import RegisterServerRequest, ServerType

# 注册服务器
server = await client.servers.register_server(
    RegisterServerRequest(
        name="MyServer",
        address="127.0.0.1:25565",
        server_type=ServerType.MINECRAFT,
        description="我的Minecraft服务器"
    )
)

# 获取服务器详细信息
detail = await client.servers.get_server_detail(server.id)
```

### 监控服务

```python
from newnanmanager.models import HeartbeatRequest
import time

# 发送心跳
heartbeat = await client.monitor.heartbeat(
    server_id,
    HeartbeatRequest(
        timestamp=int(time.time()),
        current_players=10,
        max_players=50,
        tps=19.8,
        version="1.20.1"
    )
)

# 获取服务器状态
status = await client.monitor.get_server_status(server_id)

# 获取延迟统计
latency_stats = await client.monitor.get_latency_stats(server_id)
```

### 城镇管理

```python
from newnanmanager.models import CreateTownRequest

# 创建城镇
town = await client.towns.create_town(
    CreateTownRequest(
        name="MyTown",
        level=1,
        description="我的城镇"
    )
)

# 获取城镇成员
members = await client.towns.get_town_members(town.id)
```

### Token管理

```python
from newnanmanager.models import CreateApiTokenRequest

# 创建API Token
token_data = await client.tokens.create_api_token(
    CreateApiTokenRequest(
        name="MyToken",
        role="admin",
        description="管理员Token",
        expire_days=30
    )
)

print(f"新Token: {token_data.token_value}")

# 获取Token列表
tokens = await client.tokens.list_api_tokens()
```

## 错误处理

```python
from newnanmanager.exceptions import (
    NewNanManagerException,
    ApiErrorException,
    HttpException
)

try:
    player = await client.players.get_player(999)
except ApiErrorException as e:
    print(f"API错误: {e.error_code} - {e.message}")
    print(f"请求ID: {e.request_id}")
except HttpException as e:
    print(f"HTTP错误: {e.status_code} - {e.message}")
except NewNanManagerException as e:
    print(f"SDK错误: {e.message}")
```

## 测试

运行测试：

```bash
# 安装开发依赖
pip install -e ".[dev]"

# 运行测试
pytest

# 运行测试并生成覆盖率报告
pytest --cov=newnanmanager --cov-report=html
```

## 开发

设置开发环境：

```bash
# 克隆项目
git clone https://github.com/NewNanCity/NewNanManager-SDK.git
cd NewNanManager-SDK/clients/python

# 创建虚拟环境
python -m venv venv
source venv/bin/activate  # Linux/Mac
# 或
venv\Scripts\activate  # Windows

# 安装开发依赖
pip install -e ".[dev]"

# 运行代码格式化
black newnanmanager tests
isort newnanmanager tests

# 运行类型检查
mypy newnanmanager

# 运行代码检查
ruff check newnanmanager tests
```

## 许可证

MIT License
