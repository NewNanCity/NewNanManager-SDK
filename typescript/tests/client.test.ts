import assert from 'node:assert/strict';
import { test } from 'node:test';
import { createServer, Server } from 'node:http';
import { AddressInfo } from 'node:net';
import { spawnSync } from 'node:child_process';
import { AxiosError, AxiosHeaders, InternalAxiosRequestConfig } from 'axios';
import { NewNanManagerClient } from '../src/client';
import { NewNanManagerHttpError } from '../src/utils/errorHandler';
import { AuthScheme, IPBan, PlayerServersResponse, SessionContext } from '../src/types';

const validSessionId = '0123456789abcdef0123456789abcdef';

function client(): NewNanManagerClient {
  return new NewNanManagerClient({ baseUrl: 'https://audit.invalid', token: 'synthetic' });
}

test('generated session calls select exactly one API credential header', async () => {
  const sdk = new NewNanManagerClient({
    baseUrl: 'https://audit.invalid',
    token: 'api-token-secret',
    authScheme: AuthScheme.API_TOKEN
  });
  const api = sdk.monitor.heartbeat;
  let request: InternalAxiosRequestConfig | undefined;
  api.setDefaultAxiosConfig({ adapter: async (config) => {
    request = config;
    return {
      config,
      status: 200,
      statusText: 'OK',
      headers: {},
      data: { received_at: 1, response_at: 2, expire_duration_ms: 30000 }
    };
  } });
  await api(
    { serverId: 7, currentPlayers: 1, maxPlayers: 20 },
    { id: validSessionId, epoch: 3 } satisfies SessionContext
  );
  assert.ok(request);
  assert.equal(request.headers.get('X-API-Token'), 'api-token-secret');
  assert.equal(request.headers.has('Authorization'), false);
  assert.equal(request.headers.get('X-NNM-Session-ID'), validSessionId);
  assert.equal(request.headers.get('X-NNM-Session-Epoch'), '3');
});

test('generated session operations reject invalid fencing context before network I/O', async () => {
  const api = client().monitor.heartbeat;
  let calls = 0;
  api.setDefaultAxiosConfig({ adapter: async (config) => {
    calls++;
    return { config, status: 200, statusText: 'OK', headers: {}, data: {} };
  } });
  await assert.rejects(
    () => api({ serverId: 7, currentPlayers: 0, maxPlayers: 20 }, { id: ' ', epoch: 0 }),
    /session id must be a non-empty string/
  );
  await assert.rejects(
    () => api({ serverId: 7, currentPlayers: 0, maxPlayers: 20 }, { id: 'too-short', epoch: 1 }),
    /session id length must be between 32 and 64/
  );
  assert.equal(calls, 0);
});

test('generated session creation validates the server-issued fencing tuple', async () => {
  const api = client().monitor.createServerSession;
  api.setDefaultAxiosConfig({ adapter: async (config) => ({
    config,
    status: 200,
    statusText: 'OK',
    headers: {},
    data: { session_id: 'too-short', session_epoch: 1 }
  }) });
  await assert.rejects(
    () => api(7),
    /session id length must be between 32 and 64/
  );

  api.setDefaultAxiosConfig({ adapter: async (config) => ({
    config,
    status: 200,
    statusText: 'OK',
    headers: {},
    data: { session_id: null, session_epoch: 2 }
  }) });
  await assert.rejects(
    () => api(7),
    /session id must be a non-empty string/
  );

  api.setDefaultAxiosConfig({ adapter: async (config) => ({
    config,
    status: 200,
    statusText: 'OK',
    headers: {},
    data: { session_id: validSessionId, session_epoch: 2 }
  }) });
  assert.deepEqual(await api(7), { id: validSessionId, epoch: 2 });
});

