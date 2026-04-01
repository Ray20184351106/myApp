# GHCR 部署快速开始指南

##  overview

本指南将帮助你在 10 分钟内完成 GHCR 部署配置。

## 架构图

```
┌─────────────────────────────────────────────────────────────────┐
│                    GitHub Actions (CI/CD)                       │
│                                                                 │
│  代码推送 → 构建 Maven → 构建 Node → 构建 Docker → 推送 GHCR    │
│                                                                 │
└────────────────────────────┬────────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│                    GHCR (镜像仓库)                              │
│  ghcr.io/ray20184351106/myapp/mes-auth:latest                  │
│  ghcr.io/ray20184351106/myapp/mes-system:latest                │
│  ghcr.io/ray20184351106/myapp/mes-gateway:latest               │
│  ghcr.io/ray20184351106/myapp/mes-frontend:latest              │
└────────────────────────────┬────────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│                    生产服务器                                   │
│  docker-compose pull → docker-compose up -d                    │
└─────────────────────────────────────────────────────────────────┘
```

---

## 第一步：创建 GitHub Personal Access Token (2 分钟)

1. 访问 https://github.com/settings/tokens
2. 点击 **Generate new token (classic)**
3. 填写说明（如：GHCR Deployment）
4. 勾选以下权限：
   - [x] `repo` (完整控制)
   - [x] `write:packages` (上传包)
   - [x] `delete:packages` (删除包)
5. 点击 **Generate token**
6. **复制 Token**（只显示一次，格式如 `ghp_xxxxxxxxxxxx`）

---

## 第二步：配置 GitHub Secrets (1 分钟)

进入你的 GitHub 仓库 → **Settings** → **Secrets and variables → Actions** → **New repository secret**

添加以下 4 个 secrets：

| Name | Value |
|------|-------|
| `SERVER_HOST` | 你的服务器 IP，如 `1.2.3.4` |
| `SERVER_USER` | SSH 用户名，通常是 `root` |
| `SSH_PRIVATE_KEY` | SSH 私钥（见下方） |
| `DEPLOY_PATH` | 部署目录，如 `/opt/mes` |

### 如何获取 SSH Private Key

**方法 A：使用现有密钥**
```bash
# 查看现有 SSH 私钥
cat ~/.ssh/id_rsa
# 或
cat ~/.ssh/id_ed25519
```

**方法 B：生成新密钥**
```bash
# 生成新密钥
ssh-keygen -t ed25519 -C "github-actions" -f ~/.ssh/github_actions

# 查看私钥
cat ~/.ssh/github_actions
```

将私钥完整内容（包含 `-----BEGIN...` 到 `-----END...`）复制到 `SSH_PRIVATE_KEY`。

---

## 第三步：服务器配置 (3 分钟)

### 3.1 登录服务器
```bash
ssh root@你的服务器 IP
```

### 3.2 添加 SSH 公钥
```bash
# 在本地机器查看公钥
cat ~/.ssh/github_actions.pub
# 或
cat ~/.ssh/id_rsa.pub
```

在服务器上执行：
```bash
# 创建 .ssh 目录
mkdir -p ~/.ssh
chmod 700 ~/.ssh

# 编辑 authorized_keys
nano ~/.ssh/authorized_keys

# 粘贴公钥内容，保存退出
# 按 Ctrl+O → Enter → Ctrl+X
```

### 3.3 创建部署目录
```bash
mkdir -p /opt/mes
chown $USER:$USER /opt/mes
```

### 3.4 验证 SSH 连接
```bash
# 退出服务器
exit

# 在本地测试连接
ssh -i ~/.ssh/github_actions root@你的服务器 IP
```

---

## 第四步：首次部署 (4 分钟)

### 方法 A：自动部署（推荐）

```bash
# 1. 提交并推送代码
git add .
git commit -m "ci: enable GHCR deployment"
git push origin main

# 2. 在 GitHub 查看构建进度
# Actions → Build and Deploy
```

### 方法 B：手动触发

1. 进入 GitHub 仓库 **Actions** 标签
2. 点击 **Build and Deploy**
3. 点击 **Run workflow**
4. 选择 **main** 分支
5. 点击 **Run workflow**

---

## 第五步：验证部署

### 检查服务状态
```bash
# SSH 登录服务器
ssh root@你的服务器 IP

# 查看容器状态
docker-compose ps

# 查看日志
docker-compose logs -f
```

### 测试接口
```bash
# 测试登录接口
curl -X POST http://你的服务器 IP:9000/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"123456"}'

# 预期返回
# {"code":200,"msg":"操作成功","data":"..."}
```

### 访问前端
打开浏览器访问：`http://你的服务器 IP`

---

## 后续部署

每次推送代码到 main 分支时，会自动触发部署：

```bash
git push origin main
```

或手动触发 Workflow。

---

## 本地开发部署

在本地开发时，可以使用传统方式构建：

```bash
# 本地构建并启动（不经过 GHCR）
docker-compose up --build -d
```

---

## 常见问题

### Q1: Workflow 失败，显示 "Permission denied"
**A:** 检查 SSH 密钥配置：
```bash
# 在服务器上
cat ~/.ssh/authorized_keys
# 确认包含你的公钥
```

### Q2: GHCR 拉取失败 "unauthorized"
**A:** 确保 Workflow 中有登录步骤，或手动登录：
```bash
# 在服务器上
echo "ghp_xxxxx" | docker login ghcr.io -u ray20184351106 --password-stdin
```

### Q3: 部署时间过长
**A:** 首次部署需要拉取所有镜像，后续只拉取变化的镜像。

### Q4: 如何回滚到旧版本
**A:** 使用镜像的 SHA 标签：
```bash
# 修改 docker-compose.yml 中的镜像标签
image: ghcr.io/ray20184351106/myapp/mes-auth:abc1234
```

---

## 文件清单

```
项目根目录/
├── .github/workflows/deploy.yml    # CI/CD 配置
├── docker-compose.yml               # Docker 编排
├── scripts/
│   ├── deploy-ghcr.sh              # GHCR 部署脚本
│   └── init-server.sh              # 服务器初始化脚本
└── docs/
    ├── GITHUB_SECRETS_SETUP.md     # Secrets 配置详解
    └── DEPLOYMENT.md               # 部署方案文档
```

---

## 需要帮助？

如遇问题，请查看：
1. GitHub Actions 日志
2. 服务器日志：`docker-compose logs`
3. 文档：`docs/` 目录
