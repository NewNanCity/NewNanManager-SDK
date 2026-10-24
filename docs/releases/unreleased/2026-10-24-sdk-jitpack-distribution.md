---
type: feature
scope: workspace
audience: developer
summary: 为 Java SDK 增加固定 JDK、保留测试的 JitPack 多模块分发配置
breaking: false
demo_ready: false
tests: mvn -B -ntp -f pom.xml clean install in an isolated checkout/cache
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

本地用 JDK 21 和全新 Maven local repository 验证了原始 `com.newnancity` 坐标的三模块 reactor。Maven 版本沿用 JitPack OpenJDK 21 镜像提供的 `mvn`，本配置没有单独锁定 Maven 版本。尚未 push、tag 或触发 JitPack 远端构建；真实坐标改写、构建日志、三个远端模块坐标和 Guardian 对 JitPack 仓库的解析仍待协调者审查后验证。

## JitPack 规则

- [JitPack Building](https://docs.jitpack.io/building/) 规定：Maven 多模块仓库的模块 groupId 是 `com.github.User.Repo`，模块 artifactId 保持原 POM 值；聚合依赖使用 `com.github.User:Repo:Version`。
- [JitPack `maven-modular` 示例](https://github.com/jitpack/maven-modular) 进一步确认父 POM、模块和模块间依赖会在发布物中改写到 JitPack group，不需要手改仓库内 POM 或生成代码。
- JitPack 默认 Maven 命令为 `mvn install -DskipTests`，默认 JDK 为 8；本项目用 `jitpack.yml` 显式选择 OpenJDK 21 并执行不带跳过参数的标准 `clean install`。

## 本地验证

- 从 HEAD 的 Git archive 检出到临时目录，叠加待提交的 `jitpack.yml`，使用 JDK 21 和全新 Maven local repository 执行 `mvn -B -ntp -f pom.xml clean install`：三模块 reactor 成功，facade 9/9 测试通过，0 failure、0 error、0 skipped。日志：`C:\Users\nmg_w\AppData\Local\Codex\private-reports\newnan-sdk-jitpack-20261024\reactor-clean-install.log`。
- 本次没有验证 JitPack 的真实坐标改写或远端消费者解析。该闭包需由协调者在远端构建后以空缓存消费者验收。

## 待远端验证与推送范围

本地候选 commit 由本次提交产生；JitPack 使用该完整 commit hash 作为候选版本，不需要先创建 `2.0.0` tag。获准后只推送当前 SDK 本地提交链到 `NewNanCity/NewNanManager-SDK` 的 `main`（远端起点 `fe45110863b2a51999bb767a5610f0f8e69ac4bf`），不推送父 Manager，不创建 tag，不触发 JitPack。远端审查时应先请求 facade 模块坐标，确认 JitPack 构建日志和三模块/传递依赖，再由协调者决定是否让 NextPlugins 切换。
