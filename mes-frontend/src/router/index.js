import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '../stores/user'

// 静态路由（无需权限）
export const constantRoutes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/404',
    name: 'NotFound',
    component: () => import('../views/404.vue'),
    meta: { title: '页面不存在' }
  }
]

// 动态路由（需要权限）
export const dynamicRoutes = [
  {
    path: '/',
    name: 'Layout',
    component: () => import('../layout/index.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('../views/Dashboard.vue'),
        meta: { title: '控制台', icon: 'Odometer' }
      },
      {
        path: 'system',
        name: 'System',
        redirect: '/system/user',
        meta: { title: '系统管理', icon: 'Setting' },
        children: [
          {
            path: 'user',
            name: 'SystemUser',
            component: () => import('../views/system/User.vue'),
            meta: { title: '用户管理', icon: 'User', perms: 'system:user:list' }
          },
          {
            path: 'role',
            name: 'SystemRole',
            component: () => import('../views/system/Role.vue'),
            meta: { title: '角色管理', icon: 'UserFilled', perms: 'system:role:list' }
          },
          {
            path: 'menu',
            name: 'SystemMenu',
            component: () => import('../views/system/Menu.vue'),
            meta: { title: '菜单管理', icon: 'Menu', perms: 'system:menu:list' }
          }
        ]
      }
    ]
  },
  // 404 页面必须放在最后
  {
    path: '/:pathMatch(.*)*',
    redirect: '/404'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes: constantRoutes
})

// 路由守卫
router.beforeEach(async (to, from, next) => {
  document.title = to.meta.title || 'MES 制造执行系统'

  const userStore = useUserStore()
  const token = userStore.token || localStorage.getItem('token')

  if (token) {
    if (to.path === '/login') {
      // 已登录，跳转到首页
      next({ path: '/' })
    } else {
      // 检查是否已获取用户信息
      if (userStore.userInfo) {
        next()
      } else {
        try {
          // 获取用户信息和菜单
          await userStore.getUserInfo()

          // 动态添加路由
          dynamicRoutes.forEach(route => {
            router.addRoute(route)
          })

          // 重新导航到目标路由
          next({ ...to, replace: true })
        } catch (error) {
          // 获取用户信息失败，清除token并跳转登录页
          userStore.clearToken()
          next(`/login?redirect=${to.path}`)
        }
      }
    }
  } else {
    // 未登录
    if (to.path === '/login') {
      next()
    } else {
      next(`/login?redirect=${to.path}`)
    }
  }
})

export default router
