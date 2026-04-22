import { useUserStore } from '../stores/user'

/**
 * 检查用户是否具有指定权限
 * @param {string|string[]} permission 权限标识
 * @returns {boolean}
 */
export function hasPermission(permission) {
  const userStore = useUserStore()
  const permissions = userStore.permissions || []

  // 超级管理员拥有所有权限
  if (permissions.includes('*:*:*')) {
    return true
  }

  if (Array.isArray(permission)) {
    return permission.some(p => permissions.includes(p))
  }

  return permissions.includes(permission)
}

/**
 * 检查用户是否具有指定角色
 * @param {string|string[]} role 角色标识
 * @returns {boolean}
 */
export function hasRole(role) {
  const userStore = useUserStore()
  const roles = userStore.roles || []

  // 超级管理员角色
  if (roles.includes('admin') || roles.includes('superadmin')) {
    return true
  }

  if (Array.isArray(role)) {
    return role.some(r => roles.includes(r))
  }

  return roles.includes(role)
}
