import { NewNanManagerClient } from './client';
import {
  CreatePlayerRequest,
  CreateServerRequest,
  CreateTownRequest,
  ValidateRequest,
  PlayerValidateInfo,
  SetPlayersOfflineRequest,
  GetTownMembersRequest,
  GetOnlinePlayersRequest,
  ListPlayersRequest,
  ListServersRequest,
  ListTownsRequest,
  ListIPsRequest
} from './types';

/**
 * Comprehensive Test for NewNanManager TypeScript SDK
 * Tests all API interfaces in realistic business scenarios
 */
async function runComprehensiveTest() {
  console.log('=== NewNanManager TypeScript SDK Comprehensive Test ===\n');

  const client = new NewNanManagerClient({
    token: '7p9piy2NagtMAryeyBBY7vzUKK1qDJOq',
    baseUrl: 'http://localhost:8000'
  });

  let testPlayerId: number | null = null;
  let testServerId: number | null = null;
  let testTownId: number | null = null;

  try {
    // ========== 1. Player Management Tests ==========
    console.log('=== 1. Player Management Tests ===');

    // 1.1 List existing players
    console.log('1.1 Listing existing players...');
    try {
      const playersRequest: ListPlayersRequest = { page: 1, pageSize: 10 };
      const players = await client.players.listPlayers(playersRequest);
      console.log(`✓ Found ${players.total} players`);
      if (players.players.length > 0) {
        testPlayerId = players.players[0].id;
        console.log(`  Using existing player: ${players.players[0].name} (ID: ${testPlayerId})`);
      }
    } catch (e: any) {
      console.log(`✗ List players failed: ${e.message}`);
    }

    // 1.2 Create a new player
    console.log('\n1.2 Creating a new player...');
    try {
      const timestamp = Date.now();
      const createRequest: CreatePlayerRequest = {
        name: `TestPlayerTS_${timestamp}`,
        qq: '123456789',
        inQqGroup: true,
        inQqGuild: false,
        inDiscord: false
      };
      const newPlayer = await client.players.createPlayer(createRequest);
      testPlayerId = newPlayer.id;
      console.log(`✓ Created player: ${newPlayer.name} (ID: ${newPlayer.id})`);
    } catch (e: any) {
      console.log(`✗ Create player failed: ${e.message}`);
    }

    // 1.3 Get player details
    if (testPlayerId !== null) {
      console.log('\n1.3 Getting player details...');
      try {
        const player = await client.players.getPlayer({ id: testPlayerId });
        console.log(`✓ Player details: ${player.name}, Ban Mode: ${player.banMode}`);
      } catch (e: any) {
        console.log(`✗ Get player failed: ${e.message}`);
      }
    }

    // 1.4 Update player information
    if (testPlayerId !== null) {
      console.log('\n1.4 Updating player information...');
      try {
        const updateRequest = {
          id: testPlayerId,
          discord: 'updated_discord_id'
        };
        const updatedPlayer = await client.players.updatePlayer(updateRequest);
        console.log(`✓ Updated player: ${updatedPlayer.name}`);
      } catch (e: any) {
        console.log(`✗ Update player failed: ${e.message}`);
      }
    }

    // ========== 2. Server Management Tests ==========
    console.log('\n=== 2. Server Management Tests ===');

    // 2.1 List existing servers
    console.log('2.1 Listing existing servers...');
    try {
      const serversRequest: ListServersRequest = { page: 1, pageSize: 10 };
      const servers = await client.servers.listServers(serversRequest);
      console.log(`✓ Found ${servers.total} servers`);
      if (servers.servers.length > 0) {
        testServerId = servers.servers[0].id;
        console.log(`  Using existing server: ${servers.servers[0].name} (ID: ${testServerId})`);
      }
    } catch (e: any) {
      console.log(`✗ List servers failed: ${e.message}`);
    }

    // 2.2 Register a new server
    console.log('\n2.2 Registering a new server...');
    try {
      const timestamp = Date.now();
      const createRequest: CreateServerRequest = {
        name: `TestServerTS_${timestamp}`,
        address: `test${timestamp}.example.com:25565`,
        description: 'Test server for comprehensive testing'
      };
      const newServer = await client.servers.createServer(createRequest);
      testServerId = newServer.id;
      console.log(`✓ Registered server: ${newServer.name} (ID: ${newServer.id})`);
    } catch (e: any) {
      console.log(`✗ Register server failed: ${e.message}`);
    }

    // 2.3 Get server details
    if (testServerId !== null) {
      console.log('\n2.3 Getting server details...');
      try {
        const serverDetail = await client.servers.getServer({ id: testServerId, detail: true });
        console.log(`✓ Server: ${serverDetail.server.name}, Address: ${serverDetail.server.address}`);
      } catch (e: any) {
        console.log(`✗ Get server detail failed: ${e.message}`);
      }
    }

    // ========== 3. Town Management Tests ==========
    console.log('\n=== 3. Town Management Tests ===');

    // 3.1 List existing towns
    console.log('3.1 Listing existing towns...');
    try {
      const townsRequest: ListTownsRequest = { page: 1, pageSize: 10 };
      const towns = await client.towns.listTowns(townsRequest);
      console.log(`✓ Found ${towns.total} towns`);
      if (towns.towns.length > 0) {
        testTownId = towns.towns[0].id;
        console.log(`  Using existing town: ${towns.towns[0].name} (ID: ${testTownId})`);
      }
    } catch (e: any) {
      console.log(`✗ List towns failed: ${e.message}`);
    }

    // 3.2 Create a new town
    console.log('\n3.2 Creating a new town...');
    try {
      const timestamp = Date.now();
      const createRequest: CreateTownRequest = {
        name: `TestTownTS_${timestamp}`,
        level: 1,
        description: 'Test town for comprehensive testing'
      };
      const newTown = await client.towns.createTown(createRequest);
      testTownId = newTown.id;
      console.log(`✓ Created town: ${newTown.name} (ID: ${newTown.id})`);
    } catch (e: any) {
      console.log(`✗ Create town failed: ${e.message}`);
    }

    // ========== 4. Player Validation Tests ==========
    console.log('\n=== 4. Player Validation Tests ===');

    if (testServerId !== null) {
      console.log('4.1 Testing batch player validation...');
      try {
        const validateRequest: ValidateRequest = {
          players: [
            {
              playerName: 'TestPlayer123',
              ip: '192.168.1.100',
              clientVersion: '1.20.1',
              protocolVersion: '763'
            } as PlayerValidateInfo,
            {
              playerName: 'TestPlayer456',
              ip: '192.168.1.101',
              clientVersion: '1.19.4'
            } as PlayerValidateInfo
          ],
          serverId: testServerId,
          login: true,
          timestamp: Date.now()
        };
        const validateResult = await client.players.validate(validateRequest);
        console.log(`✓ Validated ${validateResult.results.length} players`);
        validateResult.results.forEach(result => {
          const status = result.allowed ? 'ALLOWED' : `DENIED (${result.reason})`;
          console.log(`  - ${result.playerName}: ${status}`);
        });
      } catch (e: any) {
        console.log(`✗ Player validation failed: ${e.message}`);
      }
    }

    console.log('\n=== Comprehensive Test Completed ===');
    console.log('Summary:');
    console.log(`- Test Player ID: ${testPlayerId}`);
    console.log(`- Test Server ID: ${testServerId}`);
    console.log(`- Test Town ID: ${testTownId}`);

  } catch (e: any) {
    console.log(`Critical error during testing: ${e.message}`);
    console.error(e);
  }
}

// Run the test
runComprehensiveTest().catch(console.error);