test('integration runners require explicit credentials and endpoint before any API call', () => {
  for (const runner of ['src/integration-test.ts', 'src/comprehensive-test.ts']) {
    const result = spawnSync(process.execPath, ['-r', 'ts-node/register', runner], {
      encoding: 'utf8',
      env: { ...process.env, NANMANAGER_TOKEN: '', NANMANAGER_BASE_URL: '' },
      timeout: 15000
    });
    assert.equal(result.error, undefined);
    assert.notEqual(result.status, 0);
    assert.match(result.stderr, /NANMANAGER_TOKEN and NANMANAGER_BASE_URL are required/);
  }
});

test('HTTP errors retain status, retry delay and request ID', async () => {
  const api = client().players.getPlayer;
  api.setDefaultAxiosConfig({ adapter: async (config) => {
    throw new AxiosError('HTTP error', 'ERR_BAD_REQUEST', config, undefined, {
      config, status: 429, statusText: 'Too Many Requests',
      headers: new AxiosHeaders({ 'retry-after': '60', 'x-request-id': 'audit-request' }),
      data: { detail: 'rate limited' }
    });
  } });
  await assert.rejects(async () => api({ id: 1 }), (error: unknown) => {
    assert.ok(error instanceof NewNanManagerHttpError);
    assert.equal(error.statusCode, 429);
    assert.equal(error.retryAfter, '60');
    assert.equal(error.requestId, 'audit-request');
    assert.equal(error.message, 'rate limited');
    assert.equal(JSON.stringify(error).includes('Bearer synthetic'), false);
    return true;
  });
});

test('template errors expose machine metadata and body request IDs', async () => {
  const api = client().players.getPlayer;
  api.setDefaultAxiosConfig({ adapter: async (config) => {
    throw new AxiosError('HTTP error', 'ERR_BAD_REQUEST', config, undefined, {
      config, status: 400, statusText: 'Bad Request', headers: {},
      data: {
        code: 1004000,
        message: 'invalid request parameters',
        request_id: 'body-request',
        trace_id: 'trace-1',
        error: { category: 'invalid_argument', code: 'common.invalid_argument' }
      }
    });
  } });
  await assert.rejects(async () => api({ id: 1 }), (error: unknown) => {
    assert.ok(error instanceof NewNanManagerHttpError);
    assert.equal(error.message, 'invalid request parameters');
    assert.equal(error.apiCode, 1004000);
    assert.equal(error.errorCategory, 'invalid_argument');
    assert.equal(error.machineCode, 'common.invalid_argument');
    assert.equal(error.requestId, 'body-request');
    assert.equal(error.traceId, 'trace-1');
    return true;
  });
});

test('optional updates retain false and empty strings while omitting absent fields', async () => {
  const api = client().players.updatePlayer;
  let request: InternalAxiosRequestConfig | undefined;
  api.setDefaultAxiosConfig({ adapter: async (config) => {
    request = config;
    return { config, status: 200, statusText: 'OK', headers: {}, data: { id: 1 } };
  } });
  await api({ id: 1, inDiscord: false, qq: '' });
  assert.ok(request);
  assert.deepEqual(JSON.parse(request.data as string), { in_discord: false, qq: '' });
  assert.equal(request.headers.get('Authorization'), 'Bearer synthetic');
  assert.equal(request.headers.has('X-API-Token'), false);
});

test('player server relations expose the actual unpaginated response', async () => {
  const empty: PlayerServersResponse = { servers: [], total: 0 };
  assert.equal(empty.total, 0);
  const api = client().playerServers.getPlayerServers;
  api.setDefaultAxiosConfig({ adapter: async (config) => ({
    config, status: 200, statusText: 'OK', headers: {},
    data: { servers: [{ player_id: 1, server_id: 2, online: false,
      joined_at: '2026-09-06T01:00:00Z', created_at: '2026-09-01T00:00:00Z', updated_at: '2026-09-06T01:00:00Z' }], total: 1 }
  }) });
  const response = await api({ playerId: 1 });
  assert.deepEqual(Object.keys(response).sort(), ['servers', 'total']);
  assert.equal(response.total, 1);
  assert.equal(response.servers[0].online, false);
});

