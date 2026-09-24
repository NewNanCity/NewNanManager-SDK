---
type: fix
scope: runtime
audience: public
summary: 补齐Token分页与服务器模型，更新兼容依赖并提供Python3.12受控环境锁
breaking: true
demo_ready: false
tests:
  - npm test
  - npm run build
  - npm audit --package-lock-only --registry=https://registry.npmjs.org --ignore-scripts --json
  - pnpm audit --json
  - python -B -m unittest discover -s tests -v
  - go test -race ./...
  - go vet ./...
  - govulncheck -json ./...
  - dotnet test Tests/NewNanManager.Client.Tests.csproj --no-restore --filter Category!=Integration
  - .\gradlew.bat --console=plain test
artifacts:
  - typescript/package.json
  - typescript/package-lock.json
  - typescript/pnpm-lock.yaml
  - golang/go.mod
  - golang/go.sum
  - python/requirements-test-py312.txt
  - kotlin/build.gradle.kts
  - kotlin/src/test/kotlin/com/nanmanager/bukkit/JacksonCompatibilityTest.kt
  - golang/modules/token.go
  - csharp/Services/TokenService.cs
  - csharp/Models/Server.cs
  - python/newnanmanager/models/token.py
  - docs/audits/2026-09-06-dependency-acceptance.md
  - docs/audits/2026-09-06-contract-decisions.md
---

## What changed

本条保留第一阶段依赖修复快照；其中Go/Python基线待决项已由后续[基线升级](2026-09-06-runtime-sdk-go-python-baselines.md)完成，当前最低版本以该条为准。

Python/C#保留Token列表total/page/page_size；Go新增ListApiTokensPage和WithContext入口、C#新增ListApiTokensPageAsync，旧调用不变。TypeScript Token列表search只保留弃用类型，不向后端发送。C#服务器模型增加Active，ServerType原类型保留并标记Obsolete/JsonIgnore，仅作本地兼容属性。

Python `ListApiTokensData` 新增分页字段按IDL必填：旧的手工构造 `ListApiTokensData(tokens=...)` 和不完整fixture需提供真实元数据，因此本条标记breaking。真实后端完整响应可直接解析。

TypeScript运行依赖提升至Axios1.20.0、Lodash4.18.1及兼容传递修复；npm/pnpm锁一致。Go x/net0.17.0升至0.35.0，仍声明Go1.21。Python新增带hash的Windows/CPython3.12运行测试锁，aiohttp固定3.14.3；公开Python>=3.9声明保持不变。

Kotlin的三个Jackson直接依赖升至2.18.10，解析出的annotations/BOM同步；Kotlin/Java/OkHttp及7个非Jackson组件保持原版本。新增依赖级有界数字解析约束回归及实际SDK分页解析测试。

## Why it matters

Token列表可访问后续页、准确展示总数；服务器模型和查询参数不再把缺失契约当成真实后端支持。TypeScript官方audit从8个命中包降到0，Python受控环境避免使用已知存在客户端解析缺陷的旧aiohttp；Go兼容升级减少两条模块公告。

契约回归先复现后修复。最终TypeScript11、Python受控3.12环境11、C#15、Kotlin14项通过；Go在x/net升级后race三包及vet通过。TypeScript构建和生成弃用声明检查通过，npm/pnpm官方审查均0；Python锁定的14个运行依赖逐版本PyPI元数据无漏洞匹配；Kotlin12个运行组件OSV由9条匹配降到0。Go仍有12条模块公告，受影响包和符号无命中。

## Demo posture / limitations

未提交、发布、部署或操作真实API，不代表生产安全认证或所有下游解析环境均无风险。Python3.9不支持修复版aiohttp3.14.3；Go1.21不支持清除所有模块公告所需的x/net0.56.0/Go1.25。两项公开基线决策保留；Go实际测试工具链为1.26.6，Python只验证受控3.12.12环境。

C#测试依赖图有两条旧System包公告，但net8.0产物没有选择其运行资产；KotlinOSV范围仅实际运行组件，未扫描构建插件/测试依赖或自定义下游类型。详细证据、官方来源和扫描缺口见[依赖验收](../../audits/2026-09-06-dependency-acceptance.md)。

## 计划归档

- [x] 保存官方npm审查基线并核实维护者兼容修复版本。
- [x] 升级TypeScript依赖、同步两锁文件并完成测试/构建/审查。
- [x] 生成Python3.12哈希锁，在独立环境验证aiohttp修复及完整回归。
- [x] 在Go1.21声明范围内更新x/net并记录剩余模块公告与基线决策。
- [x] 合并Kotlin运行组件盘点、兼容修复、OSV复查及测试结果。
- [x] 补齐Token分页/服务器模型，归档源码兼容影响与逐语言证据。
