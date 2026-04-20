<template>
  <div class="page-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">菜单管理</h1>
        <p class="page-desc">配置系统菜单结构与权限标识</p>
      </div>
      <div class="header-actions">
        <el-button @click="toggleExpandAll" class="action-btn">
          <el-icon><Sort /></el-icon>
          {{ isExpandAll ? '折叠全部' : '展开全部' }}
        </el-button>
        <el-button type="primary" @click="handleAdd(null)" class="add-btn">
          <el-icon><Plus /></el-icon>
          新增菜单
        </el-button>
      </div>
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
        class="data-table menu-table"
      >
        <el-table-column label="菜单名称" min-width="220">
          <template #default="{ row }">
            <div class="menu-cell">
              <span class="menu-icon" :class="row.menuType.toLowerCase()">
                <el-icon v-if="row.icon"><component :is="row.icon" /></el-icon>
                <el-icon v-else><Menu /></el-icon>
              </span>
              <span class="menu-name">{{ row.menuName }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="menuType" label="类型" width="90" align="center">
          <template #default="{ row }">
            <span class="type-badge" :class="row.menuType.toLowerCase()">
              {{ menuTypeText(row.menuType) }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="sort" label="排序" width="70" align="center">
          <template #default="{ row }">
            <span class="sort-num">{{ row.sort }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="path" label="路由路径" min-width="160">
          <template #default="{ row }">
            <code class="path-code" v-if="row.path">{{ row.path }}</code>
            <span v-else class="text-muted">-</span>
          </template>
        </el-table-column>
        <el-table-column prop="perms" label="权限标识" min-width="140">
          <template #default="{ row }">
            <code class="perms-code" v-if="row.perms">{{ row.perms }}</code>
            <span v-else class="text-muted">-</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="90" align="center">
          <template #default="{ row }">
            <span class="status-badge" :class="row.status === 1 ? 'success' : 'danger'">
              <span class="status-dot"></span>
              {{ row.status === 1 ? '正常' : '禁用' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right" align="center">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button type="primary" text size="small" @click="handleEdit(row)">
                <el-icon><Edit /></el-icon>
              </el-button>
              <el-button
                type="success"
                text
                size="small"
                @click="handleAdd(row)"
                v-if="row.menuType !== 'F'"
              >
                <el-icon><Plus /></el-icon>
              </el-button>
              <el-button type="danger" text size="small" @click="handleDelete(row)">
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
        <el-form-item label="上级菜单" prop="parentId">
          <el-tree-select
            v-model="formData.parentId"
            :data="menuOptions"
            :props="{ label: 'menuName', value: 'id', children: 'children' }"
            placeholder="请选择上级菜单"
            check-strictly
            clearable
            style="width: 100%"
          />
        </el-form-item>

        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="菜单类型" prop="menuType">
              <el-radio-group v-model="formData.menuType" class="type-radio">
                <el-radio value="M">
                  <span class="radio-content">
                    <el-icon><Folder /></el-icon>
                    目录
                  </span>
                </el-radio>
                <el-radio value="C">
                  <span class="radio-content">
                    <el-icon><Document /></el-icon>
                    菜单
                  </span>
                </el-radio>
                <el-radio value="F">
                  <span class="radio-content">
                    <el-icon><Key /></el-icon>
                    按钮
                  </span>
                </el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="排序" prop="sort">
              <el-input-number v-model="formData.sort" :min="0" :max="999" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="菜单名称" prop="menuName">
          <el-input v-model="formData.menuName" placeholder="请输入菜单名称" />
        </el-form-item>

        <el-row :gutter="16" v-if="formData.menuType !== 'F'">
          <el-col :span="12">
            <el-form-item label="路由路径" prop="path">
              <el-input v-model="formData.path" placeholder="请输入路由路径" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="图标" prop="icon">
              <el-input v-model="formData.icon" placeholder="请输入图标名称">
                <template #prefix>
                  <el-icon><Picture /></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="组件路径" prop="component" v-if="formData.menuType === 'C'">
          <el-input v-model="formData.component" placeholder="请输入组件路径" />
        </el-form-item>

        <el-form-item label="权限标识" prop="perms" v-if="formData.menuType !== 'M'">
          <el-input v-model="formData.perms" placeholder="请输入权限标识" />
          <div class="field-tip">权限标识用于接口权限控制，如：system:user:list</div>
        </el-form-item>

        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio :value="1">正常</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
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
import { Plus, Edit, Delete, Menu, Folder, Document, Key, Picture, Sort } from '@element-plus/icons-vue'
import { listMenu, getMenu, addMenu, updateMenu, deleteMenu } from '../../api/menu'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const formRef = ref(null)
const tableRef = ref(null)
const tableData = ref([])
const menuOptions = ref([])
const isExpandAll = ref(true)

const queryForm = reactive({
  pageNum: 1,
  pageSize: 100
})

const formData = reactive({
  id: null,
  parentId: 0,
  menuName: '',
  menuType: 'C',
  path: '',
  component: '',
  perms: '',
  icon: '',
  sort: 0,
  status: 1
})

const isEdit = computed(() => !!formData.id)
const dialogTitle = computed(() => isEdit.value ? '编辑菜单' : '新增菜单')

const rules = {
  menuName: [
    { required: true, message: '请输入菜单名称', trigger: 'blur' }
  ],
  menuType: [
    { required: true, message: '请选择菜单类型', trigger: 'change' }
  ]
}

const menuTypeText = (type) => {
  const map = { M: '目录', C: '菜单', F: '按钮' }
  return map[type] || type
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await listMenu(queryForm)
    if (res.code === 200) {
      tableData.value = buildTree(res.data.list || res.data)
    }
  } catch (error) {
    ElMessage.error('获取菜单列表失败')
  } finally {
    loading.value = false
  }
}

const buildTree = (list) => {
  const map = {}
  const roots = []

  list.forEach(item => {
    map[item.id] = { ...item, children: [] }
  })

  list.forEach(item => {
    const parent = map[item.parentId]
    if (parent && item.id !== item.parentId) {
      parent.children.push(map[item.id])
    } else {
      roots.push(map[item.id])
    }
  })

  const cleanChildren = (nodes) => {
    nodes.forEach(node => {
      if (node.children && node.children.length === 0) {
        delete node.children
      } else if (node.children) {
        cleanChildren(node.children)
      }
    })
  }
  cleanChildren(roots)

  return roots
}

const fetchMenuOptions = async () => {
  try {
    const res = await listMenu(queryForm)
    if (res.code === 200) {
      menuOptions.value = [
        { id: 0, menuName: '根目录', children: buildTree(res.data.list || res.data) }
      ]
    }
  } catch (error) {
    console.error('获取菜单选项失败')
  }
}

const toggleExpandAll = () => {
  isExpandAll.value = !isExpandAll.value
  fetchData()
}

const handleAdd = (parent) => {
  fetchMenuOptions()
  Object.assign(formData, {
    id: null,
    parentId: parent ? parent.id : 0,
    menuName: '',
    menuType: parent ? (parent.menuType === 'M' ? 'C' : 'F') : 'M',
    path: '',
    component: '',
    perms: '',
    icon: '',
    sort: 0,
    status: 1
  })
  dialogVisible.value = true
}

const handleEdit = async (row) => {
  fetchMenuOptions()
  try {
    const res = await getMenu(row.id)
    if (res.code === 200) {
      Object.assign(formData, res.data)
      dialogVisible.value = true
    }
  } catch (error) {
    ElMessage.error('获取菜单信息失败')
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该菜单吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await deleteMenu(row.id)
      if (res.code === 200) {
        ElMessage.success('删除成功')
        fetchData()
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
      ? await updateMenu(formData)
      : await addMenu(formData)
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

/* ===== 表格区域 ===== */
.table-section {
  background: var(--mes-bg-primary);
  border-radius: 12px;
  border: 1px solid var(--mes-border);
  overflow: hidden;
}

.menu-table {
  --el-table-border-color: var(--mes-border);
}

/* 菜单单元格 */
.menu-cell {
  display: flex;
  align-items: center;
  gap: 12px;
}

.menu-icon {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.menu-icon.m {
  background: rgba(26, 86, 219, 0.1);
  color: var(--mes-primary);
}

.menu-icon.c {
  background: rgba(5, 150, 105, 0.1);
  color: var(--mes-success);
}

.menu-icon.f {
  background: rgba(217, 119, 6, 0.1);
  color: var(--mes-warning);
}

.menu-name {
  font-weight: 500;
  color: var(--mes-text-primary);
}

/* 类型徽章 */
.type-badge {
  display: inline-flex;
  align-items: center;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
}

.type-badge.m {
  background: rgba(26, 86, 219, 0.1);
  color: var(--mes-primary);
}

.type-badge.c {
  background: rgba(5, 150, 105, 0.1);
  color: var(--mes-success);
}

.type-badge.f {
  background: rgba(217, 119, 6, 0.1);
  color: var(--mes-warning);
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

.path-code, .perms-code {
  padding: 4px 10px;
  background: var(--mes-bg-tertiary);
  border-radius: 6px;
  font-size: 12px;
  color: var(--mes-text-secondary);
  font-family: 'JetBrains Mono', monospace;
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

.field-tip {
  font-size: 12px;
  color: var(--mes-text-muted);
  margin-top: 4px;
}

/* 类型单选 */
.type-radio {
  display: flex;
  gap: 8px;
}

.type-radio :deep(.el-radio) {
  margin-right: 0;
}

.type-radio :deep(.el-radio__label) {
  padding-left: 6px;
}

.radio-content {
  display: flex;
  align-items: center;
  gap: 4px;
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