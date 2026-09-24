---
type: fix
scope: runtime
audience: developer
summary: 五语言 SDK 同步模板错误响应并拒绝 null 完整快照语义
breaking: false
demo_ready: false
tests: python -m pytest -q; go test ./...; npm test; npm run build; dotnet test Tests/NewNanManager.Client.Tests.csproj --no-restore --filter FullyQualifiedName~HttpClientTests; ./gradlew.bat test --no-daemon
artifacts: python/newnanmanager/exceptions.py, python/newnanmanager/http_client.py, golang/utils/error_handler.go, typescript/src/utils/errorHandler.ts, csharp/Http/HttpClientBase.cs, kotlin/src/main/kotlin/com/nanmanager/bukkit/http/HttpClient.kt
---

What changed

五语言客户端现在解析模板错误体的 `message`、数值 `code`、嵌套机器类别/code 以及 request/trace ID，同时保留旧 `detail` 回退。异常对象暴露这些机器字段，便于调用方稳定分支。契约文档明确完整快照清空必须使用 `players=[]`，不再把 null 当成清空。

Why it matters

客户端可以在服务端切换到模板错误注册表后继续保留状态码、重试头和关联标识，避免依赖展示文本或丢失排障上下文。迁移期双格式读取让 SDK 与尚未切换的旧实例保持可用。

Demo posture / limitations

这次不代表后端运行服务已经切换或五语言包已发布；C# 全量测试仍包含需要外部服务的集成用例，本轮只把 fake/loopback 的客户端错误回归作为验收证据。
