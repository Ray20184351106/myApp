<template>
  <div class="dashboard">
    <!-- 欢迎区域 -->
    <div class="welcome-section">
      <div class="welcome-content">
        <h1 class="welcome-title">
          <span class="greeting">{{ greeting }}</span>
          <span class="name">管理员</span>
        </h1>
        <p class="welcome-subtitle">欢迎回来，今天是 {{ currentDate }}</p>
      </div>
      <div class="quick-actions">
        <el-button type="primary" :icon="Plus">新建工单</el-button>
        <el-button :icon="DataLine">查看报表</el-button>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-grid">
      <div class="stat-card" v-for="(stat, index) in stats" :key="index">
        <div class="stat-icon" :style="{ background: stat.gradient }">
          <el-icon :size="24"><component :is="stat.icon" /></el-icon>
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ stat.value }}</span>
          <span class="stat-label">{{ stat.label }}</span>
        </div>
        <div class="stat-trend" :class="stat.trend > 0 ? 'up' : 'down'">
          <el-icon :size="14"><component :is="stat.trend > 0 ? 'Top' : 'Bottom'" /></el-icon>
          <span>{{ Math.abs(stat.trend) }}%</span>
        </div>
      </div>
    </div>

    <!-- 主内容区 -->
    <div class="main-grid">
      <!-- 生产概览 -->
      <div class="panel production-panel">
        <div class="panel-header">
          <h3>生产概览</h3>
          <el-button text type="primary" :icon="ArrowRight">查看详情</el-button>
        </div>
        <div class="panel-content">
          <div class="production-chart">
            <div class="chart-bar" v-for="(item, index) in productionData" :key="index">
              <div class="bar-label">{{ item.label }}</div>
              <div class="bar-track">
                <div class="bar-fill" :style="{ width: item.value + '%', background: item.color }"></div>
              </div>
              <div class="bar-value">{{ item.value }}%</div>
            </div>
          </div>
        </div>
      </div>

      <!-- 最近工单 -->
      <div class="panel orders-panel">
        <div class="panel-header">
          <h3>最近工单</h3>
          <el-button text type="primary" :icon="ArrowRight">查看全部</el-button>
        </div>
        <div class="panel-content">
          <div class="order-list">
            <div class="order-item" v-for="(order, index) in recentOrders" :key="index">
              <div class="order-icon" :style="{ background: order.color }">
                <el-icon :size="16"><component :is="order.icon" /></el-icon>
              </div>
              <div class="order-info">
                <span class="order-name">{{ order.name }}</span>
                <span class="order-time">{{ order.time }}</span>
              </div>
              <el-tag :type="order.statusType" size="small">{{ order.status }}</el-tag>
            </div>
          </div>
        </div>
      </div>

      <!-- 设备状态 -->
      <div class="panel equipment-panel">
        <div class="panel-header">
          <h3>设备状态</h3>
          <el-button text type="primary" :icon="ArrowRight">设备管理</el-button>
        </div>
        <div class="panel-content">
          <div class="equipment-grid">
            <div class="equipment-item" v-for="(eq, index) in equipmentList" :key="index">
              <div class="eq-status" :class="eq.status"></div>
              <span class="eq-name">{{ eq.name }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 系统公告 -->
      <div class="panel notice-panel">
        <div class="panel-header">
          <h3>系统公告</h3>
          <el-button text type="primary" :icon="ArrowRight">更多</el-button>
        </div>
        <div class="panel-content">
          <div class="notice-list">
            <div class="notice-item" v-for="(notice, index) in notices" :key="index">
              <div class="notice-dot"></div>
              <div class="notice-content">
                <span class="notice-title">{{ notice.title }}</span>
                <span class="notice-date">{{ notice.date }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import {
  Plus, DataLine, Odometer, User, Document, Monitor,
  Top, Bottom, ArrowRight, Timer, Finished, Warning, CircleCheck
} from '@element-plus/icons-vue'

const greeting = computed(() => {
  const hour = new Date().getHours()
  if (hour < 12) return '早上好'
  if (hour < 18) return '下午好'
  return '晚上好'
})

const currentDate = computed(() => {
  const now = new Date()
  return now.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    weekday: 'long'
  })
})

