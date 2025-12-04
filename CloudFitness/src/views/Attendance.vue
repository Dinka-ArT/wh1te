<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">签到记录</h2>
      <div class="header-actions">
        <el-date-picker
          v-model="filterDate"
          type="month"
          placeholder="选择月份"
          format="YYYY-MM"
          value-format="YYYY-MM"
          @change="fetchAttendance"
        />
      </div>
    </div>

    <el-row :gutter="20" class="charts-row">
      <el-col :xs="24" :md="12">
        <div class="card chart-card">
          <div class="card-header">
            <span class="card-title">每月出勤率</span>
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

    <div class="card attendance-table-card">
      <div class="card-header">
        <span class="card-title">签到明细</span>
      </div>
      <div v-loading="loading" class="table-container">
        <el-empty
          v-if="!loading && attendanceList.length === 0"
          description="暂无签到记录"
        />
        
        <el-table
          v-else
          :data="attendanceList"
          stripe
          style="width: 100%"
        >
          <el-table-column prop="course_name" label="课程名称" width="200" />
          <el-table-column label="签到时间" width="180">
            <template #default="{ row }">
              {{ formatDate(row.check_in_time) }}
            </template>
          </el-table-column>
          <el-table-column label="课程时间" width="180">
            <template #default="{ row }">
              {{ formatDate(row.schedule) }}
            </template>
          </el-table-column>
          <el-table-column label="是否准时" width="120">
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
        </el-table>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getAttendance } from '@/api/attendance'
import { formatDate } from '@/utils/date'
import dayjs from 'dayjs'

const attendanceList = ref([])
const loading = ref(false)
const filterDate = ref(dayjs().format('YYYY-MM'))
const attendanceChartData = ref(null)
const courseTypeChartData = ref(null)

const fetchAttendance = async () => {
  loading.value = true
  try {
    const params = {}
    if (filterDate.value) {
      params.month = filterDate.value
    }
    const data = await getAttendance(params)
    attendanceList.value = data.attendance || data || []
    
    // 处理数据，计算是否准时
    attendanceList.value = attendanceList.value.map(item => {
      const checkInTime = dayjs(item.check_in_time)
      const courseTime = dayjs(item.schedule)
      const diffMinutes = checkInTime.diff(courseTime, 'minute')
      
      return {
        ...item,
        is_on_time: diffMinutes <= 10, // 10分钟内算准时
        late_minutes: diffMinutes > 10 ? diffMinutes : 0
      }
    })
    
    generateCharts(attendanceList.value)
  } catch (error) {
    console.error('获取签到记录失败:', error)
  } finally {
    loading.value = false
  }
}

const generateCharts = (data) => {
  // 每月出勤率折线图
  const monthlyData = {}
  data.forEach(item => {
    const month = dayjs(item.check_in_time).format('YYYY-MM')
    if (!monthlyData[month]) {
      monthlyData[month] = { total: 0, onTime: 0 }
    }
    monthlyData[month].total++
    if (item.is_on_time) {
      monthlyData[month].onTime++
    }
  })

  const months = Object.keys(monthlyData).sort()
  const rates = months.map(month => {
    const { total, onTime } = monthlyData[month]
    return total > 0 ? ((onTime / total) * 100).toFixed(1) : 0
  })

  attendanceChartData.value = {
    tooltip: {
      trigger: 'axis',
      formatter: '{b}<br/>{a}: {c}%'
    },
    xAxis: {
      type: 'category',
      data: months
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
        data: rates,
        type: 'line',
        smooth: true,
        itemStyle: {
          color: '#67c23a'
        },
        areaStyle: {
          color: {
            type: 'linear',
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [
              { offset: 0, color: 'rgba(103, 194, 58, 0.3)' },
              { offset: 1, color: 'rgba(103, 194, 58, 0.1)' }
            ]
          }
        }
      }
    ]
  }

  // 课程类型分布饼图
  const courseTypeData = {}
  data.forEach(item => {
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

onMounted(() => {
  fetchAttendance()
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

.attendance-table-card {
  .table-container {
    min-height: 200px;
  }
}
</style>

