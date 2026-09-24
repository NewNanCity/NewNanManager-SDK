# OpenAPI SDK 生成

`../../contracts/newnanmanager.openapi.json` 是 HTTP 契约唯一事实源。`openapi-thrift` 仍只负责把这份契约投影成后端 Hertz 使用的 Thrift；多语言客户端使用这里固定版本的 OpenAPI Generator。

生成器版本由 `manifest.json` 锁定：npm CLI `2.41.0`，底层 generator `7.25.0`。脚本会先检查实际版本，再验证契约，最后把输出写到带 `.nnm-generated.json` 所有权标记的目录。

在 SDK 根目录执行：

```powershell
python codegen/generate.py --validate-only --all
python codegen/generate.py --language typescript
python codegen/generate.py --language java
python codegen/generate.py --all --force
python codegen/generate.py --all --check
python -m unittest discover -s codegen -p "test*.py" -v
```

默认输出为 `sdk/generated/<language>/`，目前包含 TypeScript、Go、Python、C#、Kotlin 和 Java 六种低级客户端。生成目录内的文件是低级传输层，不手工编辑；重复的 OpenAPI 文件、测试项目、发布脚本和 CI 文件会被排除，构建缓存由 `generated/.gitignore` 忽略。业务 SDK 仍由各语言 facade 负责鉴权选择、错误映射、重试边界、分页辅助和 Guardian session fencing；Java 手写 facade 位于 `java/`，不与生成输出混放。Java 使用 OkHttp/Gson 模板，以保留 OpenAPI 中两种鉴权方案的可配置入口，并只保留 Maven `pom.xml` 作为构建入口，避免生成器附带的旧 Android Gradle 模板。生成器的 Java README 仍保留 Gradle 作为下游依赖示例；该目录没有 Gradle 构建文件，实际构建使用 Maven。生成层和 facade 可从 SDK 根目录用 `mvn -B -ntp -f pom.xml test` 一起验证。

Kotlin 生成项目的构建文件由生成脚本按 `manifest.json` 的固定版本做后处理：Kotlin Gradle plugin 2.2.0、Gradle 8.14、Jackson 2.18.10 和 OkHttp 5.1.0。后处理会移除生成模板中不需要的 Spotless、Android/旧测试依赖，并保留 `mavenCentral()` 与 Gradle wrapper。生成后可以在 `generated/kotlin/` 使用 `./gradlew --offline build`（Windows 使用 `gradlew.bat`）验证低级客户端；`kotlin/` facade 还会把同一生成源码作为 source set 编译并通过 facade 的错误、鉴权和 session 适配层。TypeScript facade 在 `npm run build` 前通过 `typescript/scripts/sync-generated.mjs` 同步同一生成树，最终 dist 自包含 transport；`npm test` 会重复执行这一步。

Kotlin Gradle wrapper 首次运行产生的 `gradle-wrapper.jar` 属于运行时缓存，已从生成漂移摘要中排除；它仍会由 Gradle 按需下载。

这里的 facade 不是“无语言”的共享运行时，也不是 OpenAPI 生成代码的别名。它是每种语言各自手写的业务入口：统一凭证选择（Bearer 或 X-API-Token 二选一）、错误对象映射、重试边界、分页辅助，以及 Guardian 的 session/epoch fencing。调用链是“业务代码 → 语言 facade → generated 低级客户端 → HTTP API”。当前六种语言都有 facade；Java facade 复用生成模型，默认不自动重试，`Retry-After` 交给调用方决定。

空对象请求和 `x-nnm-*` 扩展会按生成器的通用规则落地；它们不能替代服务端的 Casbin、业务不变量或会话刷新逻辑。Kotlin 已完成 generated transport 接入；其余手写 facade 仍按各自的行为对照计划推进，生成脚本不会覆盖 `python/`、`typescript/`、`golang/`、`csharp/`、`kotlin/` 下的手写实现。
