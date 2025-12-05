<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">数据统计</h2>
    </div>

    <!-- 统计指标卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :xs="24" :sm="12" :md="6" :lg="4">
        <div class="stat-card card">
          <div class="stat-icon">
            <el-icon :size="32" color="#667eea"><User /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-label">总用户数</div>
            <div class="stat-value">{{ stats.total_users }}</div>
          </div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6" :lg="4">
        <div class="stat-card card">
          <div class="stat-icon">
            <el-icon :size="32" color="#67c23a"><UserFilled /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-label">会员数</div>
            <div class="stat-value">{{ stats.total_members }}</div>
          </div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6" :lg="4">
        <div class="stat-card card">
          <div class="stat-icon">
            <el-icon :size="32" color="#409eff"><UserFilled /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-label">教练数</div>
            <div class="stat-value">{{ stats.total_coaches }}</div>
          </div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6" :lg="4">
        <div class="stat-card card">
          <div class="stat-icon">
            <el-icon :size="32" color="#e6a23c"><Setting /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-label">管理员数</div>
            <div class="stat-value">{{ stats.total_admins }}</div>
          </div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6" :lg="4">
        <div class="stat-card card">
          <div class="stat-icon">
            <el-icon :size="32" color="#f56c6c"><Plus /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-label">今日新增用户</div>
            <div class="stat-value">{{ stats.today_new_users }}</div>
          </div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6" :lg="4">
        <div class="stat-card card">
          <div class="stat-icon">
            <el-icon :size="32" color="#909399"><Calendar /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-label">总课程数</div>
            <div class="stat-value">{{ stats.total_courses }}</div>
          </div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="stats-row">
      <el-col :xs="24" :sm="12" :md="6" :lg="4">
        <div class="stat-card card">
          <div class="stat-icon">
            <el-icon :size="32" color="#409eff"><Calendar /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-label">今日课程数</div>
            <div class="stat-value">{{ stats.today_courses }}</div>
          </div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6" :lg="4">
        <div class="stat-card card">
          <div class="stat-icon">
            <el-icon :size="32" color="#67c23a"><Document /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-label">总预约数</div>
            <div class="stat-value">{{ stats.total_reservations }}</div>
          </div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6" :lg="4">
        <div class="stat-card card">
          <div class="stat-icon">
            <el-icon :size="32" color="#e6a23c"><Document /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-label">今日预约数</div>
            <div class="stat-value">{{ stats.today_reservations }}</div>
          </div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6" :lg="4">
        <div class="stat-card card">
          <div class="stat-icon">
            <el-icon :size="32" color="#67c23a"><Check /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-label">总签到数</div>
            <div class="stat-value">{{ stats.total_attendance }}</div>
          </div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6" :lg="4">
        <div class="stat-card card">
          <div class="stat-icon">
            <el-icon :size="32" color="#409eff"><Check /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-label">今日签到数</div>
            <div class="stat-value">{{ stats.today_attendance }}</div>
          </div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6" :lg="4">
        <div class="stat-card card">
          <div class="stat-icon">
            <el-icon :size="32" color="#667eea"><CreditCard /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-label">有效会员卡数</div>
            <div class="stat-value">{{ stats.valid_memberships }}</div>
          </div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="stats-row">
      <el-col :xs="24" :sm="12" :md="6" :lg="4">
        <div class="stat-card card">
          <div class="stat-icon">
            <el-icon :size="32" color="#909399"><Box /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-label">使用中储物柜数</div>
            <div class="stat-value">{{ stats.in_use_lockers }}</div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="charts-row">
      <el-col :xs="24" :md="12">
        <div class="card chart-card">
          <div class="card-header">
            <span class="card-title">用户增长趋势</span>
          </div>
          <div class="chart-container">
            <v-chart
              v-if="userGrowthChartData"
              :option="userGrowthChartData"
              class="chart"
            />
            <el-skeleton v-else :rows="5" animated />
          </div>
        </div>
      </el-col>

      <el-col :xs="24" :md="12">
        <div class="card chart-card">
          <div class="card-header">
            <span class="card-title">课程预约统计</span>
          </div>
          <div class="chart-container">
            <v-chart
              v-if="courseReservationChartData"
              :option="courseReservationChartData"
              class="chart"
            />
            <el-skeleton v-else :rows="5" animated />
          </div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="charts-row">
      <el-col :xs="24" :md="12">
        <div class="card chart-card">
          <div class="card-header">
            <span class="card-title">签到出勤率</span>
          </div>
          <div class="chart-container">
            <v-chart
              v-if="attendanceRateChartData"
              :option="attendanceRateChartData"
              class="chart"
            />
            <el-skeleton v-else :rows="5" animated />
          </div>
        </div>
      </el-col>

      <el-col :xs="24" :md="12">
        <div class="card chart-card">
          <div class="card-header">
            <span class="card-title">会员卡类型分布</span>
          </div>
          <div class="chart-container">
            <v-chart
              v-if="membershipDistributionChartData"
              :option="membershipDistributionChartData"
              class="chart"
            />
            <el-skeleton v-else :rows="5" animated />
          </div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="charts-row">
      <el-col :xs="24" :md="12">
        <div class="card chart-card">
          <div class="card-header">
            <span class="card-title">课程类型分布</span>
          </div>
          <div class="chart-container">
            <v-chart
              v-if="courseDistributionChartData"
              :option="courseDistributionChartData"
              class="chart"
            />
            <el-skeleton v-else :rows="5" animated />
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { LineChart, BarChart, PieChart } from 'echarts/charts'
import {
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent
} from 'echarts/components'
import VChart from 'vue-echarts'
import {
  User,
  UserFilled,
  Setting,
  Plus,
  Calendar,
  Document,
  Check,
  CreditCard,
  Box
} from '@element-plus/icons-vue'
import {
  getAdminDashboard,
  getAdminDashboardUserGrowth,
  getAdminDashboardCourseReservations,
  getAdminDashboardAttendanceRate,
  getAdminDashboardMembershipDistribution,
  getAdminDashboardCourseDistribution
} from '@/api/admin'

