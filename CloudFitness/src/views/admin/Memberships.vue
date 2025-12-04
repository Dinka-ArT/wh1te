<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">会员卡管理</h2>
      <el-button type="primary" :icon="Plus" @click="handleAdd">
        新增会员卡
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
        <el-form-item label="会员卡类型">
          <el-select
            v-model="searchForm.membership_type"
            placeholder="请选择"
            clearable
            style="width: 150px"
          >
            <el-option label="全部" value="" />
            <el-option label="月度" value="monthly" />
            <el-option label="年度" value="annual" />
            <el-option label="其他" value="other" />
          </el-select>
        </el-form-item>
        <el-form-item label="到期时间">
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
        <el-form-item label="会员状态">
          <el-select
            v-model="searchForm.status"
            placeholder="请选择"
            clearable
            style="width: 150px"
          >
            <el-option label="全部" value="" />
            <el-option label="有效" value="valid" />
            <el-option label="已过期" value="expired" />
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
        <el-table-column prop="membership_id" label="会员卡ID" width="100" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="phone_number" label="手机号" width="130" />
        <el-table-column label="会员卡类型" width="120">
          <template #default="{ row }">
            <el-tag type="info">
              {{ getMembershipTypeText(row.membership_type) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="开始时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.start_date, 'YYYY-MM-DD') }}
          </template>
        </el-table-column>
        <el-table-column label="到期时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.expiry_date, 'YYYY-MM-DD') }}
          </template>
        </el-table-column>
        <el-table-column label="会员状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getMembershipStatusType(row)">
              {{ getMembershipStatusText(row) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280" fixed="right">
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
              type="success"
              link
              size="small"
              :icon="Refresh"
              @click="handleRenew(row)"
            >
              续费
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
        <el-form-item label="用户" prop="user_id">
          <el-select
            v-model="formData.user_id"
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
        <el-form-item label="会员卡类型" prop="membership_type">
          <el-select
            v-model="formData.membership_type"
            placeholder="请选择类型"
            style="width: 100%"
          >
            <el-option label="月度会员" value="monthly" />
            <el-option label="年度会员" value="annual" />
            <el-option label="其他" value="other" />
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
        <el-form-item label="到期时间" prop="expiry_date">
          <el-date-picker
            v-model="formData.expiry_date"
            type="datetime"
            placeholder="请选择到期时间"
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

    <!-- 续费弹窗 -->
    <el-dialog
      v-model="renewVisible"
      title="续费会员卡"
      width="400px"
    >
      <el-form
        ref="renewFormRef"
        :model="renewForm"
        :rules="renewRules"
        label-width="100px"
      >
        <el-form-item label="续费月数" prop="months">
          <el-input-number
            v-model="renewForm.months"
            :min="1"
            :max="24"
            placeholder="请输入续费月数（1-24）"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="renewVisible = false">取消</el-button>
        <el-button type="primary" @click="handleRenewSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 详情弹窗 -->
    <el-dialog
      v-model="detailVisible"
      title="会员卡详情"
      width="700px"
    >
      <div v-if="selectedRow" class="detail-content">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="会员卡ID">
            {{ selectedRow.membership_id }}
          </el-descriptions-item>
          <el-descriptions-item label="用户名">
            {{ selectedRow.username }}
          </el-descriptions-item>
          <el-descriptions-item label="手机号">
            {{ selectedRow.phone_number }}
          </el-descriptions-item>
          <el-descriptions-item label="会员卡类型">
            <el-tag type="info">
              {{ getMembershipTypeText(selectedRow.membership_type) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="开始时间">
            {{ formatDate(selectedRow.start_date, 'YYYY-MM-DD') }}
          </el-descriptions-item>
          <el-descriptions-item label="到期时间">
            {{ formatDate(selectedRow.expiry_date, 'YYYY-MM-DD') }}
          </el-descriptions-item>
          <el-descriptions-item label="会员状态" :span="2">
            <el-tag :type="getMembershipStatusType(selectedRow)">
              {{ getMembershipStatusText(selectedRow) }}
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
  Delete
} from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import dayjs from 'dayjs'
import {
  getAdminMemberships,
  createAdminMembership,
  updateAdminMembership,
  deleteAdminMembership,
  renewAdminMembership,
  getAllMembers
} from '@/api/admin'

const loading = ref(false)
const formVisible = ref(false)
const detailVisible = ref(false)
const renewVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)
const renewFormRef = ref(null)
const selectedRow = ref(null)

const searchForm = reactive({
  username: '',
  phone_number: '',
  membership_type: '',
  dateRange: null,
  status: ''
})

const pagination = reactive({
  page: 1,
  page_size: 20,
  total: 0
})

const formData = reactive({
  membership_id: null,
  user_id: null,
  membership_type: '',
  start_date: '',
  expiry_date: ''
})

const renewForm = reactive({
  months: 1
})

const users = ref([])

const formRules = {
  user_id: [
    { required: true, message: '请选择用户', trigger: 'change' }
  ],
  membership_type: [
    { required: true, message: '请选择会员卡类型', trigger: 'change' }
  ],
  start_date: [
    { required: true, message: '请选择开始时间', trigger: 'change' }
  ],
  expiry_date: [
    { required: true, message: '请选择到期时间', trigger: 'change' }
  ]
}

const renewRules = {
  months: [
    { required: true, message: '请输入续费月数', trigger: 'blur' },
    { type: 'number', min: 1, max: 24, message: '续费月数范围为1-24', trigger: 'blur' }
  ]
}


const tableData = ref([])

const formTitle = computed(() => isEdit.value ? '编辑会员卡' : '新增会员卡')

const getMembershipTypeText = (type) => {
  const map = {
    monthly: '月度会员',
    annual: '年度会员',
    other: '其他'
  }
  return map[type] || type
}

const getMembershipStatusText = (row) => {
  if (isExpired(row.expiry_date)) return '已过期'
  return '有效'
}

const getMembershipStatusType = (row) => {
  if (isExpired(row.expiry_date)) return 'danger'
  return 'success'
}

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
    if (searchForm.membership_type) {
      params.membership_type = searchForm.membership_type
    }
    if (searchForm.dateRange && searchForm.dateRange.length === 2) {
      params.start_date = searchForm.dateRange[0]
      params.end_date = searchForm.dateRange[1]
    }
    if (searchForm.status) {
      params.status = searchForm.status
    }
    const response = await getAdminMemberships(params)
    tableData.value = response.list || []
    pagination.total = response.total || 0
  } catch (error) {
    ElMessage.error('获取会员卡列表失败')
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
    membership_type: '',
    dateRange: null,
    status: ''
  })
  handleSearch()
}

const handleAdd = () => {
  isEdit.value = false
  Object.assign(formData, {
    membership_id: null,
    user_id: null,
    membership_type: '',
    start_date: '',
    expiry_date: ''
  })
  formVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(formData, {
    membership_id: row.membership_id,
    user_id: row.user_id,
    membership_type: row.membership_type,
    start_date: row.start_date,
    expiry_date: row.expiry_date
  })
  formVisible.value = true
}

const handleView = (row) => {
  selectedRow.value = row
  detailVisible.value = true
}

const handleRenew = (row) => {
  selectedRow.value = row
  renewForm.months = 1
  renewVisible.value = true
}

const handleRenewSubmit = async () => {
  if (!renewFormRef.value) return
  
  await renewFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        await renewAdminMembership(selectedRow.value.membership_id, { months: renewForm.months })
        ElMessage.success('续费成功')
        renewVisible.value = false
        fetchData()
      } catch (error) {
        ElMessage.error('续费失败')
      }
    }
  })
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (isEdit.value) {
          await updateAdminMembership(formData.membership_id, formData)
          ElMessage.success('编辑成功')
        } else {
          await createAdminMembership(formData)
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
  ElMessageBox.confirm('确定要删除该会员卡吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteAdminMembership(row.membership_id)
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
    users.value = response.list || []
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

