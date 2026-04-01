# GHCR 部署最终配置指南

> 本文档将指导你完成 GHCR 部署的所有手动配置步骤

---

## 📋 配置总览

```
✅ 已完成 (自动):
├── GitHub Actions Workflow 配置
├── docker-compose.yml 镜像配置
├── 部署脚本创建
└── 文档创建

⬜ 需要手动完成:
├── 1. 创建 GitHub Personal Access Token
├── 2. 配置 GitHub Secrets
├── 3. 服务器 SSH 密钥配置
└── 4. 首次部署测试
```

---

## 步骤 1: 创建 GitHub Personal Access Token

### 1.1 访问 Token 创建页面

打开：https://github.com/settings/tokens

### 1.2 生成新 Token

1. 点击 **Generate new token (classic)**
2. 填写说明：`GHCR Deployment`
3. 选择过期时间：建议 `No expiration` 或 `90 days`
4. 勾选以下权限：
   ```
   ✅ repo (Full control of private repositories)
   ✅ write:packages (Upload packages to GitHub Packages)
   ✅ delete:packages (Delete packages from GitHub Packages)
   ```
5. 点击 **Generate token**
6. **立即复制 Token**（格式如：`ghp_xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx`）

⚠️ **重要**: Token 只显示一次，请妥善保管！

---

## 步骤 2: 生成 SSH 密钥

### 2.1 在本地终端执行

```bash
# 生成新的 SSH 密钥
ssh-keygen -t ed25519 -C "github-actions-deploy" -f ~/.ssh/github_actions_deploy

# 按 Enter 接受默认 passphrase（或直接按 Enter 跳过）
```

### 2.2 查看公钥内容

```bash
cat ~/.ssh/github_actions_deploy.pub
```

输出类似：
```
ssh-ed25519 AAAAC3NzaC1lZDI1NTE5AAAAI... github-actions-deploy
```

**复制完整的公钥内容**，后面要用。

### 2.3 查看私钥内容

```bash
cat ~/.ssh/github_actions_deploy
```

**复制完整的私钥内容**（包括 BEGIN 和 END），用于配置 GitHub Secrets。

---

## 步骤 3: 配置 GitHub Secrets

### 3.1 访问 Secrets 页面

打开：https://github.com/Ray20184351106/myApp/settings/secrets/actions

### 3.2 添加 4 个 Secrets

点击 **New repository secret**，依次添加：

#### Secret 1: SERVER_HOST
| 字段 | 值 |
|------|-----|
| Name | `SERVER_HOST` |
| Value | 你的服务器公网 IP 或域名 |

#### Secret 2: SERVER_USER
| 字段 | 值 |
|------|-----|
| Name | `SERVER_USER` |
| Value | `root` (或你的 SSH 用户名) |

#### Secret 3: SSH_PRIVATE_KEY
| 字段 | 值 |
|------|-----|
| Name | `SSH_PRIVATE_KEY` |
| Value | 步骤 2.3 中复制的私钥内容 |

⚠️ **注意**: 私钥内容要完整，包括：
```
-----BEGIN OPENSSH PRIVATE KEY-----
xxxxx...
-----END OPENSSH PRIVATE KEY-----
```

#### Secret 4: DEPLOY_PATH
| 字段 | 值 |
|------|-----|
| Name | `DEPLOY_PATH` |
| Value | `/opt/mes` |

---

## 步骤 4: 服务器配置

### 4.1 SSH 登录服务器

```bash
ssh root@你的服务器 IP
```

### 4.2 创建 .ssh 目录

```bash
mkdir -p ~/.ssh
chmod 700 ~/.ssh
```

### 4.3 添加公钥到 authorized_keys

```bash
# 编辑 authorized_keys 文件
nano ~/.ssh/authorized_keys
```

**粘贴步骤 2.2 中的公钥内容**，然后：
- 按 `Ctrl + O` 保存
- 按 `Enter` 确认文件名
- 按 `Ctrl + X` 退出

设置权限：
```bash
chmod 600 ~/.ssh/authorized_keys
```

### 4.4 创建部署目录

```bash
mkdir -p /opt/mes
chown $USER:$USER /opt/mes
```

### 4.5 验证 SSH 连接

打开新的本地终端窗口测试：
```bash
ssh -i ~/.ssh/github_actions_deploy root@你的服务器 IP
```

如果能成功登录，说明 SSH 密钥配置正确。

---

## 步骤 5: 提交配置到 Git

### 5.1 添加并提交所有更改

