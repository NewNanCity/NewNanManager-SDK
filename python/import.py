import asyncio
import itertools
import json
from pathlib import Path
from typing import Iterable, Literal

import aiofiles
from pydantic import BaseModel

from newnanmanager import NewNanManagerClient
from newnanmanager.models.enums import BanMode
from newnanmanager.models.requests import (
    BanPlayerRequest,
    CreatePlayerRequest,
    CreateServerRequest,
    CreateTownRequest,
    UpdateTownRequest,
)


class Player(BaseModel):
    id: int
    name: str
    qq: str | None
    qqguild: str | None
    discord: str | None
    inqqgroup: Literal[0, 1]
    inqqguild: Literal[0, 1]
    indiscord: Literal[0, 1]
    town: int | None
    ban_mode: Literal[0, 1, 2]
    ban_expire: None
    cur_server: None


class Town(BaseModel):
    id: int
    name: str
    level: int
    leader: int | None
    qqgroup: str | None


async def f():
    client = NewNanManagerClient(
        base_url="https://manager-api.newnan.city",
        token="oPjrb509VqEApZzioDbELbH5sJBfhrvu",
    )
    try:
        await client.servers.create_server(
            request=CreateServerRequest(
                name="牛腩主服",
                address="minecraft1.newnan.city",
                description="牛腩核心服务器",
            )
        )
        await client.servers.create_server(
            request=CreateServerRequest(
                name="曼巴小镇",
                address="man.newnan.city",
                description="Man!",
            )
        )

        player_ips: set[tuple[int, str]] = set()
        players: list[Player] = []
        towns: list[Town] = []

        player_id_map: dict[int, tuple[Player, int]] = {}
        town_id_map: dict[int, tuple[Town, int]] = {}

        async with aiofiles.open(
            (Path("data") / "players.json").as_posix(), encoding="utf-8"
        ) as f:
            data = json.loads(await f.read())
            for raw_player in data["players"]:
                player = Player.model_validate(raw_player)
                players.append(player)
        async with aiofiles.open(
            (Path("data") / "towns.json").as_posix(), encoding="utf-8"
        ) as f:
            data = json.loads(await f.read())
            for raw_town in data["towns"]:
                town = Town.model_validate(raw_town)
                towns.append(town)
        async with aiofiles.open(
            (Path("data") / "player_ips.json").as_posix(), encoding="utf-8"
        ) as f:
            data = json.loads(await f.read())
            for raw_player_ip in data["player_ips"]:
                # 去掉所有的内网IP
                if (
                    raw_player_ip["ip"].startswith("10.")
                    or raw_player_ip["ip"].startswith("192.168.")
                    or (
                        raw_player_ip["ip"].startswith("172.")
                        and int(raw_player_ip["ip"].split(".")[1]) < 32
                        and int(raw_player_ip["ip"].split(".")[1]) >= 16
                    )
                    or raw_player_ip["ip"].startswith("169.254.")
                    or raw_player_ip["ip"] == "127.0.0.1"
                    or raw_player_ip["ip"] == "0.0.0.0"
                    or raw_player_ip["ip"] == "::1"
                    or raw_player_ip["ip"] == "::"
                    or raw_player_ip["ip"] == "0:0:0:0:0:0:0:1"
                ):
                    continue
                player_ip = (int(raw_player_ip["id"]), raw_player_ip["ip"])
                player_ips.add(player_ip)
        for town in towns:
            t = await client.towns.create_town(
                CreateTownRequest(
                    name=town.name,
                    level=town.level,
                    leader_id=None,
                    qq_group=town.qqgroup,
                )
            )
            town_id_map[town.id] = (town, t.id)
        for player in players:
            p = await client.players.create_player(
                CreatePlayerRequest(
                    name=player.name,
                    qq=player.qq,
                    qqguild=player.qqguild,
                    discord=player.discord,
                    in_qq_group=player.inqqgroup == 1,
                    in_qq_guild=player.inqqguild == 1,
                    in_discord=player.indiscord == 1,
                    town_id=town_id_map[player.town][1]
                    if (player.town is not None and player.town in town_id_map)
                    else None,
                )
            )
            player_id_map[player.id] = (player, p.id)
            mode = {0: BanMode.NORMAL, 1: BanMode.TEMPORARY, 2: BanMode.PERMANENT}[
                player.ban_mode
            ]
            if mode != BanMode.NORMAL:
                await client.players.ban_player(
                    p.id,
                    BanPlayerRequest(
                        duration_seconds=None, ban_mode=mode, reason="违规行为"
                    ),
                )
        for town in towns:
            await client.towns.update_town(
                town_id_map[town.id][1],
                UpdateTownRequest(
                    leader_id=player_id_map[town.leader][1]
                    if (town.leader is not None and town.leader in player_id_map)
                    else None,
                ),
            )

        def chunks(items: Iterable, size: int):
            it = iter(items)
            while chunk := list(itertools.islice(it, size)):
                yield chunk

        async with aiofiles.open(
            (Path("data") / "insert_player_ips.sql").as_posix(),
            encoding="utf-8",
            mode="w",
        ) as f:
            # 分批
            BATCH_SIZE = 100
            for items in chunks(player_ips, BATCH_SIZE):
                await f.write(f"INSERT INTO player_ips (player_id, ip) VALUES\n{',\n'.join(f'    ({player_id_map[player_id][1]}, \'{ip}\')' for player_id, ip in items)};\n")

    except Exception as e:
        print(f"Error: {e}")
    await client.close()


if __name__ == "__main__":
    asyncio.run(f())
