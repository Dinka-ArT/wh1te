<template>
  <view class="container">
    <view class="page-header">
      <text class="page-title">我的预约</text>
    </view>

    <view v-if="loading" class="loading">
      <text>加载中...</text>
    </view>

    <view v-else-if="reservations.length === 0" class="empty">
      <text>暂无预约记录</text>
    </view>

    <scroll-view v-else scroll-y class="reservations-list">
      <view
        v-for="reservation in reservations"
        :key="reservation.reservation_id"
        class="reservation-card"
        @click="showDetail(reservation)"
      >
        <view class="reservation-header">
          <text class="course-name">{{ reservation.course_name }}</text>
          <text :class="['status-tag', getStatusType(reservation.status)]">
            {{ getStatusText(reservation.status) }}
          </text>
        </view>
        <view class="reservation-info">
          <view class="info-item">
            <text class="label">预约时间：</text>
            <text class="value">{{ formatDate(reservation.reservation_date) }}</text>
          </view>
          <view class="info-item">
            <text class="label">课程时间：</text>
            <text class="value">{{ formatDate(reservation.schedule) }}</text>
          </view>
          <view class="info-item">
            <text class="label">教练：</text>
            <text class="value">{{ reservation.instructor_name || '待定' }}</text>
          </view>
        </view>
        <view v-if="canCancel(reservation)" class="reservation-actions">
          <button class="cancel-button" @click.stop="handleCancel(reservation)">
            取消预约
          </button>
        </view>
      </view>
    </scroll-view>

    <!-- 详情弹窗 -->
    <view v-if="detailVisible" class="modal" @click="closeDetail">
      <view class="modal-content" @click.stop>
        <view class="modal-header">
          <text class="modal-title">{{ selectedReservation?.course_name }}</text>
          <text class="close-btn" @click="closeDetail">×</text>
        </view>
        <view v-if="selectedReservation" class="modal-body">
          <view class="detail-item">
            <text class="detail-label">课程名称：</text>
            <text class="detail-value">{{ selectedReservation.course_name }}</text>
          </view>
          <view class="detail-item">
            <text class="detail-label">状态：</text>
            <text :class="['status-tag', getStatusType(selectedReservation.status)]">
              {{ getStatusText(selectedReservation.status) }}
            </text>
          </view>
          <view class="detail-item">
            <text class="detail-label">预约时间：</text>
            <text class="detail-value">{{ formatDate(selectedReservation.reservation_date) }}</text>
          </view>
          <view class="detail-item">
            <text class="detail-label">课程时间：</text>
            <text class="detail-value">{{ formatDate(selectedReservation.schedule) }}</text>
          </view>
          <view class="detail-item">
            <text class="detail-label">教练：</text>
            <text class="detail-value">{{ selectedReservation.instructor_name || '待定' }}</text>
          </view>
          <view class="detail-item">
            <text class="detail-label">签到状态：</text>
            <text :class="['status-tag', selectedReservation.attendance ? 'success' : 'info']">
              {{ selectedReservation.attendance ? '已签到' : '未签到' }}
            </text>
          </view>
          <view v-if="selectedReservation.attendance" class="attendance-section">
            <view class="detail-item">
              <text class="detail-label">签到时间：</text>
              <text class="detail-value">{{ formatDate(selectedReservation.attendance.check_in_time) }}</text>
            </view>
            <view class="detail-item">
              <text class="detail-label">是否准时：</text>
              <text :class="['status-tag', selectedReservation.attendance.is_on_time ? 'success' : 'warning']">
                {{ selectedReservation.attendance.is_on_time ? '准时' : '迟到' }}
              </text>
            </view>
          </view>
        </view>
        <view class="modal-footer">
          <button class="modal-button" @click="closeDetail">关闭</button>
          <button
            v-if="canCancel(selectedReservation)"
            class="modal-button danger"
            @click="handleCancel(selectedReservation)"
          >
            取消预约
          </button>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { getReservations, cancelReservation } from '../../api/reservation.js'
import { formatDate } from '../../utils/date.js'

