# SDK 当前工作与检查欠账

已完成的审查修复及验证记录归档到 [第一阶段](docs/releases/unreleased/2026-09-06-runtime-sdk-audit-remediation.md)、[D03/D04及契约同步](docs/releases/unreleased/2026-09-06-runtime-sdk-contract-cancellation.md)、[依赖与分页补齐](docs/releases/unreleased/2026-09-06-runtime-sdk-dependencies-pagination.md)、[Go/Python基线升级](docs/releases/unreleased/2026-09-06-runtime-sdk-go-python-baselines.md)。以下仅保留未完成的边界与维护事项。

## 运行基线与依赖维护

- [ ] 清理C#旧测试依赖图的System包公告并评估NuGet锁定；当前net8.0产物无对应旧运行资产。Kotlin 的运行组件已完成依赖核查，构建插件/测试依赖图与依赖锁策略仍待单独评估。
- [ ] TypeScript后续评估已deprecated的glob10开发依赖更新；当前官方audit为0，跨主版本兼容未评估。

## 后续检查

- [ ] 将已生成并通过漂移检查的 `generated/<language>/` 低级客户端逐语言接入现有 facade；TypeScript 主客户端、Java facade 已接入生成层并通过本地回归，Kotlin 普通业务服务已接入生成 transport 并通过 MockWebServer 回归。Go/Python/C# 暂保留既有手写 transport 以维持行为边界；六种 facade 都支持 Bearer/API Token 二选一和显式 session fencing，剩余工作是其余 transport 接入与全字段行为对照。
- [x] 已为 `x-nnm-session-headers` 和必需空对象请求补 facade 适配，并为六种 facade 增加 session ID/epoch 的本地校验；生成器输出本身仍不代表 Casbin、业务不变量或 Guardian 重连策略已验收。
- [ ] 评估是否把 `x-nnm-legacy-validation` 注册为可执行模板 validator；在此之前继续由领域层和 facade 适配承接该扩展。
- [ ] 等后端模板服务切换完成后，移除五语言错误解析中的 legacy `detail` 回退，并补做真实部署的错误关联字段验收；Java facade 已采用标准字段并保留迁移期 `detail` 回退。

- [ ] Python 既有 Ruff 诊断：本轮修改文件共 34 条，主要是旧 Dict/Type 注解、导入格式和异常链；相同 HTTP 文件的 HEAD 基线为 35 条。本轮新增测试、异常元数据和模型导入检查通过，未扩大到全包格式迁移。
- [ ] Python 基线升级时的全包 `ruff check --select F,E9 newnanmanager tests` 返回56条星号导入相关诊断；对应两个 `__init__.py` 未修改，作为既有导出方式的维护项保留。Ruff 顶层 lint 配置也有弃用提示。
- [ ] TypeScript 浏览器适配器的重定向策略需要单独验证；当前明确验收范围为 Node HTTP 适配器。
- [ ] 凭证所有者确认历史 runner 凭证的用途和有效性，再决定轮换与历史清理。本轮不使用凭证，不改 Git 历史。
