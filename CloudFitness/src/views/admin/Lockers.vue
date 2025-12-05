<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">储物柜管理</h2>
      <el-button type="primary" :icon="Plus" @click="handleAdd">
        新增储物柜
      </el-button>
    </div>

    <!-- 搜索区 -->
    <div class="search-container card">
      <el-form :model="searchForm" :inline="true">
        <el-form-item label="储物柜编号">
          <el-input
            v-model="searchForm.locker_number"
            placeholder="请输入储物柜编号"
            clearable
            style="width: 200px"
          />
        </el-form-item>
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
        <el-form-item label="使用状态">
          <el-select
            v-model="searchForm.status"
            placeholder="请选择"
            clearable
            style="width: 150px"
          >
            <el-option label="全部" value="" />
            <el-option label="使用中" value="in_use" />
            <el-option label="空闲" value="free" />
            <el-option label="已到期" value="expired" />
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
        <el-table-column prop="locker_id" label="储物柜ID" width="100" />
        <el-table-column prop="locker_number" label="储物柜编号" width="120" />
        <el-table-column label="用户名" width="120">
          <template #default="{ row }">
            {{ row.username || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="手机号" width="130">
          <template #default="{ row }">
            {{ row.phone_number || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="开始使用时间" width="180">
          <template #default="{ row }">
            {{ row.start_date ? formatDate(row.start_date, 'YYYY-MM-DD') : '-' }}
          </template>
        </el-table-column>
        <el-table-column label="结束时间" width="180">
          <template #default="{ row }">
            {{ row.end_date ? formatDate(row.end_date, 'YYYY-MM-DD') : '-' }}
          </template>
        </el-table-column>
        <el-table-column label="使用状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row)">
              {{ getStatusText(row) }}
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
              v-if="!row.user_id"
              type="success"
              link
              size="small"
              :icon="User"
              @click="handleAssign(row)"
            >
              分配
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
              v-if="row.user_id"
              type="warning"
              link
              size="small"
              :icon="Refresh"
              @click="handleRelease(row)"
            >
              释放
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
        <el-form-item label="储物柜编号" prop="locker_number">
          <el-input-number
            v-model="formData.locker_number"
            :min="1"
            placeholder="请输入储物柜编号"
            :disabled="isEdit"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="用户" prop="user_id">
          <el-select
            v-model="formData.user_id"
            placeholder="请选择用户（可选）"
            filterable
            clearable
            style="width: 100%"
          >
            <el-option
              v-for="user in users"
              :key="user.user_id"
              :label="`${user.username} (${user.phone_number})`"
              :value="user.user_id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="开始时间" prop="start_date">
          <el-date-picker
            v-model="formData.start_date"
            type="datetime"
            placeholder="请选择开始时间"
            format="YYYY-MM-DD HH:mm"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="结束时间" prop="end_date">
          <el-date-picker
            v-model="formData.end_date"
            type="datetime"
            placeholder="请选择结束时间（可选）"
            format="YYYY-MM-DD HH:mm"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="formVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 分配弹窗 -->
    <el-dialog
      v-model="assignVisible"
      title="分配储物柜"
      width="500px"
    >
      <el-form
        ref="assignFormRef"
        :model="assignForm"
        :rules="assignRules"
        label-width="100px"
      >
        <el-form-item label="用户" prop="user_id">
          <el-select
            v-model="assignForm.user_id"
            placeholder="请选择用户"
            filterable
            style="width: 100%"
          >
            <el-option
              v-for="user in users"
              :key="user.user_id"
              :label="`${user.username} (${user.phone_number})`"
              :value="user.user_id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="开始时间" prop="start_date">
          <el-date-picker
            v-model="assignForm.start_date"
            type="datetime"
            placeholder="请选择开始时间"
            format="YYYY-MM-DD HH:mm"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="结束时间" prop="end_date">
          <el-date-picker
            v-model="assignForm.end_date"
            type="datetime"
            placeholder="请选择结束时间（可选）"
            format="YYYY-MM-DD HH:mm"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="assignVisible = false">取消</el-button>
        <el-button type="primary" @click="handleAssignSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 详情弹窗 -->
    <el-dialog
      v-model="detailVisible"
      title="储物柜详情"
      width="700px"
    >
      <div v-if="selectedRow" class="detail-content">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="储物柜ID">
            {{ selectedRow.locker_id }}
          </el-descriptions-item>
          <el-descriptions-item label="储物柜编号">
            {{ selectedRow.locker_number }}
          </el-descriptions-item>
          <el-descriptions-item label="用户名">
            {{ selectedRow.username || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="手机号">
            {{ selectedRow.phone_number || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="开始使用时间">
            {{ selectedRow.start_date ? formatDate(selectedRow.start_date, 'YYYY-MM-DD') : '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="结束时间">
            {{ selectedRow.end_date ? formatDate(selectedRow.end_date, 'YYYY-MM-DD') : '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="使用状态" :span="2">
            <el-tag :type="getStatusType(selectedRow)">
              {{ getStatusText(selectedRow) }}
            </el-tag>
          </el-descriptions-item>
        </el-descriptions>
      </div>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { formatDate, isExpired } from '@/utils/date'
import {
  Plus,
  Search,
  Refresh,
  View,
  Edit,
  Delete,
  User
} from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getAdminLockers,
  createAdminLocker,
  updateAdminLocker,
  deleteAdminLocker,
  assignAdminLocker,
  releaseAdminLocker,
  getAllMembers
} from '@/api/admin'

const loading = ref(false)
const formVisible = ref(false)
const detailVisible = ref(false)
const assignVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)
const assignFormRef = ref(null)
const selectedRow = ref(null)

const searchForm = reactive({
  locker_number: '',
  username: '',
  phone_number: '',
  status: ''
})

const pagination = reactive({
  page: 1,
  page_size: 20,
  total: 0
})

const formData = reactive({
  locker_id: null,
  locker_number: null,
  user_id: null,
  start_date: '',
  end_date: ''
})

const assignForm = reactive({
  user_id: null,
  start_date: '',
  end_date: ''
})

const users = ref([])

// 兼容后端返回的用户字段（userId/phoneNumber），统一为数字 user_id，避免下拉选择失效
const normalizeUserOption = (item) => ({
  ...item,
  user_id: Number(item.user_id ?? item.userId),
  username: item.username,
  phone_number: item.phone_number ?? item.phoneNumber
})

const formRules = {
  locker_number: [
    { required: true, message: '请输入储物柜编号', trigger: 'blur' },
    { type: 'number', min: 1, message: '储物柜编号必须大于0', trigger: 'blur' }
  ],
  start_date: [
    { required: true, message: '请选择开始时间', trigger: 'change' }
  ]
}

const assignRules = {
  user_id: [
    { required: true, message: '请选择用户', trigger: 'change' }
  ],
  start_date: [
    { required: true, message: '请选择开始时间', trigger: 'change' }
  ]
}


const tableData = ref([])

const formTitle = computed(() => isEdit.value ? '编辑储物柜' : '新增储物柜')

const getStatusText = (row) => {
  if (!row.user_id) return '空闲'
  if (row.end_date && isExpired(row.end_date)) return '已到期'
  return '使用中'
}

const getStatusType = (row) => {
  const status = getStatusText(row)
  if (status === '已到期') return 'danger'
  if (status === '使用中') return 'success'
  return 'info'
}

const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      page_size: pagination.page_size
    }
    if (searchForm.locker_number) {
      params.locker_number = searchForm.locker_number
    }
    if (searchForm.username) {
      params.username = searchForm.username
    }
    if (searchForm.phone_number) {
      params.phone_number = searchForm.phone_number
    }
    if (searchForm.status) {
      params.status = searchForm.status
    }
    const response = await getAdminLockers(params)
    tableData.value = response.list || []
    pagination.total = response.total || 0
  } catch (error) {
    ElMessage.error('获取储物柜列表失败')
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
    locker_number: '',
    username: '',
    phone_number: '',
    status: ''
  })
  handleSearch()
}

const handleAdd = () => {
  isEdit.value = false
  Object.assign(formData, {
    locker_id: null,
    locker_number: null,
    user_id: null,
    start_date: '',
    end_date: ''
  })
  formVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(formData, {
    locker_id: row.locker_id,
    locker_number: row.locker_number,
    user_id: row.user_id ? Number(row.user_id) : null,
    start_date: row.start_date || '',
    end_date: row.end_date || ''
  })
  formVisible.value = true
}

const handleView = (row) => {
  selectedRow.value = row
  detailVisible.value = true
}

const handleAssign = (row) => {
  selectedRow.value = row
  Object.assign(assignForm, {
    user_id: null,
    start_date: '',
    end_date: ''
  })
  assignVisible.value = true
}

const handleAssignSubmit = async () => {
  if (!assignFormRef.value) return
  
  await assignFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        await assignAdminLocker(selectedRow.value.locker_id, assignForm)
        ElMessage.success('分配成功')
        assignVisible.value = false
        fetchData()
      } catch (error) {
        ElMessage.error('分配失败')
      }
    }
  })
}

const handleRelease = (row) => {
  ElMessageBox.confirm('确定要释放该储物柜吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await releaseAdminLocker(row.locker_id)
      ElMessage.success('释放成功')
      fetchData()
    } catch (error) {
      ElMessage.error('释放失败')
    }
  }).catch(() => {})
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (isEdit.value) {
          await updateAdminLocker(formData.locker_id, formData)
          ElMessage.success('编辑成功')
        } else {
          await createAdminLocker(formData)
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

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该储物柜记录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteAdminLocker(row.locker_id)
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

const loadUsers = async () => {
  try {
    const response = await getAllMembers()
    users.value = (response.list || []).map(normalizeUserOption)
  } catch (error) {
    console.error('加载用户列表失败', error)
  }
}

onMounted(() => {
  fetchData()
  loadUsers()
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

