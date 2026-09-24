---
type: fix
scope: runtime
audience: public
summary: 将Go和Python SDK最低版本升至1.25.0及3.10，并采用修复版运行依赖
breaking: true
demo_ready: false
tests:
  - go test -race ./... -count=1 -timeout=120s
  - go vet ./...
  - go mod verify
  - go mod tidy -diff
  - govulncheck -scan=module
  - python -B -m unittest discover -s tests -v
  - uv pip check --python <isolated-python>
  - uv build --wheel --python <isolated-python>
  - python verify_python_runtime.py --wheel <wheel> --output <audit-json>
  - git -c core.safecrlf=false diff --check
artifacts:
  - golang/go.mod
  - golang/go.sum
  - golang/README.md
  - python/pyproject.toml
  - python/requirements-test-py310.txt
  - python/requirements-test-py312.txt
  - python/README.md
  - README.md
  - CLAUDE.md
  - docs/audits/2026-09-06-dependency-acceptance.md
---

## What changed

Go SDK最低版本从1.21升至1.25.0，x/net从上一阶段0.35.0升至0.56.0，Resty仍为2.11.0。Python SDK最低版本从3.9升至3.10，aiohttp最低版本从3.9.0升至3.14.3；Black、Mypy和Ruff检查目标同步到3.10。Python新增3.10哈希锁并更新3.12锁的项目约束，两个环境共享14个包版本，3.10额外锁定async-timeout5.0.1。

Go1.21至1.24、Python3.9不再受支持，故标记breaking。下游需先升级工具链或解释器，再升级SDK并重新解析自己的依赖锁。根后端已使用Go1.26.6，本轮没有修改其基线，也没有修改SDK方法或后端API。

## Why it matters

原最低版本限制了修复依赖的采用。现在公开依赖约束和可复现测试环境一致，Go SDK上一阶段12条模块公告降到0，Python两个实际安装环境的逐版本PyPI公告均无匹配。

Go1.25.14的三包race、vet、模块校验和tidy检查通过；Python3.10.19、3.12.12各11项测试及依赖一致性检查通过。wheel构建、Requires-Python/Requires-Dist检查通过，并在3.10环境实际安装后隔离导入成功。源码审查与扫描范围见[依赖验收](../../audits/2026-09-06-dependency-acceptance.md)。

## Demo posture / limitations

尚未提交、发布或部署，没有更换全局Go或Python。Python锁及实际测试针对Windows的两个明确解释器版本；15包/14包的公告核查不包含build-system或dev/docs/examples extras，也不代表任意下游解析环境的安全认证。Go扫描只针对SDK与Go1.25.14，根后端的OpenPGP公告边界未由此消除。

全包Ruff定向检查仍有56条既有星号导入诊断及顶层配置弃用提示，对应两个未修改的`__init__.py`，没有改规则隐藏结果。其他历史lint、C#测试依赖图及生产验证事项保留在TODO。

## 计划归档

- [x] 核对x/net0.56.0与aiohttp3.14.3的官方最低版本要求。
- [x] 升级Go基线和依赖，在1.25系列实际完成race/vet/模块校验及漏洞扫描。
- [x] 升级Python基线及工具配置，在3.10/3.12隔离环境安装哈希锁并完成回归。
- [x] 构建与安装wheel，核对元数据并扫描两个环境的全部运行依赖。
- [x] 同步模块README、项目索引、根审查状态与TODO，保留既有维护欠账。
