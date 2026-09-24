---
type: fix
scope: workspace
audience: developer
summary: 为 Python 和 C# SDK 增加服务器会话 fencing 入口
breaking: false
demo_ready: false
tests: python -B -m unittest discover -s tests -v; dotnet test Tests/NewNanManager.Client.Tests.csproj --no-restore --filter Category!=Integration; dotnet build NewNanManager.Client.csproj --no-restore
artifacts:
  - python/newnanmanager/session.py
  - python/newnanmanager/http_client.py
  - python/newnanmanager/services/player.py
  - python/newnanmanager/services/monitor.py
  - python/newnanmanager/services/player_server.py
  - csharp/Models/Session.cs
  - csharp/Http/HttpClientBase.cs
  - csharp/Services/PlayerService.cs
  - csharp/Services/MonitorService.cs
  - csharp/Services/PlayerServerService.cs
---

What changed

Python 和 C# facade 新增服务器会话签发、不可变会话上下文，以及玩家准入、心跳、批量离线操作的显式 session 调用。有效请求发送 `X-NNM-Session-ID` 和 `X-NNM-Session-Epoch`；无效会话在网络请求前拒绝。

Why it matters

五种主要 facade 现在都能表达服务端的连接 fencing 约束，旧连接不能仅凭继续发送心跳或快照覆盖新连接状态。

Demo posture / limitations

旧入口保持兼容且不伪造 fencing 头；本条只覆盖 Python/C# 本地 fake transport 回归，不代表 Java facade、Guardian 双服顶号或生产部署验收已经完成。
