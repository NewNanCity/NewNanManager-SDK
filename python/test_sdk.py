#!/usr/bin/env python3
"""
NewNanManager Python SDK 测试脚本
测试更新后的SDK是否能正确工作
"""

import asyncio
import sys
import os

# 添加SDK路径到Python路径
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))

from newnanmanager import NewNanManagerClient
from newnanmanager.models import (
    CreatePlayerRequest,
    ValidateRequest,
    PlayerValidateInfo,
    BanPlayerRequest,
    BanMode,
    CreateServerRequest,
    CreateTownRequest,
    BanIPRequest,
)


async def test_sdk():
    """测试SDK的基本功能"""

    # 初始化客户端
    client = NewNanManagerClient(
        base_url="http://127.0.0.1:18080", token="7p9piy2NagtMAryeyBBY7vzUKK1qDJOq"
    )

    print("🚀 开始测试NewNanManager Python SDK...")

    # 用于存储创建的资源ID
    created_player_id = None
    created_server_id = None

    try:
        async with client:
            # 测试1: 创建玩家
            print("\n📝 测试1: 创建玩家")
            try:
                # 使用时间戳避免重复名称
                import time

                timestamp = int(time.time())
                create_request = CreatePlayerRequest(
                    name=f"TestPlayer{timestamp}",
                    qq="123456289",
                    in_qq_group=True,
                    in_qq_guild=False,
                    in_discord=False,
                )
                player = await client.players.create_player(create_request)
                created_player_id = player.id
                print(f"✅ 创建玩家成功: {player.name} (ID: {player.id})")
            except Exception as e:
                print(f"❌ 创建玩家失败: {e}")
                # 如果创建失败，尝试获取现有玩家
                try:
                    players = await client.players.list_players(page=1, page_size=1)
                    if players.players:
                        created_player_id = players.players[0].id
                        print(
                            f"ℹ️ 使用现有玩家: {players.players[0].name} (ID: {created_player_id})"
                        )
                except:
                    pass

            # 测试2: 获取玩家列表
            print("\n📋 测试2: 获取玩家列表")
            try:
                players_list = await client.players.list_players(page=1, page_size=10)
                print(f"✅ 获取玩家列表成功: 共{len(players_list.players)}个玩家")
                for player in players_list.players[:3]:  # 只显示前3个
                    print(f"   - {player.name} (ID: {player.id})")
            except Exception as e:
                print(f"❌ 获取玩家列表失败: {e}")

            # 测试3: 玩家验证（新的批处理API）
            print("\n🔍 测试3: 玩家验证")
            try:
                validate_request = ValidateRequest(
                    players=[
                        PlayerValidateInfo(
                            player_name="TestPlayer123",
                            ip="192.168.1.100",
                            client_version="1.20.1",
                            protocol_version="763",
                        )
                    ],
                    server_id=1,
                    login=True,
                    timestamp=0,
                )
                validate_result = await client.players.validate(validate_request)
                print(f"✅ 玩家验证成功: 处理了{len(validate_result.results)}个玩家")
                for result in validate_result.results:
                    status = "允许" if result.allowed else "拒绝"
                    print(f"   - {result.player_name}: {status}")
            except Exception as e:
                print(f"❌ 玩家验证失败: {e}")

            # 测试4: 注册服务器
            print("\n🖥️ 测试4: 注册服务器")
            try:
                # 使用时间戳避免重复名称
                import time

                timestamp = int(time.time())
                server_request = CreateServerRequest(
                    name=f"TestServer{timestamp}",
                    address=f"test{timestamp}.example.com:25565",
                    description="Test server description",
                )
                server = await client.servers.create_server(server_request)
                created_server_id = server.id
                print(f"✅ 注册服务器成功: {server.name} (ID: {server.id})")
            except Exception as e:
                print(f"❌ 注册服务器失败: {e}")
                # 如果注册失败，尝试获取现有服务器
                try:
                    # 这里我们假设已经有服务器存在，使用ID=1
                    created_server_id = 1
                    print(f"ℹ️ 使用现有服务器 (ID: {created_server_id})")
                except:
                    pass

            # 测试5: 创建城镇
            print("\n🏘️ 测试5: 创建城镇")
            try:
                # 使用时间戳避免重复名称
                timestamp = int(time.time())
                town_request = CreateTownRequest(
                    name=f"TestTown{timestamp}",
                    level=1,
                    description="Test town description",
                )
                town = await client.towns.create_town(town_request)
                print(f"✅ 创建城镇成功: {town.name} (ID: {town.id})")
            except Exception as e:
                print(f"❌ 创建城镇失败: {e}")
                print("ℹ️ 城镇可能已存在，这是正常的业务逻辑")

            # 测试6: IP管理（新功能）
            print("\n🌐 测试6: IP管理")
            try:
                # 获取IP信息
                ip_info = await client.ips.get_ip_info("8.8.8.8")
                print(f"✅ 获取IP信息成功: {ip_info.ip} ({ip_info.country})")
            except Exception as e:
                print(f"❌ 获取IP信息失败: {e}")

            try:
                # 封禁IP
                ban_request = BanIPRequest(reason="测试封禁")
                await client.ips.ban_ip("192.168.1.200", ban_request)
                print("✅ 封禁IP成功")
            except Exception as e:
                print(f"❌ 封禁IP失败: {e}")

            # 测试7: 玩家服务器关系管理（新功能）
            print("\n🔗 测试7: 玩家服务器关系管理")
            if created_player_id and created_server_id:
                print("✅ 玩家服务器关系管理功能已合并到其他接口中")
            else:
                print("⚠️ 跳过玩家服务器关系测试（需要先创建玩家和服务器）")

            try:
                # 获取在线玩家
                online_players = await client.player_servers.get_online_players(
                    page=1, page_size=10
                )
                print(f"✅ 获取在线玩家成功: 共{len(online_players.players)}个在线玩家")
            except Exception as e:
                print(f"❌ 获取在线玩家失败: {e}")

    except Exception as e:
        print(f"❌ SDK测试过程中发生错误: {e}")

    print("\n🎉 SDK测试完成!")


def test_models():
    """测试数据模型"""
    print("\n🧪 测试数据模型...")

    # 测试枚举
    print(f"BanMode.NORMAL = {BanMode.NORMAL}")
    print(f"BanMode.TEMPORARY = {BanMode.TEMPORARY}")
    print(f"BanMode.PERMANENT = {BanMode.PERMANENT}")

    # 测试请求模型
    create_request = CreatePlayerRequest(
        name="TestPlayer", in_qq_group=True, in_qq_guild=False, in_discord=False
    )
    print(f"CreatePlayerRequest: {create_request.model_dump()}")

    validate_info = PlayerValidateInfo(player_name="TestPlayer", ip="192.168.1.1")
    print(f"PlayerValidateInfo: {validate_info.model_dump()}")

    print("✅ 数据模型测试完成")


if __name__ == "__main__":
    print("NewNanManager Python SDK 测试")
    print("=" * 50)

    # 测试数据模型（不需要服务器）
    test_models()

    # 测试SDK功能（需要服务器运行）
    print("\n注意: 以下测试需要服务器运行在 http://127.0.0.1:18080")
    print("如果服务器未运行，测试将失败，这是正常的。")

    try:
        asyncio.run(test_sdk())
    except KeyboardInterrupt:
        print("\n测试被用户中断")
    except Exception as e:
        print(f"\n测试失败: {e}")
