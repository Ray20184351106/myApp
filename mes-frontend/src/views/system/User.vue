<template>
  <div class="page-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">用户管理</h1>
        <p class="page-desc">管理系统用户账户、权限及状态</p>
      </div>
      <div class="header-actions">
        <el-button type="primary" @click="handleAdd" class="add-btn" v-permission="'system:user:add'">
          <el-icon><Plus /></el-icon>
          新增用户
        </el-button>
      </div>
    </div>

    <!-- 搜索过滤区 -->
    <div class="filter-section">
      <div class="filter-form">
        <div class="filter-item">
          <label>用户名</label>
          <el-input
            v-model="queryForm.username"
            placeholder="请输入用户名"
            clearable
            @keyup.enter="handleQuery"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </div>
      </div>
      <div class="filter-actions">
        <el-button type="primary" @click="handleQuery">
          <el-icon><Search /></el-icon>
          搜索
        </el-button>
        <el-button @click="handleReset">
          <el-icon><Refresh /></el-icon>
          重置
        </el-button>
      </div>
    </div>

    <!-- 数据表格 -->
    <div class="table-section">
      <el-table
        :data="tableData"
        v-loading="loading"
        class="data-table"
      >
        <el-table-column type="index" label="#" width="60" align="center" />
        <el-table-column label="用户信息" min-width="200">
          <template #default="{ row }">
            <div class="user-cell">
              <el-avatar :size="40" class="user-avatar">
                {{ row.nickname?.charAt(0) || row.username?.charAt(0) }}
              </el-avatar>
              <div class="user-info">
                <span class="user-name">{{ row.username }}</span>
                <span class="user-nickname">{{ row.nickname }}</span>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="角色" min-width="140">
          <template #default="{ row }">
            <div class="role-tags" v-if="row.roles && row.roles.length > 0">
              <el-tag
                v-for="role in row.roles.slice(0, 2)"
                :key="role.id"
                size="small"
                class="role-tag"
              >
                {{ role.roleName }}
              </el-tag>
              <el-tag v-if="row.roles.length > 2" size="small" type="info" class="role-tag">
                +{{ row.roles.length - 2 }}
              </el-tag>
            </div>
            <span v-else class="text-muted">未分配</span>
          </template>
        </el-table-column>
        <el-table-column prop="email" label="邮箱" min-width="160">
          <template #default="{ row }">
            <span class="text-muted">{{ row.email || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="手机号" min-width="120">
          <template #default="{ row }">
            <span class="text-muted">{{ row.phone || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="部门" min-width="120">
          <template #default="{ row }">
            <span v-if="row.deptName" class="dept-tag">{{ row.deptName }}</span>
            <span v-else class="text-muted">未分配</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <span class="status-badge" :class="row.status === 1 ? 'success' : 'danger'">
              <span class="status-dot"></span>
              {{ row.status === 1 ? '正常' : '禁用' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160">
          <template #default="{ row }">
            <span class="text-muted">{{ formatTime(row.createTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right" align="center">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button type="primary" text size="small" @click="handleEdit(row)" title="编辑" v-permission="'system:user:edit'">
                <el-icon><Edit /></el-icon>
              </el-button>
              <el-button type="success" text size="small" @click="handleAssignRole(row)" title="分配角色" v-permission="'system:user:edit'">
                <el-icon><UserFilled /></el-icon>
              </el-button>
              <el-button
                :type="row.status === 1 ? 'warning' : 'success'"
                text
                size="small"
                @click="handleToggleStatus(row)"
                :title="row.status === 1 ? '禁用' : '启用'"
                v-permission="'system:user:edit'"
              >
                <el-icon>
                  <CircleClose v-if="row.status === 1" />
                  <CircleCheck v-else />
                </el-icon>
              </el-button>
              <el-button type="danger" text size="small" @click="handleDelete(row)" title="删除" v-permission="'system:user:remove'">
                <el-icon><Delete /></el-icon>
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-section">
        <span class="total-count">共 {{ total }} 条记录</span>
        <el-pagination
          v-model:current-page="queryForm.pageNum"
          v-model:page-size="queryForm.pageSize"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="sizes, prev, pager, next, jumper"
          @size-change="fetchData"
          @current-change="fetchData"
        />
      </div>
    </div>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="520px"
      :close-on-click-modal="false"
      class="form-dialog"
    >
      <el-form :model="formData" :rules="rules" ref="formRef" label-position="top" class="form-content">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="用户名" prop="username">
              <el-input v-model="formData.username" placeholder="请输入用户名" :disabled="isEdit" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="昵称" prop="nickname">
              <el-input v-model="formData.nickname" placeholder="请输入昵称" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="密码" :prop="isEdit ? '' : 'password'">
          <el-input v-model="formData.password" type="password" placeholder="请输入密码" show-password />
          <div v-if="isEdit" class="field-tip">留空则不修改密码</div>
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="formData.email" placeholder="请输入邮箱" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="formData.phone" placeholder="请输入手机号" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="所属部门" prop="deptId">
              <el-tree-select
                v-model="formData.deptId"
                :data="deptOptions"
                :props="{ label: 'deptName', value: 'id', children: 'children' }"
                placeholder="请选择部门"
                check-strictly
                clearable
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="formData.status">
                <el-radio :value="1">正常</el-radio>
                <el-radio :value="0">禁用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="formData.remark" type="textarea" :rows="3" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">
          {{ isEdit ? '保存修改' : '确认新增' }}
        </el-button>
      </template>
    </el-dialog>

    <!-- 角色分配对话框 -->
    <el-dialog
      v-model="roleDialogVisible"
      title="分配角色"
      width="480px"
      :close-on-click-modal="false"
      class="form-dialog"
    >
      <div class="role-header">
        <el-icon class="role-icon"><UserFilled /></el-icon>
        <span>为用户 <strong>{{ currentUserName }}</strong> 分配角色</span>
      </div>
      <div class="role-checkbox-group">
        <el-checkbox-group v-model="selectedRoleIds">
          <div
            v-for="role in allRoles"
            :key="role.id"
            class="role-checkbox-item"
            :class="{ checked: selectedRoleIds.includes(role.id) }"
          >
            <el-checkbox :value="role.id">
              <div class="role-info">
                <span class="role-name">{{ role.roleName }}</span>
                <span class="role-key">{{ role.roleKey }}</span>
              </div>
            </el-checkbox>
          </div>
        </el-checkbox-group>
      </div>
      <template #footer>
        <el-button @click="roleDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSaveRoles" :loading="roleSubmitLoading">
          保存
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Refresh, Edit, Delete, CircleClose, CircleCheck, UserFilled } from '@element-plus/icons-vue'
import request from '../../utils/request'

const loading = ref(false)
const submitLoading = ref(false)
const roleSubmitLoading = ref(false)
const dialogVisible = ref(false)
const roleDialogVisible = ref(false)
const formRef = ref(null)
const tableData = ref([])
const total = ref(0)
const allRoles = ref([])
const deptOptions = ref([])
const currentUserId = ref(null)
const currentUserName = ref('')
const selectedRoleIds = ref([])

const queryForm = reactive({
  username: '',
  pageNum: 1,
  pageSize: 10
})

const formData = reactive({
  id: null,
  username: '',
  nickname: '',
  password: '',
  email: '',
  phone: '',
  deptId: null,
  status: 1,
  remark: ''
})

const isEdit = computed(() => !!formData.id)
const dialogTitle = computed(() => isEdit.value ? '编辑用户' : '新增用户')

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ]
}

const formatTime = (time) => {
  if (!time) return '-'
  return new Date(time).toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await request.get('/system/user/list', { params: queryForm })
    if (res.code === 200) {
      // 获取每个用户的角色信息
      const users = res.data.list || []
      for (const user of users) {
        try {
          const roleRes = await request.get(`/system/user/${user.id}/roles`)
          if (roleRes.code === 200 && roleRes.data) {
            user.roles = roleRes.data.map(roleId => {
              return allRoles.value.find(r => r.id === roleId) || { id: roleId, roleName: '未知角色' }
            })
          }
        } catch {
          user.roles = []
        }
      }
      tableData.value = users
      total.value = res.data.total
    }
  } catch (error) {
    ElMessage.error('获取用户列表失败')
  } finally {
    loading.value = false
  }
}

const fetchAllRoles = async () => {
  try {
    const res = await request.get('/system/role/all')
    if (res.code === 200) {
      allRoles.value = res.data || []
    }
  } catch (error) {
    console.error('获取角色列表失败')
  }
}

// 获取部门树
const fetchDeptTree = async () => {
  try {
    const res = await request.get('/system/dept/tree')
    if (res.code === 200) {
      deptOptions.value = res.data || []
    }
  } catch (error) {
    console.error('获取部门列表失败')
  }
}

const handleQuery = () => {
  queryForm.pageNum = 1
  fetchData()
}

const handleReset = () => {
  queryForm.username = ''
  queryForm.pageNum = 1
  fetchData()
}

const handleAdd = () => {
  Object.assign(formData, {
    id: null,
    username: '',
    nickname: '',
    password: '',
    email: '',
    phone: '',
    deptId: null,
    status: 1,
    remark: ''
  })
  dialogVisible.value = true
}

const handleEdit = async (row) => {
  try {
    const res = await request.get(`/system/user/${row.id}`)
    if (res.code === 200) {
      Object.assign(formData, res.data)
      formData.password = ''
      dialogVisible.value = true
    }
  } catch (error) {
    ElMessage.error('获取用户信息失败')
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该用户吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await request.delete(`/system/user/${row.id}`)
      if (res.code === 200) {
        ElMessage.success('删除成功')
        fetchData()
      }
    } catch (error) {
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

const handleToggleStatus = async (row) => {
  const newStatus = row.status === 1 ? 0 : 1
  const statusText = newStatus === 1 ? '启用' : '禁用'
  try {
    const res = await request.put('/system/user', {
      ...row,
      status: newStatus
    })
    if (res.code === 200) {
      ElMessage.success(`${statusText}成功`)
      fetchData()
    }
  } catch (error) {
    ElMessage.error(`${statusText}失败`)
  }
}

// 分配角色
const handleAssignRole = async (row) => {
  currentUserId.value = row.id
  currentUserName.value = row.nickname || row.username

  // 获取用户当前角色
  try {
    const res = await request.get(`/system/user/${row.id}/roles`)
    if (res.code === 200) {
      selectedRoleIds.value = res.data || []
      roleDialogVisible.value = true
    }
  } catch (error) {
    ElMessage.error('获取用户角色失败')
  }
}

// 保存角色分配
const handleSaveRoles = async () => {
  roleSubmitLoading.value = true
  try {
    const res = await request.put('/system/user/roles', {
      userId: currentUserId.value,
      roleIds: selectedRoleIds.value
    })
    if (res.code === 200) {
      ElMessage.success('角色分配成功')
      roleDialogVisible.value = false
      fetchData()
    }
  } catch (error) {
    ElMessage.error('角色分配失败')
  } finally {
    roleSubmitLoading.value = false
  }
}

const handleSubmit = async () => {
  await formRef.value.validate()
  submitLoading.value = true
  try {
    const data = { ...formData }
    if (isEdit.value && !data.password) {
      delete data.password
    }
    const res = isEdit.value
      ? await request.put('/system/user', data)
      : await request.post('/system/user', data)
    if (res.code === 200) {
      ElMessage.success(isEdit.value ? '修改成功' : '新增成功')
      dialogVisible.value = false
      fetchData()
    }
  } catch (error) {
    ElMessage.error(isEdit.value ? '修改失败' : '新增失败')
  } finally {
    submitLoading.value = false
  }
}

onMounted(() => {
  fetchAllRoles()
  fetchDeptTree()
  fetchData()
})
</script>

<style scoped>
/* ===== 页面容器 ===== */
.page-container {
  padding: 0;
}

/* ===== 页面标题 ===== */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.page-title {
  font-size: 24px;
  font-weight: 700;
  color: var(--mes-text-primary);
  margin: 0 0 4px 0;
}

.page-desc {
  font-size: 14px;
  color: var(--mes-text-secondary);
  margin: 0;
}

.add-btn {
  height: 40px;
  padding: 0 20px;
  border-radius: 8px;
  font-weight: 500;
}

/* ===== 搜索过滤区 ===== */
.filter-section {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  gap: 20px;
  margin-bottom: 20px;
  padding: 20px;
  background: var(--mes-bg-primary);
  border-radius: 12px;
  border: 1px solid var(--mes-border);
}

.filter-form {
  display: flex;
  gap: 20px;
  flex-wrap: wrap;
}

.filter-item {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.filter-item label {
  font-size: 13px;
  font-weight: 500;
  color: var(--mes-text-secondary);
}

.filter-item .el-input {
  width: 240px;
}

.filter-actions {
  display: flex;
  gap: 10px;
}

/* ===== 表格区域 ===== */
.table-section {
  background: var(--mes-bg-primary);
  border-radius: 12px;
  border: 1px solid var(--mes-border);
  overflow: hidden;
}

.data-table {
  --el-table-border-color: var(--mes-border);
}

/* 用户信息单元格 */
.user-cell {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-avatar {
  background: linear-gradient(135deg, var(--mes-primary) 0%, var(--mes-accent) 100%);
  color: #fff;
  font-weight: 600;
  flex-shrink: 0;
}

.user-info {
  display: flex;
  flex-direction: column;
}

.user-name {
  font-weight: 600;
  color: var(--mes-text-primary);
}

.user-nickname {
  font-size: 12px;
  color: var(--mes-text-secondary);
}

/* 角色标签 */
.role-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
}

.role-tag {
  border-radius: 12px;
}

.text-muted {
  color: var(--mes-text-secondary);
}

/* 部门标签 */
.dept-tag {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 2px 10px;
  background: rgba(26, 86, 219, 0.1);
  color: var(--mes-primary);
  border-radius: 4px;
  font-size: 12px;
}

/* 状态徽章 */
.status-badge {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
}

.status-badge.success {
  background: rgba(5, 150, 105, 0.1);
  color: var(--mes-success);
}

.status-badge.danger {
  background: rgba(220, 38, 38, 0.1);
  color: var(--mes-danger);
}

.status-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: currentColor;
}

/* 操作按钮 */
.action-buttons {
  display: flex;
  justify-content: center;
  gap: 4px;
}

.action-buttons .el-button {
  padding: 6px 8px;
}

/* ===== 分页 ===== */
.pagination-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-top: 1px solid var(--mes-border);
}

.total-count {
  font-size: 13px;
  color: var(--mes-text-secondary);
}

/* ===== 对话框 ===== */
.form-dialog :deep(.el-dialog__header) {
  padding: 20px 24px 0;
  border-bottom: none;
}

.form-dialog :deep(.el-dialog__body) {
  padding: 20px 24px;
}

.form-dialog :deep(.el-dialog__footer) {
  padding: 0 24px 20px;
  border-top: none;
}

.form-content :deep(.el-form-item__label) {
  font-weight: 500;
  color: var(--mes-text-primary);
}

.field-tip {
  font-size: 12px;
  color: var(--mes-text-muted);
  margin-top: 4px;
}

/* 角色分配对话框 */
.role-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 16px;
  padding: 12px 16px;
  background: var(--mes-bg-secondary);
  border-radius: 8px;
}

.role-icon {
  color: var(--mes-primary);
  font-size: 20px;
}

.role-header span {
  font-size: 14px;
  color: var(--mes-text-secondary);
}

.role-header strong {
  color: var(--mes-primary);
}

.role-checkbox-group {
  max-height: 320px;
  overflow-y: auto;
}

.role-checkbox-item {
  padding: 12px 16px;
  margin-bottom: 8px;
  border: 1px solid var(--mes-border);
  border-radius: 8px;
  transition: all 0.2s ease;
  cursor: pointer;
}

.role-checkbox-item:hover {
  border-color: var(--mes-primary);
  background: var(--mes-bg-secondary);
}

.role-checkbox-item.checked {
  border-color: var(--mes-primary);
  background: rgba(26, 86, 219, 0.05);
}

.role-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.role-name {
  font-weight: 500;
  color: var(--mes-text-primary);
}

.role-key {
  font-size: 12px;
  color: var(--mes-text-muted);
  font-family: 'JetBrains Mono', monospace;
}

/* ===== 响应式 ===== */
@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }

  .filter-section {
    flex-direction: column;
    align-items: stretch;
  }

  .filter-form {
    flex-direction: column;
  }

  .filter-item .el-input {
    width: 100%;
  }

  .pagination-section {
    flex-direction: column;
    gap: 12px;
  }
}
</style>