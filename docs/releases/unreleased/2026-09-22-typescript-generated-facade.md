---
type: feature
scope: workspace
audience: developer
summary: 将 TypeScript 主客户端接入 OpenAPI 生成 transport
breaking: false
demo_ready: false
tests: >-
  npm run build;
  npm test;
  python codegen/generate.py --all --check
artifacts:
  - typescript/src/client.ts
  - typescript/src/generatedServices.ts
  - typescript/scripts/sync-generated.mjs
  - typescript/tests/client.test.ts
---

## What changed

TypeScript 主客户端现在通过构建前同步的 OpenAPI 生成源码发起服务请求，保留原有 camelCase facade 模型、错误对象和 `setDefaultAxiosConfig` 测试入口。鉴权拦截器只发送 Bearer 或 `X-API-Token` 其中一种；监控心跳、玩家验证和批量离线接口提供显式 `SessionContext` 参数，旧单参数调用保留兼容 HTTP 路径。

## Why it matters

请求路径、查询编码、请求体和 session 扩展头由 canonical OpenAPI 生成链维护，手写层只负责业务模型映射、错误边界和兼容入口。生成源码由 marker 校验的同步脚本复制进最终 dist，npm 包不依赖尚未发布的 sibling 包。

## Demo posture / limitations

本条已通过 strict TypeScript 构建和 14 条 Node fake/loopback 回归，并通过六语言生成漂移检查；这不代表浏览器重定向、真实服务权限、Guardian 双服演练或生产网络已经验收。无 session 的旧入口仍不携带 fencing 头，生产服务器应传入 `SessionContext`。
