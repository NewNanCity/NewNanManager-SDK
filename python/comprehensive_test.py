#!/usr/bin/env python3
"""
Comprehensive Test for NewNanManager Python SDK
Tests all API interfaces in realistic business scenarios
"""

import asyncio
import time
from typing import Optional

from newnanmanager import NewNanManagerClient
from newnanmanager.models import (
    BanIPRequest,
    BanPlayerRequest,
    CreatePlayerRequest,
    CreateTownRequest,
    PlayerValidateInfo,
    CreateServerRequest,
    UpdatePlayerRequest,
    ValidateRequest,
)


async def run_comprehensive_test():
    """Run comprehensive test for all SDK functionality."""
    print("=== NewNanManager Python SDK Comprehensive Test ===\n")

    client = NewNanManagerClient(
        base_url="http://localhost:8000",
        token="7p9piy2NagtMAryeyBBY7vzUKK1qDJOq",
        timeout=30.0,
    )

    test_player_id: Optional[int] = None
    test_server_id: Optional[int] = None
    test_town_id: Optional[int] = None

    try:
        # ========== 1. Player Management Tests ==========
        print("=== 1. Player Management Tests ===")

        # 1.1 List existing players
        print("1.1 Listing existing players...")
        try:
            players = await client.players.list_players(page=1, page_size=10)
            print(f"✓ Found {players.total} players")
            if players.players:
                test_player_id = players.players[0].id
                print(
                    f"  Using existing player: {players.players[0].name} (ID: {test_player_id})"
                )
        except Exception as e:
            print(f"✗ List players failed: {e}")

        # 1.2 Create a new player
        print("\n1.2 Creating a new player...")
        try:
            timestamp = int(time.time())
            create_request = CreatePlayerRequest(
                name=f"TestPlayerPy_{timestamp}",
                qq="123456789",
                in_qq_group=True,
                in_qq_guild=False,
                in_discord=False,
            )
            new_player = await client.players.create_player(create_request)
            test_player_id = new_player.id
            print(f"✓ Created player: {new_player.name} (ID: {new_player.id})")
        except Exception as e:
            print(f"✗ Create player failed: {e}")

        # 1.3 Get player details
        if test_player_id is not None:
            print("\n1.3 Getting player details...")
            try:
                player = await client.players.get_player(test_player_id)
                print(f"✓ Player details: {player.name}, Ban Mode: {player.ban_mode}")
            except Exception as e:
                print(f"✗ Get player failed: {e}")

        # 1.4 Update player information
        if test_player_id is not None:
            print("\n1.4 Updating player information...")
            try:
                update_request = UpdatePlayerRequest(discord="updated_discord_id")
                updated_player = await client.players.update_player(
                    test_player_id, update_request
                )
                print(f"✓ Updated player: {updated_player.name}")
            except Exception as e:
                print(f"✗ Update player failed: {e}")

        # ========== 2. Server Management Tests ==========
        print("\n=== 2. Server Management Tests ===")

        # 2.1 List existing servers
        print("2.1 Listing existing servers...")
        try:
            servers = await client.servers.list_servers(page=1, page_size=10)
            print(f"✓ Found {servers.total} servers")
            if servers.servers:
                test_server_id = servers.servers[0].id
                print(
                    f"  Using existing server: {servers.servers[0].name} (ID: {test_server_id})"
                )
        except Exception as e:
            print(f"✗ List servers failed: {e}")

        # 2.2 Register a new server
        print("\n2.2 Registering a new server...")
        try:
            timestamp = int(time.time())
            register_request = CreateServerRequest(
                name=f"TestServerPy_{timestamp}",
                address=f"test{timestamp}.example.com:25565",
                description="Test server for comprehensive testing",
            )
            new_server = await client.servers.create_server(register_request)
            test_server_id = new_server.id
            print(f"✓ Registered server: {new_server.name} (ID: {new_server.id})")
        except Exception as e:
            print(f"✗ Register server failed: {e}")

        # 2.3 Get server details
        if test_server_id is not None:
            print("\n2.3 Getting server details...")
            try:
                server = await client.servers.get_server(test_server_id)
                print(f"✓ Server: {server.name}, Address: {server.address}")
            except Exception as e:
                print(f"✗ Get server detail failed: {e}")

        # ========== 3. Town Management Tests ==========
        print("\n=== 3. Town Management Tests ===")

        # 3.1 List existing towns
        print("3.1 Listing existing towns...")
        try:
            towns = await client.towns.list_towns(page=1, page_size=10)
            print(f"✓ Found {towns.total} towns")
            if towns.towns:
                test_town_id = towns.towns[0].id
                print(
                    f"  Using existing town: {towns.towns[0].name} (ID: {test_town_id})"
                )
        except Exception as e:
            print(f"✗ List towns failed: {e}")

        # 3.2 Create a new town
        print("\n3.2 Creating a new town...")
        try:
            timestamp = int(time.time())
            create_request = CreateTownRequest(
                name=f"TestTownPy_{timestamp}",
                level=1,
                description="Test town for comprehensive testing",
            )
            new_town = await client.towns.create_town(create_request)
            test_town_id = new_town.id
            print(f"✓ Created town: {new_town.name} (ID: {new_town.id})")
        except Exception as e:
            print(f"✗ Create town failed: {e}")

        # ========== 4. Player Validation Tests ==========
        print("\n=== 4. Player Validation Tests ===")

        if test_server_id is not None:
            print("4.1 Testing batch player validation...")
            try:
                validate_request = ValidateRequest(
                    players=[
                        PlayerValidateInfo(
                            player_name="TestPlayer123",
                            ip="192.168.1.100",
                            client_version="1.20.1",
                            protocol_version="763",
                        ),
                        PlayerValidateInfo(
                            player_name="TestPlayer456",
                            ip="192.168.1.101",
                            client_version="1.19.4",
                        ),
                    ],
                    server_id=test_server_id,
                    login=True,
                    timestamp=int(time.time() * 1000),
                )
                validate_result = await client.players.validate(validate_request)
                print(f"✓ Validated {len(validate_result.results)} players")
                for result in validate_result.results:
                    status = (
                        "ALLOWED" if result.allowed else f"DENIED ({result.reason})"
                    )
                    print(f"  - {result.player_name}: {status}")
            except Exception as e:
                print(f"✗ Player validation failed: {e}")

        # ========== 5. IP Management Tests ==========
        print("\n=== 5. IP Management Tests ===")

        print("5.1 Getting IP information...")
        try:
            ip_info = await client.ips.get_ip_info("8.8.8.8")
            print(f"✓ IP Info: {ip_info.ip} ({ip_info.country or 'Unknown'})")
        except Exception as e:
            print(f"✗ Get IP info failed: {e}")

        print("\n5.2 Testing IP ban/unban...")
        try:
            ban_request = BanIPRequest(
                ip="192.168.1.200", reason="Test ban for comprehensive testing"
            )
            await client.ips.ban_ip("192.168.1.200", ban_request)
            print("✓ IP banned successfully")

            await client.ips.unban_ip("192.168.1.200")
            print("✓ IP unbanned successfully")
        except Exception as e:
            print(f"✗ IP ban/unban failed: {e}")

        # ========== 6. Token Management Tests ==========
        print("\n=== 6. Token Management Tests ===")

        print("6.1 Listing API tokens...")
        try:
            tokens = await client.tokens.list_api_tokens()
            print(f"✓ Found {len(tokens.tokens)} tokens")
            for token in tokens.tokens[:3]:
                print(f"  - {token.name} (Role: {token.role})")
        except Exception as e:
            print(f"✗ List tokens failed: {e}")

        # ========== 7. Player-Server Relationship Tests ==========
        print("\n=== 7. Player-Server Relationship Tests ===")

        print("\n7.1 Getting online players...")
        try:
            online_players = await client.player_servers.get_online_players(
                page=1, page_size=10
            )
            print(f"✓ Found {online_players.total} online players")
            for player in online_players.players[:3]:
                print(f"  - {player.player_name} on {player.server_name}")
        except Exception as e:
            print(f"✗ Get online players failed: {e}")

        # ========== 8. Monitor Service Tests ==========
        print("\n=== 8. Monitor Service Tests ===")

        if test_server_id is not None:
            print("8.1 Getting server status...")
            try:
                server_status = await client.monitor.get_server_status(test_server_id)
                print(
                    f"✓ Server Status: Online={server_status.online}, Players={server_status.current_players}/{server_status.max_players}"
                )
            except Exception as e:
                print(f"✗ Get server status failed: {e}")

        # ========== 9. Town Member Management Tests ==========
        print("\n=== 9. Town Member Management Tests ===")

        if test_town_id is not None and test_player_id is not None:
            print("9.1 Adding player to town using updateTown...")
            try:
                from newnanmanager.models import UpdateTownRequest

                update_request = UpdateTownRequest(add_players=[test_player_id])
                await client.towns.update_town(test_town_id, update_request)
                print("✓ Player added to town")
            except Exception as e:
                print(f"✗ Add player to town failed: {e}")

            print("\n9.2 Getting town details with members...")
            try:
                town_detail = await client.towns.get_town(test_town_id, detail=True)
                print(f"✓ Town details retrieved: {town_detail.name}")
            except Exception as e:
                print(f"✗ Get town details failed: {e}")

        # ========== 10. Error Handling Tests ==========
        print("\n=== 10. Error Handling Tests ===")

        print("10.1 Testing invalid player ID...")
        try:
            await client.players.get_player(999999)
            print("✗ Should have thrown an error for invalid player ID")
        except Exception as e:
            print(f"✓ Correctly handled error: {e}")

        print("\n10.2 Testing invalid server ID...")
        try:
            await client.servers.get_server(999999)
            print("✗ Should have thrown an error for invalid server ID")
        except Exception as e:
            print(f"✓ Correctly handled error: {e}")

        print("\n=== Comprehensive Test Completed ===")
        print("Summary:")
        print(f"- Test Player ID: {test_player_id}")
        print(f"- Test Server ID: {test_server_id}")
        print(f"- Test Town ID: {test_town_id}")

    except Exception as e:
        print(f"Critical error during testing: {e}")
        import traceback

        traceback.print_exc()

    finally:
        await client.close()


if __name__ == "__main__":
    asyncio.run(run_comprehensive_test())