use([
  CanvasRenderer,
  LineChart,
  BarChart,
  PieChart,
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent
])

const stats = ref({
  total_users: 0,
  total_members: 0,
  total_coaches: 0,
  total_admins: 0,
  today_new_users: 0,
  total_courses: 0,
  today_courses: 0,
  total_reservations: 0,
  today_reservations: 0,
  total_attendance: 0,
  today_attendance: 0,
  valid_memberships: 0,
  in_use_lockers: 0
})

const userGrowthChartData = ref(null)
const courseReservationChartData = ref(null)
const attendanceRateChartData = ref(null)
const membershipDistributionChartData = ref(null)
const courseDistributionChartData = ref(null)

const loadDashboardData = async () => {
  try {
    // 加载统计数据
    const statsResponse = await getAdminDashboard()
    if (statsResponse) {
      // 兼容后端字段命名
      Object.assign(stats.value, {
        total_users: statsResponse.total_users ?? statsResponse.totalUsers ?? 0,
        total_members: statsResponse.total_members ?? statsResponse.totalMembers ?? 0,
        total_coaches: statsResponse.total_coaches ?? statsResponse.totalCoaches ?? 0,
        total_admins: statsResponse.total_admins ?? statsResponse.totalAdmins ?? 0,
        today_new_users: statsResponse.today_new_users ?? statsResponse.todayNewUsers ?? 0,
        total_courses: statsResponse.total_courses ?? statsResponse.totalCourses ?? 0,
        today_courses: statsResponse.today_courses ?? statsResponse.todayCourses ?? 0,
        total_reservations: statsResponse.total_reservations ?? statsResponse.totalReservations ?? 0,
        today_reservations: statsResponse.today_reservations ?? statsResponse.todayReservations ?? 0,
        total_attendance: statsResponse.total_attendance ?? statsResponse.totalAttendance ?? 0,
        today_attendance: statsResponse.today_attendance ?? statsResponse.todayAttendance ?? 0,
        valid_memberships: statsResponse.valid_memberships ?? statsResponse.validMemberships ?? 0,
        in_use_lockers: statsResponse.in_use_lockers ?? statsResponse.inUseLockers ?? 0
      })
    }
  } catch (error) {
    console.error('加载统计数据失败', error)
  }
}

