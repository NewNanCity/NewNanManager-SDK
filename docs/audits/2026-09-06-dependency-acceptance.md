# SDK 依赖验收

2026-09-06本地修复与核查，已追加用户授权的Go/Python最低版本升级。没有提交、发布、部署或执行真实业务集成；依赖下载只使用公开registry，既有源码WIP保留。Python wheel通过隔离构建后端生成，运行环境依赖仅按hash安装wheel。根项目的二进制扫描单独记录在 `working-delta/2026-09-06-followup/acceptance.md`，不与SDK结论混用。

## TypeScript

修复前npm锁文件命中8个包：生产axios1.11.0、follow-redirects1.15.11、form-data4.0.4、lodash4.17.21；开发brace-expansion2.0.2、diff4.0.2、glob10.4.5、minimatch9.0.5。包的漏洞级别合计6 high、1 moderate、1 low，不代表每个漏洞条件都可经SDK触发。

修复后Axios1.20.0、Lodash4.18.1、follow-redirects1.16.0、form-data4.0.6；开发依赖分别2.1.4、4.0.4、10.5.0、9.0.9。直接依赖最低Axios1.20.0和Lodash4.18.1，保持现有主版本。Lodash原本已由axios-api的peer依赖引入，显式声明用于限制该运行依赖的最低安全版本。

