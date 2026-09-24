# NewNanManager SDK

NewNanManager 的 Python、TypeScript、Go、C#、Kotlin 和 Java 客户端。本目录是独立 Git 仓库，HTTP API 事实源为父项目 `../contracts/newnanmanager.openapi.json`；Thrift 只是后端 Hertz 生成链的中间产物。

| 语言 | HTTP/模型实现 | 文档 |
| --- | --- | --- |
| Python | aiohttp/Pydantic，异步 | [README](python/README.md) |
| TypeScript | Axios + OpenAPI generated transport，Promise | [README](typescript/README.md) |
| Go | Resty，同步 | [README](golang/README.md) |
| C# | .NET HttpClient，Task | [README](csharp/README.md) |
| Kotlin | OkHttp/Jackson，同步 | [README](kotlin/README.md) |
| Java | OkHttp/Gson，同步 facade + 生成客户端（Maven） | [facade](java/README.md)、[生成输出](generated/java/README.md) |

详细调用示例：[TypeScript](docs/examples/typescript.md)、[Go](docs/examples/golang.md)、[Kotlin](docs/examples/kotlin.md)。Python 和 C# 的示例保留在对应模块 README。

低级多语言客户端可由 [OpenAPI 生成工具](codegen/README.md) 重复生成。生成代码放在 `generated/<language>/`，手写 facade 不在生成目录中；鉴权凭证选择、错误兼容、分页和 Guardian session fencing 由各语言 facade 负责。Java facade 位于 `java/`，通过 SDK 根目录的 Maven reactor 依赖 `generated/java/`，生成目录可以安全地重新生成。

## 使用边界

各客户端提供玩家、城镇、服务器、Token、IP、监控和玩家服务器关系服务。默认 HTTP 客户端使用 Authorization，拒绝自动重定向；请配置最终 API 地址。默认请求超时 30 秒。Python 仅对读取请求的传输错误重试，写入请求不自动重发。

Go 所有服务方法增加请求级 WithContext 入口；六个统计计数已改为 int64。TypeScript 玩家关系仅返回 servers/total，封禁时间保持未知。源码迁移影响见 [契约决策](docs/audits/2026-09-06-contract-decisions.md)。TypeScript 的重定向控制只在 Node HTTP 适配器中验证，浏览器边界见模块文档。

现有六语言 facade 已同步 Token 的 server_id 绑定、监控来源及延迟口径、limit/cursor/next_cursor 分页；Java、Kotlin 和 TypeScript 主 facade 已接入 canonical OpenAPI generated transport。六种语言都可在 Bearer 与 `X-API-Token` 间选择，且每个请求只发送一种凭证头；六种 facade 也都提供显式 server session fencing 入口。名称仍为跨平台玩家唯一标识；login=false 是不可拆分的完整快照，允许空列表，最多1000人。字段约束与调用入口见 [契约与分页](docs/contracts.md)。

模板重建错误响应使用 `code/message/error/request_id/trace_id`；六语言异常对象保留机器错误类别和 code，并兼容迁移期的旧 `detail`。Java facade 另外保留原始 HTTP 状态、响应头和响应体。完整快照清空使用显式 `players=[]`，`players=null` 不再作为清空语义。

Token列表补齐分页元数据，Go/C#提供兼容新增的单页入口；TypeScript的Token搜索字段保留弃用声明但不发送。C#服务器模型提供Active，不再把缺失的ServerType解释为真实类型。Python手工构造Token列表模型时必须补齐total/page/page_size，见[契约迁移](docs/audits/2026-09-06-contract-decisions.md)。

Go SDK最低版本为1.25.0，x/net升级到0.56.0；Python SDK最低版本为3.10，aiohttp要求>=3.14.3，并提供3.10/3.12受控环境哈希锁。旧Go1.21至1.24与Python3.9用户需先升级工具链及下游依赖锁。Go1.25.14和Python3.10.19/3.12.12的回归通过，基线变更见[unreleased](docs/releases/unreleased/2026-09-06-runtime-sdk-go-python-baselines.md)。

依赖复核还更新了TypeScript兼容版本及Kotlin Jackson。Java 生成层使用 OkHttp/Gson 模板，facade 使用同一 HTTP 栈；生成层 70 个源文件和 facade reactor 已通过本地 Maven 测试，并验证 Bearer/X-API-Token 鉴权入口。Kotlin facade 已将普通业务服务接入生成 OpenAPI transport，生成源代码随 facade 一起编译，且 Kotlin 2.2.0、Gradle 8.14、Jackson 2.18.10 和 OkHttp 5.1.0 的离线测试通过；需要服务器 fencing 的三个入口使用显式 `SessionContext` 重载。当前 npm/pnpm 审查、Go SDK 模块扫描、Python 锁定运行依赖和 Kotlin 运行组件核查边界见[依赖验收](docs/audits/2026-09-06-dependency-acceptance.md)。

## 本地验证

在各语言目录执行，依赖需已安装或缓存：

```text
python -B -m unittest discover -s tests -v
npm test
npm run build
go test ./...
go vet ./...
dotnet test Tests/NewNanManager.Client.Tests.csproj --no-restore --filter Category!=Integration
.\gradlew.bat --offline test
```

这些回归使用 fake transport、loopback、模型样本和公共导入验证，不调用真实 API。没有全代码覆盖率报告，不宣称 100% 覆盖或生产验收。

TypeScript 集成 runner 必须显式配置 NANMANAGER_TOKEN 和 NANMANAGER_BASE_URL，可能操作测试数据；npm test 不执行真实服务请求。C# 真实服务测试标记 Category=Integration。TypeScript 构建会把 `generated/typescript/` 同步进最终 dist，旧模块工厂仍保留给兼容调用；浏览器重定向和生产 session 仍未验收。

本轮尚未提交、发布或部署；变更见 [unreleased](docs/releases/unreleased/)。
