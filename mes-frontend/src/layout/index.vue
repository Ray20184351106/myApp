<template>
  <div class="layout">
    <el-container>
      <!-- 侧边栏 -->
      <el-aside :width="isCollapse ? '64px' : '220px'" class="sidebar">
        <!-- Logo区域 -->
        <div class="logo-container" :class="{ collapsed: isCollapse }">
          <div class="logo-icon">
            <svg viewBox="0 0 32 32" fill="none" xmlns="http://www.w3.org/2000/svg">
              <rect x="2" y="2" width="28" height="28" rx="6" stroke="currentColor" stroke-width="2"/>
              <path d="M8 16H24M16 8V24M10 10L22 22M22 10L10 22" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
            </svg>
          </div>
          <transition name="fade">
            <div v-if="!isCollapse" class="logo-text">
              <span class="logo-title">MES</span>
              <span class="logo-subtitle">制造执行系统</span>
            </div>
          </transition>
        </div>

        <!-- 导航菜单 -->
        <el-menu
          :default-active="activeMenu"
          :collapse="isCollapse"
          router
          class="sidebar-menu"
        >
          <el-menu-item index="/dashboard">
            <el-icon><Odometer /></el-icon>
            <template #title>控制台</template>
          </el-menu-item>

          <el-sub-menu index="system">
            <template #title>
              <el-icon><Setting /></el-icon>
              <span>系统管理</span>
            </template>
            <el-menu-item index="/system/user">
              <el-icon><User /></el-icon>
              <span>用户管理</span>
            </el-menu-item>
            <el-menu-item index="/system/role">
              <el-icon><UserFilled /></el-icon>
              <span>角色管理</span>
            </el-menu-item>
            <el-menu-item index="/system/menu">
              <el-icon><Menu /></el-icon>
              <span>菜单管理</span>
            </el-menu-item>
          </el-sub-menu>
        </el-menu>

        <!-- 折叠按钮 -->
        <div class="collapse-btn" @click="isCollapse = !isCollapse">
          <el-icon :size="18">
            <Fold v-if="!isCollapse" />
            <Expand v-else />
          </el-icon>
        </div>
      </el-aside>

      <el-container class="main-container">
        <!-- 顶部导航 -->
        <el-header class="header">
          <div class="header-left">
            <div class="breadcrumb">
              <el-icon><Location /></el-icon>
              <span>{{ currentPageTitle }}</span>
            </div>
          </div>

          <div class="header-right">
            <!-- 通知 -->
            <el-badge :value="3" class="notification-badge">
              <el-button :icon="Bell" circle class="icon-btn" />
            </el-badge>

            <!-- 用户信息 -->
            <el-dropdown @command="handleCommand" trigger="click">
              <div class="user-info">
                <el-avatar :size="36" class="user-avatar">
                  <el-icon :size="20"><User /></el-icon>
                </el-avatar>
                <div class="user-detail">
                  <span class="user-name">管理员</span>
                  <span class="user-role">超级管理员</span>
                </div>
                <el-icon class="dropdown-arrow"><ArrowDown /></el-icon>
              </div>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile">
                    <el-icon><User /></el-icon>个人中心
                  </el-dropdown-item>
                  <el-dropdown-item command="settings">
                    <el-icon><Setting /></el-icon>系统设置
                  </el-dropdown-item>
                  <el-dropdown-item divided command="logout">
                    <el-icon><SwitchButton /></el-icon>退出登录
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </el-header>

        <!-- 主内容区 -->
        <el-main class="main-content">
          <router-view v-slot="{ Component }">
            <transition name="slide-fade" mode="out-in">
              <component :is="Component" />
            </transition>
          </router-view>
        </el-main>

        <!-- 页脚 -->
        <el-footer class="footer">
          <span>MES 制造执行系统 v1.0.0</span>
          <span class="divider">|</span>
          <span>技术支持</span>
        </el-footer>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  Odometer, Setting, User, UserFilled, Menu,
  Fold, Expand, Location, Bell, ArrowDown, SwitchButton
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const isCollapse = ref(false)

const activeMenu = computed(() => route.path)

