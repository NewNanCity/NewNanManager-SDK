---
type: test
scope: workspace
audience: developer
summary: 完成 Java 生成客户端的 Maven 构建验收
breaking: false
demo_ready: false
tests: >-
  mvn -B -ntp -DskipTests package -f generated/java/pom.xml;
  python codegen/generate.py --all --check;
  python -m unittest discover -s codegen -p 'test*.py' -v
artifacts:
  - generated/java/pom.xml
  - README.md
  - TODO.md
  - codegen/README.md
---

## What changed

Java 低级生成客户端已通过 Maven `package`，70 个源文件、POM 和双鉴权入口均完成验证；构建缓存和产物已在验收后清理。

## Why it matters

Java 生成层具备可复查的编译证据，可以继续按统一 OpenAPI 生成链维护；随后新增的 Java facade 复用该生成层，边界和发布依赖见同日 facade 条目。

## Demo posture / limitations

当时尚未完成的 Java facade 和 Kotlin 构建已在同日后续条目中完成本地验收；逐语言行为对照、生成 Kotlin 客户端接入和 SDK 发布仍未完成。构建使用临时 Maven 镜像解决本机 Maven Central TLS 握手失败，镜像配置没有写入仓库。
