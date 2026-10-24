---
type: feature
scope: workspace
audience: developer
summary: 完成 Java SDK 固定工具链的 JitPack 多模块分发与 Guardian 消费验证
breaking: false
demo_ready: false
tests: JitPack 8020eb3 build exit 0 with Maven 3.9.9 and 9/9 facade tests; isolated Maven consumer compile and Java 21 classpath probe; NextPlugins Guardian check; git diff --check
artifacts:
  - jitpack.yml
  - README.md
  - TODO.md
  - CLAUDE.md
---

What changed

新增根级 `jitpack.yml`，固定 OpenJDK 21 和 Maven 3.9.9，并以 `mvn -B -ntp -f pom.xml clean install` 构建整个 Maven reactor；该命令不使用 `-DskipTests`。原有 `com.newnancity` POM 坐标、facade 对 generated 的依赖和生成代码保持不变，公开分发坐标由 JitPack 在仓库层自动改写。固定 commit `8020eb3a3008bafeb0edc3ead543dd139b8a4368` 已完成远端构建、制品下载和消费者接入验证。

Why it matters

JitPack 的 Maven 默认命令会跳过测试，且默认使用 JDK 8；该服务默认的 Maven 也不满足本项目插件要求。显式工具链确保远端构建使用 JDK 21、Maven 3.9.9 并运行正常测试，同时发布父 POM、generated-java 和 facade 的完整依赖闭包。消费者可使用固定 commit `8020eb3a3008bafeb0edc3ead543dd139b8a4368`，无需内嵌 SDK 源码。

Demo posture / limitations

JitPack 固定 commit 已发布并可由独立 Maven 消费者解析。该结果是 Java 21 构建/类路径和 Guardian 构建期消费证据，不代表 Java 17、Paper 服务端运行时或生产环境验收；JitPack 发布也没有把原有 `com.newnancity:2.0.0` 坐标发布到 Maven Central。

## JitPack 规则

