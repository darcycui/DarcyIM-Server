# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概述

Spring Boot 3.5.12 + Kotlin 1.9.25 + Java 21 即时通讯服务端，Gradle 8.13 构建。
入口类：`DemoWebsocketApplication.kt`

详细背景见 [AGENTS.md](./AGENTS.md)（本文件为补充，不重复已写内容）。

## 构建与运行命令

```bash
# 运行全部测试
./gradlew test

# 运行单个测试类
./gradlew test --tests "*X3DHTests*"

# 启动应用（dev profile，本地 MySQL）
./gradlew bootRun --args='--spring.profiles.active=dev'

# 跳过测试构建
./gradlew build -x test
```

JDK 21 必须可用。

## 代码架构

### 分层结构

```
api/                              — REST 接口定义（Interface）
http/
├── controller/                   — REST 控制器实现
├── service/                      — 业务逻辑层
├── repository/                   — Spring Data JPA 数据访问层（Repository）
└── x3dh/                         — X3DH / Signal 协议实现（chain, exchange, sign, user）
```

### WebSocket

- `websocket/` — **已废弃**（全部已注释），基于 javax.websocket（@ServerEndpoint）
- `websocket_stomp/` — **活跃实现**，基于 Spring STOMP over WebSocket

STOMP 通道拦截器（interceptor/）：
- `in/InUserInterceptor` — 用户身份注入
- `in/InReceiptInterceptor` — STOMP receipt 确认帧
- `in/InDecryptInterceptor` — 入站消息解密
- `out/OutEncryptInterceptor` — 出站消息加密

### 通信与安全

- **认证**：JWT（jjwt 0.13.0），`Authorization: Bearer <token>` 头部传递
- **HTTP 加密**：`@Encrypted` 注解触发 `DecryptRequestBodyAdvice` / `EncryptResponseBodyAdvice`（AES-256-GCM/ChaCha20，AAD 包含 method + URL）
- **WebSocket 加密**：STOMP channel interceptor 实现传输加密
- **密钥派生**：DH 交换 → `TransportKeyManager` 内存缓存
- **端到端加密**：X3DH（Signal 协议预密钥），密钥存储在 MySQL

### 响应与异常体系

- 统一响应包装：`UnifiedResponseAdvice`（`@Priority(1)`）将控制器返回值包装为 `ResultEntity`
- 统一异常处理：`GlobalExceptionAdvice`（`@Priority(2)`）捕获 `BaseException` → `ResultEntity`
- 响应加密：`EncryptResponseBodyAdvice`（`@Priority(3)`）对 `@Encrypted` 的响应加密

异常按错误码分包（`exception/codeXXX/`）：
- 100 = 用户 / 200 = 文件 / 300 = DB / 600 = 参数
- 700 = 会话 / 800 = STOMP / 900 = 群组 / 1000 = X3DH / 1100 = 已读状态

### 配置

三个 profile（`src/main/resources/`）：
- **default** — SSL 7443 端口，启用 HTTPS
- **dev** — 本地 MySQL，开启 Flyway 迁移 + DEBUG 日志
- **server** — Docker 环境，JPA ddl-auto 建表，关闭 Flyway

### 测试

- JUnit 5 + Spring Boot Test + Mockito 5.23.0（Java Agent 注入）
- 部分测试需要运行中的 MySQL（继承 `WebSocketBase` 的测试类）

### 日志

自定义扩展：`src/main/kotlin/.../log/` 包含 `Logger.kt`（`logD`/`logI`/`logW`/`logE` 顶层函数），以及彩色输出转换器。
