---
type: feature
scope: workspace
audience: developer
summary: 为 Java SDK 增加固定 JDK、保留测试的 JitPack 多模块分发配置
breaking: false
demo_ready: false
tests: remote JitPack build failure preserved; python -B -c "import yaml, pathlib; yaml.safe_load(pathlib.Path('jitpack.yml').read_text(encoding='utf-8'))"; git diff --check
artifacts:
  - jitpack.yml
  - README.md
  - TODO.md
  - CLAUDE.md
---

What changed

新增根级 `jitpack.yml`，固定 OpenJDK 21，并以 `mvn -B -ntp -f pom.xml clean install` 构建整个 Maven reactor；该命令不使用 `-DskipTests`。原有 `com.newnancity` POM 坐标、facade 对 generated 的依赖和生成代码保持不变，公开分发坐标由 JitPack 在仓库层自动改写。

Why it matters

JitPack 的 Maven 默认命令会跳过测试，且默认使用 JDK 8。显式配置确保远端构建使用与本地验收一致的 JDK 21 并运行正常测试，同时保留 Maven reactor 的父 POM、generated-java 和 facade 发布闭包。消费者使用固定 commit 的 `com.github.NewNanCity.NewNanManager-SDK:newnanmanager-java:<commit>`，无需在本地或 NextPlugins 内嵌源码。

Demo posture / limitations

本地用 JDK 21 和全新 Maven local repository 验证了原始 `com.newnancity` 坐标的三模块 reactor。首次远端候选 `d0bd657` 暴露 JitPack 默认 Maven 过旧，现已在 `before_install` 中用 SDKMAN 固定 Maven 3.9.9；新提交的真实坐标改写、三个远端模块坐标和 Guardian 对 JitPack 仓库的解析仍待协调者复测。

## JitPack 规则

- [JitPack Building](https://docs.jitpack.io/building/) 规定：Maven 多模块仓库的模块 groupId 是 `com.github.User.Repo`，模块 artifactId 保持原 POM 值；聚合依赖使用 `com.github.User:Repo:Version`。
- [JitPack `maven-modular` 示例](https://github.com/jitpack/maven-modular) 进一步确认父 POM、模块和模块间依赖会在发布物中改写到 JitPack group，不需要手改仓库内 POM 或生成代码。
- JitPack 默认 Maven 命令为 `mvn install -DskipTests`，默认 JDK 为 8；本项目用 `jitpack.yml` 显式选择 OpenJDK 21 并执行不带跳过参数的标准 `clean install`。
- [JitPack Building](https://docs.jitpack.io/building/) 的 SDKMAN 示例允许在 `before_install` 中执行 `sdk install` 和 `sdk use`；[SDKMAN Usage](https://sdkman.io/usage/) 规定指定版本使用 `sdk install <candidate> <version>`，随后用 `sdk use` 切换到该版本。

## 本地验证

- 从 HEAD 的 Git archive 检出到临时目录，叠加待提交的 `jitpack.yml`，使用 JDK 21 和全新 Maven local repository 执行 `mvn -B -ntp -f pom.xml clean install`：三模块 reactor 成功，facade 9/9 测试通过，0 failure、0 error、0 skipped。日志：`C:\Users\nmg_w\AppData\Local\Codex\private-reports\newnan-sdk-jitpack-20261024\reactor-clean-install.log`。
- 首次远端候选 `d0bd657253e6ea67ceef795e10c98642407fb224` 在 generated 模块失败：`build-helper-maven-plugin:3.5.0` 要求 Maven 3.6.3，JitPack 默认 `mvn` 不满足要求，构建退出码为 1。原始日志已保存到 `C:\Users\nmg_w\AppData\Local\Codex\private-reports\newnan-sdk-jitpack-20261024\jitpack-d0bd657-failure.log`，公开原址为 `https://jitpack.io/com/github/NewNanCity/NewNanManager-SDK/d0bd657253e6ea67ceef795e10c98642407fb224/build.log`。

## 远端工具链修复

`jitpack.yml` 现在在 `before_install` 中执行 `sdk install maven 3.9.9`、`sdk use maven 3.9.9` 和 `mvn -version`，再以 Maven 3.9.9 运行原 reactor 命令。该修复只改变 JitPack 构建工具选择，不降级插件，不修改 POM、公共 API 或生成代码；旧候选的失败日志保留为本轮根因证据。修复后的 commit 与真实 JitPack 复测结果由协调者生成和记录。

本次仍未验证 JitPack 的真实坐标改写或远端消费者解析。该闭包需由协调者在远端构建后以空缓存消费者验收。

## 待远端验证与推送范围

修复后的候选 commit 由本次提交产生；JitPack 使用该完整 commit hash 作为候选版本，不需要先创建 `2.0.0` tag。`d0bd657253e6ea67ceef795e10c98642407fb224` 已在 `NewNanCity/NewNanManager-SDK` 的 `main`，获准后只追加推送本次修复 commit，不推送父 Manager，不创建 tag。远端审查时应先请求 facade 模块坐标，确认 JitPack 构建日志和三模块/传递依赖，再由协调者决定是否让 NextPlugins 切换。
