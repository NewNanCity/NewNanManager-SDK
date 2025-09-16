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
    // HTTP客户端 - 使用OkHttp，同步调用
    implementation("com.squareup.okhttp3:okhttp:5.1.0")

    // JSON序列化 - 使用Jackson，性能优异
    implementation("com.fasterxml.jackson.core:jackson-core:2.18.3")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.18.3")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.18.3")

    // 测试依赖
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")
    testImplementation("com.squareup.okhttp3:mockwebserver:5.1.0")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)

    compilerOptions {
        freeCompilerArgs.add("-Xannotation-default-target=param-property")
    }
}

application {
    mainClass.set("com.nanmanager.bukkit.examples.BasicExampleKt")
}

// 创建源码jar
tasks.register<Jar>("sourcesJar") {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

// 创建文档jar
tasks.register<Jar>("javadocJar") {
    archiveClassifier.set("javadoc")
    from(tasks.javadoc)
}

artifacts {
    archives(tasks["sourcesJar"])
    archives(tasks["javadocJar"])
}
