<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">管理员管理</h2>
      <el-button type="primary" :icon="Plus" @click="handleAdd">
        新增管理员
      </el-button>
    </div>

    <!-- 搜索区 -->
    <div class="search-container card">
      <el-form :model="searchForm" :inline="true">
        <el-form-item label="用户名">
          <el-input
            v-model="searchForm.username"
            placeholder="请输入用户名"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input
            v-model="searchForm.phone_number"
            placeholder="请输入手机号"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="注册时间">
          <el-date-picker
            v-model="searchForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            style="width: 240px"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select
            v-model="searchForm.status"
            placeholder="请选择"
            clearable
            style="width: 150px"
          >
            <el-option label="全部" value="" />
            <el-option label="启用" value="active" />
            <el-option label="禁用" value="inactive" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">
            搜索
          </el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 列表区 -->
    <div class="table-container card">
      <el-table
        v-loading="loading"
        :data="tableData"
        stripe
        style="width: 100%"
      >
        <el-table-column prop="user_id" label="用户ID" width="80" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="phone_number" label="手机号" width="130" />
        <el-table-column prop="email" label="邮箱" width="180" />
        <el-table-column label="注册时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.registration_date) }}
          </template>
        </el-table-column>
        <el-table-column label="最后登录时间" width="180">
          <template #default="{ row }">
            {{ row.last_login_time ? formatDate(row.last_login_time) : '-' }}
          </template>
        </el-table-column>
        <el-table-column label="角色" width="120">
          <template #default="{ row }">
            <el-tag v-for="role in row.roles" :key="role.role_id" type="info" style="margin-right: 4px">
              {{ role.role_name }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'active' ? 'success' : 'danger'">
              {{ row.status === 'active' ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="320" fixed="right">
          <template #default="{ row }">
            <el-button
              type="primary"
              link
              size="small"
              :icon="View"
              @click="handleView(row)"
            >
              查看详情
            </el-button>
            <el-button
              type="primary"
              link
              size="small"
              :icon="Edit"
              @click="handleEdit(row)"
            >
              编辑
            </el-button>
            <el-button
              type="warning"
              link
              size="small"
              :icon="Key"
              @click="handleResetPassword(row)"
            >
              重置密码
            </el-button>
            <el-button
              :type="row.status === 'active' ? 'danger' : 'success'"
              link
              size="small"
              :icon="row.status === 'active' ? CircleClose : CircleCheck"
              @click="handleToggleStatus(row)"
            >
              {{ row.status === 'active' ? '禁用' : '启用' }}
            </el-button>
            <el-button
              type="info"
              link
              size="small"
              :icon="Setting"
              @click="handleAssignRoles(row)"
            >
              分配角色
            </el-button>
            <el-button
              type="danger"
              link
              size="small"
              :icon="Delete"
              @click="handleDelete(row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.page_size"
          :total="pagination.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </div>

    <!-- 新增/编辑表单弹窗 -->
    <el-dialog
      v-model="formVisible"
      :title="formTitle"
      width="600px"
      @close="handleFormClose"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="用户名" prop="username">
          <el-input
            v-model="formData.username"
            placeholder="请输入用户名（3-20位，字母数字下划线）"
            :disabled="isEdit"
          />
        </el-form-item>
        <el-form-item label="手机号" prop="phone_number">
          <el-input
            v-model="formData.phone_number"
            placeholder="请输入手机号（11位，1开头）"
          />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input
            v-model="formData.email"
            placeholder="请输入邮箱"
          />
        </el-form-item>
        <el-form-item label="密码" prop="password" :required="!isEdit">
          <el-input
            v-model="formData.password"
            type="password"
            :placeholder="isEdit ? '不修改请留空' : '请输入密码（6-20位）'"
            show-password
          />
        </el-form-item>
        <el-form-item label="角色" prop="role_ids">
          <el-select
            v-model="formData.role_ids"
            multiple
            placeholder="请选择角色"
            style="width: 100%"
          >
            <el-option
              v-for="role in allRoles"
              :key="role.role_id"
              :label="role.role_name"
              :value="role.role_id"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="formVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 详情弹窗 -->
    <el-dialog
      v-model="detailVisible"
      title="管理员详情"
      width="700px"
    >
      <div v-if="selectedRow" class="detail-content">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="用户ID">
            {{ selectedRow.user_id }}
          </el-descriptions-item>
          <el-descriptions-item label="用户名">
            {{ selectedRow.username }}
          </el-descriptions-item>
          <el-descriptions-item label="手机号">
            {{ selectedRow.phone_number }}
          </el-descriptions-item>
          <el-descriptions-item label="邮箱">
            {{ selectedRow.email || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="selectedRow.status === 'active' ? 'success' : 'danger'">
              {{ selectedRow.status === 'active' ? '启用' : '禁用' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="注册时间">
            {{ formatDate(selectedRow.registration_date) }}
          </el-descriptions-item>
          <el-descriptions-item label="最后登录时间">
            {{ selectedRow.last_login_time ? formatDate(selectedRow.last_login_time) : '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="角色" :span="2">
            <el-tag
              v-for="role in selectedRow.roles"
              :key="role.role_id"
              type="info"
              style="margin-right: 8px"
            >
              {{ role.role_name }}
            </el-tag>
            <span v-if="!selectedRow.roles || selectedRow.roles.length === 0">-</span>
          </el-descriptions-item>
        </el-descriptions>
      </div>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 分配角色弹窗 -->
    <el-dialog
      v-model="rolesVisible"
      title="分配角色权限"
      width="500px"
    >
      <el-checkbox-group v-model="selectedRoleIds">
        <el-checkbox
          v-for="role in allRoles"
          :key="role.role_id"
          :label="role.role_id"
        >
          {{ role.role_name }}
        </el-checkbox>
      </el-checkbox-group>
      <template #footer>
        <el-button @click="rolesVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSaveRoles">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { formatDate } from '@/utils/date'
import {
  Plus,
  Search,
  Refresh,
  View,
  Edit,
  Delete,
  Key,
  Setting,
  CircleClose,
  CircleCheck
} from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getAdminAdmins,
  createAdminAdmin,
  updateAdminAdmin,
  deleteAdminAdmin,
  resetAdminAdminPassword,
  updateAdminAdminStatus,
  getAllRoles
} from '@/api/admin'

const loading = ref(false)
const formVisible = ref(false)
const detailVisible = ref(false)
const rolesVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)
const selectedRow = ref(null)
const selectedRoleIds = ref([])

const searchForm = reactive({
  username: '',
  phone_number: '',
  dateRange: null,
  status: ''
})

const pagination = reactive({
  page: 1,
  page_size: 20,
  total: 0
})

const formData = reactive({
  user_id: null,
  username: '',
  phone_number: '',
  email: '',
  password: '',
  role_ids: []
})

const allRoles = ref([])

const formRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度为3-20位', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9_]+$/, message: '用户名只能包含字母、数字和下划线', trigger: 'blur' }
  ],
  phone_number: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度为6-20位', trigger: 'blur' }
  ],
  role_ids: [
    { required: true, message: '请选择至少一个角色', trigger: 'change' }
  ]
}


const tableData = ref([])

const formTitle = computed(() => isEdit.value ? '编辑管理员' : '新增管理员')

const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      page_size: pagination.page_size
    }
    if (searchForm.username) {
      params.username = searchForm.username
    }
    if (searchForm.phone_number) {
      params.phone_number = searchForm.phone_number
    }
    if (searchForm.dateRange && searchForm.dateRange.length === 2) {
      params.start_date = searchForm.dateRange[0]
      params.end_date = searchForm.dateRange[1]
    }
    if (searchForm.status) {
      params.status = searchForm.status
    }
    const response = await getAdminAdmins(params)
    tableData.value = (response.list || []).map(normalizeAdmin)
    pagination.total = response.total || 0
  } catch (error) {
    ElMessage.error('获取管理员列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.page = 1
  fetchData()
}

const handleReset = () => {
  Object.assign(searchForm, {
    username: '',
    phone_number: '',
    dateRange: null,
    status: ''
  })
  handleSearch()
}

// 兼容后端返回的字段命名（驼峰/下划线）和角色字段
const normalizeAdmin = (item) => ({
  ...item,
  user_id: item.user_id ?? item.userId,
  username: item.username,
  phone_number: item.phone_number ?? item.phoneNumber,
  email: item.email,
  status: item.status ?? item.userStatus,
  registration_date: item.registration_date ?? item.registrationDate,
  last_login_time: item.last_login_time ?? item.lastLoginTime,
  roles: item.roles ?? item.roleList ?? []
})

const handleAdd = () => {
  isEdit.value = false
  Object.assign(formData, {
    user_id: null,
    username: '',
    phone_number: '',
    email: '',
    password: '',
    role_ids: []
  })
  formVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(formData, {
    user_id: row.user_id,
    username: row.username,
    phone_number: row.phone_number,
    email: row.email,
    password: '',
    role_ids: row.roles ? row.roles.map(r => r.role_id) : []
  })
  formVisible.value = true
}

const handleView = (row) => {
  selectedRow.value = row
  detailVisible.value = true
}

const handleAssignRoles = (row) => {
  selectedRow.value = row
  selectedRoleIds.value = row.roles ? row.roles.map(r => r.role_id) : []
  rolesVisible.value = true
}

const handleSaveRoles = async () => {
  if (selectedRoleIds.value.length === 0) {
    ElMessage.warning('请至少选择一个角色')
    return
  }
  try {
    await updateAdminAdmin(selectedRow.value.user_id, { role_ids: selectedRoleIds.value })
    ElMessage.success('角色分配成功')
    rolesVisible.value = false
    fetchData()
  } catch (error) {
    ElMessage.error('角色分配失败')
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const data = { ...formData }
        if (isEdit.value && !data.password) {
          delete data.password
        }
        if (isEdit.value) {
          await updateAdminAdmin(data.user_id, data)
          ElMessage.success('编辑成功')
        } else {
          await createAdminAdmin(data)
          ElMessage.success('新增成功')
        }
        formVisible.value = false
        fetchData()
      } catch (error) {
        ElMessage.error(isEdit.value ? '编辑失败' : '新增失败')
      }
    }
  })
}

