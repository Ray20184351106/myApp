import request from '../utils/request'

// 角色列表
export function listRole(params) {
  return request.get('/system/role/list', { params })
}

// 角色详情
export function getRole(id) {
  return request.get(`/system/role/${id}`)
}

// 新增角色
export function addRole(data) {
  return request.post('/system/role', data)
}

// 修改角色
export function updateRole(data) {
  return request.put('/system/role', data)
}

// 删除角色
export function deleteRole(id) {
  return request.delete(`/system/role/${id}`)
}

// 获取所有角色（下拉选择用）
export function listAllRole() {
  return request.get('/system/role/all')
}

// 分配菜单权限
export function assignMenu(roleId, menuIds) {
  return request.put('/system/role/menu', { roleId, menuIds })
}

// 获取角色已分配的菜单ID
export function getRoleMenuIds(roleId) {
  return request.get(`/system/role/${roleId}/menu`)
}