const stats = [
  { icon: Document, label: '今日工单', value: '128', trend: 12, gradient: 'linear-gradient(135deg, #1a56db 0%, #3b82f6 100%)' },
  { icon: Odometer, label: '生产进度', value: '86%', trend: 5, gradient: 'linear-gradient(135deg, #059669 0%, #10b981 100%)' },
  { icon: Monitor, label: '设备在线', value: '24', trend: 0, gradient: 'linear-gradient(135deg, #7c3aed 0%, #a855f7 100%)' },
  { icon: User, label: '在线人员', value: '56', trend: -3, gradient: 'linear-gradient(135deg, #dc2626 0%, #f87171 100%)' }
]

const productionData = [
  { label: '焊接车间', value: 92, color: '#1a56db' },
  { label: '装配车间', value: 78, color: '#059669' },
  { label: '涂装车间', value: 65, color: '#7c3aed' },
  { label: '质检车间', value: 88, color: '#dc2626' }
]

const recentOrders = [
  { name: 'WO-2024-0128', time: '10分钟前', status: '进行中', statusType: 'warning', icon: Timer, color: '#fef3c7' },
  { name: 'WO-2024-0127', time: '1小时前', status: '已完成', statusType: 'success', icon: Finished, color: '#d1fae5' },
  { name: 'WO-2024-0126', time: '2小时前', status: '待审核', statusType: 'info', icon: Warning, color: '#e0e7ff' },
  { name: 'WO-2024-0125', time: '昨天', status: '已完成', statusType: 'success', icon: CircleCheck, color: '#d1fae5' }
]

const equipmentList = [
  { name: '焊接机器人-01', status: 'online' },
  { name: '焊接机器人-02', status: 'online' },
  { name: '装配线-A', status: 'online' },
  { name: '装配线-B', status: 'warning' },
  { name: '涂装设备-01', status: 'online' },
  { name: '质检设备-01', status: 'offline' }
]

const notices = [
  { title: '系统升级通知：将于本周六凌晨进行系统维护', date: '2024-01-15' },
  { title: '新功能上线：设备实时监控功能已开放', date: '2024-01-12' },
  { title: '安全提醒：请定期修改登录密码', date: '2024-01-10' }
]
</script>

<style scoped>
/* ===== CSS变量 ===== */
.dashboard {
  --primary: #1a56db;
  --primary-light: #3b82f6;
  --accent: #06b6d4;
  --success: #059669;
  --warning: #d97706;
  --danger: #dc2626;
  --card-bg: #ffffff;
  --border: #e2e8f0;
  --text-primary: #1e293b;
  --text-secondary: #64748b;
  --bg-secondary: #f8fafc;
}

/* ===== 欢迎区域 ===== */
.welcome-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding: 24px 32px;
  background: linear-gradient(135deg, var(--primary) 0%, var(--primary-light) 100%);
  border-radius: 16px;
  color: #fff;
  box-shadow: 0 8px 24px rgba(26, 86, 219, 0.25);
}

.welcome-title {
  font-size: 28px;
  font-weight: 700;
  margin: 0 0 8px 0;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.welcome-title .greeting {
  font-size: 14px;
  font-weight: 400;
  opacity: 0.8;
}

.welcome-title .name {
  font-family: 'Orbitron', 'Rajdhani', sans-serif;
  letter-spacing: 2px;
}

.welcome-subtitle {
  margin: 0;
  font-size: 14px;
  opacity: 0.8;
}

.quick-actions {
  display: flex;
  gap: 12px;
}

.quick-actions .el-button {
  border-radius: 8px;
}

.quick-actions .el-button--primary {
  background: rgba(255, 255, 255, 0.2);
  border-color: rgba(255, 255, 255, 0.3);
}

.quick-actions .el-button--default {
  background: rgba(255, 255, 255, 0.9);
  color: var(--primary);
  border: none;
}

/* ===== 统计卡片 ===== */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 24px;
}

