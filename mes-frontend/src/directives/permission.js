import { hasPermission } from '../utils/permission'

/**
 * 权限指令 v-permission
 * 使用方式：
 *   v-permission="'system:user:add'"  // 单个权限
 *   v-permission="['system:user:add', 'system:user:edit']"  // 多个权限（OR关系）
 */
export const permission = {
  mounted(el, binding) {
    const { value } = binding

    if (value) {
      const hasAuth = hasPermission(value)

      if (!hasAuth) {
        // 无权限时移除元素
        el.parentNode && el.parentNode.removeChild(el)
      }
    }
  }
}

/**
 * 角色指令 v-role
 * 使用方式：
 *   v-role="'admin'"  // 单个角色
 *   v-role="['admin', 'operator']"  // 多个角色（OR关系）
 */
export const role = {
  mounted(el, binding) {
    const { value } = binding
    const roles = JSON.parse(localStorage.getItem('roles') || '[]')

    // 超级管理员角色放行
    if (roles.includes('admin') || roles.includes('superadmin')) {
      return
    }

    if (value) {
      let hasRole = false
      if (Array.isArray(value)) {
        hasRole = value.some(r => roles.includes(r))
      } else {
        hasRole = roles.includes(value)
      }

      if (!hasRole) {
        el.parentNode && el.parentNode.removeChild(el)
      }
    }
  }
}

/**
 * 注册全局指令
 */
export function setupPermissionDirectives(app) {
  app.directive('permission', permission)
  app.directive('role', role)
}
