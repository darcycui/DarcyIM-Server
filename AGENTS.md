# DarcyIM — 代理指南

## 项目概览

Spring Boot 3.5.12 + Kotlin 1.9.25 + Java 21 即时通讯服务端，Gradle 8.13 构建。

## 快速启动

```bash
./gradlew bootRun --args='--spring.profiles.active=dev'
```

入口类：`DemoWebsocketApplication.kt`

运行前需启动 MySQL，`dev` profile 使用 `localhost:3306`，`server` profile 使用 Docker 服务名 `mysql:3306`。

## 关键命令

| 命令 | 说明 |
|------|------|
| `./gradlew test` | 运行全部测试 |
| `./gradlew test --tests "*X3DHTests*"` | 运行单个测试类 |
| `./gradlew build -x test` | 跳过测试构建 |

JDK 21 必须可用。Gradle wrapper 从腾讯云镜像下载。

## 架构要点

- `websocket/` 包已全部注释，**已废弃**。活跃实现是 `websocket_stomp/`（STOMP over WebSocket）。
- 三个 Spring profile：`dev`（本地 + Flyway）、`server`（Docker + JPA ddl-auto）、default（SSL 7443 端口）。
- REST 接口定义在 `api/` 包中，实现在 `http/controller/`。
- 异常按错误码范围分包：100=用户, 200=文件, 300=DB, 600=参数, 700=会话, 800=STOMP, 900=群组, 1000=X3DH, 1100=已读状态。
- `application-dev.yaml` 启用 Flyway（`db/migration/`）；`application-server.yaml` 禁用 Flyway 依赖 JPA ddl-auto。

## 通信与加密

- WebSocket：通过 STOMP channel interceptor 实现 AES-256-GCM/ChaCha20 传输加密，密钥由 DH 交换派生。
- HTTP：`@Encrypted` 注解触发 `DecryptRequestBodyAdvice` / `EncryptResponseBodyAdvice`。
- X3DH（Signal 协议）用于安全会话建立，密钥存储在 MySQL。
- JWT 认证（jjwt 0.13.0），`Authorization: Bearer <token>` 头部传递。

## 测试注意

- Mockito 5.23.0 通过 Java Agent 注入（`build.gradle.kts` 中 `jvmArgs` 配置），`./gradlew test` 自动处理。
- 使用 JUnit 5 + Spring Boot Test。
- 数据库测试需要运行中的 MySQL（`dev` profile）。

## 文件上传

路径配置在 `application.yaml`（`D:/uploads/image`），通过 `UploadFileController` 处理。

## 配置文件位置

`src/main/resources/` 下有 `application.yaml`、`application-dev.yaml`、`application-server.yaml`。`src/main/resources/db/` 下有 Flyway 迁移和 `db_init.sql`（已全部注释，无种子数据）。
