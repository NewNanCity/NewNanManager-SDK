## @newnanmanager/generated-typescript-sdk@2.0.0

This generator creates TypeScript/JavaScript client that utilizes [axios](https://github.com/axios/axios). The generated Node module can be used in the following environments:

Environment
* Node.js
* Webpack
* Browserify

Language level
* ES5 - you must have a Promises/A+ library installed
* ES6

Module system
* CommonJS
* ES6 module system

It can be used in both TypeScript and JavaScript. In TypeScript, the definition will be automatically resolved via `package.json`. ([Reference](https://www.typescriptlang.org/docs/handbook/declaration-files/consumption.html))

### Building

To build and compile the typescript sources to javascript use:
```
npm install
npm run build
```

### Publishing

First build the package then run `npm publish`

### Consuming

navigate to the folder of your consuming project and run one of the following commands.

_published:_

```
npm install @newnanmanager/generated-typescript-sdk@2.0.0 --save
```

_unPublished (not recommended):_

```
npm install PATH_TO_GENERATED_PACKAGE --save
```

### Documentation for API Endpoints

All URIs are relative to *http://localhost*

Class | Method | HTTP request | Description
------------ | ------------- | ------------- | -------------
*HealthApi* | [**ping**](docs/HealthApi.md#ping) | **GET** /ping | 健康检查接口
*IPServiceApi* | [**banIP**](docs/IPServiceApi.md#banip) | **POST** /api/v1/ips/ban | 封禁IP
*IPServiceApi* | [**getBannedIPs**](docs/IPServiceApi.md#getbannedips) | **GET** /api/v1/ips/banned | 获取被封禁的IP列表
*IPServiceApi* | [**getHighRiskIPs**](docs/IPServiceApi.md#gethighriskips) | **GET** /api/v1/ips/high-risk | 获取高风险IP列表
*IPServiceApi* | [**getIPInfo**](docs/IPServiceApi.md#getipinfo) | **GET** /api/v1/ips/{ip} | 获取IP信息（包含风险信息）
*IPServiceApi* | [**getIPStatistics**](docs/IPServiceApi.md#getipstatistics) | **GET** /api/v1/ips/statistics | 获取IP统计信息
*IPServiceApi* | [**getSuspiciousIPs**](docs/IPServiceApi.md#getsuspiciousips) | **GET** /api/v1/ips/suspicious | 获取可疑IP列表
*IPServiceApi* | [**listIPs**](docs/IPServiceApi.md#listips) | **GET** /api/v1/ips | 获取IP列表
*IPServiceApi* | [**unbanIP**](docs/IPServiceApi.md#unbanip) | **POST** /api/v1/ips/unban | 解封IP
*MonitorServiceApi* | [**createServerSession**](docs/MonitorServiceApi.md#createserversession) | **POST** /api/v1/monitor/{server_id}/session | 签发服务器会话代次
*MonitorServiceApi* | [**getMonitorStats**](docs/MonitorServiceApi.md#getmonitorstats) | **GET** /api/v1/monitor/{server_id}/stats | 获取监控统计
*MonitorServiceApi* | [**heartbeat**](docs/MonitorServiceApi.md#heartbeat) | **POST** /api/v1/monitor/{server_id}/heartbeat | 发送心跳
*PlayerServerServiceApi* | [**getPlayerServers**](docs/PlayerServerServiceApi.md#getplayerservers) | **GET** /api/v1/players/{player_id}/servers | 获取玩家的服务器关系
*PlayerServerServiceApi* | [**getServerPlayers**](docs/PlayerServerServiceApi.md#getserverplayers) | **GET** /api/v1/server-players | 获取全局在线玩家
*PlayerServerServiceApi* | [**setPlayersOffline**](docs/PlayerServerServiceApi.md#setplayersoffline) | **POST** /api/v1/servers/players/offline | 设置玩家离线状态 - 在玩家退出时调用
*PlayerServiceApi* | [**banPlayer**](docs/PlayerServiceApi.md#banplayer) | **POST** /api/v1/players/{player_id}/ban | 封禁玩家
*PlayerServiceApi* | [**createPlayer**](docs/PlayerServiceApi.md#createplayer) | **POST** /api/v1/players | 创建玩家
*PlayerServiceApi* | [**deletePlayer**](docs/PlayerServiceApi.md#deleteplayer) | **DELETE** /api/v1/players/{id} | 删除玩家
*PlayerServiceApi* | [**getPlayer**](docs/PlayerServiceApi.md#getplayer) | **GET** /api/v1/players/{id} | 获取玩家详情
*PlayerServiceApi* | [**listPlayers**](docs/PlayerServiceApi.md#listplayers) | **GET** /api/v1/players | 获取玩家列表
*PlayerServiceApi* | [**unbanPlayer**](docs/PlayerServiceApi.md#unbanplayer) | **POST** /api/v1/players/{player_id}/unban | 解封玩家
*PlayerServiceApi* | [**updatePlayer**](docs/PlayerServiceApi.md#updateplayer) | **PUT** /api/v1/players/{id} | 更新玩家信息
*PlayerServiceApi* | [**validate**](docs/PlayerServiceApi.md#validate) | **POST** /api/v1/players/validate | 玩家验证（支持批处理） - 在玩家登录或定期检查时调用
*ServerServiceApi* | [**createServer**](docs/ServerServiceApi.md#createserver) | **POST** /api/v1/servers | 注册服务器
*ServerServiceApi* | [**deleteServer**](docs/ServerServiceApi.md#deleteserver) | **DELETE** /api/v1/servers/{id} | 删除服务器
*ServerServiceApi* | [**getServer**](docs/ServerServiceApi.md#getserver) | **GET** /api/v1/servers/{id} | 获取服务器信息
*ServerServiceApi* | [**listServers**](docs/ServerServiceApi.md#listservers) | **GET** /api/v1/servers | 获取服务器列表
*ServerServiceApi* | [**updateServer**](docs/ServerServiceApi.md#updateserver) | **PUT** /api/v1/servers/{id} | 更新服务器信息
*TokenServiceApi* | [**createApiToken**](docs/TokenServiceApi.md#createapitoken) | **POST** /api/v1/tokens | 创建API Token
*TokenServiceApi* | [**deleteApiToken**](docs/TokenServiceApi.md#deleteapitoken) | **DELETE** /api/v1/tokens/{id} | 删除API Token
*TokenServiceApi* | [**getApiToken**](docs/TokenServiceApi.md#getapitoken) | **GET** /api/v1/tokens/{id} | 获取API Token详情
*TokenServiceApi* | [**listApiTokens**](docs/TokenServiceApi.md#listapitokens) | **GET** /api/v1/tokens | 获取API Token列表
*TokenServiceApi* | [**updateApiToken**](docs/TokenServiceApi.md#updateapitoken) | **PUT** /api/v1/tokens/{id} | 更新API Token
*TownServiceApi* | [**createTown**](docs/TownServiceApi.md#createtown) | **POST** /api/v1/towns | 创建城镇
*TownServiceApi* | [**deleteTown**](docs/TownServiceApi.md#deletetown) | **DELETE** /api/v1/towns/{id} | 删除城镇
*TownServiceApi* | [**getTown**](docs/TownServiceApi.md#gettown) | **GET** /api/v1/towns/{id} | 获取城镇详情
*TownServiceApi* | [**listTowns**](docs/TownServiceApi.md#listtowns) | **GET** /api/v1/towns | 获取城镇列表
*TownServiceApi* | [**updateTown**](docs/TownServiceApi.md#updatetown) | **PUT** /api/v1/towns/{id} | 更新城镇信息


### Documentation For Models

 - [ApiToken](docs/ApiToken.md)
 - [BanIPRequest](docs/BanIPRequest.md)
 - [BanPlayerRequest](docs/BanPlayerRequest.md)
 - [CreateApiTokenRequest](docs/CreateApiTokenRequest.md)
 - [CreateApiTokenResponse](docs/CreateApiTokenResponse.md)
 - [CreatePlayerRequest](docs/CreatePlayerRequest.md)
 - [CreateServerRequest](docs/CreateServerRequest.md)
 - [CreateTownRequest](docs/CreateTownRequest.md)
 - [ErrorDetails](docs/ErrorDetails.md)
 - [ErrorResponse](docs/ErrorResponse.md)
 - [GetMonitorStatsResponse](docs/GetMonitorStatsResponse.md)
 - [HeartbeatRequest](docs/HeartbeatRequest.md)
 - [HeartbeatResponse](docs/HeartbeatResponse.md)
 - [IPInfo](docs/IPInfo.md)
 - [IPStatistics](docs/IPStatistics.md)
 - [ListApiTokensResponse](docs/ListApiTokensResponse.md)
 - [ListIPsResponse](docs/ListIPsResponse.md)
 - [ListPlayersResponse](docs/ListPlayersResponse.md)
 - [ListServersResponse](docs/ListServersResponse.md)
 - [ListTownsResponse](docs/ListTownsResponse.md)
 - [MonitorStatRecord](docs/MonitorStatRecord.md)
 - [OnlinePlayer](docs/OnlinePlayer.md)
 - [Ping200Response](docs/Ping200Response.md)
 - [Player](docs/Player.md)
 - [PlayerLoginInfo](docs/PlayerLoginInfo.md)
 - [PlayerServer](docs/PlayerServer.md)
 - [PlayerServersResponse](docs/PlayerServersResponse.md)
 - [PlayerValidateInfo](docs/PlayerValidateInfo.md)
 - [PlayerValidateResult](docs/PlayerValidateResult.md)
 - [ServerDetailResponse](docs/ServerDetailResponse.md)
 - [ServerPlayersResponse](docs/ServerPlayersResponse.md)
 - [ServerRegistry](docs/ServerRegistry.md)
 - [ServerSessionResponse](docs/ServerSessionResponse.md)
 - [ServerStatus](docs/ServerStatus.md)
 - [SetPlayersOfflineRequest](docs/SetPlayersOfflineRequest.md)
 - [Town](docs/Town.md)
 - [TownDetailResponse](docs/TownDetailResponse.md)
 - [UnbanIPRequest](docs/UnbanIPRequest.md)
 - [UpdateApiTokenRequest](docs/UpdateApiTokenRequest.md)
 - [UpdatePlayerRequest](docs/UpdatePlayerRequest.md)
 - [UpdateServerRequest](docs/UpdateServerRequest.md)
 - [UpdateTownRequest](docs/UpdateTownRequest.md)
 - [ValidateRequest](docs/ValidateRequest.md)
 - [ValidateResponse](docs/ValidateResponse.md)


<a id="documentation-for-authorization"></a>
## Documentation For Authorization


Authentication schemes defined for the API:
<a id="BearerAuth"></a>
### BearerAuth

- **Type**: Bearer authentication

<a id="ApiKeyAuth"></a>
### ApiKeyAuth

- **Type**: API key
- **API key parameter name**: X-API-Token
- **Location**: HTTP header