const currentPageTitle = computed(() => {
  const titleMap = {
    '/dashboard': '控制台',
    '/system/user': '用户管理',
    '/system/role': '角色管理',
    '/system/menu': '菜单管理'
  }
  return titleMap[route.path] || '控制台'
})

const handleCommand = (command) => {
  if (command === 'logout') {
    localStorage.removeItem('token')
    router.push('/login')
  }
}
</script>

<style scoped>
/* ===== CSS变量定义 ===== */
.layout {
  --primary-color: #1a56db;
  --primary-light: #3b82f6;
  --primary-dark: #1e40af;
  --accent-color: #06b6d4;
  --accent-glow: rgba(6, 182, 212, 0.4);
  --sidebar-bg: linear-gradient(180deg, #0f172a 0%, #1e293b 100%);
  --sidebar-text: #94a3b8;
  --sidebar-active: #f1f5f9;
  --header-bg: rgba(255, 255, 255, 0.95);
  --content-bg: #f8fafc;
  --card-bg: #ffffff;
  --border-color: #e2e8f0;
  --text-primary: #1e293b;
  --text-secondary: #64748b;

  height: 100vh;
  background: var(--content-bg);
}

.el-container {
  height: 100%;
}

/* ===== 侧边栏样式 ===== */
.sidebar {
  background: var(--sidebar-bg);
  position: relative;
  transition: width 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.15);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.sidebar::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url("data:image/svg+xml,%3Csvg width='60' height='60' viewBox='0 0 60 60' xmlns='http://www.w3.org/2000/svg'%3E%3Cg fill='none' fill-rule='evenodd'%3E%3Cg fill='%23334155' fill-opacity='0.1'%3E%3Cpath d='M36 34v-4h-2v4h-4v2h4v4h2v-4h4v-2h-4zm0-30V0h-2v4h-4v2h4v4h2V6h4V4h-4zM6 34v-4H4v4H0v2h4v4h2v-4h4v-2H6zM6 4V0H4v4H0v2h4v4h2V6h4V4H6z'/%3E%3C/g%3E%3C/g%3E%3C/svg%3E");
  pointer-events: none;
  opacity: 0.5;
}

/* Logo区域 */
.logo-container {
  height: 72px;
  display: flex;
  align-items: center;
  padding: 0 20px;
  gap: 12px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
  position: relative;
  z-index: 1;
}

.logo-container.collapsed {
  padding: 0;
  justify-content: center;
}

.logo-icon {
  width: 40px;
  height: 40px;
  color: var(--accent-color);
  flex-shrink: 0;
  filter: drop-shadow(0 0 8px var(--accent-glow));
  animation: pulse-glow 3s ease-in-out infinite;
}

@keyframes pulse-glow {
  0%, 100% { filter: drop-shadow(0 0 8px var(--accent-glow)); }
  50% { filter: drop-shadow(0 0 16px var(--accent-glow)); }
}

