# 分支与发布流程

仓库使用四条长期分支：

| 分支 | 用途 | 部署环境 |
| --- | --- | --- |
| `main` | 稳定主干，汇总已验证功能 | 不直接部署 |
| `develop` | 日常开发与功能集成 | 开发 / 预发布 |
| `test` | 测试验收与回归验证 | 测试环境 |
| `production` | 正式上线版本 | 生产环境 |

## 推荐流程

1. 从 `develop` 创建功能分支：

   ```bash
   git checkout develop
   git pull
   git checkout -b feature/your-feature
   ```

2. 完成开发与本地验证后合入 `develop`。
3. 将 `develop` 合入 `test` 进行测试验收。
4. 测试通过后合入 `production` 并部署。
5. 定期将 `production` 的修复回合 `main`，保持主干稳定。

## 发布标签

建议每次生产发布创建版本标签：

```bash
git checkout production
git pull
git tag -a v1.0.0 -m "release v1.0.0"
git push origin v1.0.0
```

## 部署环境变量

- 开发：`deploy` 目录和 `frontend/.env.example`
- 测试：独立 `.env.test`
- 生产：独立 `.env.production`

任何环境的 `.env` 文件、AccessKey、数据库密码都不能提交到 Git。
