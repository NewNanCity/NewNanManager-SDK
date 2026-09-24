---
type: fix
scope: workspace
audience: developer
summary: 暴露 Python SDK 按请求附加 session 头的静态类型契约
breaking: false
demo_ready: false
tests: python -B -m unittest discover -s tests -v; ruff check --select F,E9 newnanmanager/http_client.py newnanmanager/services
artifacts:
  - python/newnanmanager/http_client.py
---

What changed

Python `HttpClient` 的 `get`、`post` 和 `put` 重载现在声明了实现已支持的关键字 `headers` 参数，服务层传递 `SessionContext` 头时静态类型检查与运行时行为一致。

Why it matters

调用方可以在不绕过类型检查的情况下使用 session fencing；鉴权头仍由连接级配置管理，请求级头只用于会话上下文。

Demo posture / limitations

本条只修正 Python facade 的类型声明并复用现有本地回归，不代表全包 Ruff、mypy、真实 Guardian 或生产环境验收已经完成。