export default {
  data() {
    return {
      reservations: [],
      loading: false,
      detailVisible: false,
      selectedReservation: null
    }
  },
  onLoad() {
    this.loadReservations()
  },
  onShow() {
    this.loadReservations()
  },
  methods: {
    async loadReservations() {
      this.loading = true
      try {
        const data = await getReservations()
        this.reservations = Array.isArray(data) ? data : (data.reservations || [])
      } catch (error) {
        uni.showToast({
          title: '获取预约列表失败',
          icon: 'none'
        })
      } finally {
        this.loading = false
      }
    },
    getStatusText(status) {
      const statusMap = {
        pending: '待开始',
        confirmed: '已确认',
        completed: '已完成',
        cancelled: '已取消'
      }
      return statusMap[status] || status
    },
    getStatusType(status) {
      const typeMap = {
        pending: 'warning',
        confirmed: 'success',
        completed: 'info',
        cancelled: 'info'
      }
      return typeMap[status] || 'info'
    },
    canCancel(reservation) {
      if (!reservation) return false
      if (reservation.status === 'cancelled' || reservation.status === 'completed') {
        return false
      }
      const courseTime = new Date(reservation.schedule)
      const now = new Date()
      return courseTime > now
    },
    showDetail(reservation) {
      this.selectedReservation = reservation
      this.detailVisible = true
    },
    closeDetail() {
      this.detailVisible = false
      this.selectedReservation = null
    },
    async handleCancel(reservation) {
      if (!this.canCancel(reservation)) {
        uni.showToast({
          title: '无法取消此预约',
          icon: 'none'
        })
        return
      }

      uni.showModal({
        title: '确认取消',
        content: `确定要取消预约"${reservation.course_name}"吗？`,
        success: async (res) => {
          if (res.confirm) {
            try {
              await cancelReservation(reservation.reservation_id)
              uni.showToast({
                title: '取消成功',
                icon: 'success'
              })
              await this.loadReservations()
              this.closeDetail()
            } catch (error) {
              uni.showToast({
                title: error.message || '取消失败',
                icon: 'none'
              })
            }
          }
        }
      })
    },
    formatDate
  }
}
</script>

<style scoped>
.container {
  padding: 40rpx;
  background: #f5f7fa;
  min-height: 100vh;
}

.page-header {
  margin-bottom: 40rpx;
}

.page-title {
  font-size: 48rpx;
  font-weight: 600;
  color: #303133;
}

.loading,
.empty {
  text-align: center;
  padding: 120rpx 0;
  color: #909399;
  font-size: 28rpx;
}

.reservations-list {
  height: calc(100vh - 200rpx);
}

.reservation-card {
  background: #ffffff;
  border-radius: 24rpx;
  padding: 40rpx;
  margin-bottom: 32rpx;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.05);
}

.reservation-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 32rpx;
}

.course-name {
  font-size: 36rpx;
  font-weight: 600;
  color: #303133;
  flex: 1;
}

.status-tag {
  padding: 8rpx 24rpx;
  border-radius: 8rpx;
  font-size: 24rpx;
}

.status-tag.success {
  background: #f0f9ff;
  color: #67c23a;
}

.status-tag.warning {
  background: #fdf6ec;
  color: #e6a23c;
}

.status-tag.info {
  background: #f4f4f5;
  color: #909399;
}

.reservation-info {
  margin-bottom: 32rpx;
}

.info-item {
  display: flex;
  margin-bottom: 16rpx;
  font-size: 28rpx;
}

.info-item:last-child {
  margin-bottom: 0;
}

.label {
  color: #909399;
  margin-right: 16rpx;
}

.value {
  color: #303133;
}

.reservation-actions {
  margin-top: 32rpx;
  padding-top: 32rpx;
  border-top: 1px solid #f0f0f0;
}

.cancel-button {
  width: 100%;
  height: 80rpx;
  background: #f56c6c;
  color: #ffffff;
  border-radius: 16rpx;
  font-size: 28rpx;
  border: none;
}

.modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  width: 90%;
  max-width: 600rpx;
  background: #ffffff;
  border-radius: 24rpx;
  overflow: hidden;
  max-height: 80vh;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 40rpx;
  border-bottom: 1px solid #f0f0f0;
}

.modal-title {
  font-size: 36rpx;
  font-weight: 600;
  color: #303133;
}

.close-btn {
  font-size: 64rpx;
  color: #909399;
  line-height: 1;
}

.modal-body {
  padding: 40rpx;
  max-height: 50vh;
  overflow-y: auto;
}

.detail-item {
  margin-bottom: 32rpx;
  font-size: 28rpx;
}

.detail-item:last-child {
  margin-bottom: 0;
}

.detail-label {
  color: #909399;
  margin-right: 16rpx;
}

.detail-value {
  color: #303133;
}

.attendance-section {
  margin-top: 32rpx;
  padding-top: 32rpx;
  border-top: 1px solid #f0f0f0;
}

.modal-footer {
  display: flex;
  padding: 40rpx;
  border-top: 1px solid #f0f0f0;
  gap: 24rpx;
}

.modal-button {
  flex: 1;
  height: 80rpx;
  background: #f5f7fa;
  color: #303133;
  border-radius: 16rpx;
  font-size: 28rpx;
  border: none;
}

.modal-button.danger {
  background: #f56c6c;
  color: #ffffff;
}
</style>

