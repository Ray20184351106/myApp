<template>
  <div class="layout">
    <el-container>
      <el-aside width="200px">
        <div class="logo">MES</div>
        <el-menu :default-active="activeMenu" router>
          <el-menu-item index="/dashboard">
            <span>首页</span>
          </el-menu-item>
          <el-sub-menu index="system">
            <template #title>
              <span>系统管理</span>
            </template>
            <el-menu-item index="/system/user">用户管理</el-menu-item>
          </el-sub-menu>
        </el-menu>
      </el-aside>
      <el-container>
        <el-header>
          <div class="header-right">
            <el-dropdown @command="handleCommand">
              <span class="el-dropdown-link">
                管理员 <el-icon><ArrowDown /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="logout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </el-header>
        <el-main>
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

const activeMenu = computed(() => route.path)

const handleCommand = (command) => {
  if (command === 'logout') {
    localStorage.removeItem('token')
    router.push('/login')
  }
}
</script>

<style scoped>
.layout {
  height: 100vh;
}
.el-container {
  height: 100%;
}
.el-aside {
  background: #304156;
  color: #fff;
}
.logo {
  height: 60px;
  line-height: 60px;
  text-align: center;
  font-size: 24px;
  font-weight: bold;
  color: #fff;
  background: #263445;
}
.el-menu {
  border-right: none;
  background: #304156;
}
.el-header {
  background: #fff;
  box-shadow: 0 1px 4px rgba(0,21,41,.08);
  display: flex;
  align-items: center;
  justify-content: flex-end;
  padding: 0 20px;
}
.el-main {
  background: #f0f2f5;
}
</style>
