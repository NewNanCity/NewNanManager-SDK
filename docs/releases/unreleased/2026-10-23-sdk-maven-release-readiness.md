---
type: docs
scope: workspace
audience: developer
summary: 核实 Java SDK Maven 发布机制、2.0.0 发布证据与可执行发布门禁
breaking: false
demo_ready: false
tests: git diff --check; public Maven Central and GitHub read-only preflight queries
artifacts:
  - README.md
  - TODO.md
  - CLAUDE.md
---

What changed

记录 Java SDK 的当前 Maven 产物、源码与版本关系、公开仓库查询结果、发布前门禁、回退边界，以及正式发布前必须由用户提供的配置。仅做只读侦察和文档准备，没有修改版本、POM、公共契约或全局配置，也没有发布、推送或部署。

> 2026-10-24 决策更新：用户已批准采用 JitPack；本页保留为本地 Maven 构建与制品边界参考，公开分发配置与候选验证见 [JitPack distribution](2026-10-24-sdk-jitpack-distribution.md)。

## 当时事实（2026-10-23）

- 父仓本地 HEAD 为 `1e1009d0c089c53ef263a50baf12f2bfa052ed50`，`origin/main` 为 `b73ac4bcc1f1811c705095ec5694a05f31942757`，本地领先 2 个提交。
- SDK 本地 HEAD 为 `9c0e401e537e51cae46a2fe3a8f756cd6ae88b76`，`origin/main` 为 `fe45110863b2a51999bb767a5610f0f8e69ac4bf`，本地领先 2 个提交。
- SDK Git 远端是 `ssh://github.com/NewNanCity/NewNanManager-SDK.git`；本地和远端均没有 tag。
- 根 POM、生成 Java POM 和 facade POM 都固定为 `2.0.0`；canonical OpenAPI 的 `info.version` 仍是 `2.0.0-rebuild-draft.3`。这两个版本没有自动同步机制。
- 根 [pom.xml](../../../pom.xml:7) 是 `pom` 聚合工程，模块为 `generated/java` 和 `java`；没有 `distributionManagement`、发布插件或默认仓库。
- 生成 Java [pom.xml](../../../generated/java/pom.xml:4) 定义 `com.newnancity:newnanmanager-generated-java:2.0.0`，包含 sources/Javadoc 和仅作用于该模块的 `sign-artifacts` GPG profile，但同样没有默认部署仓库。
- Facade [pom.xml](../../../java/pom.xml:7) 定义 `com.newnancity:newnanmanager-java:2.0.0`，依赖同版本 generated artifact；目前没有 sources/Javadoc attachment 或签名配置。

## 现有发布机制

仓库内没有 SDK 自动 Maven 发布工作流：父仓的两个 workflow 只验证后端，`sdk/` 下没有 `.github/workflows`。当前可复现路径只有 Maven 手工 `deploy`；历史验证使用 `mvn package org.apache.maven.plugins:maven-deploy-plugin:3.1.4:deploy -DaltDeploymentRepository=newnanManager::<URL>` 发布到临时 file 仓库，再由 Gradle 经 loopback HTTP 解析。该验证证明 reactor 顺序和完整依赖关系可用，不证明任何远端仓库已存在或已接收 2.0.0。

本机没有 `~/.m2/settings.xml` 或 `~/.gradle/gradle.properties`，当前环境也没有 Maven/Gradle 发布仓库变量。NextPlugins 只定义了可配置的 `newnanManagerRepositoryUrl`、用户名和密码入口；它不内置真实仓库地址。因未配置目标且未使用凭证，私有 Maven 仓库中是否已有 2.0.0 仍无法核实。

