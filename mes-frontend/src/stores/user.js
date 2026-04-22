import { defineStore } from 'pinia'
import { ref } from 'vue'
import request from '../utils/request'

export const useUserStore = defineStore('user', () => {
  // 状态
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(null)
  const roles = ref(JSON.parse(localStorage.getItem('roles') || '[]'))
  const permissions = ref(JSON.parse(localStorage.getItem('permissions') || '[]'))
  const menus = ref(JSON.parse(localStorage.getItem('menus') || '[]'))

  // 设置Token
  const setToken = (newToken) => {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }

  // 清除Token
  const clearToken = () => {
    token.value = ''
    localStorage.removeItem('token')
  }

  // 登录
  const login = async (loginForm) => {
    const res = await request.post('/auth/login', loginForm)
    if (res.code === 200) {
      setToken(res.data)
    }
    return res
  }

  // 获取用户信息
  const getUserInfo = async () => {
    try {
      const res = await request.get('/auth/getInfo')
      if (res.code === 200) {
        userInfo.value = res.data.user
        roles.value = Array.from(res.data.roles || [])
        permissions.value = Array.from(res.data.permissions || [])

        // 持久化存储
        localStorage.setItem('roles', JSON.stringify(roles.value))
        localStorage.setItem('permissions', JSON.stringify(permissions.value))
      }
      return res
    } catch (error) {
      return null
    }
  }

  // 获取用户菜单
  const getMenus = async () => {
    try {
      const res = await request.get('/auth/getRouters')
      if (res.code === 200) {
        menus.value = res.data || []
        localStorage.setItem('menus', JSON.stringify(menus.value))
      }
      return res
    } catch (error) {
      return null
    }
  }

  // 登出
  const logout = async () => {
    try {
      await request.post('/auth/logout')
    } catch (error) {
      // ignore
    } finally {
      clearToken()
      userInfo.value = null
      roles.value = []
      permissions.value = []
      menus.value = []
      localStorage.removeItem('roles')
      localStorage.removeItem('permissions')
      localStorage.removeItem('menus')
    }
  }

  return {
    token,
    userInfo,
    roles,
    permissions,
    menus,
    setToken,
    clearToken,
    login,
    getUserInfo,
    getMenus,
    logout
  }
})