const loadCharts = async () => {
  try {
    // 用户增长趋势
    const userGrowthResponse = await getAdminDashboardUserGrowth({ days: 30 })
    if (userGrowthResponse && (userGrowthResponse.data || userGrowthResponse.list)) {
      const rows = userGrowthResponse.data || userGrowthResponse.list
      const dates = rows.map(item => item.date || item.day || item.stat_date)
      const userCounts = rows.map(item => item.count ?? item.total ?? 0)
      
      userGrowthChartData.value = {
        tooltip: {
          trigger: 'axis'
        },
        xAxis: {
          type: 'category',
          data: dates.map(d => d.split('-')[2])
        },
        yAxis: {
          type: 'value'
        },
        series: [
          {
            name: '用户数',
            data: userCounts,
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
    }

    // 课程预约统计
    const courseReservationResponse = await getAdminDashboardCourseReservations()
    if (courseReservationResponse && (courseReservationResponse.data || courseReservationResponse.list)) {
      const rows = courseReservationResponse.data || courseReservationResponse.list
      const courseNames = rows.map(item => item.course_name || item.courseName)
      const reservationCounts = rows.map(item => item.count ?? item.total ?? 0)
      
      courseReservationChartData.value = {
        tooltip: {
          trigger: 'axis'
        },
        xAxis: {
          type: 'category',
          data: courseNames
        },
        yAxis: {
          type: 'value'
        },
        series: [
          {
            name: '预约数',
            data: reservationCounts,
            type: 'bar',
            itemStyle: {
              color: '#67c23a'
            }
          }
        ]
      }
    }

    // 签到出勤率
    const attendanceRateResponse = await getAdminDashboardAttendanceRate({ days: 7 })
    if (attendanceRateResponse && (attendanceRateResponse.data || attendanceRateResponse.list)) {
      const rows = attendanceRateResponse.data || attendanceRateResponse.list
      const attendanceDates = rows.map(item => item.date || item.day || item.stat_date)
      const attendanceRates = rows.map(item => item.rate ?? item.value ?? 0)
      
      attendanceRateChartData.value = {
        tooltip: {
          trigger: 'axis',
          formatter: '{b}<br/>{a}: {c}%'
        },
        xAxis: {
          type: 'category',
          data: attendanceDates.map(d => d.split('-')[2])
        },
        yAxis: {
          type: 'value',
          max: 100,
          axisLabel: {
            formatter: '{value}%'
          }
        },
        series: [
          {
            name: '出勤率',
            data: attendanceRates,
            type: 'line',
            smooth: true,
            itemStyle: {
              color: '#409eff'
            },
            areaStyle: {
              color: {
                type: 'linear',
                x: 0,
                y: 0,
                x2: 0,
                y2: 1,
                colorStops: [
                  { offset: 0, color: 'rgba(64, 158, 255, 0.3)' },
                  { offset: 1, color: 'rgba(64, 158, 255, 0.1)' }
                ]
              }
            }
          }
        ]
      }
    }

    // 会员卡类型分布
    const membershipDistributionResponse = await getAdminDashboardMembershipDistribution()
    if (membershipDistributionResponse && (membershipDistributionResponse.data || membershipDistributionResponse.list)) {
      const rows = membershipDistributionResponse.data || membershipDistributionResponse.list
      const membershipData = rows.map(item => ({
        value: item.count ?? item.total ?? 0,
        name: (item.type || item.membership_type) === 'annual'
          ? '年度会员'
          : (item.type || item.membership_type) === 'monthly'
          ? '月度会员'
          : '其他'
      }))
      
      membershipDistributionChartData.value = {
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
            data: membershipData,
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

    // 课程类型分布
    const courseDistributionResponse = await getAdminDashboardCourseDistribution()
    if (courseDistributionResponse && (courseDistributionResponse.data || courseDistributionResponse.list)) {
      const rows = courseDistributionResponse.data || courseDistributionResponse.list
      const courseData = rows.map(item => ({
        value: item.count ?? item.total ?? 0,
        name: item.course_name || item.courseName
      }))
      
      courseDistributionChartData.value = {
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
            data: courseData,
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
  } catch (error) {
    console.error('加载图表数据失败', error)
  }
}

onMounted(() => {
  loadDashboardData()
  loadCharts()
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

.stats-row {
  margin-bottom: 20px;
  
  .stat-card {
    padding: 20px;
    display: flex;
    align-items: center;
    gap: 16px;
    transition: all 0.3s ease;
    
    &:hover {
      transform: translateY(-4px);
      box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
    }
    
    .stat-icon {
      flex-shrink: 0;
    }
    
    .stat-content {
      flex: 1;
      
      .stat-label {
        font-size: 14px;
        color: #909399;
        margin-bottom: 8px;
      }
      
      .stat-value {
        font-size: 28px;
        font-weight: 600;
        color: #303133;
      }
    }
  }
}

.charts-row {
  margin-bottom: 20px;
}

.chart-card {
  .card-header {
    padding: 16px 20px;
    border-bottom: 1px solid #f0f0f0;
    
    .card-title {
      font-size: 16px;
      font-weight: 600;
      color: #303133;
    }
  }
  
  .chart-container {
    height: 300px;
    padding: 20px;
    
    .chart {
      width: 100%;
      height: 100%;
    }
  }
}
</style>

