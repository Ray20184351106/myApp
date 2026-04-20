<template>
  <div class="login-container">
    <!-- 背景装饰 -->
    <div class="bg-decoration">
      <div class="grid-pattern"></div>
      <div class="glow-orb orb-1"></div>
      <div class="glow-orb orb-2"></div>
      <div class="glow-orb orb-3"></div>
    </div>

    <!-- 登录卡片 -->
    <div class="login-card">
      <!-- 左侧品牌区 -->
      <div class="brand-section">
        <div class="brand-content">
          <div class="brand-icon">
            <svg viewBox="0 0 48 48" fill="none" xmlns="http://www.w3.org/2000/svg">
              <rect x="4" y="4" width="40" height="40" rx="8" stroke="currentColor" stroke-width="2"/>
              <path d="M12 24H36M24 12V36M16 16L32 32M32 16L16 32" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
            </svg>
          </div>
          <h1 class="brand-title">MES</h1>
          <p class="brand-subtitle">Manufacturing Execution System</p>
          <div class="brand-features">
            <div class="feature">
              <el-icon><DataLine /></el-icon>
              <span>实时生产监控</span>
            </div>
            <div class="feature">
              <el-icon><Connection /></el-icon>
              <span>设备互联管理</span>
            </div>
            <div class="feature">
              <el-icon><TrendCharts /></el-icon>
              <span>数据分析决策</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧登录表单 -->
      <div class="form-section">
        <div class="form-content">
          <div class="form-header">
            <h2>欢迎回来</h2>
            <p>请登录您的账户以继续</p>
          </div>

          <el-form :model="form" :rules="rules" ref="formRef" class="login-form">
            <el-form-item prop="username">
              <el-input
                v-model="form.username"
                placeholder="请输入用户名"
                size="large"
                :prefix-icon="User"
              />
            </el-form-item>

            <el-form-item prop="password">
              <el-input
                v-model="form.password"
                type="password"
                placeholder="请输入密码"
                size="large"
                :prefix-icon="Lock"
                show-password
                @keyup.enter="handleLogin"
              />
            </el-form-item>

            <div class="form-options">
              <el-checkbox v-model="rememberMe">记住我</el-checkbox>
              <a href="#" class="forgot-link">忘记密码？</a>
            </div>

            <el-form-item>
              <el-button
                type="primary"
                size="large"
                :loading="loading"
                @click="handleLogin"
                class="login-btn"
              >
                <span v-if="!loading">登 录</span>
                <span v-else>登录中...</span>
              </el-button>
            </el-form-item>
          </el-form>

          <div class="form-footer">
            <span>还没有账户？</span>
            <a href="#">联系管理员</a>
          </div>
        </div>
      </div>
    </div>

    <!-- 版权信息 -->
    <div class="copyright">
      <span>© 2024 MES 制造执行系统 · 技术驱动未来</span>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, DataLine, Connection, TrendCharts } from '@element-plus/icons-vue'
import request from '../utils/request'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)
const rememberMe = ref(false)

const form = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  await formRef.value.validate()
  loading.value = true
  try {
    const res = await request.post('/auth/login', form)
    localStorage.setItem('token', res.data)
    ElMessage.success('登录成功')
    router.push('/')
  } catch (err) {
    // 错误已在拦截器中处理
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
/* ===== CSS变量 ===== */
.login-container {
  --primary: #1a56db;
  --primary-light: #3b82f6;
  --accent: #06b6d4;
  --accent-glow: rgba(6, 182, 212, 0.5);
  --dark-bg: #0f172a;
  --card-bg: #ffffff;

  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #0f172a 0%, #1e293b 50%, #0f172a 100%);
  position: relative;
  overflow: hidden;
  padding: 40px 20px;
}

/* ===== 背景装饰 ===== */
.bg-decoration {
  position: absolute;
  inset: 0;
  pointer-events: none;
}

.grid-pattern {
  position: absolute;
  inset: 0;
  background-image:
    linear-gradient(rgba(255,255,255,0.03) 1px, transparent 1px),
    linear-gradient(90deg, rgba(255,255,255,0.03) 1px, transparent 1px);
  background-size: 60px 60px;
  animation: grid-move 20s linear infinite;
}

@keyframes grid-move {
  0% { transform: translate(0, 0); }
  100% { transform: translate(60px, 60px); }
}

.glow-orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  animation: float 8s ease-in-out infinite;
}

.orb-1 {
  width: 400px;
  height: 400px;
  background: var(--primary);
  opacity: 0.15;
  top: -100px;
  left: -100px;
  animation-delay: 0s;
}

.orb-2 {
  width: 300px;
  height: 300px;
  background: var(--accent);
  opacity: 0.12;
  bottom: -50px;
  right: -50px;
  animation-delay: -3s;
}

.orb-3 {
  width: 200px;
  height: 200px;
  background: var(--primary-light);
  opacity: 0.1;
  top: 50%;
  right: 20%;
  animation-delay: -5s;
}

