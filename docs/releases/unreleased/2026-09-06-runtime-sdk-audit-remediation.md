---
type: fix
scope: runtime
audience: developer
summary: 修复五语言 SDK 的 HTTP 安全与错误处理缺陷，补充本地回归并记录待决类型纠正。
breaking: true
demo_ready: false
tests:
  - "python: python -B -m unittest discover -s tests -v"
  - "typescript: npm test"
  - "typescript: npm run build"
  - "typescript: npm pack --dry-run --json --ignore-scripts --offline"
  - "golang: go test -race ./..."
  - "golang: go vet ./..."
  - "csharp: dotnet test Tests/NewNanManager.Client.Tests.csproj --no-restore --filter Category!=Integration"
  - "kotlin: .\\gradlew.bat --offline test"
artifacts:
  - python/newnanmanager/http_client.py
  - python/newnanmanager/models/town.py
  - python/newnanmanager/exceptions.py
  - python/tests/
  - typescript/src/client.ts
  - typescript/src/utils/errorHandler.ts
  - typescript/src/integration-test.ts
  - typescript/src/comprehensive-test.ts
  - typescript/tests/client.test.ts
  - golang/client.go
  - golang/modules/types.go
  - golang/utils/error_handler.go
  - golang/client_test.go
  - golang/modules/ip_test.go
  - golang/utils/error_handler_test.go
  - csharp/Http/HttpClientBase.cs
  - csharp/NewNanManagerClient.cs
  - csharp/Exceptions/NewNanManagerException.cs
  - csharp/Tests/HttpClientTests.cs
  - kotlin/src/main/kotlin/com/nanmanager/bukkit/http/HttpClient.kt
  - kotlin/src/test/kotlin/com/nanmanager/bukkit/HttpClientTest.kt
  - docs/audits/2026-09-06-contract-decisions.md
  - docs/examples/
---

## What changed

此条目记录第一阶段的验收状态。后续授权完成了 D03/D04 与新增后端契约对齐，见 [后续变更](2026-09-06-runtime-sdk-contract-cancellation.md)；下文的“待决”是该阶段留下的边界。

- D01：修复 Python Town 响应模型缺失的 Player 导入，公共入口可以加载。
- D02：Python 只对 GET/HEAD/OPTIONS 的传输错误自动重试；POST/PUT/DELETE 只发送一次，HTTP 错误不自动重试。
- D03：Go 默认总超时为 30 秒，兼容新增 SetTimeout；无响应连接会按截止时间退出。请求级 context 取消入口尚未实现。
- D04：Go 兼容新增 completed/pending/failed/datacenter_ips 四个 int64 字段，保留旧字段与类型。三项公开类型纠正仍待确认，TypeScript bannedAt 的错误映射未修复。
- D05：Kotlin 使用 OkHttp HttpUrl 编码查询参数，保留特殊字符本义。
- D06：SDK HTTP 错误保留状态、请求 ID 和 Retry-After；Kotlin 不再吞掉 ApiException。TypeScript 新增不携带 Axios 请求配置的 NewNanManagerHttpError，避免序列化异常时泄露认证头。
- S09：Python 与 C# 不再记录完整请求/响应体；Kotlin 解析失败异常不包含原始响应体或泄露该响应的 Jackson cause。
- S10：五语言默认客户端只发送 Authorization 并拒绝自动重定向。Kotlin 同时关闭连接失败后的自动重发，并设置总调用超时。
- S11：TypeScript 集成 runner 的服务地址和凭证改为显式环境变量，缺失时在构建客户端前失败。构建先清理 dist，排除 runner；npm 包仅包含 dist、README 和 npm 自带元数据。

兼容说明：保留已有函数和异常构造签名，新增 Go SetTimeout、错误元数据和缺失字段。breaking 标记指 HTTP 行为收紧：之前依赖自动重定向、写请求自动重发或隐式集成配置的调用需要调整；待决的公开类型破坏没有实施。TypeScript 浏览器适配器不在拒绝重定向的验收范围内，C# 调用者注入的 HttpClient 需自行配置 handler。

## Why it matters

修复阻止 SDK 入口加载的错误，防止非幂等写入在提交状态不明时被重发，避免凭证进入响应日志、异常配置或重定向目的地。调用者可以通过结构化错误元数据判断 404/409/429，而不依赖异常消息文本；有限总超时避免业务线程无限等待。

失败回归覆盖 Python 导入失败、写入重发、敏感体日志和跨源头泄露；Go 无超时、认证头、缺失 DTO 字段与错误元数据；TypeScript 错误元数据、异常序列化和重定向；C# 敏感体日志、头与错误元数据；Kotlin 查询编码、吞异常、重定向和解析异常体泄露。集成 runner 的配置缺失测试在去掉凭证默认值后运行，避免启动旧入口访问真实服务。README 已改为当前可用接口，去除不存在的方法和未有证据的覆盖率声明。

复核后将仍有用的 TypeScript、Go、Kotlin 创建、验证和查询示例恢复到 `docs/examples/`，模块 README 保留链接；Python 和 C# 的详细用法没有大幅删减。旧签名纠正为现有服务模块与请求类型，不恢复未经核实的公共包安装或生产就绪声明。

示例验证：Markdown 中 3 段 TypeScript 函数通过本地 TypeScript Compiler API 的 noEmit 类型检查；3 段 Go 函数临时抽取为测试源码，通过 `go test -run '^$'` 仅编译；2 段 Kotlin SDK 函数临时抽取后通过 `gradlew.bat --offline compileTestKotlin`。临时源码已删除，没有执行函数；Bukkit 宿主示例仅核对 API 用法，未编译或运行。

## Demo posture / limitations

这次不代表什么：不代表真实 API、MC、数据库或生产部署验收；未调用真实服务、未使用原有凭证、未安装依赖、未提交、未推送、未发布、未轮换凭证、未改 Git 历史。

本地结果：Python 6 项、TypeScript 4 项、Go 5 项、C# 3 项、Kotlin 8 项通过；Go race/vet 通过；TypeScript 构建与离线 pack dry-run 通过，包清单 46 个文件，没有 src、tests 或两个 runner。C# 使用缓存依赖与 --no-restore，排除 Category=Integration；Kotlin 使用 --offline。Go 验证时 GOPROXY/GOSUMDB 均为 off。全部网络回归仅使用 fake 或 loopback。

Python 定向 Ruff 检查仍报告 34 条既有诊断，相同 HEAD HTTP 文件基线为 35 条；新增测试、异常模块和模型导入未引入诊断。未进行完整 mypy 或浏览器 HTTP 适配器验证，也没有依赖漏洞重新扫描或覆盖率证明。Go 请求级取消、D04 的三项公开类型纠正及历史凭证处置留在 TODO。

已完成计划归档：Python、Go、TypeScript、C#、Kotlin 的本轮行为修复与即时回归，自审、README/CLAUDE 同步和发布条目记录均完成；未决事项保留在 TODO，不把部分完成标成全部关闭。