.stat-card {
  background: var(--card-bg);
  border-radius: 16px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  border: 1px solid var(--border);
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  flex-shrink: 0;
}

.stat-info {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: var(--text-primary);
  font-family: 'Orbitron', 'Rajdhani', sans-serif;
}

.stat-label {
  font-size: 13px;
  color: var(--text-secondary);
}

.stat-trend {
  display: flex;
  align-items: center;
  gap: 2px;
  font-size: 12px;
  font-weight: 600;
  padding: 4px 8px;
  border-radius: 20px;
}

.stat-trend.up {
  color: var(--success);
  background: rgba(5, 150, 105, 0.1);
}

.stat-trend.down {
  color: var(--danger);
  background: rgba(220, 38, 38, 0.1);
}

/* ===== 主内容网格 ===== */
.main-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

/* ===== 面板通用样式 ===== */
.panel {
  background: var(--card-bg);
  border-radius: 16px;
  border: 1px solid var(--border);
  overflow: hidden;
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid var(--border);
}

.panel-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
}

.panel-content {
  padding: 20px;
}

/* ===== 生产概览 ===== */
.production-chart {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.chart-bar {
  display: flex;
  align-items: center;
  gap: 12px;
}

.bar-label {
  width: 80px;
  font-size: 13px;
  color: var(--text-secondary);
}

.bar-track {
  flex: 1;
  height: 8px;
  background: var(--bg-secondary);
  border-radius: 4px;
  overflow: hidden;
}

.bar-fill {
  height: 100%;
  border-radius: 4px;
  transition: width 0.6s ease;
}

.bar-value {
  width: 40px;
  font-size: 13px;
  font-weight: 600;
  color: var(--text-primary);
  text-align: right;
}

/* ===== 工单列表 ===== */
.order-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.order-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: var(--bg-secondary);
  border-radius: 10px;
  transition: all 0.2s ease;
}

.order-item:hover {
  background: #f1f5f9;
}

.order-icon {
  width: 36px;
  height: 36px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--text-primary);
}

.order-info {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.order-name {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
}

.order-time {
  font-size: 12px;
  color: var(--text-secondary);
}

/* ===== 设备状态 ===== */
.equipment-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
}

.equipment-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px;
  background: var(--bg-secondary);
  border-radius: 8px;
}

.eq-status {
  width: 8px;
  height: 8px;
  border-radius: 50%;
}

.eq-status.online {
  background: var(--success);
  box-shadow: 0 0 8px rgba(5, 150, 105, 0.5);
}

.eq-status.warning {
  background: var(--warning);
  box-shadow: 0 0 8px rgba(217, 119, 6, 0.5);
}

.eq-status.offline {
  background: var(--danger);
  box-shadow: 0 0 8px rgba(220, 38, 38, 0.5);
}

.eq-name {
  font-size: 12px;
  color: var(--text-primary);
}

/* ===== 系统公告 ===== */
.notice-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.notice-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
}

.notice-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: var(--primary);
  margin-top: 6px;
  flex-shrink: 0;
}

.notice-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.notice-title {
  font-size: 14px;
  color: var(--text-primary);
  line-height: 1.5;
}

.notice-date {
  font-size: 12px;
  color: var(--text-secondary);
}

/* ===== 响应式 ===== */
@media (max-width: 1200px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .welcome-section {
    flex-direction: column;
    gap: 20px;
    text-align: center;
  }

  .stats-grid {
    grid-template-columns: 1fr;
  }

  .main-grid {
    grid-template-columns: 1fr;
  }

  .equipment-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