@keyframes float {
  0%, 100% { transform: translate(0, 0) scale(1); }
  33% { transform: translate(30px, -30px) scale(1.1); }
  66% { transform: translate(-20px, 20px) scale(0.9); }
}

/* ===== 登录卡片 ===== */
.login-card {
  display: flex;
  width: 900px;
  min-height: 520px;
  background: var(--card-bg);
  border-radius: 24px;
  box-shadow:
    0 25px 50px -12px rgba(0, 0, 0, 0.4),
    0 0 0 1px rgba(255, 255, 255, 0.1);
  overflow: hidden;
  position: relative;
  z-index: 1;
}

/* ===== 品牌区域 ===== */
.brand-section {
  flex: 1;
  background: linear-gradient(135deg, var(--dark-bg) 0%, #1e293b 100%);
  padding: 48px;
  display: flex;
  align-items: center;
  position: relative;
}

.brand-section::before {
  content: '';
  position: absolute;
  inset: 0;
  background: url("data:image/svg+xml,%3Csvg width='60' height='60' viewBox='0 0 60 60' xmlns='http://www.w3.org/2000/svg'%3E%3Cg fill='none' fill-rule='evenodd'%3E%3Cg fill='%23334155' fill-opacity='0.15'%3E%3Cpath d='M36 34v-4h-2v4h-4v2h4v4h2v-4h4v-2h-4zm0-30V0h-2v4h-4v2h4v4h2V6h4V4h-4zM6 34v-4H4v4H0v2h4v4h2v-4h4v-2H6zM6 4V0H4v4H0v2h4v4h2V6h4V4H6z'/%3E%3C/g%3E%3C/g%3E%3C/svg%3E");
  opacity: 0.5;
}

.brand-content {
  position: relative;
  z-index: 1;
}

.brand-icon {
  width: 64px;
  height: 64px;
  color: var(--accent);
  margin-bottom: 24px;
  filter: drop-shadow(0 0 20px var(--accent-glow));
  animation: icon-pulse 3s ease-in-out infinite;
}

@keyframes icon-pulse {
  0%, 100% { transform: scale(1); filter: drop-shadow(0 0 20px var(--accent-glow)); }
  50% { transform: scale(1.05); filter: drop-shadow(0 0 30px var(--accent-glow)); }
}

.brand-title {
  font-size: 42px;
  font-weight: 800;
  color: #fff;
  margin: 0 0 8px 0;
  letter-spacing: 4px;
  font-family: 'Orbitron', 'Rajdhani', sans-serif;
}

.brand-subtitle {
  font-size: 14px;
  color: var(--accent);
  margin: 0 0 40px 0;
  letter-spacing: 2px;
  text-transform: uppercase;
}

.brand-features {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.feature {
  display: flex;
  align-items: center;
  gap: 12px;
  color: #94a3b8;
  font-size: 14px;
}

.feature .el-icon {
  color: var(--accent);
  font-size: 18px;
}

/* ===== 表单区域 ===== */
.form-section {
  flex: 1;
  padding: 48px;
  display: flex;
  align-items: center;
  background: #fff;
}

.form-content {
  width: 100%;
}

.form-header {
  margin-bottom: 32px;
}

.form-header h2 {
  font-size: 28px;
  font-weight: 700;
  color: #1e293b;
  margin: 0 0 8px 0;
}

.form-header p {
  font-size: 14px;
  color: #64748b;
  margin: 0;
}

.login-form {
  margin-bottom: 24px;
}

.login-form :deep(.el-input__wrapper) {
  border-radius: 12px;
  box-shadow: 0 0 0 1px #e2e8f0;
  transition: all 0.2s ease;
}

.login-form :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #94a3b8;
}

.login-form :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 2px var(--primary);
}

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.forgot-link {
  color: var(--primary);
  text-decoration: none;
  font-size: 13px;
}

.forgot-link:hover {
  text-decoration: underline;
}

.login-btn {
  width: 100%;
  height: 48px;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  background: linear-gradient(135deg, var(--primary) 0%, var(--primary-light) 100%);
  border: none;
  transition: all 0.3s ease;
}

.login-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(26, 86, 219, 0.4);
}

.form-footer {
  text-align: center;
  font-size: 13px;
  color: #64748b;
}

.form-footer a {
  color: var(--primary);
  text-decoration: none;
  font-weight: 500;
}

.form-footer a:hover {
  text-decoration: underline;
}

/* ===== 版权信息 ===== */
.copyright {
  position: absolute;
  bottom: 24px;
  left: 50%;
  transform: translateX(-50%);
  color: #64748b;
  font-size: 12px;
  z-index: 1;
}

/* ===== 响应式 ===== */
@media (max-width: 768px) {
  .login-card {
    flex-direction: column;
    width: 100%;
    max-width: 400px;
  }

  .brand-section {
    padding: 32px;
  }

  .brand-title {
    font-size: 32px;
  }

  .brand-features {
    display: none;
  }

  .form-section {
    padding: 32px;
  }
}
</style>
