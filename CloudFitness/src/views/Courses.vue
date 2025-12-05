<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">课程中心</h2>
      <div class="header-actions">
        <el-date-picker
          v-model="filterDate"
          type="date"
          placeholder="选择日期"
          format="YYYY-MM-DD"
          value-format="YYYY-MM-DD"
          @change="handleFilterChange"
        />
        <el-select
          v-model="filterInstructor"
          placeholder="选择教练"
          clearable
          @change="handleFilterChange"
        >
          <el-option
            v-for="instructor in instructors"
            :key="instructor.user_id"
            :label="instructor.username"
            :value="instructor.user_id"
          />
        </el-select>
      </div>
    </div>

    <div v-loading="loading" class="courses-container">
      <el-empty v-if="!loading && courses.length === 0" description="暂无课程" />
      
      <el-row :gutter="20" v-else>
        <el-col
          v-for="course in courses"
          :key="course.course_id"
          :xs="24"
          :sm="12"
          :md="8"
          :lg="6"
        >
          <div class="course-card card" @click="showCourseDetail(course)">
            <div class="course-header">
              <h3 class="course-name">{{ course.course_name }}</h3>
              <el-tag
                :type="getCourseStatusType(course)"
                size="small"
              >
                {{ getCourseStatus(course) }}
              </el-tag>
            </div>
            <div class="course-info">
              <div class="info-item">
                <el-icon><Clock /></el-icon>
                <span>{{ formatDate(course.schedule, 'YYYY-MM-DD HH:mm') }}</span>
              </div>
              <div class="info-item">
                <el-icon><User /></el-icon>
                <span>{{ course.instructor_name || '待定' }}</span>
              </div>
              <div class="info-item">
                <el-icon><UserFilled /></el-icon>
                <span>{{ course.reserved_count || 0 }} / {{ course.capacity || 20 }} 人</span>
              </div>
            </div>
            <div class="course-actions">
              <el-button
                type="primary"
                :disabled="!canReserve(course) && !isReserved(course)"
                @click.stop="handleReserve(course)"
              >
                {{ isReserved(course) ? '取消预约' : '立即预约' }}
              </el-button>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 课程详情弹窗 -->
    <el-dialog
      v-model="detailVisible"
      :title="selectedCourse?.course_name"
      width="600px"
    >
      <div v-if="selectedCourse" class="course-detail">
        <div class="detail-section">
          <h4>课程信息</h4>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="开课时间">
              {{ formatDate(selectedCourse.schedule, 'YYYY-MM-DD HH:mm') }}
            </el-descriptions-item>
            <el-descriptions-item label="课程状态">
              <el-tag :type="getCourseStatusType(selectedCourse)">
                {{ getCourseStatus(selectedCourse) }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="教练">
              {{ selectedCourse.instructor_name || '待定' }}
            </el-descriptions-item>
            <el-descriptions-item label="预约人数">
              {{ selectedCourse.reserved_count || 0 }} / {{ selectedCourse.capacity || 20 }}
            </el-descriptions-item>
          </el-descriptions>
        </div>
        <div class="detail-section" v-if="selectedCourse.description">
          <h4>课程介绍</h4>
          <p>{{ selectedCourse.description }}</p>
        </div>
        <div class="detail-section" v-if="isUpcoming(selectedCourse)">
          <h4>开课倒计时</h4>
          <div class="countdown">{{ getTimeRemaining(selectedCourse.schedule) }}</div>
        </div>
      </div>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
        <el-button
          type="primary"
          :disabled="!canReserve(selectedCourse)"
          @click="handleReserve(selectedCourse)"
        >
          {{ isReserved(selectedCourse) ? '已预约' : '立即预约' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getCourses, reserveCourse } from '@/api/course'
import { useReservationStore } from '@/stores/reservation'
import { formatDate, getTimeRemaining } from '@/utils/date'
import { Clock, User, UserFilled } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import dayjs from 'dayjs'

const reservationStore = useReservationStore()

const courses = ref([])
const instructors = ref([])

// 兼容后端字段命名，统一 course_id 等
const normalizeCourse = (item) => {
  if (!item) return null
  return {
    ...item,
    course_id: item.course_id ?? item.courseId,
    course_name: item.course_name ?? item.courseName,
    instructor_id: item.instructor_id ?? item.instructorId,
    instructor_name: item.instructor_name ?? item.instructorName,
    schedule: item.schedule,
    capacity: item.capacity,
    reserved_count: item.reserved_count ?? item.reservedCount,
    description: item.description,
    status: item.status
  }
}
const loading = ref(false)
const filterDate = ref(null)
const filterInstructor = ref(null)
const detailVisible = ref(false)
const selectedCourse = ref(null)

const canReserve = (course) => {
  if (!course) return false
  const now = dayjs()
  const courseTime = dayjs(course.schedule)
  if (courseTime.isBefore(now)) return false
  if (isReserved(course)) return false
  if (course.reserved_count >= (course.capacity || 20)) return false
  return true
}

const getReservationForCourse = (course) => {
  if (!course) return null
  return reservationStore.reservations.find(
    r => r.course_id === course.course_id && (r.status === 'pending' || r.status === 'confirmed')
  )
}

const isReserved = (course) => !!getReservationForCourse(course)

const isUpcoming = (course) => {
  if (!course) return false
  const now = dayjs()
  const courseTime = dayjs(course.schedule)
  return courseTime.isAfter(now)
}

const getCourseStatus = (course) => {
  if (!course) return '未知'
  const now = dayjs()
  const courseTime = dayjs(course.schedule)
  
  if (courseTime.isBefore(now)) return '已结束'
  if (courseTime.diff(now, 'hour') < 1) return '即将开始'
  return '未开始'
}

const getCourseStatusType = (course) => {
  const status = getCourseStatus(course)
  if (status === '已结束') return 'info'
  if (status === '即将开始') return 'warning'
  return 'success'
}

const fetchCourses = async () => {
  loading.value = true
  try {
    const params = {}
    if (filterDate.value) {
      params.date = filterDate.value
    }
    if (filterInstructor.value) {
      params.instructor_id = filterInstructor.value
    }
    const data = await getCourses(params)
    const list = data.courses || data.list || data
    courses.value = (list || []).map(normalizeCourse)
    instructors.value = (data.instructors || []).map(item => ({
      ...item,
      user_id: item.user_id ?? item.userId,
      username: item.username
    }))
  } catch (error) {
    ElMessage.error('获取课程列表失败')
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleFilterChange = () => {
  fetchCourses()
}

const showCourseDetail = (course) => {
  selectedCourse.value = course
  detailVisible.value = true
}

const handleReserve = async (course) => {
  const existing = getReservationForCourse(course)
  if (existing) {
    // 退课（取消预约）
    try {
      await ElMessageBox.confirm(
        `确定取消课程 "${course.course_name}" 的预约吗？`,
        '取消预约',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }
      )
      const res = await reservationStore.cancel(existing.reservation_id)
      if (res.success) {
        ElMessage.success('已取消预约')
        await fetchCourses()
        detailVisible.value = false
      } else {
        ElMessage.error(res.message || '取消失败')
      }
    } catch (error) {
      // 用户取消不提示
    }
    return
  }

  if (!canReserve(course)) {
    ElMessage.warning('无法预约此课程')
    return
  }

  try {
    await ElMessageBox.confirm(
      `确定要预约课程"${course.course_name}"吗？`,
      '确认预约',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'info'
      }
    )

    await reserveCourse(course.course_id)
    ElMessage.success('预约成功')
    await reservationStore.fetchReservations()
    await fetchCourses()
    detailVisible.value = false
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '预约失败')
    }
  }
}