.logo-text {
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.logo-title {
  font-size: 22px;
  font-weight: 700;
  color: #fff;
  letter-spacing: 2px;
  font-family: 'Orbitron', 'Rajdhani', sans-serif;
}

.logo-subtitle {
  font-size: 11px;
  color: var(--accent-color);
  letter-spacing: 1px;
  margin-top: 2px;
}

/* 菜单样式 */
.sidebar-menu {
  flex: 1;
  border-right: none;
  background: transparent;
  position: relative;
  z-index: 1;
  padding: 12px 0;
}

.sidebar-menu:not(.el-menu--collapse) {
  width: 100%;
}

/* 未激活菜单项 - 柔和的灰蓝色 */
:deep(.el-menu-item),
:deep(.el-sub-menu__title) {
  color: #94a3b8;
  height: 48px;
  line-height: 48px;
  margin: 4px 12px;
  border-radius: 8px;
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
}

/* 悬停效果 - 渐亮 */
:deep(.el-menu-item:hover),
:deep(.el-sub-menu__title:hover) {
  background: rgba(6, 182, 212, 0.12);
  color: #e2e8f0;
}

/* 悬停时图标颜色 */
:deep(.el-menu-item:hover .el-icon),
:deep(.el-sub-menu__title:hover .el-icon) {
  color: var(--accent-color);
}

/* 激活菜单项 - 青色渐变高亮 */
:deep(.el-menu-item.is-active) {
  background: linear-gradient(135deg, rgba(6, 182, 212, 0.2) 0%, rgba(6, 182, 212, 0.08) 100%);
  color: #22d3ee;
  border-left: 3px solid var(--accent-color);
  margin-left: 12px;
  padding-left: 17px;
  box-shadow: 0 0 20px rgba(6, 182, 212, 0.15);
}

:deep(.el-menu-item.is-active .el-icon) {
  color: #22d3ee;
}

/* 子菜单项样式 */
:deep(.el-sub-menu .el-menu-item) {
  padding-left: 52px !important;
  height: 42px;
  line-height: 42px;
  font-size: 13px;
}

/* 展开的子菜单标题 - 青色强调 */
:deep(.el-sub-menu.is-opened > .el-sub-menu__title) {
  color: #e2e8f0;
}

:deep(.el-sub-menu.is-opened > .el-sub-menu__title .el-icon) {
  color: var(--accent-color);
}

/* 激活的子菜单标题 */
:deep(.el-sub-menu.is-active > .el-sub-menu__title) {
  color: #22d3ee;
}

:deep(.el-sub-menu.is-active > .el-sub-menu__title .el-icon) {
  color: #22d3ee;
}

/* 子菜单展开后的背景 */
:deep(.el-sub-menu .el-menu) {
  background: rgba(0, 0, 0, 0.15);
}

/* 折叠按钮 */
.collapse-btn {
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--sidebar-text);
  cursor: pointer;
  border-top: 1px solid rgba(255, 255, 255, 0.08);
  transition: all 0.2s ease;
  position: relative;
  z-index: 1;
}

.collapse-btn:hover {
  color: var(--accent-color);
  background: rgba(255, 255, 255, 0.05);
}

/* ===== 主内容区 ===== */
.main-container {
  display: flex;
  flex-direction: column;
  background: var(--content-bg);
}

/* 顶部导航 */
.header {
  background: var(--header-bg);
  backdrop-filter: blur(12px);
  border-bottom: 1px solid var(--border-color);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  height: 64px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.breadcrumb {
  display: flex;
  align-items: center;
  gap: 8px;
  color: var(--text-secondary);
  font-size: 14px;
}

.breadcrumb .el-icon {
  color: var(--primary-color);
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.notification-badge {
  margin-right: 8px;
}

.icon-btn {
  border: none;
  background: transparent;
  color: var(--text-secondary);
}

.icon-btn:hover {
  background: var(--content-bg);
  color: var(--primary-color);
}

/* 用户信息 */
.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 6px 12px;
  border-radius: 24px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.user-info:hover {
  background: var(--content-bg);
}

.user-avatar {
  background: linear-gradient(135deg, var(--primary-color) 0%, var(--accent-color) 100%);
  color: #fff;
}

.user-detail {
  display: flex;
  flex-direction: column;
}

.user-name {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
}

.user-role {
  font-size: 11px;
  color: var(--text-secondary);
}

.dropdown-arrow {
  color: var(--text-secondary);
  transition: transform 0.2s ease;
}

.user-info:hover .dropdown-arrow {
  transform: rotate(180deg);
}

/* 主内容 */
.main-content {
  flex: 1;
  padding: 24px;
  overflow-y: auto;
  background: var(--content-bg);
}

/* 页脚 */
.footer {
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  font-size: 12px;
  color: var(--text-secondary);
  border-top: 1px solid var(--border-color);
  background: var(--card-bg);
}

.footer .divider {
  color: var(--border-color);
}

/* ===== 过渡动画 ===== */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

.slide-fade-enter-active {
  transition: all 0.3s ease-out;
}

.slide-fade-leave-active {
  transition: all 0.2s ease-in;
}

.slide-fade-enter-from {
  transform: translateX(20px);
  opacity: 0;
}

.slide-fade-leave-to {
  transform: translateX(-20px);
  opacity: 0;
}

/* ===== 滚动条美化 ===== */
::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

::-webkit-scrollbar-track {
  background: transparent;
}

::-webkit-scrollbar-thumb {
  background: var(--border-color);
  border-radius: 3px;
}

::-webkit-scrollbar-thumb:hover {
  background: var(--text-secondary);
}
</style>
