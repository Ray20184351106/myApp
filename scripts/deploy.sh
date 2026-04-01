#!/bin/bash
# MES 系统部署脚本
# 用于在服务器上构建和部署系统

set -e

echo "=========================================="
echo "MES System Deploy Script"
echo "=========================================="

DEPLOY_DIR="${DEPLOY_DIR:-/opt/mes}"
cd "$DEPLOY_DIR"

# 1. 停止现有服务
echo "[1/6] Stopping existing services..."
docker-compose down

# 2. 清理旧镜像
echo "[2/6] Cleaning up old images..."
docker image prune -f

# 3. 构建后端服务（先构建，失败则立即退出）
echo "[3/6] Building backend services..."
docker-compose build mes-auth mes-system mes-gateway

# 4. 构建前端服务
echo "[4/6] Building frontend service (this may take 5-10 minutes)..."
docker-compose build mes-frontend

# 5. 启动服务
echo "[5/6] Starting services..."
docker-compose up -d

# 6. 等待并检查
echo "[6/6] Waiting for services to start..."
sleep 30

echo ""
echo "=========================================="
echo "Service Status:"
echo "=========================================="
docker-compose ps

echo ""
echo "=========================================="
echo "Recent Logs:"
echo "=========================================="
docker-compose logs --tail=5 mes-auth || true
docker-compose logs --tail=5 mes-system || true
docker-compose logs --tail=5 mes-gateway || true

echo ""
echo "=========================================="
echo "Deploy completed!"
echo "=========================================="