onMounted(async () => {
  await reservationStore.fetchReservations()
  await fetchCourses()
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
  margin-bottom: 24px;
  
  .page-title {
    font-size: 24px;
    font-weight: 600;
    color: #303133;
  }
  
  .header-actions {
    display: flex;
    gap: 12px;
  }
}

.courses-container {
  min-height: 400px;
}

.course-card {
  cursor: pointer;
  transition: all 0.3s ease;
  margin-bottom: 20px;
  
  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  }
  
  .course-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 16px;
    
    .course-name {
      font-size: 18px;
      font-weight: 600;
      color: #303133;
      flex: 1;
    }
  }
  
  .course-info {
    margin-bottom: 16px;
    
    .info-item {
      display: flex;
      align-items: center;
      gap: 8px;
      margin-bottom: 8px;
      font-size: 14px;
      color: #606266;
      
      &:last-child {
        margin-bottom: 0;
      }
      
      .el-icon {
        color: #909399;
      }
    }
  }
  
  .course-actions {
    .el-button {
      width: 100%;
    }
  }
}

.course-detail {
  .detail-section {
    margin-bottom: 24px;
    
    &:last-child {
      margin-bottom: 0;
    }
    
    h4 {
      font-size: 16px;
      font-weight: 600;
      color: #303133;
      margin-bottom: 12px;
    }
    
    p {
      font-size: 14px;
      color: #606266;
      line-height: 1.6;
    }
    
    .countdown {
      font-size: 24px;
      font-weight: 600;
      color: #667eea;
      text-align: center;
      padding: 20px;
      background: #f5f7fa;
      border-radius: 8px;
    }
  }
}
</style>