- [JitPack Building](https://docs.jitpack.io/building/) 规定：Maven 多模块仓库的模块 groupId 是 `com.github.User.Repo`，模块 artifactId 保持原 POM 值；聚合依赖使用 `com.github.User:Repo:Version`。
- [JitPack `maven-modular` 示例](https://github.com/jitpack/maven-modular) 进一步确认父 POM、模块和模块间依赖会在发布物中改写到 JitPack group，不需要手改仓库内 POM 或生成代码。
- JitPack 默认 Maven 命令为 `mvn install -DskipTests`，默认 JDK 为 8；本项目用 `jitpack.yml` 显式选择 OpenJDK 21 并执行不带跳过参数的标准 `clean install`。
- [JitPack Building](https://docs.jitpack.io/building/) 的 SDKMAN 示例允许在 `before_install` 中执行 `sdk install` 和 `sdk use`；[SDKMAN Usage](https://sdkman.io/usage/) 规定指定版本使用 `sdk install <candidate> <version>`，随后用 `sdk use` 切换到该版本。

## 发布与消费者验证

- 从 HEAD 的 Git archive 检出到临时目录，叠加待提交的 `jitpack.yml`，使用 JDK 21 和全新 Maven local repository 执行 `mvn -B -ntp -f pom.xml clean install`：三模块 reactor 成功，facade 9/9 测试通过，0 failure、0 error、0 skipped。日志：`C:\Users\nmg_w\AppData\Local\Codex\private-reports\newnan-sdk-jitpack-20261024\reactor-clean-install.log`。
- 首次远端候选 `d0bd657253e6ea67ceef795e10c98642407fb224` 在 generated 模块失败：`build-helper-maven-plugin:3.5.0` 要求 Maven 3.6.3，JitPack 默认 `mvn` 不满足要求，构建退出码为 1。原始日志继续保存在 `C:\Users\nmg_w\AppData\Local\Codex\private-reports\newnan-sdk-jitpack-20261024\jitpack-d0bd657-failure.log`，公开原址为 `https://jitpack.io/com/github/NewNanCity/NewNanManager-SDK/d0bd657253e6ea67ceef795e10c98642407fb224/build.log`。
- 修复候选 `8020eb3a3008bafeb0edc3ead543dd139b8a4368` 的真实 JitPack 构建设置了 OpenJDK 21 和 Maven 3.9.9，三模块 reactor 成功，facade 9/9 通过，构建退出码为 0。日志：`D:\Developer\Minecraft\NextPlugins\.tmp\jitpack-consumer\build-8020eb3-final.log`；公开原址为 `https://jitpack.io/com/github/NewNanCity/NewNanManager-SDK/8020eb3a3008bafeb0edc3ead543dd139b8a4368/build.log`。
- 已发布的三个模块坐标为 `com.github.NewNanCity.NewNanManager-SDK:newnanmanager-sdk:8020eb3a3008bafeb0edc3ead543dd139b8a4368`、`com.github.NewNanCity.NewNanManager-SDK:newnanmanager-generated-java:8020eb3a3008bafeb0edc3ead543dd139b8a4368` 和 `com.github.NewNanCity.NewNanManager-SDK:newnanmanager-java:8020eb3a3008bafeb0edc3ead543dd139b8a4368`。
- 下载的 facade POM 已把 parent 和 generated dependency 改写到同一 JitPack group 与完整 commit；聚合 POM 存在并返回 HTTP 200，坐标是 `com.github.NewNanCity:NewNanManager-SDK:8020eb3a3008bafeb0edc3ead543dd139b8a4368`。POM 快照见 `D:\Developer\Minecraft\NextPlugins\.tmp\jitpack-consumer\facade-8020eb3.pom` 和 `aggregate-8020eb3.pom`。
- 全新 Maven 缓存中的独立消费者编译成功，Java 21 classpath probe 同时解析 facade、generated、Gson 和 OkHttp。日志：`D:\Developer\Minecraft\NextPlugins\.tmp\jitpack-consumer\consumer-8020eb3.log` 和 `classpath-probe-java21.log`。
- NextPlugins 的 Guardian paper-module check 成功：81 个 actionable task 中 6 executed、75 up-to-date，39 pass、1 skip；`packaged SDK request, JSON response, 503 propagation and invalid URL` probe 为 PASS。日志：`D:\Developer\Minecraft\NextPlugins\.tmp\jitpack-consumer\guardian-check.log`。这证明当前构建期消费路径，不代表真实 Paper 服务启动、Java 17 运行时或生产验收。

## 远端工具链修复

`jitpack.yml` 在 `before_install` 中执行 `sdk install maven 3.9.9`、`sdk use maven 3.9.9` 和 `mvn -version`，再以 Maven 3.9.9 运行原 reactor 命令。`d0bd657` 与 `8020eb3` 的唯一发布配置差异是后者加入该 Maven 固定步骤及其文档；插件、POM、公共 API 和生成代码均未降级或修改。旧候选失败日志和修复候选成功日志分别保留，不能用成功结果覆盖首次根因。

JitPack 真实坐标改写、三模块制品、聚合 POM 和独立消费者解析均已验证。Java 17、真实 Paper 服务端启动、生产鉴权/网络策略和生产部署仍不在本轮证据范围内。

## 发布状态

`d0bd657253e6ea67ceef795e10c98642407fb224` 和 `8020eb3a3008bafeb0edc3ead543dd139b8a4368` 均已推送到 `NewNanCity/NewNanManager-SDK` 的 `main`；本轮没有创建 `2.0.0` tag。JitPack 使用完整 commit 固定源码版本；这不等于制品立即不可变或按字节可重现，下载制品应保存校验值。原始 `com.newnancity` 坐标只用于本地 reactor，`com.newnancity` 的 2.0.0 未发布到 Maven Central。
