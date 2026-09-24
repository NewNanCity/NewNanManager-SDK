---
type: feat
scope: workspace
audience: developer
summary: 建立从 canonical OpenAPI 生成六种语言低级 SDK 的可复现入口
breaking: false
demo_ready: false
tests: python codegen/generate.py --all --check; python -m unittest discover -s codegen -p 'test*.py' -v
artifacts:
  - codegen/manifest.json
  - codegen/generate.py
  - codegen/test_manifest.py
  - codegen/README.md
  - TODO.md
  - generated/.gitignore
  - generated/<language>/ (TypeScript, Go, Python, C#, Kotlin, Java)
---

## What changed

SDK 仓新增固定 OpenAPI Generator CLI 与 engine 版本的清单和生成/漂移检查脚本。脚本支持 TypeScript、Go、Python、C#、Kotlin、Java 六种输出，Go 模块元数据对齐 1.25.0，Java 使用 OkHttp/Gson 并保留 Bearer/X-API-Token 鉴权入口，摘要对 Windows/Linux 换行差异稳定，并用稳定的仓库相对所有权标记防止误覆盖手写 SDK。

## Why it matters

HTTP 契约现在明确以父项目 `contracts/newnanmanager.openapi.json` 为事实源。Thrift 继续服务于 Hertz 后端生成，不再作为多语言 HTTP SDK 的人工事实源，减少契约分叉。

## Demo posture / limitations

本条只建立低级传输客户端的生成链，未替换现有五语言 facade，也不代表 API Token/Casbin、错误兼容、Guardian session fencing 或生产发布已经验收。Go/Python/TypeScript/C#/Java 本地构建通过；Kotlin 构建需补齐 Gradle/Maven 依赖环境。Java 仍需后续补手写业务 facade。生成输出应先经过逐语言行为对照后再接入公共 SDK。
