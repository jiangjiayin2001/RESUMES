# 正式域名部署指南

项目采用“公开主页 + 私密管理后台”的结构：

- `/`：公开个人主页，雇主无需登录即可访问
- `/login`：管理员登录
- `/admin`：简历信息管理后台
- `/api/public/resume`：公开主页数据接口
- `/api/resumes/current`：后台受保护的数据接口

## 一、上线前需要准备什么

### 1. 域名

可以在阿里云、腾讯云、Cloudflare、Namecheap 等平台购买，例如：

- `yourname.com`
- `yourname.me`
- `resume.yourname.com`

建议：

- `.com` 最正式，适合求职
- `.me` 更偏个人品牌
- 使用子域名便于以后扩展个人网站

### 2. 服务器

推荐配置：

- 最低：2 核 CPU / 2 GB 内存 / 40 GB SSD
- 推荐：2 核 CPU / 4 GB 内存 / 60 GB SSD
- 系统：Ubuntu 24.04 LTS

Spring Boot + MySQL + Redis 同时运行时，2 GB 内存可以启动，但 4 GB 更稳妥。

### 3. 中国内地服务器与 ICP 备案

如果服务器位于中国大陆，域名通常必须先完成 ICP 备案，否则云厂商会拦截 80/443 端口访问。

- 选择阿里云/腾讯云中国内地节点：准备备案
- 选择香港、新加坡等境外节点：通常不需要 ICP，但访问速度和合规要求不同

## 二、配置 DNS

在域名控制台添加 A 记录：

| 主机记录 | 类型 | 值 |
| --- | --- | --- |
| `@` | A | 服务器公网 IP |
| `www` | CNAME | 你的主域名 |

如果使用子域名，例如 `resume.yourname.com`：

| 主机记录 | 类型 | 值 |
| --- | --- | --- |
| `resume` | A | 服务器公网 IP |

DNS 生效通常需要几分钟到几小时。

## 三、服务器安装 Docker

登录服务器后执行：

```bash
curl -fsSL https://get.docker.com | sh
sudo usermod -aG docker "$USER"
newgrp docker
docker --version
docker compose version
```

开放防火墙端口：

```bash
sudo ufw allow 22/tcp
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp
sudo ufw enable
```

云服务器还需要在安全组中放行 `22`、`80`、`443`。

## 四、上传项目

将项目上传到服务器，例如 `/opt/resume-studio`。

```bash
cd /opt/resume-studio
cp .env.example .env
nano .env
```

生产 `.env` 至少修改：

```dotenv
DOMAIN=resume.yourname.com
MYSQL_ROOT_PASSWORD=请更换为高强度随机密码
DB_USERNAME=resume_studio
DB_PASSWORD=请更换为高强度随机密码
REDIS_PASSWORD=请更换为高强度随机密码
APP_ADMIN_USERNAME=admin
APP_ADMIN_PASSWORD=请更换为12位以上高强度密码
SESSION_COOKIE_SECURE=true
VITE_API_BASE_URL=/api
```

注意：生产环境不要把 `SESSION_COOKIE_SECURE` 设置为 `false`。

## 五、启动正式环境

```bash
cd /opt/resume-studio
docker compose -f deploy/docker-compose.prod.yml --env-file .env up -d --build
```

查看状态：

```bash
docker compose -f deploy/docker-compose.prod.yml --env-file .env ps
```

查看日志：

```bash
docker compose -f deploy/docker-compose.prod.yml --env-file .env logs -f backend
docker compose -f deploy/docker-compose.prod.yml --env-file .env logs -f caddy
```

Caddy 会自动申请并续期 Let's Encrypt HTTPS 证书。

访问：

- 公开主页：`https://resume.yourname.com/`
- 管理后台：`https://resume.yourname.com/login`

## 六、首次上线操作

1. 使用 `.env` 中的管理员账号登录 `/login`
2. 填写基本信息、教育经历、校园经历、课程、技能和项目
3. 点击“保存”
4. 进入“公开主页”，打开发布开关
5. 再次点击“保存”
6. 打开 `https://你的域名/` 验证公开页面

未填写或留空的模块不会展示在公开主页。

## 七、Cloudflare 说明

如果使用 Cloudflare：

- 首次配置证书时，建议先将 DNS 设为“仅 DNS”
- 确认 HTTPS 正常后，再开启橙色云代理
- SSL/TLS 模式选择 **Full (strict)**
- 不要使用“灵活 / Flexible”模式

## 八、备份与恢复

备份 MySQL：

```bash
docker compose -f deploy/docker-compose.prod.yml --env-file .env exec -T mysql \
  sh -c 'exec mysqldump -uroot -p"$MYSQL_ROOT_PASSWORD" resume_studio' \
  > resume-studio-backup.sql
```

恢复：

```bash
cat resume-studio-backup.sql | docker compose -f deploy/docker-compose.prod.yml --env-file .env exec -T mysql \
  sh -c 'exec mysql -uroot -p"$MYSQL_ROOT_PASSWORD" resume_studio'
```

建议每天自动备份，并把备份同步到对象存储。

## 九、更新版本

```bash
cd /opt/resume-studio
git pull
docker compose -f deploy/docker-compose.prod.yml --env-file .env up -d --build
```

## 十、上线检查清单

- [ ] 域名 A 记录已生效
- [ ] HTTPS 访问正常且无证书警告
- [ ] `/login` 使用高强度密码
- [ ] 公开主页内容已确认
- [ ] 手机和电脑浏览器均已检查
- [ ] MySQL 数据卷已备份
- [ ] 服务器仅开放必要端口
- [ ] `.env` 未提交到 Git
- [ ] 中国大陆服务器已完成 ICP 备案

## 十一、图片上传与阿里云 OSS

头像上传由后端统一处理，AccessKey 不会暴露给浏览器。

### 本地存储

开发环境默认使用：

```dotenv
STORAGE_TYPE=local
STORAGE_LOCAL_ROOT=./data/uploads
```

Docker 会自动把图片保存到 `uploads-data` 数据卷。备份数据库时也建议同步备份该卷。

### 阿里云 OSS

1. 创建 OSS Bucket，例如 `yourname-resume`
2. 地域建议与服务器一致，例如上海
3. 为图片设置公开读，或通过 CDN 域名访问
4. 创建只拥有该 Bucket 上传权限的 RAM 子账号
5. 在 `.env` 配置：

```dotenv
STORAGE_TYPE=oss
OSS_ENDPOINT=oss-cn-shanghai.aliyuncs.com
OSS_BUCKET=yourname-resume
OSS_ACCESS_KEY_ID=你的RAM账号AccessKeyId
OSS_ACCESS_KEY_SECRET=你的RAM账号AccessKeySecret
OSS_PUBLIC_BASE_URL=https://yourname-resume.oss-cn-shanghai.aliyuncs.com
```

如果绑定了 CDN，可设置：

```dotenv
OSS_PUBLIC_BASE_URL=https://img.yourname.com
```

保存后重新启动后端：

```bash
docker compose -f deploy/docker-compose.prod.yml --env-file .env up -d --build backend
```

注意：

- 不要使用阿里云主账号 AccessKey
- 不要在前端代码中写 AccessKey
- 建议 RAM 权限只允许 `oss:PutObject` 到 `resume/avatar/*`
- 图片限制为 JPG、PNG、WebP，最大 8 MB
- 后端会校验文件头，不依赖用户伪造的扩展名或 Content-Type

