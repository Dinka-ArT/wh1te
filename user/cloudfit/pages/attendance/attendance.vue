<template>
  <view class="container">
    <view class="page-header">
      <text class="page-title">签到记录</text>
    </view>

    <view v-if="loading" class="loading">
      <text>加载中...</text>
    </view>

    <view v-else-if="attendanceList.length === 0" class="empty">
      <text>暂无签到记录</text>
    </view>

    <scroll-view v-else scroll-y class="attendance-list">
      <view
        v-for="item in attendanceList"
        :key="item.attendance_id"
        class="attendance-card"
      >
        <view class="attendance-header">
          <text class="course-name">{{ item.course_name || '自由训练' }}</text>
          <text :class="['status-tag', item.is_on_time ? 'success' : 'warning']">
            {{ item.is_on_time ? '准时' : '迟到' }}
          </text>
        </view>
        <view class="attendance-info">
          <view class="info-item">
            <text class="label">签到时间：</text>
            <text class="value">{{ formatDate(item.check_in_time) }}</text>
          </view>
          <view v-if="item.check_out_time" class="info-item">
            <text class="label">签退时间：</text>
            <text class="value">{{ formatDate(item.check_out_time) }}</text>
          </view>
        </view>
      </view>
    </scroll-view>
  </view>
</template>

<script>
import { getAttendance } from '../../api/attendance.js'
import { formatDate } from '../../utils/date.js'

export default {
  data() {
    return {
      attendanceList: [],
      loading: false
    }
  },
  onLoad() {
    this.loadAttendance()
  },
  methods: {
    async loadAttendance() {
      this.loading = true
      try {
        const data = await getAttendance({ limit: 50 })
        this.attendanceList = Array.isArray(data) ? data : (data.attendance || [])
      } catch (error) {
        uni.showToast({
          title: '获取签到记录失败',
          icon: 'none'
        })
      } finally {
        this.loading = false
      }
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

.attendance-list {
  height: calc(100vh - 200rpx);
}

.attendance-card {
  background: #ffffff;
  border-radius: 24rpx;
  padding: 40rpx;
  margin-bottom: 32rpx;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.05);
}

.attendance-header {
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

.attendance-info {
  display: flex;
  flex-direction: column;
  gap: 24rpx;
}

.info-item {
  display: flex;
  font-size: 28rpx;
}

.label {
  color: #909399;
  margin-right: 16rpx;
}

.value {
  color: #303133;
}
</style>

