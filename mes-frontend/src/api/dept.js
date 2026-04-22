import request from '../utils/request'

// 部门列表（树形）
export function listDept(params) {
  return request.get('/system/dept/list', { params })
}

// 部门树（下拉选择用）
export function listDeptTree() {
  return request.get('/system/dept/tree')
}

// 部门详情
export function getDept(id) {
  return request.get(`/system/dept/${id}`)
}

// 新增部门
export function addDept(data) {
  return request.post('/system/dept', data)
}

// 修改部门
export function updateDept(data) {
  return request.put('/system/dept', data)
}

// 删除部门
export function deleteDept(id) {
  return request.delete(`/system/dept/${id}`)
}