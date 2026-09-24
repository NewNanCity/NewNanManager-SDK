---
type: fix
scope: workspace
audience: developer
summary: 让 Go、Python、C# SDK 支持 Bearer 与 X-API-Token 二选一鉴权
breaking: false
demo_ready: false
tests: go test ./...; go vet ./...; python -B -m unittest discover -s tests -v; dotnet test Tests/NewNanManager.Client.Tests.csproj --no-restore --filter Category!=Integration
artifacts:
  - golang/auth.go
  - golang/client.go
  - python/newnanmanager/config.py
  - python/newnanmanager/http_client.py
  - csharp/AuthScheme.cs
  - csharp/NewNanManagerClient.cs
---

What changed

Go、Python、C# facade 新增统一的鉴权方案选择，默认行为仍是 Bearer；选择 API Token 时只发送 `X-API-Token`，不会把两种凭证头同时发出。每种语言都新增 loopback 或 fake transport 回归。

Why it matters

部署可以继续按现有 Bearer 配置运行，也可以直接使用生产兼容的 `X-API-Token`，避免不同语言客户端对同一 OpenAPI security scheme 的行为不一致。

Demo posture / limitations

本条只覆盖客户端鉴权头选择和本地回归，不代表生成 transport 已接入 Go/Python/C#，也不代表真实生产 token、Guardian session fencing 或部署验收已经完成。
