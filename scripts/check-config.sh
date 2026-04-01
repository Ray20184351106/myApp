#!/bin/bash
# GHCR 部署配置检查脚本
# 在本地执行，检查配置状态

echo "=========================================="
echo "GHCR Deployment Configuration Checker"
echo "=========================================="
echo ""

# 颜色定义
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 检查计数
PASS=0
WARN=0
FAIL=0

# 检查函数
check_file() {
    if [ -f "$1" ]; then
        echo -e "${GREEN}✓${NC} File exists: $1"
        ((PASS++))
    else
        echo -e "${RED}✗${NC} File missing: $1"
        ((FAIL++))
    fi
}

check_dir() {
    if [ -d "$1" ]; then
        echo -e "${GREEN}✓${NC} Directory exists: $1"
        ((PASS++))
    else
        echo -e "${RED}✗${NC} Directory missing: $1"
        ((FAIL++))
    fi
}

check_ssh_key() {
    if [ -f "$1" ]; then
        echo -e "${GREEN}✓${NC} SSH key exists: $1"
        ((PASS++))
    else
        echo -e "${YELLOW}!${NC} SSH key not found: $1"
        ((WARN++))
    fi
}

echo "=== Checking Project Structure ==="
echo ""

# 检查关键文件
check_file ".github/workflows/deploy.yml"
check_file "docker-compose.yml"
check_file "mes-frontend/Dockerfile"
check_file "mes-services/mes-auth/Dockerfile"
check_file "mes-services/mes-system/Dockerfile"
check_file "mes-gateway/Dockerfile"

echo ""
echo "=== Checking Scripts ==="
echo ""

check_file "scripts/deploy-ghcr.sh"
check_file "scripts/init-server.sh"

echo ""
echo "=== Checking Documentation ==="
echo ""

check_file "docs/GHCR_QUICKSTART.md"
check_file "docs/GHCR_CHECKLIST.md"
check_file "docs/GITHUB_SECRETS_SETUP.md"
check_file "docs/FINAL_SETUP.md"

echo ""
echo "=== Checking SSH Keys (Optional) ==="
echo ""

# 检查本地 SSH 密钥
if command -v ssh-keygen &> /dev/null; then
    check_ssh_key "$HOME/.ssh/github_actions_deploy"
    check_ssh_key "$HOME/.ssh/github_actions_deploy.pub"
    check_ssh_key "$HOME/.ssh/id_rsa"
    check_ssh_key "$HOME/.ssh/id_rsa.pub"
else
    echo -e "${YELLOW}!${NC} ssh-keygen not found"
    ((WARN++))
fi

echo ""
echo "=== Checking Git Status ==="
echo ""

# 检查 git 状态
if command -v git &> /dev/null; then
    UNCOMMITTED=$(git status --porcelain 2>/dev/null | wc -l)
    if [ "$UNCOMMITTED" -gt 0 ]; then
        echo -e "${YELLOW}!${NC} You have $UNCOMMITTED uncommitted change(s)"
        echo "   Run 'git add .' and 'git commit' to stage changes"
        ((WARN++))
    else
        echo -e "${GREEN}✓${NC} No uncommitted changes"
        ((PASS++))
    fi

    # 检查远程仓库
    REMOTE=$(git remote get-url origin 2>/dev/null)
    if [ -n "$REMOTE" ]; then
        echo -e "${GREEN}✓${NC} Remote repository: $REMOTE"
        ((PASS++))
    else
        echo -e "${RED}✗${NC} No remote repository configured"
        ((FAIL++))
    fi
else
    echo -e "${RED}✗${NC} git not found"
    ((FAIL++))
fi

echo ""
echo "=== Checking Docker (Optional) ==="
echo ""

if command -v docker &> /dev/null; then
    echo -e "${GREEN}✓${NC} Docker is installed"
    ((PASS++))
else
    echo -e "${YELLOW}!${NC} Docker not found (required for local testing)"
    ((WARN++))
fi

echo ""
echo "=========================================="
echo "Summary"
echo "=========================================="
echo -e "${GREEN}Passed: $PASS${NC}"
echo -e "${YELLOW}Warnings: $WARN${NC}"
echo -e "${RED}Failed: $FAIL${NC}"
echo ""

if [ $FAIL -gt 0 ]; then
    echo -e "${RED}⚠ Some required files are missing!${NC}"
    echo "Please ensure all critical files are in place."
elif [ $WARN -gt 0 ]; then
    echo -e "${YELLOW}⚠ Configuration is incomplete.${NC}"
    echo "Please review the warnings above."
else
    echo -e "${GREEN}✓ All checks passed!${NC}"
    echo "Ready to deploy!"
fi

echo ""
echo "Next steps:"
echo "1. Generate SSH key: ssh-keygen -t ed25519 -C 'github-actions' -f ~/.ssh/github_actions_deploy"
echo "2. Create GitHub Personal Access Token at https://github.com/settings/tokens"
echo "3. Configure GitHub Secrets at https://github.com/Ray20184351106/myApp/settings/secrets/actions"
echo "4. Add SSH public key to server"
echo "5. Run: git add . && git commit -m 'ci: configure GHCR deployment' && git push origin main"
echo ""
