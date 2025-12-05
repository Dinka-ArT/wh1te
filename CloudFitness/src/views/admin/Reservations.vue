<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">预约管理</h2>
      <el-button type="primary" :icon="Plus" @click="handleAdd">
        新增预约
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
        <el-form-item label="课程名称">
          <el-input
            v-model="searchForm.course_name"
            placeholder="请输入课程名称"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="预约时间">
          <el-date-picker
            v-model="searchForm.dateRange"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            format="YYYY-MM-DD HH:mm"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 360px"
          />
        </el-form-item>
        <el-form-item label="预约状态">
          <el-select
            v-model="searchForm.status"
            placeholder="请选择"
            clearable
            style="width: 150px"
          >
            <el-option label="全部" value="" />
            <el-option label="待确认" value="pending" />
            <el-option label="已确认" value="confirmed" />
            <el-option label="已完成" value="completed" />
            <el-option label="已取消" value="cancelled" />
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
        <el-table-column prop="reservation_id" label="预约ID" width="80" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="phone_number" label="手机号" width="130" />
        <el-table-column prop="course_name" label="课程名称" width="200" />
        <el-table-column label="预约时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.reservation_date) }}
          </template>
        </el-table-column>
        <el-table-column label="课程时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.schedule, 'YYYY-MM-DD HH:mm') }}
          </template>
        </el-table-column>
        <el-table-column label="预约状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getReservationStatusType(row.status)">
              {{ getReservationStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="是否签到" width="100">
          <template #default="{ row }">
            <el-tag :type="row.has_attendance ? 'success' : 'info'">
              {{ row.has_attendance ? '已签到' : '未签到' }}
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
              v-if="row.status === 'pending'"
              type="success"
              link
              size="small"
              :icon="Check"
              @click="handleConfirm(row)"
            >
              确认预约
            </el-button>
            <el-button
              v-if="row.status !== 'cancelled' && row.status !== 'completed'"
              type="warning"
              link
              size="small"
              :icon="Close"
              @click="handleCancel(row)"
            >
              取消预约
            </el-button>
            <el-button
              v-if="row.status === 'confirmed'"
              type="info"
              link
              size="small"
              :icon="Finished"
              @click="handleComplete(row)"
            >
              标记完成
            </el-button>
            <el-button
              type="success"
              link
              size="small"
              :icon="Check"
              @click="handleViewAttendance(row)"
            >
              签到记录
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
        <el-form-item label="课程" prop="course_id">
          <el-select
            v-model="formData.course_id"
            placeholder="请选择课程"
            filterable
            style="width: 100%"
          >
            <el-option
              v-for="course in courses"
              :key="course.course_id"
              :label="`${course.course_name} (${formatDate(course.schedule, 'YYYY-MM-DD HH:mm')})`"
              :value="course.course_id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="预约状态" prop="status">
          <el-select
            v-model="formData.status"
            placeholder="请选择状态"
            style="width: 100%"
          >
            <el-option label="待确认" value="pending" />
            <el-option label="已确认" value="confirmed" />
            <el-option label="已完成" value="completed" />
            <el-option label="已取消" value="cancelled" />
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
      title="预约详情"
      width="700px"
    >
      <div v-if="selectedRow" class="detail-content">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="预约ID">
            {{ selectedRow.reservation_id }}
          </el-descriptions-item>
          <el-descriptions-item label="用户名">
            {{ selectedRow.username }}
          </el-descriptions-item>
          <el-descriptions-item label="手机号">
            {{ selectedRow.phone_number }}
          </el-descriptions-item>
          <el-descriptions-item label="课程名称">
            {{ selectedRow.course_name }}
          </el-descriptions-item>
          <el-descriptions-item label="预约时间">
            {{ formatDate(selectedRow.reservation_date) }}
          </el-descriptions-item>
          <el-descriptions-item label="课程时间">
            {{ formatDate(selectedRow.schedule, 'YYYY-MM-DD HH:mm') }}
          </el-descriptions-item>
          <el-descriptions-item label="预约状态">
            <el-tag :type="getReservationStatusType(selectedRow.status)">
              {{ getReservationStatusText(selectedRow.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="是否签到">
            <el-tag :type="selectedRow.has_attendance ? 'success' : 'info'">
              {{ selectedRow.has_attendance ? '已签到' : '未签到' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item v-if="selectedRow.attendance" label="签到时间" :span="2">
            {{ formatDate(selectedRow.attendance.check_in_time) }}
          </el-descriptions-item>
          <el-descriptions-item v-if="selectedRow.attendance" label="是否准时" :span="2">
            <el-tag :type="selectedRow.attendance.is_on_time ? 'success' : 'warning'">
              {{ selectedRow.attendance.is_on_time ? '准时' : '迟到' }}
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
import { formatDate } from '@/utils/date'
import {
  Plus,
  Search,
  Refresh,
  View,
  Edit,
  Delete,
  Check,
  Close,
  Finished
} from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getAdminReservations,
  createAdminReservation,
  updateAdminReservation,
  deleteAdminReservation,
  getAdminReservationDetail,
  getAllMembers,
  getAllCourses
} from '@/api/admin'

const loading = ref(false)
const formVisible = ref(false)
const detailVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)
const selectedRow = ref(null)

const searchForm = reactive({
  username: '',
  phone_number: '',
  course_name: '',
  dateRange: null,
  status: ''
})

const pagination = reactive({
  page: 1,
  page_size: 20,
  total: 0
})

const formData = reactive({
  reservation_id: null,
  user_id: null,
  course_id: null,
  status: 'pending'
})

const users = ref([])
const courses = ref([])

// 兼容后端返回的用户字段（userId/phoneNumber），统一为数字 user_id，避免下拉选择失效
const normalizeUserOption = (item) => ({
  ...item,
  user_id: Number(item.user_id ?? item.userId),
  username: item.username,
  phone_number: item.phone_number ?? item.phoneNumber
})

// 兼容后端返回的课程字段（courseId），统一为数字 course_id
const normalizeCourseOption = (item) => ({
  ...item,
  course_id: Number(item.course_id ?? item.courseId),
  course_name: item.course_name,
  schedule: item.schedule
})

const formRules = {
  user_id: [
    { required: true, message: '请选择用户', trigger: 'change' }
  ],
  course_id: [
    { required: true, message: '请选择课程', trigger: 'change' }
  ],
  status: [
    { required: true, message: '请选择预约状态', trigger: 'change' }
  ]
}


const tableData = ref([])

const formTitle = computed(() => isEdit.value ? '编辑预约' : '新增预约')

const getReservationStatusText = (status) => {
  const map = {
    pending: '待确认',
    confirmed: '已确认',
    completed: '已完成',
    cancelled: '已取消'
  }
  return map[status] || status
}

const getReservationStatusType = (status) => {
  const map = {
    pending: 'warning',
    confirmed: 'success',
    completed: 'info',
    cancelled: 'info'
  }
  return map[status] || 'info'
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
    if (searchForm.course_name) {
      params.course_name = searchForm.course_name
    }
    if (searchForm.dateRange && searchForm.dateRange.length === 2) {
      params.start_date = searchForm.dateRange[0]
      params.end_date = searchForm.dateRange[1]
    }
    if (searchForm.status) {
      params.status = searchForm.status
    }
    const response = await getAdminReservations(params)
    tableData.value = response.list || []
    pagination.total = response.total || 0
  } catch (error) {
    ElMessage.error('获取预约列表失败')
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
    course_name: '',
    dateRange: null,
    status: ''
  })
  handleSearch()
}

const handleAdd = () => {
  isEdit.value = false
  Object.assign(formData, {
    reservation_id: null,
    user_id: null,
    course_id: null,
    status: 'pending'
  })
  formVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(formData, {
    reservation_id: row.reservation_id,
    user_id: row.user_id ? Number(row.user_id) : null,
    course_id: row.course_id ? Number(row.course_id) : null,
    status: row.status
  })
  formVisible.value = true
}

const handleView = (row) => {
  selectedRow.value = row
  detailVisible.value = true
}

const handleConfirm = (row) => {
  ElMessageBox.confirm('确定要确认该预约吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'info'
  }).then(async () => {
    try {
      await updateAdminReservation(row.reservation_id, { status: 'confirmed' })
      ElMessage.success('确认成功')
      fetchData()
    } catch (error) {
      ElMessage.error('确认失败')
    }
  }).catch(() => {})
}

const handleCancel = (row) => {
  ElMessageBox.confirm('确定要取消该预约吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await updateAdminReservation(row.reservation_id, { status: 'cancelled' })
      ElMessage.success('取消成功')
      fetchData()
    } catch (error) {
      ElMessage.error('取消失败')
    }
  }).catch(() => {})
}

