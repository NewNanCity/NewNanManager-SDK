# NewNanManager Python SDK

Python SDK for NewNanManager API - Minecraft server management system.

## HTTP 行为

默认超时 30 秒，发送 `Authorization: Bearer ...`。需要 `X-API-Token` 时在 `ClientConfig` 或 `NewNanManagerClient` 传入 `AuthScheme.API_TOKEN`；每个请求只发送选中的一种凭证头。不会自动跟随 3xx 重定向。Debug 日志只记录方法、状态和重试次数，不记录请求或响应体。

服务器插件先调用 `await client.monitor.create_server_session(server_id)`，再把返回的 `SessionContext` 传给 `players.validate`、`monitor.heartbeat` 和 `player_servers.set_players_offline`。会话 ID 遵循服务端 32–64 字符约束；省略会话参数时保留旧 HTTP 入口，不会伪造 fencing 头。

只有 GET/HEAD/OPTIONS 的传输错误会按 `max_retries` 重试；POST/PUT/DELETE 不自动重发，避免重复创建 Token 或改变封禁时限。`ApiErrorException` 和 `HttpException` 保留 `status_code`、`request_id`、`retry_after`；后者两项在响应头缺失时为 `None`。

## 安装

需要 Python >=3.10，运行依赖要求 `aiohttp>=3.14.3`。Python 3.9 不再受支持；升级 SDK 前先升级解释器，再重新解析下游依赖锁。Black、Mypy 和 Ruff 的最低检查目标同步为 Python 3.10。

本轮源码修复尚未发布；以下包名不表示公共源的版本已经包含这些修改。

```bash
pip install newnanmanager-client
```

## 快速开始

新增契约：Token 请求与响应提供 `server_id`；状态和历史记录提供可选 `measurement_type/latency_metric`。统计通过 `get_monitor_stats(server_id, since, duration, limit=1000, cursor=cursor)` 逐页读取，返回 `next_cursor`。旧位置参数保持不变，新分页参数仅接受关键字。

字段缺失时保留 `None`。名称、Token绑定、0..1000人完整快照和分页上限见 [契约与分页](../docs/contracts.md)；本地回归覆盖这些字段及快照传输，不代表真实服务验收。

`await client.tokens.list_api_tokens(page=1, page_size=20)` 返回 `tokens/total/page/page_size`。手工构造 `ListApiTokensData` 或编写响应fixture时，必须提供服务端真实的三个分页字段；只传tokens的旧构造会触发Pydantic校验错误。

## 受控测试环境

`requirements-test-py310.txt`和`requirements-test-py312.txt`分别锁定Windows/CPython3.10、3.12的运行依赖并包含分发文件哈希。两个锁均使用aiohttp3.14.3，3.10额外需要async-timeout。测试使用标准库unittest，不安装开发、文档或示例extras。

```powershell
uv --no-config venv --python 3.10.19 .venv-py310
uv --no-config pip install --python .venv-py310/Scripts/python.exe --require-hashes --only-binary=:all: --default-index https://pypi.org/simple --keyring-provider disabled -r requirements-test-py310.txt
.\.venv-py310\Scripts\python.exe -B -m unittest discover -s tests -v
```

验证3.12时，将解释器改为3.12.12，并使用独立`.venv-py312`与`requirements-test-py312.txt`。本轮两个隔离环境各11项测试、依赖一致性检查通过，逐版本PyPI公告核查分别15、14包均无匹配。wheel的最低版本及aiohttp约束检查通过，并在3.10中实际安装后导入成功；详细依据和锁生成命令见[依赖验收](../docs/audits/2026-09-06-dependency-acceptance.md)。这些锁不覆盖其他操作系统、解释器版本、extras或下游项目自己的依赖锁。

```python
import asyncio
from newnanmanager import NewNanManagerClient
from newnanmanager.models import CreatePlayerRequest

async def main():
    # 创建客户端
    async with NewNanManagerClient("https://your-server.com", "your-api-token") as client:
        # 获取服务器列表
        servers = await client.servers.list_servers()

        # 获取玩家列表
        players = await client.players.list_players()

        # 创建玩家
        new_player = await client.players.create_player(
            CreatePlayerRequest(name="PlayerName", qq="123456789")
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
from newnanmanager.models import CreateServerRequest

# 注册服务器
server = await client.servers.create_server(
    CreateServerRequest(
        name="MyServer",
        address="127.0.0.1:25565",
        description="我的Minecraft服务器"
    )
)

# 获取服务器信息
detail = await client.servers.get_server(server.id, detail=True)
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
status = (await client.servers.get_server(server_id, detail=True)).status

# 获取延迟统计
latency_stats = await client.monitor.get_monitor_stats(server_id)
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

# 获取城镇详情（包含成员）
detail = await client.towns.get_town(town.id, detail=True)
members = detail.members
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

在 `python/` 中运行本地回归，无需真实服务：

```bash
python -B -m unittest discover -s tests -v
```

该命令验证导入、日志、重试及 loopback 重定向；不代表真实服务器集成验收或全代码覆盖。已安装开发依赖时也可使用 pytest：

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
cd NewNanManager-SDK/python

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
