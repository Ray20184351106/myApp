<template>
  <div class="page-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">部门管理</h1>
        <p class="page-desc">配置组织架构与部门层级结构</p>
      </div>
      <div class="header-actions">
        <el-button @click="toggleExpandAll" class="action-btn">
          <el-icon><Sort /></el-icon>
          {{ isExpandAll ? '折叠全部' : '展开全部' }}
        </el-button>
        <el-button type="primary" @click="handleAdd(null)" class="add-btn" v-permission="'system:dept:add'">
          <el-icon><Plus /></el-icon>
          新增部门
        </el-button>
      </div>
    </div>

    <!-- 搜索区域 -->
    <div class="search-section">
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="部门名称">
          <el-input v-model="queryParams.deptName" placeholder="请输入部门名称" clearable @keyup.enter="fetchData" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="全部" clearable style="width: 120px">
            <el-option :value="1" label="正常" />
            <el-option :value="0" label="禁用" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="fetchData">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="resetQuery">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 数据表格 -->
    <div class="table-section">
      <el-table
        ref="tableRef"
        :data="tableData"
        v-loading="loading"
        row-key="id"
        :default-expand-all="isExpandAll"
        :tree-props="{ children: 'children' }"
        class="data-table dept-table"
      >
        <el-table-column label="部门名称" min-width="200">
          <template #default="{ row }">
            <div class="dept-cell">
              <span class="dept-icon">
                <el-icon><OfficeBuilding /></el-icon>
              </span>
              <span class="dept-name">{{ row.deptName }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="deptCode" label="部门编码" width="120" align="center">
          <template #default="{ row }">
            <code class="code-badge">{{ row.deptCode }}</code>
          </template>
        </el-table-column>
        <el-table-column prop="sort" label="排序" width="80" align="center">
          <template #default="{ row }">
            <span class="sort-num">{{ row.sort }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="leader" label="负责人" width="120" align="center">
          <template #default="{ row }">
            <span v-if="row.leader">{{ row.leader }}</span>
            <span v-else class="text-muted">-</span>
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="联系电话" width="140" align="center">
          <template #default="{ row }">
            <span v-if="row.phone">{{ row.phone }}</span>
            <span v-else class="text-muted">-</span>
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
        <el-table-column label="操作" width="180" fixed="right" align="center">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button type="primary" text size="small" @click="handleEdit(row)" v-permission="'system:dept:edit'">
                <el-icon><Edit /></el-icon>
              </el-button>
              <el-button type="success" text size="small" @click="handleAdd(row)" v-permission="'system:dept:add'">
                <el-icon><Plus /></el-icon>
              </el-button>
              <el-button type="danger" text size="small" @click="handleDelete(row)" v-permission="'system:dept:remove'">
                <el-icon><Delete /></el-icon>
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="560px"
      :close-on-click-modal="false"
      class="form-dialog"
    >
      <el-form :model="formData" :rules="rules" ref="formRef" label-position="top" class="form-content">
        <el-form-item label="上级部门" prop="parentId">
          <el-tree-select
            v-model="formData.parentId"
            :data="deptOptions"
            :props="{ label: 'deptName', value: 'id', children: 'children' }"
            placeholder="请选择上级部门"
            check-strictly
            clearable
            style="width: 100%"
          />
        </el-form-item>

        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="部门名称" prop="deptName">
              <el-input v-model="formData.deptName" placeholder="请输入部门名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="部门编码" prop="deptCode">
              <el-input v-model="formData.deptCode" placeholder="请输入部门编码" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="负责人" prop="leader">
              <el-input v-model="formData.leader" placeholder="请输入负责人" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="排序" prop="sort">
              <el-input-number v-model="formData.sort" :min="0" :max="999" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="联系电话" prop="phone">
              <el-input v-model="formData.phone" placeholder="请输入联系电话" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="formData.email" placeholder="请输入邮箱" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio :value="1">正常</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="备注" prop="remark">
          <el-input v-model="formData.remark" type="textarea" :rows="2" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">
          {{ isEdit ? '保存修改' : '确认新增' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Edit, Delete, OfficeBuilding, Sort, Search, Refresh } from '@element-plus/icons-vue'
import { listDept, getDept, addDept, updateDept, deleteDept, listDeptTree } from '../../api/dept'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const formRef = ref(null)
const tableRef = ref(null)
const tableData = ref([])
const deptOptions = ref([])
const isExpandAll = ref(true)

const queryParams = reactive({
  deptName: '',
  status: null
})

const formData = reactive({
  id: null,
  parentId: 0,
  deptName: '',
  deptCode: '',
  sort: 0,
  leader: '',
  phone: '',
  email: '',
  status: 1,
  remark: ''
})

const isEdit = computed(() => !!formData.id)
const dialogTitle = computed(() => isEdit.value ? '编辑部门' : '新增部门')

const rules = {
  deptName: [
    { required: true, message: '请输入部门名称', trigger: 'blur' }
  ],
  deptCode: [
    { required: true, message: '请输入部门编码', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ]
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await listDept(queryParams)
    if (res.code === 200) {
      tableData.value = res.data || []
    }
  } catch (error) {
    ElMessage.error('获取部门列表失败')
  } finally {
    loading.value = false
  }
}

const fetchDeptOptions = async () => {
  try {
    const res = await listDeptTree()
    if (res.code === 200) {
      deptOptions.value = [
        { id: 0, deptName: '根部门', children: res.data || [] }
      ]
    }
  } catch (error) {
    console.error('获取部门选项失败')
  }
}

const toggleExpandAll = () => {
  isExpandAll.value = !isExpandAll.value
  fetchData()
}

const resetQuery = () => {
  queryParams.deptName = ''
  queryParams.status = null
  fetchData()
}

const handleAdd = (parent) => {
  fetchDeptOptions()
  Object.assign(formData, {
    id: null,
    parentId: parent ? parent.id : 0,
    deptName: '',
    deptCode: '',
    sort: 0,
    leader: '',
    phone: '',
    email: '',
    status: 1,
    remark: ''
  })
  dialogVisible.value = true
}

const handleEdit = async (row) => {
  fetchDeptOptions()
  try {
    const res = await getDept(row.id)
    if (res.code === 200) {
      Object.assign(formData, res.data)
      dialogVisible.value = true
    }
  } catch (error) {
    ElMessage.error('获取部门信息失败')
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该部门吗？删除前会检查是否存在子部门或关联用户。', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await deleteDept(row.id)
      if (res.code === 200) {
        ElMessage.success('删除成功')
        fetchData()
      } else {
        ElMessage.error(res.msg || '删除失败')
      }
    } catch (error) {
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

const handleSubmit = async () => {
  await formRef.value.validate()
  submitLoading.value = true
  try {
    const res = isEdit.value
      ? await updateDept(formData)
      : await addDept(formData)
    if (res.code === 200) {
      ElMessage.success(isEdit.value ? '修改成功' : '新增成功')
      dialogVisible.value = false
      fetchData()
    } else {
      ElMessage.error(res.msg || (isEdit.value ? '修改失败' : '新增失败'))
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

.header-actions {
  display: flex;
  gap: 12px;
}

.action-btn {
  height: 40px;
  padding: 0 16px;
  border-radius: 8px;
}

.add-btn {
  height: 40px;
  padding: 0 20px;
  border-radius: 8px;
  font-weight: 500;
}

/* ===== 搜索区域 ===== */
.search-section {
  background: var(--mes-bg-primary);
  border-radius: 12px;
  border: 1px solid var(--mes-border);
  padding: 16px 20px;
  margin-bottom: 16px;
}

.search-form :deep(.el-form-item) {
  margin-bottom: 0;
}

/* ===== 表格区域 ===== */
.table-section {
  background: var(--mes-bg-primary);
  border-radius: 12px;
  border: 1px solid var(--mes-border);
  overflow: hidden;
}

.dept-table {
  --el-table-border-color: var(--mes-border);
}

/* 部门单元格 */
.dept-cell {
  display: flex;
  align-items: center;
  gap: 12px;
}

.dept-icon {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  background: rgba(26, 86, 219, 0.1);
  color: var(--mes-primary);
}

.dept-name {
  font-weight: 500;
  color: var(--mes-text-primary);
}

.code-badge {
  padding: 4px 10px;
  background: var(--mes-bg-tertiary);
  border-radius: 6px;
  font-size: 12px;
  color: var(--mes-text-secondary);
  font-family: 'JetBrains Mono', monospace;
}

.sort-num {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 28px;
  height: 24px;
  background: var(--mes-bg-tertiary);
  border-radius: 6px;
  font-size: 13px;
  font-weight: 500;
  color: var(--mes-text-secondary);
}

.text-muted {
  color: var(--mes-text-muted);
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

/* ===== 响应式 ===== */
@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }

  .header-actions {
    width: 100%;
  }

  .header-actions .el-button {
    flex: 1;
  }
}
</style>
