import request from '../utils/request'

// 菜单列表（树形）
export function listMenu(params) {
  return request.get('/system/menu/list', { params })
}

// 菜单详情
export function getMenu(id) {
  return request.get(`/system/menu/${id}`)
}

// 新增菜单
export function addMenu(data) {
  return request.post('/system/menu', data)
}

// 修改菜单
export function updateMenu(data) {
  return request.put('/system/menu', data)
}

// 删除菜单
export function deleteMenu(id) {
  return request.delete(`/system/menu/${id}`)
}

// 菜单树（角色授权用）
export function getMenuTree() {
  return request.get('/system/menu/tree')
}
