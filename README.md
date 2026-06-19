# 物流管理系统

> ⚠️ **本科毕设项目，功能不完善，仅供参考。**

一个基于 Spring Boot + Vue 3 的全栈物流业务演示项目，覆盖「注册登录 -> 下单 -> 仓库处理 -> 运输派送 -> 轨迹查询 -> 管理统计」完整链路。

适合作为毕业设计、课程项目或 Java + Vue 的业务实践样例。

## 功能概览

- 用户注册/登录（支持账号状态控制，禁用账号不可登录）
- 订单创建（含阶梯计费预览与后端计费落库）
- 订单状态流转与权限控制（ADMIN/WAREHOUSE/COURIER）
- 公共物流轨迹查询（支持游客免登录查件）
- 管理员账号中心（创建内部账号、角色/状态维护、密码重置）
- 管理员操作日志与 CSV 导出
- 运营看板（总览、状态分布、近 7 天趋势、目的地热度、异常订单）

## 项目结构

```text
logistics-management-system/
├─ logistics-server/   # Spring Boot + MyBatis 后端
├─ logistics-web/      # Vue 3 + Vite + Element Plus 前端
└─ README.md
```

## 技术栈

### 后端

- Java 21
- Spring Boot 3.3.9
- MyBatis Spring Boot Starter 3.0.3
- MySQL 8.x
- Lombok
- spring-security-crypto（用于 BCrypt 密码哈希）

### 前端

- Vue 3
- Vue Router 4
- Vite 7
- Element Plus
- Axios

## 业务规则

### 角色模型

- ADMIN：系统管理、账号管理、看板、全量订单管理
- WAREHOUSE：仓库入库/出库、仓储流转处理
- COURIER：派送与签收处理
- USER：下单与查件

### 订单状态流

- 0 待揽件
- 1 已揽件
- 2 运输中
- 3 派送中
- 4 已签收

后端会在状态变更时自动写入轨迹记录。

### 计费公式

后端和前端保持一致：

$$
fee = 10 + weight \times 2 + \max(distance - 100, 0) \times 0.5
$$

## 环境要求

- JDK 21
- Maven 3.9+
- Node.js 18+（建议 20+）
- MySQL 8.x

## 快速开始

### 1) 克隆项目

```bash
git clone <your-repo-url>
cd logistics-management-system
```

### 2) 初始化数据库

1. 创建数据库：`logistics_db`
2. 执行你已有的基础建表 SQL（项目历史主线脚本）
3. 依次执行以下增强脚本：

```sql
-- 账号角色字段与初始化账号
SOURCE logistics-server/src/main/resources/sql/mainline-c-user-role-migration.sql;

-- 安全增强与管理员操作日志表
SOURCE logistics-server/src/main/resources/sql/security-and-admin-log-migration.sql;

-- 演示数据（可选）
SOURCE logistics-server/src/main/resources/sql/demo-data.sql;
```

### 3) 配置后端数据库连接

编辑 `logistics-server/src/main/resources/application.properties`：

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/logistics_db?serverTimezone=GMT%2B8&useUnicode=true&characterEncoding=utf-8
spring.datasource.username=root
spring.datasource.password=123456
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

### 4) 启动后端

在 `logistics-server` 目录：

```bash
# Windows
mvnw.cmd spring-boot:run

# macOS / Linux
./mvnw spring-boot:run
```

默认端口：`8080`

### 5) 启动前端

在 `logistics-web` 目录：

```bash
npm install
npm run dev
```

默认端口通常为：`5173`

## 默认账号（执行迁移脚本后）

- 管理员：admin / 123456
- 仓库员：shanghai_wh / 123456
- 快递员：courier001 / 123456

说明：系统支持「旧明文密码首次登录后自动升级为 BCrypt 哈希」。

## 前后端联调说明

- 前端当前直接请求 `http://localhost:8080`，未配置 Vite 代理
- 后端控制器使用 `@CrossOrigin(origins = "*")` 允许跨域

## 主要接口概览

### 认证

- `POST /api/auth/login`
- `POST /api/auth/register`

### 订单

- `POST /api/orders/create`
- `GET /api/orders/all`
- `PUT /api/orders/status`
- `DELETE /api/orders`
- `GET /api/orders/search`
- `PUT /api/orders/arrive`
- `PUT /api/orders/depart`

### 看板

- `GET /api/dashboard/overview`

### 管理员账号中心

- `GET /api/users/all`
- `POST /api/users/internal-create`
- `PUT /api/users/role`
- `PUT /api/users/status`
- `PUT /api/users/reset-password`
- `GET /api/users/op-logs`
- `GET /api/users/op-logs/export`
- `GET /api/users/disabled`
- `GET /api/users/role-change-stats`

## 测试与构建

### 后端

```bash
cd logistics-server
./mvnw test
```

### 前端

```bash
cd logistics-web
npm run build
npm run preview
```

