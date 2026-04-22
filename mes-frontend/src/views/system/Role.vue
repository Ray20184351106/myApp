<template>
  <div class="page-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">角色管理</h1>
        <p class="page-desc">管理系统角色及菜单权限分配</p>
      </div>
      <div class="header-actions">
        <el-button type="primary" @click="handleAdd" class="add-btn" v-permission="'system:role:add'">
          <el-icon><Plus /></el-icon>
          新增角色
        </el-button>
      </div>
    </div>

    <!-- 搜索过滤区 -->
    <div class="filter-section">
      <div class="filter-form">
        <div class="filter-item">
          <label>角色名称</label>
          <el-input
            v-model="queryForm.roleName"
            placeholder="请输入角色名称"
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
      <el-table :data="tableData" v-loading="loading" class="data-table">
        <el-table-column type="index" label="#" width="60" align="center" />
        <el-table-column prop="roleName" label="角色名称" min-width="140">
          <template #default="{ row }">
            <div class="role-cell">
              <span class="role-icon" :style="{ background: getRoleColor(row.roleKey) }">
                {{ row.roleName?.charAt(0) }}
              </span>
              <span class="role-name">{{ row.roleName }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="roleKey" label="角色标识" min-width="120">
          <template #default="{ row }">
            <code class="role-key">{{ row.roleKey }}</code>
          </template>
        </el-table-column>
        <el-table-column prop="sort" label="排序" width="80" align="center" />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <span class="status-badge" :class="row.status === 1 ? 'success' : 'danger'">
              <span class="status-dot"></span>
              {{ row.status === 1 ? '正常' : '禁用' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="170">
          <template #default="{ row }">
            <span class="text-muted">{{ formatTime(row.createTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right" align="center">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button type="primary" text size="small" @click="handleEdit(row)" v-permission="'system:role:edit'">
                <el-icon><Edit /></el-icon>
              </el-button>
              <el-button type="success" text size="small" @click="handleAssignMenu(row)" v-permission="'system:role:edit'">
                <el-icon><Key /></el-icon>
              </el-button>
              <el-button type="danger" text size="small" @click="handleDelete(row)" v-permission="'system:role:remove'">
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
      width="480px"
      :close-on-click-modal="false"
      class="form-dialog"
    >
      <el-form :model="formData" :rules="rules" ref="formRef" label-position="top" class="form-content">
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="formData.roleName" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="角色标识" prop="roleKey">
          <el-input v-model="formData.roleKey" placeholder="请输入角色标识" />
          <div class="field-tip">角色标识用于权限验证，建议使用英文</div>
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="排序" prop="sort">
              <el-input-number v-model="formData.sort" :min="0" :max="999" style="width: 100%" />
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

    <!-- 菜单授权对话框 -->
    <el-dialog
      v-model="menuDialogVisible"
      title="菜单权限配置"
      width="480px"
      :close-on-click-modal="false"
      class="form-dialog"
    >
      <div class="auth-header">
        <el-icon class="auth-icon"><Key /></el-icon>
        <span>请选择该角色可访问的菜单</span>
      </div>
      <el-tree
        ref="menuTreeRef"
        :data="menuTree"
        :props="{ label: 'menuName', children: 'children' }"
        show-checkbox
        node-key="id"
        default-expand-all
        :check-strictly="false"
        class="menu-tree"
      />
      <template #footer>
        <el-button @click="menuDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSaveMenu" :loading="menuSubmitLoading">
          保存权限
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Refresh, Edit, Delete, Key } from '@element-plus/icons-vue'
import { listRole, getRole, addRole, updateRole, deleteRole, assignMenu, getRoleMenuIds } from '../../api/role'
import { getMenuTree } from '../../api/menu'

const loading = ref(false)
const submitLoading = ref(false)
const menuSubmitLoading = ref(false)
const dialogVisible = ref(false)
const menuDialogVisible = ref(false)
const formRef = ref(null)
const menuTreeRef = ref(null)
const tableData = ref([])
const total = ref(0)
const menuTree = ref([])
const currentRoleId = ref(null)

const queryForm = reactive({
  roleName: '',
  pageNum: 1,
  pageSize: 10
})

const formData = reactive({
  id: null,
  roleName: '',
  roleKey: '',
  sort: 0,
  status: 1,
  remark: ''
})

const isEdit = computed(() => !!formData.id)
const dialogTitle = computed(() => isEdit.value ? '编辑角色' : '新增角色')

const rules = {
  roleName: [
    { required: true, message: '请输入角色名称', trigger: 'blur' }
  ],
  roleKey: [
    { required: true, message: '请输入角色标识', trigger: 'blur' },
    { pattern: /^[a-zA-Z][a-zA-Z0-9_]*$/, message: '角色标识只能包含字母、数字和下划线，且以字母开头', trigger: 'blur' }
  ]
}

const roleColors = {
  admin: 'linear-gradient(135deg, #dc2626 0%, #f87171 100%)',
  production: 'linear-gradient(135deg, #059669 0%, #10b981 100%)',
  quality: 'linear-gradient(135deg, #7c3aed 0%, #a855f7 100%)',
  operator: 'linear-gradient(135deg, #1a56db 0%, #3b82f6 100%)'
}

const getRoleColor = (roleKey) => {
  return roleColors[roleKey] || 'linear-gradient(135deg, #64748b 0%, #94a3b8 100%)'
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
    const res = await listRole(queryForm)
    if (res.code === 200) {
      tableData.value = res.data.list
      total.value = res.data.total
    }
  } catch (error) {
    ElMessage.error('获取角色列表失败')
  } finally {
    loading.value = false
  }
}

const handleQuery = () => {
  queryForm.pageNum = 1
  fetchData()
}

const handleReset = () => {
  queryForm.roleName = ''
  queryForm.pageNum = 1
  fetchData()
}

const handleAdd = () => {
  Object.assign(formData, {
    id: null,
    roleName: '',
    roleKey: '',
    sort: 0,
    status: 1,
    remark: ''
  })
  dialogVisible.value = true
}

const handleEdit = async (row) => {
  try {
    const res = await getRole(row.id)
    if (res.code === 200) {
      Object.assign(formData, res.data)
      dialogVisible.value = true
    }
  } catch (error) {
    ElMessage.error('获取角色信息失败')
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该角色吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await deleteRole(row.id)
      if (res.code === 200) {
        ElMessage.success('删除成功')
        fetchData()
      }
    } catch (error) {
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

const handleAssignMenu = async (row) => {
  currentRoleId.value = row.id
  try {
    const menuRes = await getMenuTree()
    if (menuRes.code === 200) {
      menuTree.value = menuRes.data
    }
    const roleMenuRes = await getRoleMenuIds(row.id)
    if (roleMenuRes.code === 200) {
      menuDialogVisible.value = true
      setTimeout(() => {
        menuTreeRef.value?.setCheckedKeys(roleMenuRes.data || [])
      }, 100)
    }
  } catch (error) {
    ElMessage.error('获取菜单数据失败')
  }
}

const handleSaveMenu = async () => {
  menuSubmitLoading.value = true
  try {
    const menuIds = menuTreeRef.value?.getCheckedKeys() || []
    const res = await assignMenu(currentRoleId.value, menuIds)
    if (res.code === 200) {
      ElMessage.success('授权成功')
      menuDialogVisible.value = false
    }
  } catch (error) {
    ElMessage.error('授权失败')
  } finally {
    menuSubmitLoading.value = false
  }
}

const handleSubmit = async () => {
  await formRef.value.validate()
  submitLoading.value = true
  try {
    const res = isEdit.value
      ? await updateRole(formData)
      : await addRole(formData)
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

/* 角色单元格 */
.role-cell {
  display: flex;
  align-items: center;
  gap: 12px;
}

.role-icon {
  width: 36px;
  height: 36px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-weight: 600;
  font-size: 14px;
  flex-shrink: 0;
}

.role-name {
  font-weight: 500;
  color: var(--mes-text-primary);
}

.role-key {
  padding: 4px 10px;
  background: var(--mes-bg-tertiary);
  border-radius: 6px;
  font-size: 13px;
  color: var(--mes-text-secondary);
  font-family: 'JetBrains Mono', monospace;
}

.text-muted {
  color: var(--mes-text-secondary);
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

/* 授权对话框 */
.auth-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 16px;
  padding: 12px 16px;
  background: var(--mes-bg-secondary);
  border-radius: 8px;
}

.auth-icon {
  color: var(--mes-primary);
  font-size: 20px;
}

.auth-header span {
  font-size: 14px;
  color: var(--mes-text-secondary);
}

.menu-tree {
  border: 1px solid var(--mes-border);
  border-radius: 8px;
  padding: 12px;
  max-height: 400px;
  overflow-y: auto;
}

.menu-tree :deep(.el-tree-node__content) {
  height: 36px;
  border-radius: 6px;
}

.menu-tree :deep(.el-tree-node__content:hover) {
  background: var(--mes-bg-secondary);
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