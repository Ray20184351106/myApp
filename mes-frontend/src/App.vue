<template>
  <router-view />
</template>

<script setup>
</script>

<style>
/* ===== 引入字体 ===== */
@import url('https://fonts.googleapis.com/css2?family=Rajdhani:wght@400;500;600;700&family=Inter:wght@400;500;600;700&display=swap');

/* ===== 全局重置 ===== */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html, body, #app {
  height: 100%;
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

/* ===== CSS变量 ===== */
:root {
  /* 主色调 */
  --mes-primary: #1a56db;
  --mes-primary-light: #3b82f6;
  --mes-primary-dark: #1e40af;

  /* 强调色 */
  --mes-accent: #06b6d4;
  --mes-accent-glow: rgba(6, 182, 212, 0.4);

  /* 功能色 */
  --mes-success: #059669;
  --mes-warning: #d97706;
  --mes-danger: #dc2626;
  --mes-info: #7c3aed;

  /* 中性色 */
  --mes-text-primary: #1e293b;
  --mes-text-secondary: #64748b;
  --mes-text-muted: #94a3b8;

  /* 背景色 */
  --mes-bg-primary: #ffffff;
  --mes-bg-secondary: #f8fafc;
  --mes-bg-tertiary: #f1f5f9;
  --mes-bg-dark: #0f172a;

  /* 边框色 */
  --mes-border: #e2e8f0;
  --mes-border-light: #f1f5f9;

  /* 阴影 */
  --mes-shadow-sm: 0 1px 2px rgba(0, 0, 0, 0.05);
  --mes-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
  --mes-shadow-lg: 0 10px 15px -3px rgba(0, 0, 0, 0.1);
  --mes-shadow-xl: 0 20px 25px -5px rgba(0, 0, 0, 0.1);

  /* 圆角 */
  --mes-radius-sm: 6px;
  --mes-radius: 10px;
  --mes-radius-lg: 16px;
  --mes-radius-xl: 24px;

  /* 过渡 */
  --mes-transition: all 0.2s ease;
  --mes-transition-slow: all 0.3s ease;
}

/* ===== Element Plus 主题覆盖 ===== */
.el-button--primary {
  --el-button-bg-color: var(--mes-primary);
  --el-button-border-color: var(--mes-primary);
  --el-button-hover-bg-color: var(--mes-primary-light);
  --el-button-hover-border-color: var(--mes-primary-light);
  --el-button-active-bg-color: var(--mes-primary-dark);
  --el-button-active-border-color: var(--mes-primary-dark);
}

.el-button--primary {
  background: linear-gradient(135deg, var(--mes-primary) 0%, var(--mes-primary-light) 100%);
  border: none;
  transition: var(--mes-transition-slow);
}

.el-button--primary:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(26, 86, 219, 0.3);
}

/* 卡片样式 */
.el-card {
  border-radius: var(--mes-radius-lg);
  border: 1px solid var(--mes-border);
  box-shadow: var(--mes-shadow-sm);
  transition: var(--mes-transition);
}

.el-card:hover {
  box-shadow: var(--mes-shadow);
}

/* 表格样式 */
.el-table {
  border-radius: var(--mes-radius);
  overflow: hidden;
}

.el-table th.el-table__cell {
  background: var(--mes-bg-tertiary);
  font-weight: 600;
  color: var(--mes-text-primary);
}

.el-table--striped .el-table__body tr.el-table__row--striped td.el-table__cell {
  background: var(--mes-bg-secondary);
}

/* 输入框样式 */
.el-input__wrapper {
  border-radius: var(--mes-radius-sm);
  transition: var(--mes-transition);
}

.el-input__wrapper:hover {
  box-shadow: 0 0 0 1px var(--mes-text-muted);
}

.el-input__wrapper.is-focus {
  box-shadow: 0 0 0 2px var(--mes-primary);
}

/* 标签样式 */
.el-tag {
  border-radius: 20px;
  font-weight: 500;
}

/* 对话框样式 */
.el-dialog {
  border-radius: var(--mes-radius-xl);
  overflow: hidden;
}

.el-dialog__header {
  padding: 20px 24px;
  border-bottom: 1px solid var(--mes-border);
}

.el-dialog__body {
  padding: 24px;
}

.el-dialog__footer {
  padding: 16px 24px;
  border-top: 1px solid var(--mes-border);
}

/* 分页样式 */
.el-pagination {
  gap: 8px;
}

.el-pagination .el-pager li {
  border-radius: var(--mes-radius-sm);
  transition: var(--mes-transition);
}

.el-pagination .el-pager li.is-active {
  background: var(--mes-primary);
}

/* 下拉菜单样式 */
.el-dropdown-menu {
  border-radius: var(--mes-radius);
  padding: 8px;
  box-shadow: var(--mes-shadow-lg);
}

.el-dropdown-menu__item {
  border-radius: var(--mes-radius-sm);
  padding: 8px 16px;
  transition: var(--mes-transition);
}

/* 消息提示样式 */
.el-message {
  border-radius: var(--mes-radius);
  box-shadow: var(--mes-shadow-lg);
}

/* ===== 动画 ===== */
@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

.animate-fade-in {
  animation: fadeIn 0.3s ease;
}

.animate-slide-up {
  animation: slideUp 0.4s ease;
}

/* ===== 滚动条 ===== */
::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

::-webkit-scrollbar-track {
  background: transparent;
}

::-webkit-scrollbar-thumb {
  background: var(--mes-border);
  border-radius: 3px;
}

::-webkit-scrollbar-thumb:hover {
  background: var(--mes-text-muted);
}

/* ===== 选择高亮 ===== */
::selection {
  background: var(--mes-primary);
  color: #fff;
}
</style>