npm与pnpm锁归一化后86组包名/版本完全相同，官方registry审查均0命中。Axios维护者[公告](https://github.com/axios/axios/security/advisories/GHSA-7q8q-rj6j-mhjq)给出相关1.x修复线；本次选定1.20.0也将form-data最低要求提高到4.0.6。没有使用 `audit fix --force`，没有跨主版本升级SDK直接依赖。glob10.5.0仍被维护者标记deprecated，当前audit无匹配，不将此解释为长期维护承诺。

验证：`npm test` 11项通过；`npm run build` 通过，生成声明保留Token search的弃用注释；`npm audit --package-lock-only --registry=https://registry.npmjs.org --ignore-scripts --json` 和公开registry下 `pnpm audit --json` 均0。

## Python

修复前本机CPython3.12.12运行闭包14个包中，aiohttp3.13.5命中14个去重GHSA；其中[客户端C响应解析器越界读取](https://github.com/aio-libs/aiohttp/security/advisories/GHSA-cq5v-8q36-5273)的修复版本为3.14.3。没有运行内存错误/DoS复现。

公开最低版本现为Python>=3.10，aiohttp>=3.14.3；Black、Mypy和Ruff检查目标同步到3.10。[aiohttp3.14.3官方元数据](https://pypi.org/pypi/aiohttp/3.14.3/json)要求Python>=3.10。旧Python3.9不再受支持，下游需升级解释器并重新解析依赖锁；公共包尚未发布。

`python/requirements-test-py310.txt`和`python/requirements-test-py312.txt`分别提供Windows/CPython3.10、3.12的精确版本和分发文件哈希。3.12保持上一阶段14个运行依赖版本；3.10以该锁为约束，额外包含aiohttp在3.11以前所需的async-timeout5.0.1，共15包。

锁生成命令，在`python/`目录执行：

```text
uv pip compile pyproject.toml --output-file requirements-test-py312.txt --python-version 3.12 --python-platform windows --generate-hashes --no-build --no-config --default-index https://pypi.org/simple --keyring-provider disabled
uv pip compile pyproject.toml --constraint requirements-test-py312.txt --output-file requirements-test-py310.txt --python-version 3.10 --python-platform windows --generate-hashes --no-build --no-config --default-index https://pypi.org/simple --keyring-provider disabled
```

独立CPython3.10.19和3.12.12环境按hash、仅wheel安装后各11项unittest及`uv pip check`通过。逐版本[PyPI JSON漏洞字段](https://docs.pypi.org/api/json/#get-release)核查分别15、14个实际安装包，错误和公告匹配均为0；扫描直接在各解释器的venv内运行，因此包含3.10的条件依赖。此检查不覆盖build-system、dev/docs/examples extras、其他平台或下游项目，也不是可达性扫描。

`uv build --wheel`通过，结构化读取wheel METADATA确认Requires-Python为>=3.10、Requires-Dist包含aiohttp>=3.14.3，且无Python3.9 classifier；使用3.10实际安装wheel后，在源码目录之外以`-I`隔离导入公开客户端成功。uv为本机既有工具；新3.10解释器和venv只安装于本轮产物目录，没有更换全局默认解释器。安装和测试入口见[Python README](../../python/README.md)。

全包`ruff check --select F,E9 newnanmanager tests`仍有56条既有诊断（F403 10条、F405 46条），均位于未修改的两个`__init__.py`；顶层lint配置有弃用提示。这些与此前风格欠账保留于TODO，本次没有以配置忽略规则掩盖它们。包元数据和开发工具最低版本配置已同步，但不声明全包lint或mypy通过。

## Go

用户授权后，SDK最低版本从Go1.21提高到1.25.0，x/net从上一阶段0.35.0升级至0.56.0，Resty保持2.11.0。[x/net0.56.0官方go.mod](https://github.com/golang/net/blob/v0.56.0/go.mod)要求Go1.25.0。该变更不再支持Go1.21至1.24，SDK方法签名和后端契约没有因本轮版本升级而变化。

实际下载并使用官方Go1.25.14工具链，`go test -race ./... -count=1 -timeout=120s`三包、`go vet ./...`、`go mod verify`与`go mod tidy -diff`全部通过。仅对本次命令选择工具链，没有替换全局Go，也没有改动根后端现有的Go1.26.6基线。

govulncheck1.1.4在Go1.25.14的GOROOT/PATH下执行`govulncheck -scan=module`，结果为`No vulnerabilities found.`，上一阶段12条x/net模块公告归零。此结果对应本SDK和所用补丁工具链，不包含根后端、任意下游应用或未来依赖解析。根后端的OpenPGP公告与符号提取限制仍按原审查记录保留。

可复跑命令、解释器路径、扫描脚本和验收结果见根项目`working-delta/2026-09-06-runtime-baseline/acceptance.md`；本轮变更归档于[Go/Python基线升级](../releases/unreleased/2026-09-06-runtime-sdk-go-python-baselines.md)。

## C#

公开NuGet源扫描生产net8.0项目的3个直接包和12个传递包未报告漏洞。测试项目则命中System.Net.Http4.3.0与System.Text.RegularExpressions4.3.0两条High公告，均来自xunit2.6.1到NETStandard.Library1.6.1的旧测试依赖链。

实际 `Tests/bin/Debug/net8.0/NewNanManager.Client.Tests.deps.json` 中，两包没有runtime或runtimeTargets资产；递归检查输出目录也没有对应旧DLL。此net8.0测试产物未选择旧包运行资产。没有进行进程程序集注入探测，也没有将整包图级命中当成生产可利用证据；旧测试依赖图仍可在后续维护中清理。

检查命令为 `dotnet list <项目> package --vulnerable --include-transitive --configfile <仅nuget.org配置> --format json`，分别检查客户端与测试项目。新增契约后的离线测试15项通过。没有packages.lock.json，因此结果依赖本次project.assets.json解析，不涵盖所有未来解析。

## Kotlin

使用既有Gradle8.14 wrapper盘点实际runtimeClasspath的12个外部Maven组件、9个JAR。初始Jackson2.18.3匹配9条OSV公告（core2条、databind7条）；三个直接Jackson依赖兼容升到2.18.10，annotations/BOM同步升级，其余7个非Jackson组件不变。Kotlin2.2.0、Java21、OkHttp5.1.0和测试依赖声明不变。

修复后12个精确坐标的OSV批查全部无匹配且无续页token。14项回归通过（既有12项、新增Jackson兼容测试2项）。新增数字解析约束测试使用32字符上限与4段16字节输入，旧版RED、新版GREEN；这是有界依赖级复现，SDK内置路径是同步readValue，不据此声称SDK可被相同方法利用。实际SDK回归保留4294967296总数、分页、false和缺失可选值。

依据为维护者[2.18修复线](https://github.com/FasterXML/jackson/wiki/Jackson-Release-2.18)、[2.18.10发行说明](https://github.com/FasterXML/jackson/wiki/Jackson-Release-2.18.10)及[OSV批查询](https://google.github.io/osv.dev/post-v1-querybatch/)。本地详细记录为根项目`working-delta/2026-09-06-followup/kotlin-dependency-review.md`，包含坐标、公告和前后原始结果。

验证使用 `.\gradlew.bat --console=plain test`，下载修复依赖后可用 `--offline test`。没有安装scanner；范围不含构建插件、测试依赖图、自定义下游模型或JVM符号可达性，也没有引入响应体字节上限。没有Gradle依赖锁，当前结果对应本次解析出的精确坐标。
