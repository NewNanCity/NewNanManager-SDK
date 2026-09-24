---
type: fix
scope: workspace
audience: developer
summary: 让六种 SDK 在本地校验 32–64 字符的服务器会话 ID
breaking: false
demo_ready: false
tests: go test ./...; python -B -m unittest discover -s tests -v; dotnet test Tests/NewNanManager.Client.Tests.csproj --filter Category!=Integration; npm test; npm run build; ./gradlew.bat --offline --console=plain test; mvn -B -ntp -f pom.xml test
artifacts:
  - golang/modules/session.go
  - python/newnanmanager/session.py
  - csharp/Models/Session.cs
  - java/src/main/java/com/newnancity/newnanmanager/SessionContext.java
  - kotlin/src/main/kotlin/com/nanmanager/bukkit/SessionContext.kt
  - typescript/src/generatedServices.ts
---

What changed

六种 facade 现在都执行与 canonical OpenAPI/Thrift 相同的 session ID 长度校验（32–64），Java 的原始字符串/代次重载也复用 `SessionContext` 校验；TypeScript 在创建 session 时也会立即校验服务端返回值和字段类型。测试 fixture 改为合法的 32 字符 ID，并增加短 ID、错误类型在网络请求前和 session 签发响应中的回归。

Why it matters

服务端只接受签发的会话标识。客户端提前拒绝短或异常响应，避免把必然返回 400 的 fencing 请求发送到服务器，同时让生成 transport 与手写 facade 的边界一致。

Demo posture / limitations

本条覆盖本地 facade 和生成链回归，不代表真实 Guardian、双服顶号、生产网络或长期运行验收已经完成。
