<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">课程管理</h2>
      <el-button type="primary" :icon="Plus" @click="handleAdd">
        新增课程
      </el-button>
    </div>

    <!-- 搜索区 -->
    <div class="search-container card">
      <el-form :model="searchForm" :inline="true">
        <el-form-item label="课程名称">
          <el-input
            v-model="searchForm.course_name"
            placeholder="请输入课程名称"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="教练">
          <el-select
            v-model="searchForm.instructor_id"
            placeholder="请选择教练"
            clearable
            filterable
            style="width: 200px"
          >
            <el-option
              v-for="coach in coaches"
              :key="coach.user_id"
              :label="coach.username"
              :value="coach.user_id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="开课时间">
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
        <el-form-item label="课程状态">
          <el-select
            v-model="searchForm.status"
            placeholder="请选择"
            clearable
            style="width: 150px"
          >
            <el-option label="全部" value="" />
            <el-option label="未开始" value="upcoming" />
            <el-option label="进行中" value="ongoing" />
            <el-option label="已结束" value="completed" />
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
        <el-table-column prop="course_id" label="课程ID" width="80" />
        <el-table-column prop="course_name" label="课程名称" width="200" />
        <el-table-column label="教练姓名" width="120">
          <template #default="{ row }">
            {{ row.instructor_name || '待定' }}
          </template>
        </el-table-column>
        <el-table-column label="开课时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.schedule, 'YYYY-MM-DD HH:mm') }}
          </template>
        </el-table-column>
        <el-table-column label="已预约人数" width="120">
          <template #default="{ row }">
            <el-tag type="info">{{ row.reserved_count || 0 }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="capacity" label="课程容量" width="100" />
        <el-table-column label="课程状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getCourseStatusType(row.status)">
              {{ getCourseStatusText(row.status) }}
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
              type="info"
              link
              size="small"
              :icon="Document"
              @click="handleViewReservations(row)"
            >
              预约列表
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
        <el-form-item label="课程名称" prop="course_name">
          <el-input
            v-model="formData.course_name"
            placeholder="请输入课程名称（1-255位）"
          />
        </el-form-item>
        <el-form-item label="教练" prop="instructor_id">
          <el-select
            v-model="formData.instructor_id"
            placeholder="请选择教练"
            filterable
            style="width: 100%"
          >
            <el-option
              v-for="coach in coaches"
              :key="coach.user_id"
              :label="coach.username"
              :value="coach.user_id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="开课时间" prop="schedule">
          <el-date-picker
            v-model="formData.schedule"
            type="datetime"
            placeholder="请选择开课时间"
            format="YYYY-MM-DD HH:mm"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="课程容量" prop="capacity">
          <el-input-number
            v-model="formData.capacity"
            :min="1"
            :max="100"
            placeholder="请输入课程容量（1-100）"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="课程描述" prop="description">
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="4"
            placeholder="请输入课程描述（最多1000字）"
            maxlength="1000"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="课程状态" prop="status">
          <el-select
            v-model="formData.status"
            placeholder="请选择状态"
            style="width: 100%"
          >
            <el-option label="未开始" value="upcoming" />
            <el-option label="进行中" value="ongoing" />
            <el-option label="已结束" value="completed" />
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
      title="课程详情"
      width="700px"
    >
      <div v-if="selectedRow" class="detail-content">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="课程ID">
            {{ selectedRow.course_id }}
          </el-descriptions-item>
          <el-descriptions-item label="课程名称">
            {{ selectedRow.course_name }}
          </el-descriptions-item>
          <el-descriptions-item label="教练">
            {{ selectedRow.instructor_name || '待定' }}
          </el-descriptions-item>
          <el-descriptions-item label="开课时间">
            {{ formatDate(selectedRow.schedule, 'YYYY-MM-DD HH:mm') }}
          </el-descriptions-item>
          <el-descriptions-item label="课程容量">
            {{ selectedRow.capacity }}
          </el-descriptions-item>
          <el-descriptions-item label="已预约人数">
            <el-tag type="info">{{ selectedRow.reserved_count || 0 }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="课程状态">
            <el-tag :type="getCourseStatusType(selectedRow.status)">
              {{ getCourseStatusText(selectedRow.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="课程描述" :span="2">
            {{ selectedRow.description || '-' }}
          </el-descriptions-item>
        </el-descriptions>
      </div>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 预约列表弹窗 -->
    <el-dialog
      v-model="reservationsVisible"
      title="预约列表"
      width="900px"
    >
      <el-table :data="courseReservations" stripe style="width: 100%">
        <el-table-column prop="reservation_id" label="预约ID" width="80" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="phone_number" label="手机号" width="130" />
        <el-table-column label="预约时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.reservation_date) }}
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
      </el-table>
      <template #footer>
        <el-button @click="reservationsVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 签到记录弹窗 -->
    <el-dialog
      v-model="attendanceVisible"
      title="签到记录"
      width="900px"
    >
      <el-table :data="courseAttendance" stripe style="width: 100%">
        <el-table-column prop="attendance_id" label="签到ID" width="80" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="phone_number" label="手机号" width="130" />
        <el-table-column label="签到时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.check_in_time) }}
          </template>
        </el-table-column>
        <el-table-column label="是否准时" width="100">
          <template #default="{ row }">
            <el-tag :type="row.is_on_time ? 'success' : 'warning'">
              {{ row.is_on_time ? '准时' : '迟到' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="迟到时长" width="100">
          <template #default="{ row }">
            <span v-if="!row.is_on_time && row.late_minutes">
              {{ row.late_minutes }} 分钟
            </span>
            <span v-else>-</span>
          </template>
        </el-table-column>
      </el-table>
      <template #footer>
        <el-button @click="attendanceVisible = false">关闭</el-button>
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
  Document,
  Check
} from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getAdminCourses,
  createAdminCourse,
  updateAdminCourse,
  deleteAdminCourse,
  getAdminCourseDetail,
  getAllCoaches,
  getAdminReservations
} from '@/api/admin'

const loading = ref(false)
const formVisible = ref(false)
const detailVisible = ref(false)
const reservationsVisible = ref(false)
const attendanceVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)
const selectedRow = ref(null)
const courseReservations = ref([])
const courseAttendance = ref([])

const searchForm = reactive({
  course_name: '',
  instructor_id: null,
  dateRange: null,
  status: ''
})

const pagination = reactive({
  page: 1,
  page_size: 20,
  total: 0
})

const formData = reactive({
  course_id: null,
  course_name: '',
  instructor_id: null,
  schedule: '',
  capacity: 20,
  description: '',
  status: 'upcoming'
})

const coaches = ref([])

// 兼容后端教练列表字段（userId/phoneNumber）并统一为 number 类型的 user_id，避免下拉选择失效
const normalizeCoachOption = (item) => ({
  ...item,
  user_id: Number(item.user_id ?? item.userId),
  username: item.username,
  phone_number: item.phone_number ?? item.phoneNumber
})

const formRules = {
  course_name: [
    { required: true, message: '请输入课程名称', trigger: 'blur' },
    { max: 255, message: '课程名称最多255位', trigger: 'blur' }
  ],
  instructor_id: [
    { required: true, message: '请选择教练', trigger: 'change' }
  ],
  schedule: [
    { required: true, message: '请选择开课时间', trigger: 'change' }
  ],
  capacity: [
    { required: true, message: '请输入课程容量', trigger: 'blur' },
    { type: 'number', min: 1, max: 100, message: '课程容量范围为1-100', trigger: 'blur' }
  ],
  description: [
    { max: 1000, message: '课程描述最多1000字', trigger: 'blur' }
  ]
}


const tableData = ref([])

const formTitle = computed(() => isEdit.value ? '编辑课程' : '新增课程')

const getCourseStatusText = (status) => {
  const map = {
    upcoming: '未开始',
    ongoing: '进行中',
    completed: '已结束'
  }
  return map[status] || status
}

const getCourseStatusType = (status) => {
  const map = {
    upcoming: 'success',
    ongoing: 'warning',
    completed: 'info'
  }
  return map[status] || 'info'
}

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
    if (searchForm.course_name) {
      params.course_name = searchForm.course_name
    }
    if (searchForm.instructor_id) {
      params.instructor_id = searchForm.instructor_id
    }
    if (searchForm.dateRange && searchForm.dateRange.length === 2) {
      params.start_date = searchForm.dateRange[0]
      params.end_date = searchForm.dateRange[1]
    }
    if (searchForm.status) {
      params.status = searchForm.status
    }
    const response = await getAdminCourses(params)
    tableData.value = response.list || []
    pagination.total = response.total || 0
  } catch (error) {
    ElMessage.error('获取课程列表失败')
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
    course_name: '',
    instructor_id: null,
    dateRange: null,
    status: ''
  })
  handleSearch()
}

const handleAdd = () => {
  isEdit.value = false
  Object.assign(formData, {
    course_id: null,
    course_name: '',
    instructor_id: null,
    schedule: '',
    capacity: 20,
    description: '',
    status: 'upcoming'
  })
  formVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(formData, {
    course_id: row.course_id,
    course_name: row.course_name,
    instructor_id: row.instructor_id ? Number(row.instructor_id) : null,
    schedule: row.schedule,
    capacity: row.capacity,
    description: row.description || '',
    status: row.status
  })
  formVisible.value = true
}

const handleView = (row) => {
  selectedRow.value = row
  detailVisible.value = true
}

const handleViewReservations = async (row) => {
  selectedRow.value = row
  try {
    const response = await getAdminReservations({ course_id: row.course_id })
    courseReservations.value = response.list || []
    reservationsVisible.value = true
  } catch (error) {
    ElMessage.error('获取预约列表失败')
  }
}

const handleViewAttendance = async (row) => {
  selectedRow.value = row
  try {
    const { getAdminAttendance } = await import('@/api/admin')
    const response = await getAdminAttendance({ course_id: row.course_id, page: 1, page_size: 1000 })
    courseAttendance.value = response.list || []
    attendanceVisible.value = true
  } catch (error) {
    ElMessage.error('获取签到记录失败')
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (isEdit.value) {
          await updateAdminCourse(formData.course_id, formData)
          ElMessage.success('编辑成功')
        } else {
          await createAdminCourse(formData)
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
  ElMessageBox.confirm('确定要删除该课程吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteAdminCourse(row.course_id)
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

const loadCoaches = async () => {
  try {
    const response = await getAllCoaches()
    coaches.value = (response.list || []).map(normalizeCoachOption)
  } catch (error) {
    console.error('加载教练列表失败', error)
  }
}

onMounted(() => {
  fetchData()
  loadCoaches()
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

