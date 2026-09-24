---
type: fix
scope: workspace
audience: developer
summary: 统一各 SDK 会话签发请求发送必需的空对象 body
breaking: false
demo_ready: false
tests: go test ./...; python -B -m unittest discover -s tests -v; dotnet test Tests/NewNanManager.Client.Tests.csproj --no-restore --filter Category!=Integration
artifacts:
  - golang/modules/monitor.go
  - golang/modules/session_test.go
  - python/newnanmanager/services/monitor.py
  - python/tests/test_contract.py
  - csharp/Services/MonitorService.cs
  - csharp/Tests/HttpClientTests.cs
  - TODO.md
---

What changed

Go、Python、C# 的 `create_server_session` 现在发送 `{}` 请求体，并新增回归断言；这与 canonical OpenAPI/Thrift 对必需空对象 body 的定义一致。同时刷新 SDK 待办，把已完成的 session header/body facade 适配标为完成，并保留其余生成 transport 和 legacy validator 的真实边界。

Why it matters

生成客户端和 Hertz 绑定层会拒绝缺少必填 body 的会话签发请求。统一空对象可以避免服务器插件首次建立 session 时收到 400。

Demo posture / limitations

本条覆盖本地 fake/HTTP 回归，不代表真实 Guardian、双服顶号或生产部署验收已经完成。
