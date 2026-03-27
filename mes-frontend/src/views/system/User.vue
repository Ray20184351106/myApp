<template>
  <div class="user-container">
    <el-card shadow="never">
      <!-- 搜索栏 -->
      <el-form :inline="true" :model="queryForm" class="search-form">
        <el-form-item label="用户名">
          <el-input v-model="queryForm.username" placeholder="请输入用户名" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 操作按钮 -->
      <div class="toolbar">
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon> 新增
        </el-button>
      </div>

      <!-- 用户列表 -->
      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="username" label="用户名" min-width="120" />
        <el-table-column prop="nickname" label="昵称" min-width="120" />
        <el-table-column prop="email" label="邮箱" min-width="180" />
        <el-table-column prop="phone" label="手机号" min-width="130" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
            <el-button
              :type="row.status === 1 ? 'warning' : 'success'"
              link
              @click="handleToggleStatus(row)"
            >
              {{ row.status === 1 ? '禁用' : '启用' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="queryForm.pageNum"
        v-model:page-size="queryForm.pageSize"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="fetchData"
        @current-change="fetchData"
      />
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="handleDialogClose"
    >
      <el-form :model="formData" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="formData.username" placeholder="请输入用户名" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="formData.nickname" placeholder="请输入昵称" />
        </el-form-item>
        <el-form-item label="密码" :prop="isEdit ? '' : 'password'">
          <el-input v-model="formData.password" type="password" placeholder="请输入密码" show-password />
          <span v-if="isEdit" class="tip">留空则不修改密码</span>
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="formData.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="formData.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio :value="1">正常</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="formData.remark" type="textarea" :rows="3" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import request from '../../utils/request'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const formRef = ref(null)
const tableData = ref([])
const total = ref(0)

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

// 获取用户列表
const fetchData = async () => {
  loading.value = true
  try {
    console.log('queryForm',queryForm)
    const res = await request.get('/system/user/list', { params: queryForm })
    console.log('res',res)
    if (res.code === 200) {
      tableData.value = res.data.list
      total.value = res.data.total
    }
  } catch (error) {
    ElMessage.error('获取用户列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleQuery = () => {
  queryForm.pageNum = 1
  fetchData()
}

// 重置
const handleReset = () => {
  queryForm.username = ''
  queryForm.pageNum = 1
  fetchData()
}

// 新增
const handleAdd = () => {
  Object.assign(formData, {
    id: null,
    username: '',
    nickname: '',
    password: '',
    email: '',
    phone: '',
    status: 1,
    remark: ''
  })
  dialogVisible.value = true
}

// 编辑
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

// 删除
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

// 切换状态
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

// 提交表单
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

// 关闭对话框
const handleDialogClose = () => {
  formRef.value?.resetFields()
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.user-container {
  padding: 20px;
}
.search-form {
  margin-bottom: 20px;
}
.toolbar {
  margin-bottom: 20px;
}
.el-pagination {
  margin-top: 20px;
  justify-content: flex-end;
}
.tip {
  font-size: 12px;
  color: #999;
}
</style>