```bash
cd D:/Desktop/docker

git add .
git commit -m "ci: configure GHCR deployment"
```

### 5.2 推送到 GitHub

```bash
git push origin main
```

---

## 步骤 6: 触发部署

### 方式 A: 推送代码自动触发（已在上一步完成）

推送后，GitHub Actions 会自动开始构建和部署。

### 方式 B: 手动触发 Workflow

1. 打开：https://github.com/Ray20184351106/myApp/actions
2. 点击 **Build and Deploy** workflow
3. 点击 **Run workflow** 按钮
4. 选择分支：`main`
5. 点击 **Run workflow**

---

## 步骤 7: 监控部署进度

### 7.1 查看 GitHub Actions 日志

访问：https://github.com/Ray20184351106/myApp/actions

点击正在运行的 workflow，查看实时日志。

### 7.2 部署阶段说明

```
✅ build-and-push 作业 (约 8-12 分钟)
   ├─▶ Maven 构建后端
   ├─▶ Node 构建前端
   ├─▶ 构建 Docker 镜像
   └─▶ 推送到 GHCR

✅ deploy 作业 (约 3-5 分钟)
   ├─▶ SSH 连接服务器
   ├─▶ 登录 GHCR
   ├─▶ 拉取镜像
   └─▶ 启动容器
```

---

## 步骤 8: 验证部署

### 8.1 SSH 登录服务器

```bash
ssh root@你的服务器 IP
```

### 8.2 检查容器状态

```bash
cd /opt/mes
docker-compose ps
```

预期输出：
```
NAME           STATUS    PORTS
mes-auth       Up        0.0.0.0:9001->9001/tcp
mes-system     Up        0.0.0.0:9002->9002/tcp
mes-gateway    Up        0.0.0.0:9000->9000/tcp
mes-frontend   Up        0.0.0.0:80->80/tcp
```

### 8.3 查看服务日志

```bash
docker-compose logs -f
```

### 8.4 测试 API

```bash
curl -X POST http://localhost:9000/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"123456"}'
```

### 8.5 访问前端

浏览器打开：`http://你的服务器 IP`

默认账号：`admin` / `123456`

---

## 故障排查

### 问题 1: Workflow 立即失败

**错误**: `Error: secret SERVER_HOST is not defined`

**解决**: 检查 GitHub Secrets 是否正确配置

### 问题 2: SSH 连接失败

**错误**: `Permission denied (publickey)`

**解决**:
```bash
# 在服务器上检查
cat ~/.ssh/authorized_keys
# 确认包含你的公钥

# 检查权限
chmod 700 ~/.ssh
chmod 600 ~/.ssh/authorized_keys
```

### 问题 3: GHCR 拉取失败

**错误**: `unauthorized: authentication required`

**解决**: 确认 Workflow 中有 `docker login` 步骤，或手动登录：
```bash
echo "ghp_your_token" | docker login ghcr.io -u ray20184351106 --password-stdin
```

### 问题 4: Maven 构建超时

**解决**: 检查阿里云镜像配置，或增加 timeout：
```yaml
# 在 deploy.yml 中添加
- name: Build Backend with Maven
  timeout-minutes: 15
  run: mvn clean package -DskipTests -B
```

---

## 后续操作

### 查看 GHCR 镜像

访问：
- https://github.com/Ray20184351106/myApp/pkgs/container/myapp%2Fmes-auth
- https://github.com/Ray20184351106/myApp/pkgs/container/myapp%2Fmes-system
- https://github.com/Ray20184351106/myApp/pkgs/container/myapp%2Fmes-gateway
- https://github.com/Ray20184351106/myApp/pkgs/container/myapp%2Fmes-frontend

### 日常部署

只需推送代码：
```bash
git push origin main
```

### 手动触发部署

访问：https://github.com/Ray20184351106/myApp/actions

---

## 配置完成检查清单

- [ ] Personal Access Token 已创建
- [ ] SSH 密钥已生成
- [ ] GitHub Secrets 已配置（4 个）
- [ ] 服务器已添加 SSH 公钥
- [ ] 部署目录已创建
- [ ] 配置已提交并推送
- [ ] Workflow 已成功运行
- [ ] 容器已启动
- [ ] API 测试通过
- [ ] 前端可访问

---

需要帮助？查看：
- 快速开始：`docs/GHCR_QUICKSTART.md`
- 配置详解：`docs/GITHUB_SECRETS_SETUP.md`
- 检查清单：`docs/GHCR_CHECKLIST.md`
