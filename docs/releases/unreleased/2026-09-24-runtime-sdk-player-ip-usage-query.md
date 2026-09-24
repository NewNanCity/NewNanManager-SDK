---
type: feature
scope: runtime
audience: developer
summary: 同步六语言 SDK 的玩家登录与 IP 使用查询接口
breaking: false
demo_ready: false
tests: python -B -m unittest discover -s codegen -p 'test*.py' -v; python -B codegen/generate.py --all --check --input <canonical-contract>; go test ./generated/golang; python -m compileall -q generated/python/newnanmanager_client
artifacts:
  - codegen/test_manifest.py
  - generated/typescript/api.ts
  - generated/golang/api_player_usage_service.go
  - generated/python/newnanmanager_client/api/player_usage_service_api.py
  - generated/csharp/src/NewNanManager.Generated/Api/PlayerUsageServiceApi.cs
  - generated/kotlin/src/main/kotlin/com/newnanmanager/generated/apis/PlayerUsageServiceApi.kt
  - generated/java/src/main/java/com/newnancity/newnanmanager/generated/api/PlayerUsageServiceApi.java
  - docs/releases/unreleased/2026-09-24-runtime-sdk-player-ip-usage-query.md
---

## What changed

依据父项目的 OpenAPI 契约，重新生成 TypeScript、Go、Python、C#、Kotlin 和 Java 六种低级客户端，新增按玩家查询登录/IP 使用情况和按 IP 反查活跃玩家的模型与操作。生成标记继续使用仓库约定的相对契约路径；代码生成 manifest 测试改为按生成器实际使用的规范化换行计算哈希。

## Why it matters

管理端调用方可以在各语言客户端中读取公共 IP 关联数量、登录尝试次数、首次/最近使用时间及最近一次成功登录时间，且六种客户端的请求路径、分页参数和鉴权方案与服务端契约保持一致。

## Demo posture / limitations

本条只同步低级生成客户端，没有新增各语言 facade 方法，也不代表 npm/Maven/PyPI 发布或真实服务 HTTP 验收已经完成。独立 SDK worktree 的默认相对契约路径依赖其嵌入父项目的目录布局；漂移检查使用显式 canonical contract 参数完成。
