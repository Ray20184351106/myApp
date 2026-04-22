<template>
  <div class="not-found">
    <div class="content">
      <div class="error-code">404</div>
      <div class="error-desc">页面不存在</div>
      <div class="error-tip">抱歉，您访问的页面不存在或已被删除</div>
      <el-button type="primary" @click="goHome">
        <el-icon><HomeFilled /></el-icon>
        返回首页
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { HomeFilled } from '@element-plus/icons-vue'

const router = useRouter()

const goHome = () => {
  router.push('/')
}
</script>

<style scoped>
.not-found {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #0f172a 0%, #1e293b 100%);
}

.content {
  text-align: center;
}

.error-code {
  font-size: 120px;
  font-weight: 700;
  background: linear-gradient(135deg, #06b6d4 0%, #3b82f6 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  line-height: 1;
  margin-bottom: 20px;
}

.error-desc {
  font-size: 28px;
  font-weight: 600;
  color: #f1f5f9;
  margin-bottom: 12px;
}

.error-tip {
  font-size: 14px;
  color: #94a3b8;
  margin-bottom: 32px;
}
</style>
