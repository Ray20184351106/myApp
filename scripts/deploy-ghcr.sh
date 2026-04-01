#!/bin/bash
# MES 系统 GHCR 部署脚本
# 用于在服务器上登录 GHCR 并部署系统

set -e

echo "=========================================="
echo "MES System GHCR Deployment Script"
echo "=========================================="

# 配置变量
GHCR_USERNAME="${GHCR_USERNAME:-ray20184351106}"
GHCR_TOKEN="${GHCR_TOKEN}"
DEPLOY_DIR="${DEPLOY_DIR:-/opt/mes}"

# 检查 GHCR_TOKEN 是否设置
if [ -z "$GHCR_TOKEN" ]; then
    echo "ERROR: GHCR_TOKEN environment variable is not set!"
    echo "Please set it first:"
    echo "  export GHCR_TOKEN='your_github_token_here'"
    exit 1
fi

echo "Username: $GHCR_USERNAME"
echo "Deploy Dir: $DEPLOY_DIR"
echo ""

# 1. 登录 GHCR
echo "[1/5] Logging into GHCR..."
echo "$GHCR_TOKEN" | docker login ghcr.io -u "$GHCR_USERNAME" --password-stdin

# 2. 进入部署目录
echo "[2/5] Changing to deploy directory..."
cd "$DEPLOY_DIR"

# 3. 停止现有服务
echo "[3/5] Stopping existing services..."
docker-compose down

# 4. 拉取最新镜像
echo "[4/5] Pulling latest images from GHCR..."
docker-compose pull

# 5. 启动服务
echo "[5/5] Starting services..."
docker-compose up -d

# 等待服务启动
echo ""
echo "Waiting for services to start (30s)..."
sleep 30

# 检查状态
echo ""
echo "=========================================="
echo "Service Status:"
echo "=========================================="
docker-compose ps

echo ""
echo "=========================================="
echo "Deploy completed!"
echo "=========================================="
echo ""
echo "To view logs: docker-compose logs -f"
echo "To check status: docker-compose ps"
