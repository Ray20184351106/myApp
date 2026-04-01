# GitHub Secrets 配置指南

## 必需的 Secrets

在 GitHub 仓库页面，进入 **Settings → Secrets and variables → Actions**，添加以下 secrets：

### 1. 服务器连接配置

| Secret Name | 说明 | 示例值 |
|-------------|------|--------|
| `SERVER_HOST` | 服务器 IP 地址或域名 | `192.168.1.100` 或 `example.com` |
| `SERVER_USER` | SSH 登录用户名 | `root` |
| `SSH_PRIVATE_KEY` | SSH 私钥内容 | 见下方生成方法 |
| `DEPLOY_PATH` | 部署目录路径 | `/opt/mes` |

### 2. SSH 私钥生成方法

在本地终端执行：

```bash
# 生成新的 SSH 密钥（如果已有可跳过）
ssh-keygen -t ed25519 -C "github-actions" -f ~/.ssh/github_actions

# 查看公钥内容（需要添加到服务器）
cat ~/.ssh/github_actions.pub

# 查看私钥内容（复制到 GitHub Secrets）
cat ~/.ssh/github_actions
```

将私钥内容（包含 `-----BEGIN OPENSSH PRIVATE KEY-----` 完整内容）复制到 `SSH_PRIVATE_KEY`。

### 3. 服务器配置

登录服务器，执行：

```bash
# 创建部署目录
mkdir -p /opt/mes
cd /opt/mes

# 添加 GitHub Actions 公钥到 authorized_keys
echo "你的公钥内容" >> ~/.ssh/authorized_keys
chmod 600 ~/.ssh/authorized_keys

# 验证 SSH 连接
ssh -i ~/.ssh/github_actions github-actions@github.com
```

## 验证配置

### 方法 1：手动触发 Workflow

1. 进入 GitHub 仓库 **Actions** 标签
2. 点击 **Build and Deploy** workflow
3. 点击 **Run workflow** 按钮
4. 选择 **main** 分支
5. 点击 **Run workflow**

### 方法 2：推送代码触发

```bash
git add .
git commit -m "test: trigger ci/cd deployment"
git push origin main
```

## 故障排查

### 问题 1：SSH 连接失败

```bash
# 在 GitHub Actions 日志中会显示：
# "Permission denied (publickey)"

# 解决方案：
# 1. 确认公钥已添加到服务器 ~/.ssh/authorized_keys
# 2. 检查权限：chmod 700 ~/.ssh && chmod 600 ~/.ssh/authorized_keys
```

### 问题 2：GHCR 拉取失败

```bash
# 错误信息：
# "unauthorized: authentication required"

# 解决方案：
# 确保部署脚本中有登录 GHCR 的步骤
# 或在服务器上预先登录：
# echo "GHCR_TOKEN" | docker login ghcr.io -u USERNAME --password-stdin
```

### 问题 3：docker-compose 未找到

```bash
# 在服务器上安装 docker-compose
sudo curl -L "https://github.com/docker/compose/releases/download/v2.24.0/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose
docker-compose --version
```

## 手动部署流程

如果自动部署失败，可以在服务器上手动执行：

```bash
# 1. 设置环境变量
export GHCR_USERNAME="ray20184351106"
export GHCR_TOKEN="ghp_xxxxxxxxxxxx"
export DEPLOY_DIR="/opt/mes"

# 2. 执行部署脚本
cd /opt/mes
bash /opt/mes/scripts/deploy-ghcr.sh
```

## 镜像地址

| 服务 | 镜像地址 |
|------|----------|
| mes-auth | `ghcr.io/ray20184351106/myapp/mes-auth:latest` |
| mes-system | `ghcr.io/ray20184351106/myapp/mes-system:latest` |
| mes-gateway | `ghcr.io/ray20184351106/myapp/mes-gateway:latest` |
| mes-frontend | `ghcr.io/ray20184351106/myapp/mes-frontend:latest` |

## 检查清单

- [ ] 已创建 GitHub Personal Access Token（`write:packages` 权限）
- [ ] 已添加 `SERVER_HOST` secret
- [ ] 已添加 `SERVER_USER` secret
- [ ] 已添加 `SSH_PRIVATE_KEY` secret
- [ ] 已添加 `DEPLOY_PATH` secret
- [ ] 已在服务器添加 SSH 公钥
- [ ] 已在服务器创建部署目录
- [ ] 已在服务器安装 docker-compose
