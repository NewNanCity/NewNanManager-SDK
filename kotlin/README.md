# NewNanManager Kotlin SDK - 集成测试项目

这是一个完整的Kotlin项目，用于测试NewNanManager API的Kotlin客户端SDK。

## 🚀 在IntelliJ IDEA中运行

### 1. 导入项目
1. 打开IntelliJ IDEA
2. 选择 "Open" 或 "Import Project"
3. 选择 `clients/kotlin-new` 目录
4. 等待Gradle同步完成

### 2. 运行集成测试
**最简单的方式**：
1. 打开 `src/main/kotlin/Main.kt` 文件
2. 点击 `main` 函数旁边的绿色运行按钮 ▶️
3. 查看控制台输出的测试结果

**其他运行方式**：
- 右键点击 `Main.kt` -> "Run 'MainKt'"
- 使用快捷键 `Ctrl+Shift+F10` (Windows/Linux) 或 `Cmd+Shift+R` (Mac)
- 在Terminal中运行: `./gradlew run`

## 📊 测试覆盖

### 完整的API功能测试
- ✅ **玩家管理** (8个API): 创建、查询、更新、封禁、解封、登录验证、删除
- ✅ **服务器管理** (6个API): 注册、查询、更新、删除、详细信息
- ✅ **城镇管理** (6个API): 创建、查询、更新、成员管理、删除
- ✅ **Token管理** (1个API): 列表查询
- ✅ **监控功能** (1个API): 错误处理测试

### 预期测试结果
基于其他客户端的测试结果，Kotlin客户端预期应该达到：
- **成功率**: 100% (22/22个API)
- **平均响应时间**: 300-400ms
- **错误处理**: 正确返回404状态码

## 🔧 测试配置

- **API地址**: 通过环境变量 `NANMANAGER_BASE_URL` 配置，默认为 `https://your-api-server.com`
- **Token**: 通过环境变量 `NANMANAGER_TOKEN` 配置，默认为 `your-api-token-here`

## 📁 项目结构

```
src/
├── main/kotlin/
│   ├── Main.kt                                    # 🎯 主测试文件
│   └── com/nanmanager/client/
│       ├── NewNanManagerClient.kt                 # 主客户端类
│       ├── models/Models.kt                       # 数据模型
│       └── exceptions/Exceptions.kt               # 异常定义
└── test/kotlin/                                   # 单元测试目录
```

## 🛠️ 技术栈

- **Kotlin**: 2.2.0
- **Ktor Client**: 2.3.0 (HTTP客户端)
- **Kotlinx Serialization**: 1.5.0 (JSON序列化)
- **Kotlinx Coroutines**: 1.7.1 (异步支持)
- **Gradle**: 8.0 (构建工具)

## 📈 与其他客户端对比

| 客户端     | 成功率       | 平均响应时间      | 特点                  |
| ---------- | ------------ | ----------------- | --------------------- |
| Golang     | 100%         | 305ms             | 类型安全，性能优异    |
| JavaScript | 100%         | 319ms             | 简单易用，跨平台      |
| **Kotlin** | **预期100%** | **预期300-400ms** | **协程支持，JVM生态** |

## 🔍 故障排除

### 编译错误
- 确保使用 JDK 11 或更高版本
- 检查网络连接，确保能下载依赖
- 等待Gradle同步完成

### 运行时错误
- 检查API地址和Token是否正确
- 确保网络能访问您的API服务器
- 查看控制台的详细错误信息

### 依赖问题
如果遇到依赖下载问题，可以尝试：
```bash
./gradlew clean build --refresh-dependencies
```

## 🎯 使用示例

```kotlin
import com.nanmanager.client.NewNanManagerClient
import com.nanmanager.client.models.*
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    val client = NewNanManagerClient(
        token = "your-api-token",
        baseUrl = "https://your-server.com"
    )

    // 获取玩家列表
    val players = client.listPlayers()
    println("玩家数量: ${players.total}")

    // 创建玩家
    val newPlayer = client.createPlayer(CreatePlayerRequest(
        name = "TestPlayer",
        inQqGroup = true
    ))
    println("创建玩家: ${newPlayer.name}")

    client.close()
}
```

## 📝 API文档

详细的API使用方法请参考 `NewNanManagerClient.kt` 文件中的注释和方法签名。

## 🎉 开始测试

现在您可以：
1. 在IntelliJ IDEA中打开这个项目
2. 运行 `Main.kt` 中的 `main` 函数
3. 查看完整的API测试结果
4. 验证Kotlin客户端的功能完整性

预期所有测试都会通过，展示NewNanManager API的稳定性和Kotlin客户端的可靠性！