const handleComplete = (row) => {
  ElMessageBox.confirm('确定要标记该预约为已完成吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'info'
  }).then(async () => {
    try {
      await updateAdminReservation(row.reservation_id, { status: 'completed' })
      ElMessage.success('标记成功')
      fetchData()
    } catch (error) {
      ElMessage.error('标记失败')
    }
  }).catch(() => {})
}

const handleViewAttendance = (row) => {
  if (row.has_attendance && row.attendance) {
    selectedRow.value = row
    detailVisible.value = true
  } else {
    ElMessage.info('该预约尚未签到')
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (isEdit.value) {
          await updateAdminReservation(formData.reservation_id, formData)
          ElMessage.success('编辑成功')
        } else {
          await createAdminReservation(formData)
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
  ElMessageBox.confirm('确定要删除该预约吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteAdminReservation(row.reservation_id)
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

const loadUsersAndCourses = async () => {
  try {
    const [usersRes, coursesRes] = await Promise.all([
      getAllMembers(),
      getAllCourses()
    ])
    users.value = (usersRes.list || []).map(normalizeUserOption)
    courses.value = (coursesRes.list || []).map(normalizeCourseOption)
  } catch (error) {
    console.error('加载用户和课程列表失败', error)
  }
}

onMounted(() => {
  fetchData()
  loadUsersAndCourses()
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

