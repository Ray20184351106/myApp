#!/bin/bash
# GHCR 部署初始化脚本
# 在服务器上执行，用于初始化部署环境

set -e

echo "=========================================="
echo "GHCR Deployment Initialization Script"
echo "=========================================="

# 配置
DEPLOY_DIR="${DEPLOY_DIR:-/opt/mes}"
GHCR_USERNAME="${GHCR_USERNAME:-ray20184351106}"

echo "Deploy directory: $DEPLOY_DIR"
echo "GHCR Username: $GHCR_USERNAME"
echo ""

# 检查 Docker
echo "=== Checking Docker ==="
if ! command -v docker &> /dev/null; then
    echo "ERROR: Docker is not installed!"
    echo "Please install Docker first: https://docs.docker.com/engine/install/"
    exit 1
fi
docker --version

# 检查 docker-compose
echo "=== Checking docker-compose ==="
if ! command -v docker-compose &> /dev/null; then
    echo "WARNING: docker-compose is not found!"
    echo "Installing docker-compose..."
    sudo curl -L "https://github.com/docker/compose/releases/download/v2.24.0/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
    sudo chmod +x /usr/local/bin/docker-compose
fi
docker-compose --version

# 创建部署目录
echo "=== Creating deploy directory ==="
sudo mkdir -p "$DEPLOY_DIR"
sudo chown "$USER:$USER" "$DEPLOY_DIR"
ls -ld "$DEPLOY_DIR"

# 提示输入 GHCR Token
echo ""
echo "=========================================="
echo "GHCR Login"
echo "=========================================="
echo "Please enter your GitHub Personal Access Token:"
echo "(Create one at: https://github.com/settings/tokens)"
echo "Required scopes: read:packages, write:packages, delete:packages"
echo ""
read -s -p "GHCR Token: " GHCR_TOKEN
echo ""

if [ -z "$GHCR_TOKEN" ]; then
    echo "ERROR: Token is empty!"
    exit 1
fi

# 登录 GHCR
echo "=== Logging into GHCR ==="
echo "$GHCR_TOKEN" | docker login ghcr.io -u "$GHCR_USERNAME" --password-stdin

if [ $? -eq 0 ]; then
    echo "GHCR login successful!"
else
    echo "GHCR login failed! Please check your token."
    exit 1
fi

# 保存 Token（可选）
echo ""
read -p "Save token to ~/.ghcr_token for future use? (y/N): " save_choice
if [[ "$save_choice" =~ ^[Yy]$ ]]; then
    echo "$GHCR_TOKEN" > ~/.ghcr_token
    chmod 600 ~/.ghcr_token
    echo "Token saved to ~/.ghcr_token"
    echo ""
    echo "Later you can use:"
    echo "  export GHCR_TOKEN=\$(cat ~/.ghcr_token)"
    echo "  bash scripts/deploy-ghcr.sh"
fi

echo ""
echo "=========================================="
echo "Initialization completed!"
echo "=========================================="
echo ""
echo "Next steps:"
echo "1. Clone your repository to $DEPLOY_DIR"
echo "2. Run: bash scripts/deploy-ghcr.sh"
echo ""
echo "Or manually:"
echo "  cd $DEPLOY_DIR"
echo "  docker-compose pull"
echo "  docker-compose up -d"
