---
type: fix
scope: workspace
audience: developer
summary: 同步三个会话保护接口的 409 ErrorResponse 生成契约
breaking: false
demo_ready: false
tests: python -B codegen/generate.py --validate-only --all; python -B codegen/generate.py --all --check; python -B -m unittest discover -s codegen -p "test*.py" -v; Java Maven reactor test; Python unittest 16; Go test and vet; C# tests and generated build; generated Go/Python builds; Guardian :plugins:guardian:paper:check
artifacts:
  - codegen/test_contract_session_conflicts.py
  - generated/golang/api_monitor_service.go
  - generated/golang/api_player_server_service.go
  - generated/golang/api_player_service.go
  - generated/python/newnanmanager_client/api/monitor_service_api.py
  - generated/python/newnanmanager_client/api/player_server_service_api.py
  - generated/python/newnanmanager_client/api/player_service_api.py
  - generated/csharp/src/NewNanManager.Generated/Api/MonitorServiceApi.cs
  - generated/csharp/src/NewNanManager.Generated/Api/PlayerServerServiceApi.cs
  - generated/csharp/src/NewNanManager.Generated/Api/PlayerServiceApi.cs
  - generated/java/src/main/java/com/newnancity/newnanmanager/generated/api/MonitorServiceApi.java
  - generated/java/src/main/java/com/newnancity/newnanmanager/generated/api/PlayerServerServiceApi.java
  - generated/java/src/main/java/com/newnancity/newnanmanager/generated/api/PlayerServiceApi.java
---

What changed

通过固定版本 OpenAPI Generator，将 canonical contract 中 `validate`、`heartbeat`、`setPlayersOffline` 的 409 `ErrorResponse` 同步到生成客户端。Go、Python、C#、Java 各三个 API 文件发生变化，TypeScript 与 Kotlin 无漂移；另新增契约回归，锁定三个目标操作、既有资源冲突操作集合和 `ErrorResponse` schema。

Why it matters

生成文档与生成客户端现在明确表达服务端已有的 stale-session 行为。Go 和 C# 能把 409 解码为 `ErrorResponse`，Python 声明对应响应类型，Java 在生成说明中列出 409；Java facade 原有的 `ApiException`/`HttpException` 状态保留语义不变。

Demo posture / limitations

本轮没有改服务端行为、数据库 schema、认证/权限或 Guardian 恢复策略，也没有远端发布、推送或部署。Guardian 只在临时回环 Maven 仓库与合成 fixture 下验证，生产双服顶号和真实故障恢复仍未执行。
