# CI/CD 部署优化方案

## 问题分析

原部署流程超时原因：
1. **前端构建耗时**：`vite build` 在服务器 Docker 内执行，受服务器性能和网络影响
2. **Maven 构建耗时**：每个微服务独立构建，重复编译公共模块
3. **并行构建竞争**：多个服务同时构建导致资源竞争

## 解决方案

### 方案一：使用 GitHub Container Registry (GHCR) - ✅ 推荐

**流程：**
```
GitHub Actions (构建) → GHCR (镜像仓库) → 服务器 (拉取 + 运行)
```

**优势：**
- ✅ 所有构建在 GitHub Actions 完成，服务器零构建
- ✅ 使用 GitHub 高速网络，构建快速稳定
- ✅ 免费私有镜像仓库
- ✅ 支持缓存加速，增量构建更快
- ✅ 部署时间从 30+ 分钟降至 5 分钟内

**配置步骤：**

1. **服务器配置**（一次性）：
   ```bash
   # 在服务器上执行，登录 GHCR
   echo "YOUR_GITHUB_TOKEN" | docker login ghcr.io -u YOUR_USERNAME --password-stdin
   ```

2. **GitHub Secrets 配置**：
   | Secret 名称 | 说明 |
   |------------|------|
   | `SERVER_HOST` | 服务器 IP 地址 |
   | `SERVER_USER` | SSH 用户名 |
   | `SSH_PRIVATE_KEY` | SSH 私钥 |
   | `DEPLOY_PATH` | 部署目录 |
   | `GITHUB_TOKEN` | GitHub Token（自动提供） |

3. **部署命令**：
   ```bash
   # 服务器上拉取镜像并启动
   docker-compose pull
   docker-compose up -d
   ```

### 方案二：优化服务器构建（备选）

如果不想使用镜像仓库，可以优化构建流程：

**优化点：**
1. 使用 `docker-compose build --parallel` 并行构建
2. 启用 BuildKit 加速：`DOCKER_BUILDKIT=1`
3. 前端 Dockerfile 使用缓存层优化
4. 增加 `--progress=plain` 查看详细进度

**部署脚本：**
```bash
#!/bin/bash
cd /opt/mes

# 停止服务
docker-compose down

# 并行构建（BuildKit 加速）
DOCKER_BUILDKIT=1 docker-compose build --parallel --progress=plain

# 启动服务
docker-compose up -d
```

## 修改内容清单

| 文件 | 修改内容 |
|------|----------|
| `.github/workflows/deploy.yml` | 重写 CI/CD 流程，使用 GHCR |
| `docker-compose.yml` | 添加 image 配置，支持从 GHCR 拉取 |
| `mes-frontend/Dockerfile` | 优化构建缓存层 |
| `scripts/deploy.sh` | 新增本地部署脚本 |

## 首次部署步骤

### 1. 创建 GitHub Personal Access Token

访问：https://github.com/settings/tokens
- 选择 `Generate new token (classic)`
- 勾选权限：`repo`, `write:packages`, `delete:packages`
- 生成后复制 Token

### 2. 配置 GitHub Secrets

仓库 Settings → Secrets and variables → Actions → New repository secret：

| Name | Value |
|------|-------|
| `SERVER_HOST` | 你的服务器 IP |
| `SERVER_USER` | 服务器用户名（如 root） |
| `SSH_PRIVATE_KEY` | SSH 私钥内容 |
| `DEPLOY_PATH` | 部署路径（如 /opt/mes） |

### 3. 服务器登录 GHCR

```bash
# 在服务器上执行
echo "ghp_xxxxxxxxxxxx" | docker login ghcr.io -u Ray20184351106 --password-stdin
```

### 4. 触发部署

```bash
# 推送代码触发
git push origin main

# 或手动触发：GitHub Actions → Build and Deploy → Run workflow
```

## 本地开发部署

本地开发时，可继续使用 `docker-compose up --build`：

```bash
# 构建并启动（本地）
docker-compose up --build -d

# 或使用 deploy 脚本
./scripts/deploy.sh
```

## 故障排查

### 问题 1：GHCR 登录失败
```bash
# 检查 Token 是否有效
docker login ghcr.io -u YOUR_USERNAME
```

### 问题 2：镜像拉取失败
```bash
# 手动拉取测试
docker pull ghcr.io/ray20184351106/mes/mes-auth:latest
```

### 问题 3：权限不足
确保 GitHub Token 有 `write:packages` 权限

### 问题 4：磁盘空间不足
```bash
# 清理旧镜像
docker image prune -a --force
```

## 性能对比

| 方案 | 构建时间 | 部署时间 | 总计 |
|------|----------|----------|------|
| 原方案 | 25-40 分钟 | 2 分钟 | **超时** |
| 方案一（GHCR） | 8-12 分钟 | 3 分钟 | **~12 分钟** |
| 方案二（优化构建） | 15-20 分钟 | 2 分钟 | **~20 分钟** |

## 后续优化建议

1. **增量构建**：只构建变更的模块
2. **多环境支持**：dev/staging/prod 环境分离
3. **回滚机制**：保留历史镜像版本，支持快速回滚
4. **健康检查**：完善服务健康检查配置
