# SDK 发布流程

本文档描述了NewNanManager SDK的发布流程和最佳实践。

## 发布准备

### 1. 版本管理

所有SDK遵循[语义化版本](https://semver.org/)规范：

- **主版本号**: 不兼容的API修改
- **次版本号**: 向下兼容的功能性新增
- **修订号**: 向下兼容的问题修正

### 2. 发布前检查清单

#### 代码质量
- [ ] 所有测试通过（单元测试、集成测试）
- [ ] 代码覆盖率达到要求（>80%）
- [ ] 静态代码分析通过
- [ ] 安全扫描通过
- [ ] 性能基准测试通过

#### 文档
- [ ] README文档更新
- [ ] API文档更新
- [ ] 变更日志更新
- [ ] 示例代码验证
- [ ] 迁移指南（如有破坏性变更）

#### 兼容性
- [ ] 向后兼容性检查
- [ ] 依赖版本兼容性
- [ ] 多平台测试通过

## 各语言发布流程

### Python SDK

#### 1. 准备发布
```bash
cd clients/python

# 更新版本号
vim newnanmanager/_version.py

# 更新依赖
pip install -e ".[dev]"

# 运行完整测试
make test-cov

# 构建包
make build
```

#### 2. 发布到PyPI
```bash
# 测试PyPI
make upload-test

# 生产PyPI
make upload
```

#### 3. 验证发布
```bash
pip install newnanmanager-client==1.0.0
python -c "import newnanmanager; print(newnanmanager.__version__)"
```

### C# SDK

#### 1. 准备发布
```bash
cd clients/csharp

# 更新版本号
vim NewNanManager.Client.csproj

# 运行测试
dotnet test ../csharp-tests/

# 构建包
dotnet pack -c Release
```

#### 2. 发布到NuGet
```bash
# 发布
dotnet nuget push bin/Release/NewNanManager.Client.1.0.0.nupkg \
  --api-key YOUR_API_KEY \
  --source https://api.nuget.org/v3/index.json
```

#### 3. 验证发布
```bash
dotnet add package NewNanManager.Client --version 1.0.0
```

### Go SDK

#### 1. 准备发布
```bash
cd clients/golang

# 更新版本号
vim version.go

# 运行测试
go test -v ./...

# 更新依赖
go mod tidy
```

#### 2. 创建Git标签
```bash
git tag v1.0.0
git push origin v1.0.0
```

#### 3. 验证发布
```bash
go get github.com/NewNanCity/NewNanManager-SDK/clients/golang@v1.0.0
```

### TypeScript SDK

#### 1. 准备发布
```bash
cd clients/typescript

# 更新版本号
npm version 1.0.0

# 运行测试
npm test

# 构建
npm run build
```

#### 2. 发布到npm
```bash
# 发布
npm publish --access public
```

#### 3. 验证发布
```bash
npm install @newnanmanager/client@1.0.0
```

### Kotlin SDK

#### 1. 准备发布
```bash
cd clients/kotlin

# 更新版本号
vim build.gradle.kts

# 运行测试
./gradlew test

# 构建
./gradlew build
```

#### 2. 发布到Maven Central
```bash
./gradlew publishToMavenCentral
```

## 自动化发布

### GitHub Actions工作流

创建 `.github/workflows/release.yml`:

```yaml
name: Release SDKs

on:
  push:
    tags:
      - 'v*'

jobs:
  release-python:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-python@v4
        with:
          python-version: '3.9'
      - name: Install dependencies
        run: |
          cd clients/python
          pip install -e ".[dev]"
      - name: Run tests
        run: |
          cd clients/python
          make test-cov
      - name: Build and publish
        env:
          TWINE_USERNAME: __token__
          TWINE_PASSWORD: ${{ secrets.PYPI_API_TOKEN }}
        run: |
          cd clients/python
          make build
          make upload

  release-csharp:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-dotnet@v3
        with:
          dotnet-version: '8.0'
      - name: Run tests
        run: dotnet test clients/csharp-tests/
      - name: Build and publish
        env:
          NUGET_API_KEY: ${{ secrets.NUGET_API_KEY }}
        run: |
          cd clients/csharp
          dotnet pack -c Release
          dotnet nuget push bin/Release/*.nupkg \
            --api-key $NUGET_API_KEY \
            --source https://api.nuget.org/v3/index.json

  release-go:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-go@v4
        with:
          go-version: '1.21'
      - name: Run tests
        run: |
          cd clients/golang
          go test -v ./...
      - name: Create release
        uses: actions/create-release@v1
        env:
          GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
        with:
          tag_name: ${{ github.ref }}
          release_name: Release ${{ github.ref }}
```

## 发布后验证

### 1. 功能验证
- [ ] 安装测试
- [ ] 基本功能测试
- [ ] 示例代码运行
- [ ] 文档链接检查

### 2. 监控
- [ ] 下载量监控
- [ ] 错误报告监控
- [ ] 用户反馈收集

### 3. 通知
- [ ] 发布公告
- [ ] 文档更新通知
- [ ] 社区通知

## 回滚流程

如果发现严重问题需要回滚：

### 1. 立即响应
- 评估问题严重性
- 决定是否需要立即回滚
- 通知相关团队

### 2. 执行回滚
```bash
# Python
pip install newnanmanager-client==0.9.0

# C#
dotnet add package NewNanManager.Client --version 0.9.0

# Go
go get github.com/Gk0Wk/NewNanManager/clients/golang@v0.9.0

# TypeScript
npm install @newnanmanager/client@0.9.0
```

### 3. 问题修复
- 修复问题
- 增加测试覆盖
- 重新发布

## 最佳实践

1. **渐进式发布**: 先发布到测试环境，再发布到生产环境
2. **版本兼容**: 保持向后兼容性，避免破坏性变更
3. **文档同步**: 确保文档与代码版本同步
4. **测试覆盖**: 保持高测试覆盖率
5. **安全扫描**: 定期进行安全漏洞扫描
6. **依赖管理**: 及时更新依赖，修复安全漏洞
7. **用户沟通**: 及时响应用户反馈和问题

## 联系方式

如有发布相关问题，请联系：
- 技术负责人: tech-lead@newnanmanager.com
- 发布团队: release-team@newnanmanager.com
