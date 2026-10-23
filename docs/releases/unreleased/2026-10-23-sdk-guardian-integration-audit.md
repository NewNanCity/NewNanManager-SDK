---
type: docs
scope: workspace
audience: developer
summary: 验证 Guardian 使用的 Java SDK 生成链、打包边界与 session fencing 行为
breaking: false
demo_ready: false
tests: python -B codegen/generate.py --validate-only --all; python -B codegen/generate.py --language java --check; Java Maven reactor test with 9 tests; Guardian :plugins:guardian:paper:check
artifacts:
  - docs/audits/2026-10-23-guardian-integration.md
  - README.md
  - java/README.md
  - TODO.md
---

What changed

记录了 2026-10-23 对 canonical OpenAPI、固定版本 Java 生成层、Java facade 和 Guardian 消费边界的审计证据。Java README 现在明确说明 409 会话冲突不会触发自动重建，调用方必须管理新一轮 session 生命周期。

Why it matters

Guardian 通过独立 Maven 坐标消费 SDK。本轮证明当前提交的完整 reactor 可以发布到临时仓库，并能在不使用 mavenLocal 的情况下由 Guardian 解析、编译、测试和打入 Shadow JAR。

Demo posture / limitations

发现 canonical OpenAPI 未声明运行时已有的 409 会话冲突响应；这是待批准的公共契约覆盖缺口，本轮没有修改 schema。没有连接真实 Manager/MySQL、没有执行双服顶号演练、没有远端发布或部署。