test('ban listings preserve unknown timestamps and omit unsupported filters', async () => {
  const unknownTime: IPBan = { ip: '192.0.2.1', reason: 'test reason', active: true };
  assert.equal(unknownTime.bannedAt, undefined);
  const api = client().ips.listBannedIPs;
  let request: InternalAxiosRequestConfig | undefined;
  api.setDefaultAxiosConfig({ adapter: async (config) => {
    request = config;
    return { config, status: 200, statusText: 'OK', headers: {},
      data: { ips: [{ ip: '192.0.2.1', banned: true, ban_reason: 'test reason',
        created_at: '2020-01-01T00:00:00Z', updated_at: '2026-09-06T01:00:00Z' }], total: 1, page: 2, page_size: 20 }
    };
  } });
  const response = await api({ page: 2, pageSize: 20, activeOnly: false, search: 'ignored legacy input' });
  assert.deepEqual(request?.params, { page: 2, page_size: 20 });
  assert.deepEqual(response.bans[0], { ip: '192.0.2.1', reason: 'test reason', active: true });
  assert.equal(response.bans[0].bannedAt, undefined);
  assert.equal(response.bans[0].unbannedAt, undefined);
  assert.equal(response.bans[0].unbanReason, undefined);
});

test('token server bindings survive create, read, update and list', async () => {
  const sdk = client();
  const token = { id: 1, name: 'audit', role: 'server', active: true, server_id: 7,
    created_at: '2026-09-06T00:00:00Z', updated_at: '2026-09-06T00:00:00Z' };
  const bodies: unknown[] = [];
  sdk.tokens.createApiToken.setDefaultAxiosConfig({ adapter: async (config) => {
    bodies.push(JSON.parse(config.data as string));
    return { config, status: 200, statusText: 'OK', headers: {}, data: { token_info: token, token_value: 'synthetic' } };
  } });
  const created = await sdk.tokens.createApiToken({ name: 'audit', role: 'server', serverId: 7 });
  assert.equal(created.tokenInfo.serverId, 7);
  sdk.tokens.getApiToken.setDefaultAxiosConfig({ adapter: async config => ({ config, status: 200, statusText: 'OK', headers: {}, data: token }) });
  assert.equal((await sdk.tokens.getApiToken({ id: 1 })).serverId, 7);
  sdk.tokens.listApiTokens.setDefaultAxiosConfig({ adapter: async config => ({ config, status: 200, statusText: 'OK', headers: {},
    data: { tokens: [token], total: 1, page: 1, page_size: 20 } }) });
  assert.equal((await sdk.tokens.listApiTokens({})).tokens[0].serverId, 7);
  sdk.tokens.updateApiToken.setDefaultAxiosConfig({ adapter: async (config) => {
    bodies.push(JSON.parse(config.data as string));
    return { config, status: 200, statusText: 'OK', headers: {}, data: { ...token, server_id: 8 } };
  } });
  assert.equal((await sdk.tokens.updateApiToken({ id: 1, serverId: 8, active: false })).serverId, 8);
  assert.deepEqual(bodies, [{ name: 'audit', role: 'server', server_id: 7 }, { server_id: 8, active: false }]);
});

test('token list ignores legacy search and retains pagination metadata', async () => {
  const api = client().tokens.listApiTokens;
  const queries: unknown[] = [];
  api.setDefaultAxiosConfig({ adapter: async config => {
    queries.push(config.params);
    assert.equal(config.method, 'get');
    assert.equal(config.url, '/api/v1/tokens');
    return { config, status: 200, statusText: 'OK', headers: {},
      data: { tokens: [], total: 4294967296, page: 2, page_size: 100 } };
  } });
  const result = await api({ page: 2, pageSize: 100, search: 'ignored legacy filter' });
  assert.deepEqual(queries[0], { page: 2, page_size: 100 });
  assert.deepEqual(result, { tokens: [], total: 4294967296, page: 2, pageSize: 100 });
  await api({ search: 'ignored without pagination' });
  assert.deepEqual(queries[1], {});
  await api({});
  assert.deepEqual(queries[2], {});
});

