---
type: fix
scope: workspace
audience: developer
summary: 统一 Java facade 的 JSON 解析错误映射
breaking: false
demo_ready: false
tests: >-
  mvn -B -ntp -f pom.xml -pl java -am test (本机通过临时未入库 Maven mirror)
artifacts:
  - java/src/main/java/com/newnancity/newnanmanager/exceptions/ApiExceptionMapper.java
  - java/src/main/java/com/newnancity/newnanmanager/services/AbstractService.java
  - java/src/main/java/com/newnancity/newnanmanager/NewNanManagerClient.java
  - java/src/test/java/com/newnancity/newnanmanager/NewNanManagerClientTest.java
---

## What changed

生成客户端在成功 HTTP 响应中遇到 malformed JSON 时，Java facade 现在统一抛出 facade 的 `JsonParseException`，服务方法和高级 generated 请求保持一致。

## Why it matters

调用方可以只依赖稳定的 facade 异常层处理坏响应，不需要暴露或捕获 Gson 的未声明运行时异常。

## Demo posture / limitations

本条通过本地 loopback malformed JSON 回归；真实服务响应格式、生产错误率和发布包尚未验收。普通运行时异常仍按原语义向上抛出。
