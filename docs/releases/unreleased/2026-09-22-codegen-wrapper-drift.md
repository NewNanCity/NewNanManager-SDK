---
type: fix
scope: workspace
audience: developer
summary: 排除 Kotlin Gradle wrapper 运行缓存导致的生成漂移误报
breaking: false
demo_ready: false
tests: >-
  python -m unittest discover -s codegen -p "test*.py" -v;
  python codegen/generate.py --all --check
artifacts:
  - codegen/generate.py
  - codegen/test_manifest.py
  - generated/.gitignore
---

## What changed

生成树摘要和忽略规则现在明确排除 Kotlin Gradle wrapper 首次运行产生的 `gradle/wrapper/gradle-wrapper.jar`。该文件仍可由 Gradle 按需下载，但不会被误判为 OpenAPI 生成内容漂移。

## Why it matters

Kotlin 本地构建后再次执行六语言 drift check 不会因为机器级 wrapper 缓存失败，生成门禁重新只比较可复现的源码和构建元数据。

## Demo posture / limitations

已通过 9 项 codegen manifest 测试和六语言 `--check`；这不改变生成器版本、契约内容或生产发布状态。
