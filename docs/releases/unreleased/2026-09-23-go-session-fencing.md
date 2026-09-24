---
type: fix
scope: workspace
audience: developer
summary: 为 Go SDK 增加服务器会话 fencing 入口
breaking: false
demo_ready: false
tests: go test ./...; go vet ./...
artifacts:
  - golang/modules/session.go
  - golang/modules/monitor.go
  - golang/modules/player.go
  - golang/modules/player_server.go
  - golang/modules/session_test.go
---

What changed

Go facade 新增 `SessionContext`、服务器会话签发，以及准入、心跳、批量离线操作的显式 session 重载。有效调用发送 `X-NNM-Session-ID` 和 `X-NNM-Session-Epoch`；空 ID 或非正 epoch 在网络请求前拒绝。

Why it matters

旧服务器连接不会因为继续发送心跳或离线快照而覆盖新连接的状态，Go 插件调用路径与 OpenAPI 的 session fencing 约定一致。

Demo posture / limitations

旧单参数方法仍保留兼容路径；本条只覆盖 Go facade 和本地 loopback 回归，不代表 Bukkit/Guardian 双服顶号或生产部署已验收。
