---
type: fix
scope: workspace
audience: developer
summary: 为 Java facade 增加校验后的 SessionContext 重载
breaking: false
demo_ready: false
tests: javac --release 8 generated/java/src/main/java java/src/main/java; Java facade tests passed: 9
artifacts:
  - java/src/main/java/com/newnancity/newnanmanager/SessionContext.java
  - java/src/main/java/com/newnancity/newnanmanager/services/MonitorService.java
  - java/src/main/java/com/newnancity/newnanmanager/services/PlayerService.java
  - java/src/main/java/com/newnancity/newnanmanager/services/PlayerServerService.java
---

What changed

Java facade 新增不可变 `SessionContext`、无请求体细节的会话签发入口，以及玩家准入、心跳和批量离线的 typed session 重载。空 ID 或非正 epoch 在调用生成 transport 前拒绝，原始字符串/代次重载继续保留。

Why it matters

六种 facade 现在都能用显式、经过校验的会话对象表达服务端 fencing 约束，减少插件把会话字段传反或传空的机会。

Demo posture / limitations

本条通过 Java 8 目标主源码编译和 9 项本地 facade 测试；未使用生产服务器，不代表 Guardian 双服顶号或部署验收已经完成。
