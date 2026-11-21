# 履迹 · Resume Studio

一套可直接部署的可编辑简历系统。前端使用 **Vue 3 + TypeScript + Vite**，后端使用 **Java 17 + Spring Boot 3 + MyBatis-Plus**，数据持久化到 **MySQL 8**，Redis 承载安全会话、热点简历缓存与登录失败计数，Nginx 负责静态资源、反向代理、限流与安全响应头。

系统分为两个独立界面：

- **公开主页 `/`**：面向雇主的一页式个人网站，无需登录即可访问
- **管理后台 `/admin`**：管理员登录后编辑基本信息、教育经历、校园经历、主修课程、专业技能、项目经历和发布状态

所有简历内容都由 MySQL 中的结构化数据驱动，前端不保存固定简历文案。A4 简历预览与 PDF 导出保留在后台中，作为求职投递的辅助功能。

## 功能

- 面向雇主的独立公开主页，与私密管理后台分离
- 发布开关：保存后手动决定是否对访客公开
- 基本信息、教育经历、校园经历、主修课程、专业技能、项目经历六大模块
- 列表新增、删除、上下排序，全部字段可编辑
- 右侧 A4 实时预览，响应式桌面 / 平板 / 手机布局
- 完成度提示、未保存状态、快捷键 `Ctrl/⌘ + S`
- 浏览器打印，可直接选择“另存为 PDF”
- 头像直接上传，支持服务器本地存储或阿里云 OSS
- 公开主页动态技能展示带、项目卡片与分层式版式
- Spring Security 表单会话登录、CSRF 防护、BCrypt 12 轮密码哈希
- Redis Session、简历查询缓存、登录失败锁定
- 数据库版本号乐观锁，避免多窗口互相覆盖
- Flyway 自动建表，无需手工执行 SQL
- Docker Compose 一键启动 MySQL、Redis、Java API 与 Nginx 前端

## 技术架构

```text
Browser
  │ HTTPS / HTTP
  ▼
Nginx
  ├── Vue SPA 静态资源（gzip + 长效缓存 + CSP）
  ├── 登录接口限流
  └── /api 反向代理
          │
          ▼
Spring Boot API
  ├── Spring Security + CSRF + 会话
  ├── Redis Session / 缓存 / 登录失败计数
  ├── MyBatis-Plus + HikariCP
  └── Flyway
          │
          ▼
       MySQL 8
```

### 为什么是这些中间件

| 组件 | 作用 | 性能 / 安全考虑 |
| --- | --- | --- |
| Redis | 分布式 Session、简历缓存、登录失败计数 | 避免每次鉴权查库；缓存 TTL 10 分钟，写操作主动失效 |
| Nginx | 静态资源、反向代理、gzip、限流 | 静态资源长缓存；登录接口 8 次/分钟；常规 API 30 次/秒/IP |
| HikariCP | MySQL 连接池 | 默认最大 20 连接，3 秒获取超时，避免请求堆积 |
| MySQL 索引 | 用户简历查询和子表排序 | `resumes.user_id` 唯一索引；子表使用 `(resume_id, sort_order)` 联合索引 |
| Flyway | 数据库版本迁移 | 建表可重复部署，避免环境漂移 |

## 数据模型

- `app_users`：管理员账号、BCrypt 密码、启用状态
- `resumes`：简历主表，包含联系方式、简介、发布状态、版本号
- `educations`：教育经历
- `campus_experiences`：校园经历
- `skills`：专业技能与熟练度
- `courses`：主修课程
- `projects`：项目经历

子表通过 `resume_id` 外键关联，删除简历时级联删除。排序由 `sort_order` 字段控制。

## 快速启动（Docker）

环境要求：Docker Desktop + Docker Compose。

1. 复制环境变量模板：

   ```powershell
   Copy-Item .env.example .env
   ```

2. 编辑 `.env`，至少更换以下值：

   - `MYSQL_ROOT_PASSWORD`
   - `DB_PASSWORD`
   - `REDIS_PASSWORD`
   - `APP_ADMIN_PASSWORD`（12–72 位）

3. 启动：

   ```powershell
   docker compose up --build -d
   ```

4. 打开 `http://localhost:8088/` 查看公开主页；首次启动时未发布，会显示私密状态页。
5. 打开 `http://localhost:8088/login`，使用 `.env` 中的管理员账号登录后台。
6. 填写内容、保存，并在“公开主页”模块打开发布开关。

5. 查看日志：

   ```powershell
   docker compose logs -f backend
   ```

停止服务：

```powershell
docker compose down
```

删除数据库卷（会清空全部数据）：

```powershell
docker compose down -v
```

> 本地 Compose 默认 `SESSION_COOKIE_SECURE=false`，因为示例通过 HTTP 访问。正式部署时务必配置 HTTPS，并将该值改为 `true`。

## 本地开发

### 1. 准备 MySQL 和 Redis

创建数据库和账号：

```sql
CREATE DATABASE resume_studio CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'resume_studio'@'%' IDENTIFIED BY '你的数据库密码';
GRANT ALL PRIVILEGES ON resume_studio.* TO 'resume_studio'@'%';
FLUSH PRIVILEGES;
```

Redis 建议启用密码：

```text
requirepass 你的Redis密码
appendonly yes
maxmemory 256mb
maxmemory-policy allkeys-lru
```

