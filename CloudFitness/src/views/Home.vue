<template>
  <div class="page-container">
    <div class="welcome-section">
      <h1 class="welcome-title">
        欢迎回来，<span class="username">{{ userStore.userInfo?.username || '会员' }}</span>
      </h1>
      <p class="welcome-desc">今天是 {{ currentDate }}</p>
    </div>

    <el-row :gutter="20" class="cards-row">
      <!-- 会员卡状态卡片 -->
      <el-col :xs="24" :sm="12" :md="8">
        <div class="card membership-card">
          <div class="card-header">
            <el-icon class="card-icon"><CreditCard /></el-icon>
            <span class="card-title">我的会员卡</span>
          </div>
          <div class="card-content">
            <div v-if="membership" class="membership-info">
              <div class="membership-type">{{ membership.membership_type || '标准会员' }}</div>
              <div class="membership-date">
                <span>有效期至：{{ formatDate(membership.expiry_date, 'YYYY-MM-DD') }}</span>
                <el-tag
                  :type="isExpired(membership.expiry_date) ? 'danger' : 'success'"
                  size="small"
                  class="status-tag"
                >
                  {{ isExpired(membership.expiry_date) ? '已过期' : '有效' }}
                </el-tag>
              </div>
            </div>
            <el-empty v-else description="暂无会员卡信息" :image-size="80" />
          </div>
        </div>
      </el-col>

      <!-- 今日课程卡片 -->
      <el-col :xs="24" :sm="12" :md="8">
        <div class="card today-course-card">
          <div class="card-header">
            <el-icon class="card-icon"><Calendar /></el-icon>
            <span class="card-title">今日课程</span>
          </div>
          <div class="card-content">
            <div v-if="todayCourses.length > 0" class="course-list">
              <div
                v-for="course in todayCourses"
                :key="course.course_id"
                class="course-item"
              >
                <div class="course-name">{{ course.course_name }}</div>
                <div class="course-time">{{ formatDate(course.schedule, 'HH:mm') }}</div>
              </div>
            </div>
            <el-empty v-else description="今日暂无课程" :image-size="80" />
          </div>
        </div>
      </el-col>

      <!-- 预约概览卡片 -->
      <el-col :xs="24" :sm="12" :md="8">
        <div class="card reservation-card">
          <div class="card-header">
            <el-icon class="card-icon"><Document /></el-icon>
            <span class="card-title">预约概览</span>
          </div>
          <div class="card-content">
            <div class="reservation-stats">
              <div class="stat-item">
                <div class="stat-value">{{ reservationStats.total }}</div>
                <div class="stat-label">总预约</div>
              </div>
              <div class="stat-item">
                <div class="stat-value text-primary">{{ reservationStats.pending }}</div>
                <div class="stat-label">待开始</div>
              </div>
              <div class="stat-item">
                <div class="stat-value text-success">{{ reservationStats.completed }}</div>
                <div class="stat-label">已完成</div>
              </div>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="charts-row">
      <!-- 训练趋势图 -->
      <el-col :xs="24" :md="12">
        <div class="card chart-card">
          <div class="card-header">
            <span class="card-title">训练趋势</span>
          </div>
          <div class="chart-container">
            <v-chart
              v-if="attendanceChartData"
              :option="attendanceChartData"
              class="chart"
            />
            <el-skeleton v-else :rows="5" animated />
          </div>
        </div>
      </el-col>

      <!-- 课程类型分布 -->
      <el-col :xs="24" :md="12">
        <div class="card chart-card">
          <div class="card-header">
            <span class="card-title">课程类型分布</span>
          </div>
          <div class="chart-container">
            <v-chart
              v-if="courseTypeChartData"
              :option="courseTypeChartData"
              class="chart"
            />
            <el-skeleton v-else :rows="5" animated />
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 快捷入口 -->
    <div class="card quick-actions-card">
      <div class="card-header">
        <span class="card-title">快捷入口</span>
      </div>
      <div class="quick-actions">
        <el-button
          type="primary"
          :icon="Calendar"
          @click="$router.push('/courses')"
        >
          课程预约
        </el-button>
        <el-button
          :icon="Box"
          @click="$router.push('/lockers')"
        >
          储物柜
        </el-button>
        <el-button
          :icon="User"
          @click="$router.push('/profile')"
        >
          个人资料
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useReservationStore } from '@/stores/reservation'
import { getMembership } from '@/api/membership'
import { getCourses } from '@/api/course'
import { getAttendance } from '@/api/attendance'
import { formatDate, isExpired } from '@/utils/date'
import {
  CreditCard,
  Calendar,
  Document,
  Box,
  User
} from '@element-plus/icons-vue'
import dayjs from 'dayjs'

const router = useRouter()
const userStore = useUserStore()
const reservationStore = useReservationStore()

const membership = ref(null)
const todayCourses = ref([])
const attendanceChartData = ref(null)
const courseTypeChartData = ref(null)

const currentDate = computed(() => {
  return dayjs().format('YYYY年MM月DD日 dddd')
})

const reservationStats = computed(() => {
  const reservations = reservationStore.reservations
  return {
    total: reservations.length,
    pending: reservations.filter(r => r.status === 'pending' || r.status === 'confirmed').length,
    completed: reservations.filter(r => r.status === 'completed').length
  }
})

