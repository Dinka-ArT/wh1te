<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">签到管理</h2>
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
        <el-form-item label="签到时间">
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
        <el-form-item label="是否准时">
          <el-select
            v-model="searchForm.is_on_time"
            placeholder="请选择"
            clearable
            style="width: 150px"
          >
            <el-option label="全部" value="" />
            <el-option label="准时" :value="true" />
            <el-option label="迟到" :value="false" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">
            搜索
          </el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
          <el-button type="success" :icon="Download" @click="handleExport">
            导出
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :xs="24" :sm="12" :md="6">
        <div class="stat-card card">
          <div class="stat-label">总签到数</div>
          <div class="stat-value">{{ stats.total }}</div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <div class="stat-card card">
          <div class="stat-label">准时签到</div>
          <div class="stat-value text-success">{{ stats.onTime }}</div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <div class="stat-card card">
          <div class="stat-label">迟到签到</div>
          <div class="stat-value text-warning">{{ stats.late }}</div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <div class="stat-card card">
          <div class="stat-label">出勤率</div>
          <div class="stat-value text-primary">{{ stats.attendanceRate }}%</div>
        </div>
      </el-col>
    </el-row>

    <!-- 列表区 -->
    <div class="table-container card">
      <el-table
        v-loading="loading"
        :data="tableData"
        stripe
        style="width: 100%"
      >
        <el-table-column prop="attendance_id" label="签到ID" width="80" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="phone_number" label="手机号" width="130" />
        <el-table-column prop="course_name" label="课程名称" width="200" />
        <el-table-column label="课程时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.schedule, 'YYYY-MM-DD HH:mm') }}
          </template>
        </el-table-column>
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
        <el-table-column label="迟到时长" width="120">
          <template #default="{ row }">
            <span v-if="!row.is_on_time && row.late_minutes">
              {{ row.late_minutes }} 分钟
            </span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
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

    <!-- 详情弹窗 -->
    <el-dialog
      v-model="detailVisible"
      title="签到详情"
      width="700px"
    >
      <div v-if="selectedRow" class="detail-content">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="签到ID">
            {{ selectedRow.attendance_id }}
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
          <el-descriptions-item label="课程时间">
            {{ formatDate(selectedRow.schedule, 'YYYY-MM-DD HH:mm') }}
          </el-descriptions-item>
          <el-descriptions-item label="签到时间">
            {{ formatDate(selectedRow.check_in_time) }}
          </el-descriptions-item>
          <el-descriptions-item label="是否准时">
            <el-tag :type="selectedRow.is_on_time ? 'success' : 'warning'">
              {{ selectedRow.is_on_time ? '准时' : '迟到' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="迟到时长">
            <span v-if="!selectedRow.is_on_time && selectedRow.late_minutes">
              {{ selectedRow.late_minutes }} 分钟
            </span>
            <span v-else>-</span>
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
  Search,
  Refresh,
  View,
  Delete,
  Download
} from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getAdminAttendance,
  deleteAdminAttendance
} from '@/api/admin'

const loading = ref(false)
const detailVisible = ref(false)
const selectedRow = ref(null)

const searchForm = reactive({
  username: '',
  phone_number: '',
  course_name: '',
  dateRange: null,
  is_on_time: null
})

const pagination = reactive({
  page: 1,
  page_size: 20,
  total: 0
})


const tableData = ref([])

const stats = computed(() => {
  const total = tableData.value.length
  const onTime = tableData.value.filter(item => item.is_on_time).length
  const late = total - onTime
  const attendanceRate = total > 0 ? ((onTime / total) * 100).toFixed(1) : 0
  return {
    total,
    onTime,
    late,
    attendanceRate
  }
})

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
    if (searchForm.is_on_time !== null) {
      params.is_on_time = searchForm.is_on_time
    }
    const response = await getAdminAttendance(params)
    tableData.value = response.list || []
    pagination.total = response.total || 0
  } catch (error) {
    ElMessage.error('获取签到记录失败')
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
    is_on_time: null
  })
  handleSearch()
}

const handleView = (row) => {
  selectedRow.value = row
  detailVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该签到记录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteAdminAttendance(row.attendance_id)
      ElMessage.success('删除成功')
      fetchData()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

const handleExport = () => {
  ElMessage.success('导出功能开发中')
}

const handleSizeChange = (size) => {
  pagination.page_size = size
  fetchData()
}

const handlePageChange = (page) => {
  pagination.page = page
  fetchData()
}

onMounted(() => {
  fetchData()
})
</script>

<style lang="scss" scoped>
.page-container {
  padding: 24px;
  height: 100%;
  overflow-y: auto;
}

.page-header {
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

.stats-row {
  margin-bottom: 20px;
  
  .stat-card {
    padding: 20px;
    text-align: center;
    
    .stat-label {
      font-size: 14px;
      color: #909399;
      margin-bottom: 8px;
    }
    
    .stat-value {
      font-size: 32px;
      font-weight: 600;
      color: #303133;
      
      &.text-success {
        color: #67c23a;
      }
      
      &.text-warning {
        color: #e6a23c;
      }
      
      &.text-primary {
        color: #667eea;
      }
    }
  }
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