周边 Java 仓库的只读对照没有发现组织内现成的 Maven registry 发布配置：`AreaCommandBlocker`、旧 `Guardian`、`Java_NewNanPlus` 和 `MCron` 的 `maven.yml` 虽然名为 publish，但实际只 `mvn package` 后上传 GitHub Release JAR；它们没有 `mvn deploy`。`Java_NewNanPlus` 同时通过 JitPack 读取 `com.github.NewNanCity` 依赖。因此 GitHub 是本组织现成的源码/发布托管面，但这些仓库没有提供可直接复用的 Maven 包仓库。

## 2.0.0 发布证据

- Maven Central search 对 `newnanmanager-sdk`、`newnanmanager-generated-java` 和 `newnanmanager-java` 三个 2.0.0 坐标的公开查询均为 0 条结果。
- 三个 artifact 的 `https://repo1.maven.org/maven2/com/newnancity/<artifact>/2.0.0/` 路径均返回 HTTP 404。
- GitHub 的 SDK tag/release API 均返回 0 条。
- GitHub 仓库级 Maven package API 返回 404；Maven Packages endpoint 未认证返回 401，因此不能从公开接口区分“不存在”和“私有/受限”。GitHub Packages 页面没有提供可验证的已发布包清单。
- 本地 `~/.m2` 和 Gradle cache 中的 2.0.0 是由本次空环境回环验证产生或解析的，不是远端发布证据。

结论：没有发现 Maven Central 或公开 GitHub 上可验证的 2.0.0 已发布证据；私有仓库状态未知，不能据此断言 2.0.0 从未发布。目标仓库本身必须以已认证的坐标查询为准。

## 完整制品

一次 Java SDK 发布必须包含三个 POM 层级的完整集合：

```text
com.newnancity:newnanmanager-sdk:2.0.0             pom
com.newnancity:newnanmanager-generated-java:2.0.0  pom + jar
com.newnancity:newnanmanager-java:2.0.0            pom + jar
```

对应本地文件为：

- `pom.xml`：父 POM。
- `generated/java/pom.xml`、`generated/java/target/newnanmanager-generated-java-2.0.0.jar`、`-sources.jar`、`-javadoc.jar` 和当前附加的 `-tests.jar`。
- `java/pom.xml`、`java/target/newnanmanager-java-2.0.0.jar`；当前没有 facade sources/Javadoc 制品。

生成的 Java 模块当前还产出 sources、Javadoc 和空 test JAR；facade 当前只有主 JAR。私有 Maven/GitHub Packages 可以接受这组产物；若目标改为 Maven Central，还必须补齐 facade 的 sources/Javadoc、三个 POM 的发布元数据和所有产物的签名，并确认 `com.newnancity` namespace 所有权。

当前本地构建已有 generated 主 JAR、sources、Javadoc、test JAR 和 facade 主 JAR；它们位于 `generated/java/target/` 与 `java/target/`。这些文件属于本地候选，不作为提交内容，也没有证明远端发布。

## 发布前门禁

1. 使用最终批准的发布仓库 URL 和只读/写入分离的凭据，对三个坐标的 POM 与主 JAR 做已认证 HEAD/GET。任一 2.0.0 对象已存在就停止，不得覆盖。
2. 确认源提交：本次候选为父仓 `1e1009d0c089c53ef263a50baf12f2bfa052ed50` 与 SDK `9c0e401e537e51cae46a2fe3a8f756cd6ae88b76`。正式发布前应先获得推送/标签授权，让 SDK 提交和可选 release tag 可被远端复核。
3. 在隔离的干净 SDK 检出中按固定工具链重新构建，不得用当前未跟踪 `java/target` 作为最终发布源。构建入口是 SDK 根目录的 `pom.xml`，生成的发布物必须按父 POM、generated、facade 顺序解析。
4. 校验 Maven reactor、sources/Javadoc、目标仓库要求、许可/SCM 元数据和产物 SHA-256；若有 staging repository，应先把完整制品放入 staging，再一次性 release。
5. 发布后从同一 URL 以只读身份重新下载三部分制品并核对 POM 依赖闭包。Guardian 构建通过 `newnanManagerRepositoryUrl` 读取该仓库，`exclusiveContent` 仅把 `com.newnancity` 限定到该地址。

