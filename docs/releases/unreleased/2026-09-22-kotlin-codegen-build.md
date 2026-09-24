---
type: fix
scope: workspace
audience: developer
summary: 固定 Kotlin 生成客户端的可重复构建工具链
breaking: false
demo_ready: false
tests: >-
  python codegen/generate.py --all --check;
  python -m unittest discover -s codegen -p 'test*.py' -v;
  generated/kotlin/gradlew.bat --offline --console=plain build --rerun-tasks
artifacts:
  - codegen/manifest.json
  - codegen/generate.py
  - codegen/test_manifest.py
  - generated/kotlin/build.gradle
---

## What changed

Kotlin 生成项目现在由生成脚本按 manifest 固定 Kotlin Gradle plugin 2.2.0、Gradle 8.14、Jackson 2.18.10 和 OkHttp 5.1.0，并将旧模板转换为可复现的 plugin DSL 构建。生成后会移除不参与客户端运行的 Spotless、旧 Android/测试依赖，并保留标准 Maven Central 与 wrapper 配置。

## Why it matters

生成客户端不再依赖模板携带的未锁定或当前缓存中不存在的插件，重新生成后可以在离线环境重复编译。版本来源集中在 manifest，构建模板的漂移会由 codegen 单元测试和全语言 drift-check 发现。

## Demo posture / limitations

Kotlin 生成低级客户端已通过本地离线 build，现有 Kotlin facade 也已通过独立离线测试；这不代表生成层已经接入 facade、覆盖全部业务行为、通过真实服务验收或可直接发布。生产权限、Guardian 双服演练和逐语言全字段行为对照仍是待办。