const handleFormClose = () => {
  formRef.value?.resetFields()
}

const handleResetPassword = (row) => {
  ElMessageBox.prompt('请输入新密码', '重置密码', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    inputPattern: /^.{6,20}$/,
    inputErrorMessage: '密码长度为6-20位'
  }).then(async ({ value }) => {
    try {
      await resetAdminAdminPassword(row.user_id, { new_password: value })
      ElMessage.success('密码重置成功')
    } catch (error) {
      ElMessage.error('密码重置失败')
    }
  }).catch(() => {})
}

const handleToggleStatus = (row) => {
  const action = row.status === 'active' ? '禁用' : '启用'
  const newStatus = row.status === 'active' ? 'inactive' : 'active'
  ElMessageBox.confirm(`确定要${action}该管理员吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await updateAdminAdminStatus(row.user_id, { status: newStatus })
      ElMessage.success(`${action}成功`)
      fetchData()
    } catch (error) {
      ElMessage.error(`${action}失败`)
    }
  }).catch(() => {})
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该管理员吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteAdminAdmin(row.user_id)
      ElMessage.success('删除成功')
      fetchData()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

const handleSizeChange = (size) => {
  pagination.page_size = size
  fetchData()
}

const handlePageChange = (page) => {
  pagination.page = page
  fetchData()
}

const loadRoles = async () => {
  try {
    const response = await getAllRoles()
    allRoles.value = response.list || []
  } catch (error) {
    console.error('加载角色列表失败', error)
  }
}

onMounted(() => {
  fetchData()
  loadRoles()
})
</script>

<style lang="scss" scoped>
.page-container {
  padding: 24px;
  height: 100%;
  overflow-y: auto;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  
  .page-title {
    font-size: 24px;
    font-weight: 600;
    color: #303133;
  }
}

.search-container {
  margin-bottom: 20px;
  padding: 20px;
}

.table-container {
  padding: 20px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.detail-content {
  padding: 10px 0;
}
</style>

