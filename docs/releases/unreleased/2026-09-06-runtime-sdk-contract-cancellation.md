---
type: fix
scope: runtime
audience: developer
summary: 完成 Go 请求级取消和统计类型纠正，修正 TypeScript 假字段，同步五语言 Token 绑定与监控分页契约。
breaking: true
demo_ready: false
tests:
  - "golang: go test -race ./..."
  - "golang: go vet ./..."
  - "typescript: npm test"
  - "typescript: npm run build"
  - "python: python -B -m unittest discover -s tests -v"
  - "python: ruff check newnanmanager/models/server.py newnanmanager/models/token.py newnanmanager/models/requests.py newnanmanager/services/monitor.py tests/test_contract.py"
  - "csharp: dotnet test Tests/NewNanManager.Client.Tests.csproj --no-restore --filter Category!=Integration"
  - "kotlin: .\\gradlew.bat --offline test"
  - "sdk: git diff --check"
artifacts:
  - golang/modules/
  - golang/client_test.go
  - typescript/src/types.ts
  - typescript/src/modules/ip.ts
  - typescript/src/modules/player-server.ts
  - typescript/src/modules/token.ts
  - typescript/src/modules/server.ts
  - typescript/src/modules/monitor.ts
  - typescript/tests/client.test.ts
  - python/newnanmanager/models/
  - python/newnanmanager/services/monitor.py
  - python/tests/test_contract.py
  - csharp/Models/
  - csharp/Services/MonitorService.cs
  - csharp/Tests/ContractTests.cs
  - kotlin/src/main/kotlin/com/nanmanager/bukkit/models/Models.kt
  - kotlin/src/main/kotlin/com/nanmanager/bukkit/services/MonitorService.kt
  - kotlin/src/test/kotlin/com/nanmanager/bukkit/ContractTest.kt
  - docs/contracts.md
  - docs/audits/2026-09-06-contract-decisions.md
  - README.md
  - CLAUDE.md
  - TODO.md
---

## What changed

D03：保留 Go 原有36个服务方法签名，每个新增 XxxWithContext。context 只落在本次 Resty Request；旧入口使用 context.Background()。默认总超时和配置入口保持原行为，新分页操作同样支持 context。

D04：Go 六个旧 IP 统计计数改为 int64，匹配 IDL。TypeScript PlayerServersResponse 收敛为 servers/total；IPBan.bannedAt 改为可选弃用字段，移除 created_at 假映射；unbannedAt/unbanReason 不再生成。activeOnly/search 保留弃用声明且不发送，服务端实际只使用分页。

五语言增加 Token server_id、监控 measurement_type/latency_metric、limit/cursor/next_cursor。Go 和 C# 新增接收 MonitorStatsQuery 的单页方法，保留旧统计签名；Python 新增 keyword-only 参数；TypeScript/Kotlin 扩展原请求对象。未知元数据保持未知，游标按原值编码并传递，不自动聚合所有页。

名称继续作为唯一标识，SDK 保留原拼写。验证快照最多1000人，login=false允许空列表且不可分批；请求模型注释和文档同步，五语言均验证一次完整发送0或1000人。README、CLAUDE、契约迁移与共同调用说明已更新。

## Why it matters

调用者可以取消单个 Go 请求而不影响共享客户端上的其他请求；超过 int32 的真实统计可正常读取。类型和数据不再把不存在的页码或 IP 创建时间包装成有效业务信息。Token 绑定和有上限的监控分页可通过所有现有 SDK 使用，调用者能区分采样来源和延迟口径。

RED 证据：Go 全部36个旧服务操作缺少取消入口；2147483648和9223372036854775807统计解码失败。TypeScript 回归暴露假分页属性和无效 active_only；新增契约测试显示 Go/TS/C#/Kotlin 类型或分页入口缺失，Python 模型丢弃字段且不接受 limit。修复后这些用例通过。

兼容影响：Go 六个计数字段的 int32 赋值需迁移，新增结构体字段要求位置式字面量改为带字段名的写法；TypeScript 删除假分页字段及 bannedAt 可选化会触发源码诊断。Kotlin 新字段有默认值，原 Kotlin 源码可重新编译，但数据类 JVM 构造器/copy 二进制签名变化，下游必须重新构建。旧监控方法仍可调用，但必须检查下一页游标，单页不代表全部历史。

## Demo posture / limitations

这次不代表什么：没有提交、推送、发布、部署、数据库迁移、历史改写或凭证操作；没有运行真实集成接口。测试使用 fake/loopback，验证 SDK 的请求/响应和取消行为，不证明线上权限、历史回填、服务性能或浏览器重定向策略。

本地结果：Python 10项、TypeScript 10项、C# 7项、Kotlin 12项通过；Go 三个包的 race/vet 通过；TypeScript 构建通过。Python 本轮新增契约文件的定向 Ruff 检查通过，仅提示现有配置写法弃用；第一阶段 HTTP 文件的既有诊断未扩范围处理。Git diff --check通过，Windows只提示行尾转换。

Go 使用已有缓存工具链 `C:/Users/nmg_w/go/pkg/mod/golang.org/toolchain@v0.0.1-go1.26.6.windows-amd64/bin/go.exe`，设置 GOTOOLCHAIN=local、GOPROXY=off、GOSUMDB=off。C# 使用 --no-restore，Kotlin 使用 --offline，没有新增依赖。

已完成计划归档：D03、D04、五语言新增契约、逐单元 RED/GREEN、完整本地验证、自审、README/CLAUDE和迁移说明均完成。未验证的浏览器边界、历史凭证处理及既有 Python lint 欠账仍在 TODO。
