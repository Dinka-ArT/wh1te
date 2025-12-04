<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">我的预约</h2>
    </div>

    <div v-loading="reservationStore.loading" class="reservations-container">
      <el-empty
        v-if="!reservationStore.loading && reservationStore.reservations.length === 0"
        description="暂无预约记录"
      />
      
      <el-table
        v-else
        :data="reservationStore.reservations"
        stripe
        style="width: 100%"
      >
        <el-table-column prop="course_name" label="课程名称" width="200" />
        <el-table-column label="预约时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.reservation_date) }}
          </template>
        </el-table-column>
        <el-table-column label="课程时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.schedule) }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="教练" width="120">
          <template #default="{ row }">
            {{ row.instructor_name || '待定' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button
              type="primary"
              link
              size="small"
              @click="viewDetail(row)"
            >
              查看详情
            </el-button>
            <el-button
              v-if="canCancel(row)"
              type="danger"
              link
              size="small"
              @click="handleCancel(row)"
            >
              取消预约
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 预约详情弹窗 -->
    <el-dialog
      v-model="detailVisible"
      :title="selectedReservation?.course_name"
      width="600px"
    >
      <div v-if="selectedReservation" class="reservation-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="课程名称">
            {{ selectedReservation.course_name }}
          </el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getStatusType(selectedReservation.status)">
              {{ getStatusText(selectedReservation.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="预约时间">
            {{ formatDate(selectedReservation.reservation_date) }}
          </el-descriptions-item>
          <el-descriptions-item label="课程时间">
            {{ formatDate(selectedReservation.schedule) }}
          </el-descriptions-item>
          <el-descriptions-item label="教练">
            {{ selectedReservation.instructor_name || '待定' }}
          </el-descriptions-item>
          <el-descriptions-item label="签到状态">
            <el-tag v-if="selectedReservation.attendance" type="success">
              已签到
            </el-tag>
            <el-tag v-else type="info">未签到</el-tag>
          </el-descriptions-item>
        </el-descriptions>
        
        <div v-if="selectedReservation.attendance" class="attendance-info">
          <h4>签到信息</h4>
          <el-descriptions :column="1" border>
            <el-descriptions-item label="签到时间">
              {{ formatDate(selectedReservation.attendance.check_in_time) }}
            </el-descriptions-item>
            <el-descriptions-item label="是否准时">
              <el-tag :type="selectedReservation.attendance.is_on_time ? 'success' : 'warning'">
                {{ selectedReservation.attendance.is_on_time ? '准时' : '迟到' }}
              </el-tag>
            </el-descriptions-item>
          </el-descriptions>
        </div>
      </div>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
        <el-button
          v-if="canCancel(selectedReservation)"
          type="danger"
          @click="handleCancel(selectedReservation)"
        >
          取消预约
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useReservationStore } from '@/stores/reservation'
import { formatDate } from '@/utils/date'
import { ElMessage, ElMessageBox } from 'element-plus'
import dayjs from 'dayjs'

const reservationStore = useReservationStore()

const detailVisible = ref(false)
const selectedReservation = ref(null)

const getStatusText = (status) => {
  const statusMap = {
    pending: '待开始',
    confirmed: '已确认',
    completed: '已完成',
    cancelled: '已取消'
  }
  return statusMap[status] || status
}

const getStatusType = (status) => {
  const typeMap = {
    pending: 'warning',
    confirmed: 'success',
    completed: 'info',
    cancelled: 'info'
  }
  return typeMap[status] || 'info'
}

const canCancel = (reservation) => {
  if (!reservation) return false
  if (reservation.status === 'cancelled' || reservation.status === 'completed') {
    return false
  }
  const courseTime = dayjs(reservation.schedule)
  const now = dayjs()
  return courseTime.isAfter(now)
}

const viewDetail = (reservation) => {
  selectedReservation.value = reservation
  detailVisible.value = true
}

const handleCancel = async (reservation) => {
  if (!canCancel(reservation)) {
    ElMessage.warning('无法取消此预约')
    return
  }

  try {
    await ElMessageBox.confirm(
      `确定要取消预约"${reservation.course_name}"吗？`,
      '确认取消',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    const result = await reservationStore.cancel(reservation.reservation_id)
    if (result.success) {
      ElMessage.success('取消成功')
      detailVisible.value = false
    } else {
      ElMessage.error(result.message || '取消失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('取消失败')
    }
  }
}

onMounted(async () => {
  await reservationStore.fetchReservations()
})
</script>

<style lang="scss" scoped>
.page-container {
  padding: 24px;
  height: 100%;
  overflow-y: auto;
}

.page-header {
  margin-bottom: 24px;
  
  .page-title {
    font-size: 24px;
    font-weight: 600;
    color: #303133;
  }
}

.reservations-container {
  background: #ffffff;
  border-radius: 12px;
  padding: 24px;
}

.reservation-detail {
  .attendance-info {
    margin-top: 24px;
    
    h4 {
      font-size: 16px;
      font-weight: 600;
      color: #303133;
      margin-bottom: 12px;
    }
  }
}
</style>

