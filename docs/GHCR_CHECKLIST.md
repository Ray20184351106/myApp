# GHCR 部署检查清单

## 配置状态

### GitHub 配置
- [x] Workflow 文件已创建 (`.github/workflows/deploy.yml`)
- [ ] GitHub Secrets 已配置 (需要手动完成)
- [ ] Personal Access Token 已创建 (需要手动完成)

### 服务器配置
- [ ] SSH 公钥已添加 (需要手动完成)
- [ ] 部署目录已创建 (需要手动完成)
- [ ] Docker 和 docker-compose 已安装 (需要手动完成)

---

## GitHub Secrets 配置

访问：https://github.com/Ray20184351106/myApp/settings/secrets/actions

### 需要添加的 Secrets

| Secret | 值 | 状态 |
|--------|-----|------|
| `SERVER_HOST` | 你的服务器 IP | ⬜ 待配置 |
| `SERVER_USER` | `root` 或其他用户 | ⬜ 待配置 |
| `SSH_PRIVATE_KEY` | SSH 私钥内容 | ⬜ 待配置 |
| `DEPLOY_PATH` | `/opt/mes` | ⬜ 待配置 |

---

## 服务器配置步骤

### 1. 生成 SSH 密钥（本地）

```bash
# 在本地终端执行
ssh-keygen -t ed25519 -C "github-actions" -f ~/.ssh/github_actions
```

### 2. 添加公钥到服务器

```bash
# 查看公钥
cat ~/.ssh/github_actions.pub

# 登录服务器
ssh root@你的服务器 IP

# 添加公钥
mkdir -p ~/.ssh
chmod 700 ~/.ssh
echo "刚才复制的公钥内容" >> ~/.ssh/authorized_keys
chmod 600 ~/.ssh/authorized_keys
```

### 3. 创建部署目录

```bash
# 在服务器上执行
mkdir -p /opt/mes
exit
```

### 4. 配置 GitHub Secrets

在本地终端执行以下命令获取私钥：

```bash
cat ~/.ssh/github_actions
```

复制完整输出（包括 BEGIN 和 END），添加到 GitHub Secrets。

---

## 验证配置

### 步骤 1：测试 SSH 连接

```bash
# 在本地测试
ssh -i ~/.ssh/github_actions root@你的服务器 IP
```

### 步骤 2：手动触发 Workflow

1. 访问 https://github.com/Ray20184351106/myApp/actions
2. 点击 **Build and Deploy**
3. 点击 **Run workflow**
4. 选择 **main** 分支
5. 点击 **Run workflow**

### 步骤 3：查看部署日志

在 GitHub Actions 页面查看实时日志。

---

## 镜像仓库

配置完成后，镜像将推送到：

- https://github.com/Ray20184351106/myApp/pkgs/container/myapp%2Fmes-auth
- https://github.com/Ray20184351106/myApp/pkgs/container/myapp%2Fmes-system
- https://github.com/Ray20184351106/myApp/pkgs/container/myapp%2Fmes-gateway
- https://github.com/Ray20184351106/myApp/pkgs/container/myapp%2Fmes-frontend

---

## 部署流程

```
推送代码
    ↓
GitHub Actions 触发
    ↓
Maven 构建后端 (约 2-3 分钟)
    ↓
Node 构建前端 (约 1-2 分钟)
    ↓
构建 Docker 镜像并推送 GHCR (约 3-5 分钟)
    ↓
SSH 连接服务器
    ↓
登录 GHCR 并拉取镜像 (约 2-3 分钟)
    ↓
启动容器
    ↓
健康检查
    ↓
部署完成 ✅
```

**总耗时：约 10-15 分钟**（首次）

---

## 常用命令

### 本地开发
```bash
# 使用 build 配置启动（不经过 GHCR）
docker-compose up --build -d
```

### 服务器部署
```bash
# 登录 GHCR
echo "ghp_xxx" | docker login ghcr.io -u ray20184351106 --password-stdin

# 拉取镜像
docker-compose pull

# 启动服务
docker-compose up -d

# 查看状态
docker-compose ps

# 查看日志
docker-compose logs -f
```

---

## 故障排查

### Workflow 失败
- 检查 GitHub Secrets 是否正确
- 检查 SSH 密钥权限
- 查看 Actions 日志

### 镜像拉取失败
- 确认 GHCR Token 有效
- 检查镜像名称是否正确
- 确认 GHCR 登录状态

### 容器启动失败
- 检查端口占用：`docker-compose ps`
- 查看容器日志：`docker-compose logs mes-auth`
- 检查网络配置：`docker network ls`