构建与部署命令模板如下；`SDK_RELEASE_URL` 和 Maven `settings.xml` 的 `newnanManager` server 必须由受控环境提供，凭证不进入 URL 或仓库文件：

```powershell
mvn -B -ntp -f pom.xml clean verify
mvn -B -ntp -f pom.xml package `
  org.apache.maven.plugins:maven-deploy-plugin:3.1.4:deploy `
  "-DaltDeploymentRepository=newnanManager::$SDK_RELEASE_URL"
```

第二条命令必须继续使用显式固定的 `maven-deploy-plugin:3.1.4` goal，不能简写为未锁版本的 `mvn deploy`：仓库 POM 没有锁定 deploy 插件版本或 `distributionManagement`，但既有成功证据验证的是这个 goal 与 `id::url` 参数组合。

## 最少用户输入

1. **推荐发布目标：GitHub Packages** `https://maven.pkg.github.com/newnancity/NewNanManager-SDK`。组织现有源码和周边发布都在 GitHub，Guardian 又已有仓库 URL 与独立用户名/密码入口，因此这是当前最少新增组件的方案；代价是消费者必须配置 `read:packages` 凭据，发布者需要受控的 `write:packages` 凭据。替代方案是已有的内部/私有 Maven 仓库，运维成本可能更低，但本次未发现其地址或配置；只有确认组织实际运营该仓库时才优先它。Maven Central 成本最高，需要 namespace、GPG、sources/Javadoc 和完整 POM 元数据，仅在需要无认证公共消费时采用。
2. **推荐版本：保留 2.0.0**，但必须先以目标仓库凭据确认 `com.newnancity:newnanmanager-sdk:2.0.0`、`com.newnancity:newnanmanager-generated-java:2.0.0` 和 `com.newnancity:newnanmanager-java:2.0.0` 均未占用。任何对象已存在则停止并改用一个未占用的新版本；本轮不修改版本。
3. **推荐源码边界：推送后发布**。拟推送身份为父仓 `1e1009d0c089c53ef263a50baf12f2bfa052ed50` 与 SDK `9c0e401e537e51cae46a2fe3a8f756cd6ae88b76`，两者都在 `main` 且各领先 `origin/main` 2 个提交。正式发布前应获准推送，并推荐在 SDK 提交上创建 `sdk-v<version>` annotated tag（当前候选对应 `sdk-v2.0.0`）；不推送则只能保留本地候选，不具备远端可复现的源码来源。
4. 在受控环境中配置发布仓库凭据，并将消费者只读凭据单独配置；不要把用户名、密码或 token 发到聊天、提交到仓库或写进命令参数。

## 回退边界

- Maven 版本不可覆盖。若 2.0.0 已有任何目标仓库对象，不得删除后重传或以本地新构建替换；应发布新的 patch/minor 版本。
- 若使用支持 staging 的仓库，未 release 的 staging 内容可丢弃；这是首选的回退方式。
- 若直接 deploy 到普通仓库，发布不是原子的，部分父 POM/模块可能先于其余制品可见。失败后只能在仓库策略允许的范围内隔离或移除未发布的坏版本，并让消费者回退到已知可用坐标；否则发布修复版本，不承诺原地回滚。
- Guardian 已通过仓库属性读取 SDK；切换仓库 URL 或回退版本属于消费者部署决策，必须另行验证，不能用本地文件仓库替代正式验收。

## 验证证据

本次只读检查、制品哈希、Git heads、公开 Maven/GitHub 探测和配置存在性保存在：

`C:\Users\nmg_w\AppData\Local\Codex\private-reports\newnan-sdk-maven-release-20261023\release-readiness-evidence.json`

该证据文件不含凭证。现有功能验证仍以 `docs/audits/2026-10-23-guardian-integration.md` 和同目录原始日志为准，本轮未重跑测试。
