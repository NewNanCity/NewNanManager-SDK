# NewNanManager SDK

六语言客户端的独立 Git 仓库；HTTP 契约事实源为父项目 `../contracts/newnanmanager.openapi.json`。`codegen/manifest.json` 固定 OpenAPI Generator，Thrift 只用于父项目后端 Hertz 生成。

- `python/`：aiohttp/Pydantic；`typescript/`：Axios；`golang/`：Resty；`csharp/`：.NET HttpClient；`kotlin/`：OkHttp/Jackson；`java/`：Java 8 facade，依赖 `generated/java/` 的 Maven 模块。
- `codegen/`：生成器清单、版本锁定和漂移检查；`generated/` 只存自动生成的低级客户端，不手工修改。
- 回归与兼容限制见 `docs/audits/2026-09-06-contract-decisions.md`，变更事实见 `docs/releases/unreleased/`。
- 详细调用示例见 `docs/examples/`；请求类型以各语言模块源码为准。
- 名称、完整快照、Token绑定和监控分页见 `docs/contracts.md`。Go每个服务操作支持WithContext；旧入口保留，计数i64与TypeScript假字段纠正需按契约决策迁移。
- Go最低1.25.0，Python最低3.10；Token分页及模型构造迁移见契约决策，依赖版本、Python3.10/3.12哈希锁和扫描边界见 `docs/audits/2026-09-06-dependency-acceptance.md`。

各语言目录内执行：

```text
python -B -m unittest discover -s tests -v
npm test
npm run build
go test ./...
go vet ./...
dotnet test Tests/NewNanManager.Client.Tests.csproj --no-restore --filter Category!=Integration
.\gradlew.bat --offline test
```

默认使用 Authorization，拒绝自动重定向；测试只使用 fake/loopback。集成 runner 必须显式指定测试服务和测试凭证，不能默认运行。

Java facade 从 SDK 根目录执行 `mvn -B -ntp -f pom.xml test`；生成层先于 facade 构建。Java facade 不自动重试，请调用方读取 `HttpException#getRetryAfter()` 后自行决定。
