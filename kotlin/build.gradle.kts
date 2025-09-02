plugins {
    kotlin("jvm") version "2.2.0"
    kotlin("plugin.serialization") version "2.2.0"
    application
}

group = "com.nanmanager"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    // Ktor客户端核心
    implementation("io.ktor:ktor-client-core:2.3.0")
    implementation("io.ktor:ktor-client-cio:2.3.0")

    // JSON序列化支持
    implementation("io.ktor:ktor-client-content-negotiation:2.3.0")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")

    // 日志支持
    implementation("io.ktor:ktor-client-logging:2.3.0")
    implementation("ch.qos.logback:logback-classic:1.4.7")

    // 协程支持
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")

    // 测试依赖
    testImplementation(kotlin("test"))
    testImplementation("io.ktor:ktor-client-mock:2.3.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.1")
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}

application {
    mainClass.set("com.nanmanager.client.MainKt")
}

// 添加运行测试的任务
tasks.register<JavaExec>("runIntegrationTest") {
    group = "verification"
    description = "Run integration tests"
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("com.nanmanager.client.MainKt")
}

// 添加运行更新后SDK测试的任务
tasks.register<JavaExec>("runTestUpdatedSDK") {
    group = "verification"
    description = "Run updated SDK tests"
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("TestUpdatedSDKKt")
}

// 添加运行模块化SDK测试的任务
tasks.register<JavaExec>("runTestModularSDK") {
    group = "verification"
    description = "Run modular SDK tests"
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("TestModularSDKKt")
}

// 添加运行综合测试的任务
tasks.register<JavaExec>("runComprehensiveTest") {
    group = "verification"
    description = "Run comprehensive SDK tests"
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("ComprehensiveTestKt")
}