### 2. 启动后端

PowerShell 示例：

```powershell
$env:DB_URL='jdbc:mysql://localhost:3306/resume_studio?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true'
$env:DB_USERNAME='resume_studio'
$env:DB_PASSWORD='你的数据库密码'
$env:REDIS_HOST='localhost'
$env:REDIS_PORT='6379'
$env:REDIS_PASSWORD='你的Redis密码'
$env:APP_ADMIN_USERNAME='admin'
$env:APP_ADMIN_PASSWORD='至少12位的高强度密码'
mvn spring-boot:run
```

后端地址：`http://localhost:8080`

### 3. 启动前端

```powershell
cd frontend
pnpm install
pnpm dev
```

前端地址：`http://localhost:5173`。Vite 已将 `/api` 代理到 `http://localhost:8080`。

## 安全说明

- 登录态保存在 Redis Session 中，浏览器只持有 `HttpOnly` Session Cookie。
- 所有修改接口启用 CSRF Token 校验；前端通过 `X-XSRF-TOKEN` 自动提交。
- 密码使用 BCrypt 12 轮哈希，不保存明文。
- 初始管理员密码只从环境变量读取；低于 12 位会拒绝启动。
- 登录失败计数由 Redis 保存，默认 5 次失败锁定 15 分钟；Nginx 再做一层登录限流。
- 所有接口参数均有长度、格式和列表数量上限校验。
- 链接字段仅允许 `http://` 或 `https://`，前端渲染使用 Vue 自动转义，并在跳转前再次校验协议。
- 前端生产构建由 Nginx 返回 CSP、`X-Frame-Options: DENY`、`nosniff`、Referrer Policy 等安全响应头。
- 生产环境必须启用 HTTPS、`SESSION_COOKIE_SECURE=true`，并建议在反向代理层启用 HSTS。
- 不要把 `.env` 提交到 Git；仓库已忽略该文件。

## API 摘要

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| `GET` | `/api/auth/csrf` | 获取 / 初始化 CSRF Cookie |
| `POST` | `/api/auth/login` | 管理员登录 |
| `GET` | `/api/auth/me` | 当前登录用户 |
| `POST` | `/api/auth/logout` | 退出登录 |
| `POST` | `/api/auth/password` | 修改密码 |
| `GET` | `/api/public/resume` | 获取已发布的公开简历，无需登录 |
| `GET` | `/api/resumes/current` | 获取当前用户完整简历 |
| `PUT` | `/api/resumes/current` | 原子保存完整简历 |

`PUT /api/resumes/current` 使用简历 `version` 做乐观锁。若同一账号在另一个窗口已经保存，当前请求会返回 `409`，前端提示刷新后重试，避免静默覆盖。

## 正式域名上线

完整步骤见：[docs/DEPLOYMENT.md](docs/DEPLOYMENT.md)。其中包含域名购买、DNS、ICP 备案、服务器、Docker、Caddy 自动 HTTPS、Cloudflare、阿里云 OSS、备份和更新流程。

## 生产部署检查清单

- 使用独立、随机、长密码替换 `.env.example` 中的全部占位值。
- 配置 HTTPS，设置 `SESSION_COOKIE_SECURE=true`。
- 将 `CORS_ALLOWED_ORIGINS` 改为真实域名；同源 Nginx 部署时可保持当前值。
- 删除或限制 `docker-compose.yml` 中 MySQL / 后端的宿主机端口映射。
- 定期备份 `mysql-data` 卷；Redis 只保存会话和缓存，可按需持久化。
- 在网关或云防火墙层增加访问控制、WAF 与监控告警。
- 首次登录后通过 `POST /api/auth/password` 更换初始密码。

## 验证命令

```powershell
# 后端编译
cd backend
mvn -q -DskipTests compile

# 前端类型检查
cd frontend
pnpm typecheck

# 前端生产构建
pnpm build
```

## 目录结构

```text
.
├── backend/                 Spring Boot API
│   ├── src/main/java/       Java 业务代码
│   ├── src/main/resources/  配置与 Flyway SQL
│   ├── Dockerfile
│   └── pom.xml
├── frontend/                Vue 3 + TypeScript
│   ├── src/components/      编辑器与 A4 预览
│   ├── src/stores/          Pinia 状态
│   ├── src/api/             Axios 请求层
│   ├── nginx.conf
│   └── Dockerfile
├── docker-compose.yml
├── .env.example
└── README.md
```



## 配置管理规范

业务限制和运行参数统一从 `application.yml` / `.env` 读取，不在 Java 业务代码或 Vue 组件中重复写死。

| 配置 | 说明 |
| --- | --- |
| `app.security.*` | 登录失败次数、锁定时长、账号和密码长度 |
| `app.storage.*` | 本地/OSS 存储、上传大小、尺寸、格式和缓存时间 |
| `app.cache.resume-ttl` | 简历 Redis 缓存时间 |
| `app.validation.*` | 简历字段最大长度、列表数量和技能等级范围 |

后端使用强类型 `AppProperties` 通过 `@ConfigurationProperties` 绑定配置；前端上传规则通过 `/api/uploads/config` 动态读取，不在组件中重复维护。

API 路径由 `ApiPaths`（后端）和 `API_ROUTES`（前端）统一管理，缓存名称由 `CacheNames` 统一管理。
