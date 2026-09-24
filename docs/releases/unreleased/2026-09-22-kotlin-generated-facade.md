---
type: feature
scope: workspace
audience: developer
summary: 将 Kotlin 业务 facade 接入 OpenAPI 生成 transport
breaking: false
demo_ready: false
tests: >-
  kotlin/gradlew.bat --offline --console=plain test --rerun-tasks;
  python codegen/generate.py --all --check
artifacts:
  - kotlin/build.gradle.kts
  - kotlin/src/main/kotlin/com/nanmanager/bukkit/http/HttpClient.kt
  - kotlin/src/main/kotlin/com/nanmanager/bukkit/services/
  - kotlin/src/main/kotlin/com/nanmanager/bukkit/AuthScheme.kt
  - kotlin/src/main/kotlin/com/nanmanager/bukkit/SessionContext.kt
  - kotlin/src/test/kotlin/com/nanmanager/bukkit/ContractTest.kt
---

## What changed

Kotlin 的玩家、服务器、城镇、Token、IP、玩家服务器关系和监控查询/写入服务现在通过 canonical OpenAPI 生成客户端构造请求，并继续把结果转换为原有 `com.nanmanager.bukkit.models` 类型。生成源码作为 facade 的 source set 一起编译；认证拦截器只发送 Bearer 或 `X-API-Token` 其中一种，生成响应统一回到既有异常边界。

服务器会话接口新增 `SessionContext`：可以创建会话并把 session ID/epoch 传给心跳、玩家验证和批量离线请求。旧的无 session 重载保留兼容性，但不会伪造 fencing 头。

## Why it matters

请求路径、查询编码、请求体和响应模型现在由 OpenAPI 生成链维护，手写 facade 只负责业务入口、兼容模型和错误边界。后续契约变更重新生成即可发现接口漂移，避免每个服务重复维护 HTTP 拼接逻辑。

## Demo posture / limitations

本条已通过 MockWebServer 的跨服务、两种认证、结构化错误、空响应和 session headers 回归；这不代表 Bukkit/Minecraft 宿主、真实服务器权限、Guardian 双服演练或生产网络已经验收。无 session 的旧服务器方法仍是兼容入口，接入生产服务器时应使用 `SessionContext` 重载。
