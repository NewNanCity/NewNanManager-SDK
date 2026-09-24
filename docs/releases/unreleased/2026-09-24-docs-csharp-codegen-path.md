---
type: fix
scope: docs
audience: developer
summary: 规范化 C# 生成 README 的契约路径，消除临时工作树造成的 SDK 漂移
breaking: false
demo_ready: false
tests:
  - python -B -m unittest discover -s codegen -p 'test*.py' -v
  - python codegen/generate.py --all --check
artifacts:
  - codegen/generate.py
  - codegen/test_manifest.py
  - codegen/README.md
  - generated/csharp/src/NewNanManager.Generated/README.md
---

## What changed

C# 生成器后处理现在把 README 示例中的 `inputSpec` 绝对路径替换为固定占位路径，并为该行为增加确定性测试。重新生成 C# 低级客户端，使已提交产物与新生成结果一致。

## Why it matters

不同开发机和 Codex worktree 不会再把本机目录写进生成文件；`--all --check` 能稳定区分真实契约差异和纯路径差异，SDK 生成树可复现。

## Demo posture / limitations

这次只修复生成文档的可复现性，不改变客户端运行行为，不代表任何语言 SDK 已发布或生产 API 已验收。