test('monitor pagination passes opaque cursors and preserves absent measurement metadata', async () => {
  const api = client().monitor.getMonitorStats;
  const cursor = 'opaque&+/#';
  api.setDefaultAxiosConfig({ adapter: async config => {
    assert.deepEqual(config.params, { since: 0, duration: 86400, limit: 10000, cursor });
    return { config, status: 200, statusText: 'OK', headers: {}, data: { server_id: 7,
      stats: [{ timestamp: 1, current_players: 0 }, { timestamp: 2, current_players: 0, measurement_type: 'unknown', latency_metric: 'legacy' }],
      next_cursor: cursor } };
  } });
  const result = await api({ serverId: 7, since: 0, duration: 86400, limit: 10000, cursor });
  assert.equal(result.nextCursor, cursor);
  assert.equal(result.stats[0].measurementType, undefined);
  assert.equal(result.stats[0].latencyMetric, undefined);
  assert.equal(result.stats[1].measurementType, 'unknown');
  assert.equal(result.stats[1].latencyMetric, 'legacy');
});

test('server details retain the source and metric of a measurement', async () => {
  const api = client().servers.getServer;
  api.setDefaultAxiosConfig({ adapter: async config => ({ config, status: 200, statusText: 'OK', headers: {},
    data: { server: { id: 7, name: 'audit', address: '127.0.0.1:25565', active: true,
      created_at: '2026-09-06T00:00:00Z', updated_at: '2026-09-06T00:00:00Z' },
    status: { server_id: 7, online: true, current_players: 0, max_players: 10, expire_at: '2026-09-06T00:01:00Z',
      last_heartbeat: '2026-09-06T00:00:00Z', measurement_type: 'pull', latency_metric: 'rtt' } }
  }) });
  const result = await api({ id: 7, detail: true });
  assert.equal(result.status?.measurementType, 'pull');
  assert.equal(result.status?.latencyMetric, 'rtt');
});

test('empty and full periodic player snapshots are sent once without name normalization', async () => {
  for (const count of [0, 1000]) {
    const api = client().players.validate;
    let calls = 0;
    api.setDefaultAxiosConfig({ adapter: async config => {
      calls++;
      const body: { players: { player_name: string }[]; login: boolean } = JSON.parse(config.data as string);
      assert.equal(body.login, false);
      assert.equal(body.players.length, count);
      if (count > 0) assert.equal(body.players[0].player_name, 'MiXeD0');
      return { config, status: 200, statusText: 'OK', headers: {}, data: { results: [], processed_at: 1 } };
    } });
    await api({ serverId: 7, login: false, players: Array.from({ length: count }, (_, index) => ({ playerName: `MiXeD${index}`, ip: '192.0.2.1' })) });
    assert.equal(calls, 1);
  }
});

async function listen(server: Server): Promise<string> {
  await new Promise<void>((resolve) => server.listen(0, '127.0.0.1', resolve));
  return `http://127.0.0.1:${(server.address() as AddressInfo).port}`;
}

async function close(server: Server): Promise<void> {
  await new Promise<void>((resolve, reject) => server.close(error => error ? reject(error) : resolve()));
}

test('authenticated redirects are rejected without contacting the target', async () => {
  let targetCalls = 0;
  const target = createServer((_req, res) => {
    targetCalls++;
    res.setHeader('Content-Type', 'application/json');
    res.end('{"id":1}');
  });
  const targetUrl = await listen(target);
  const source = createServer((_req, res) => {
    res.writeHead(302, { Location: targetUrl });
    res.end();
  });
  const sourceUrl = await listen(source);
  try {
    const sdk = new NewNanManagerClient({ baseUrl: sourceUrl, token: 'synthetic', timeout: 1000 });
    await assert.rejects(async () => sdk.players.getPlayer({ id: 1 }));
    assert.equal(targetCalls, 0);
  } finally {
    await close(source);
    await close(target);
  }
});
