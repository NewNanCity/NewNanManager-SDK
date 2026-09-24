---
type: feature
scope: workspace
audience: developer
summary: 新增 Java 业务 facade 与生成客户端的 Maven reactor
breaking: false
demo_ready: false
tests: >-
  mvn -B -ntp -f pom.xml -pl java -am test (本机通过临时未入库 Maven mirror);
  python codegen/generate.py --all --check;
  python -m unittest discover -s codegen -p 'test*.py' -v
artifacts:
  - pom.xml
  - java/pom.xml
  - java/src/main/java/com/newnancity/newnanmanager/
  - java/src/test/java/com/newnancity/newnanmanager/NewNanManagerClientTest.java
  - java/README.md
---

## What changed

Java facade 已接入生成客户端，覆盖核心服务入口、Bearer/API Token 选择、标准错误映射、请求超时、重定向策略以及 Guardian session/epoch 参数。SDK 根目录新增 Maven reactor，生成层和 facade 可以一起编译测试。

## Why it matters

Java 调用方可以使用与其他语言一致的服务分层和异常元数据，同时保留生成模型作为唯一数据类型来源，避免手写模型与 OpenAPI 分叉。

## Demo posture / limitations

本轮只验证本地 loopback 和生成漂移，不包含真实服务或生产权限验收，也没有发布 Maven artifact。facade 不自动重试，`Retry-After` 由调用方处理；发布时先提供 `newnanmanager-generated-java`，再提供 `newnanmanager-java`。临时 Maven 镜像配置未入库。