const fetchData = async () => {
  try {
    // 获取会员卡信息
    const membershipData = await getMembership()
    membership.value = membershipData
  } catch (error) {
    // 使用模拟数据
    membership.value = {
      membership_id: 1,
      membership_type: '年度会员',
      start_date: dayjs().subtract(6, 'month').toISOString(),
      expiry_date: dayjs().add(6, 'month').toISOString()
    }
  }

  try {
    // 获取今日课程
    const today = dayjs().format('YYYY-MM-DD')
    const coursesData = await getCourses({ date: today })
    todayCourses.value = Array.isArray(coursesData) 
      ? coursesData.filter(course => {
          const courseDate = dayjs(course.schedule).format('YYYY-MM-DD')
          return courseDate === today
        })
      : (coursesData.courses || []).filter(course => {
          const courseDate = dayjs(course.schedule).format('YYYY-MM-DD')
          return courseDate === today
        })
  } catch (error) {
    // 使用模拟数据
    todayCourses.value = [
      {
        course_id: 1,
        course_name: '瑜伽课程',
        schedule: dayjs().hour(10).minute(0).toISOString(),
        instructor_name: '张教练'
      },
      {
        course_id: 2,
        course_name: '有氧运动',
        schedule: dayjs().hour(19).minute(30).toISOString(),
        instructor_name: '李教练'
      }
    ]
  }

  try {
    // 获取签到数据用于图表
    const attendanceData = await getAttendance({ limit: 30 })
    generateCharts(Array.isArray(attendanceData) ? attendanceData : (attendanceData.attendance || []))
  } catch (error) {
    // 使用模拟数据生成图表
    const mockAttendanceData = []
    for (let i = 0; i < 30; i++) {
      mockAttendanceData.push({
        check_in_time: dayjs().subtract(i, 'day').toISOString(),
        course_name: ['瑜伽课程', '有氧运动', '力量训练', '普拉提'][i % 4]
      })
    }
    generateCharts(mockAttendanceData)
  }
}

const generateCharts = (attendanceData) => {
  // 训练趋势图
  const monthlyData = {}
  attendanceData.forEach(item => {
    const month = dayjs(item.check_in_time).format('YYYY-MM')
    monthlyData[month] = (monthlyData[month] || 0) + 1
  })

  const months = Object.keys(monthlyData).sort()
  const counts = months.map(month => monthlyData[month])

  attendanceChartData.value = {
    tooltip: {
      trigger: 'axis'
    },
    xAxis: {
      type: 'category',
      data: months
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        data: counts,
        type: 'line',
        smooth: true,
        itemStyle: {
          color: '#667eea'
        },
        areaStyle: {
          color: {
            type: 'linear',
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [
              { offset: 0, color: 'rgba(102, 126, 234, 0.3)' },
              { offset: 1, color: 'rgba(102, 126, 234, 0.1)' }
            ]
          }
        }
      }
    ]
  }

  // 课程类型分布图
  const courseTypeData = {}
  attendanceData.forEach(item => {
    const type = item.course_name || '其他'
    courseTypeData[type] = (courseTypeData[type] || 0) + 1
  })

  courseTypeChartData.value = {
    tooltip: {
      trigger: 'item'
    },
    legend: {
      orient: 'vertical',
      left: 'left'
    },
    series: [
      {
        type: 'pie',
        radius: '60%',
        data: Object.entries(courseTypeData).map(([name, value]) => ({
          name,
          value
        })),
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        }
      }
    ]
  }
}

onMounted(async () => {
  await reservationStore.fetchReservations()
  await fetchData()
})
</script>

<style lang="scss" scoped>
.page-container {
  padding: 24px;
  height: 100%;
  overflow-y: auto;
}

.welcome-section {
  margin-bottom: 24px;
  
  .welcome-title {
    font-size: 28px;
    font-weight: 600;
    color: #303133;
    margin-bottom: 8px;
    
    .username {
      color: #667eea;
    }
  }
  
  .welcome-desc {
    font-size: 14px;
    color: #909399;
  }
}

.cards-row {
  margin-bottom: 20px;
}

.card {
  height: 100%;
  min-height: 200px;
  
  .card-header {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-bottom: 16px;
    padding-bottom: 12px;
    border-bottom: 1px solid #f0f0f0;
    
    .card-icon {
      font-size: 20px;
      color: #667eea;
    }
    
    .card-title {
      font-size: 16px;
      font-weight: 600;
      color: #303133;
    }
  }
  
  .card-content {
    min-height: 120px;
  }
}

.membership-info {
  .membership-type {
    font-size: 24px;
    font-weight: 600;
    color: #303133;
    margin-bottom: 12px;
  }
  
  .membership-date {
    display: flex;
    align-items: center;
    justify-content: space-between;
    font-size: 14px;
    color: #606266;
    
    .status-tag {
      margin-left: 8px;
    }
  }
}

.course-list {
  .course-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 12px 0;
    border-bottom: 1px solid #f0f0f0;
    
    &:last-child {
      border-bottom: none;
    }
    
    .course-name {
      font-size: 14px;
      color: #303133;
      font-weight: 500;
    }
    
    .course-time {
      font-size: 14px;
      color: #909399;
    }
  }
}

.reservation-stats {
  display: flex;
  justify-content: space-around;
  align-items: center;
  
  .stat-item {
    text-align: center;
    
    .stat-value {
      font-size: 32px;
      font-weight: 600;
      color: #303133;
      margin-bottom: 8px;
      
      &.text-primary {
        color: #667eea;
      }
      
      &.text-success {
        color: #67c23a;
      }
    }
    
    .stat-label {
      font-size: 14px;
      color: #909399;
    }
  }
}

.charts-row {
  margin-bottom: 20px;
}

.chart-card {
  .chart-container {
    height: 300px;
    
    .chart {
      width: 100%;
      height: 100%;
    }
  }
}

.quick-actions-card {
  .quick-actions {
    display: flex;
    gap: 12px;
    flex-wrap: wrap;
  }
}
</style>

