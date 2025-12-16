<template>
  <view class="container">
    <view class="page-header">
      <text class="page-title">储物柜管理</text>
    </view>

    <view v-if="loading" class="loading">
      <text>加载中...</text>
    </view>

    <view v-else-if="lockers.length === 0" class="empty">
      <text>暂无储物柜租赁记录</text>
    </view>

    <scroll-view v-else scroll-y class="lockers-list">
      <view
        v-for="locker in lockers"
        :key="locker.locker_id"
        class="locker-card"
      >
        <view class="locker-header">
          <text class="locker-icon">📦</text>
          <text class="locker-number">储物柜 #{{ locker.locker_number }}</text>
        </view>
        <view class="locker-info">
          <view class="info-item">
            <text class="label">开始时间：</text>
            <text class="value">{{ formatDate(locker.start_date, 'YYYY-MM-DD') }}</text>
          </view>
          <view class="info-item">
            <text class="label">结束时间：</text>
            <text class="value">{{ formatDate(locker.end_date, 'YYYY-MM-DD') }}</text>
          </view>
          <view class="info-item">
            <text class="label">状态：</text>
            <text :class="['status-tag', getStatusType(locker)]">
              {{ getStatus(locker) }}
            </text>
          </view>
          <view v-if="locker.end_date" class="info-item">
            <text class="label">剩余时间：</text>
            <text class="value" :class="{ warning: isExpiringSoon(locker) }">
              {{ getRemainingTime(locker) }}
            </text>
          </view>
        </view>
      </view>
    </scroll-view>
  </view>
</template>

<script>
import { getLockers } from '../../api/locker.js'
import { formatDate, isExpired, getTimeRemaining } from '../../utils/date.js'

export default {
  data() {
    return {
      lockers: [],
      loading: false
    }
  },
  onLoad() {
    this.loadLockers()
  },
  methods: {
    async loadLockers() {
      this.loading = true
      try {
        const data = await getLockers()
        this.lockers = Array.isArray(data) ? data : (data.lockers || [])
      } catch (error) {
        uni.showToast({
          title: '获取储物柜信息失败',
          icon: 'none'
        })
      } finally {
        this.loading = false
      }
    },
    getStatus(locker) {
      if (!locker.end_date) return '使用中'
      if (isExpired(locker.end_date)) return '已到期'
      return '正常'
    },
    getStatusType(locker) {
      const status = this.getStatus(locker)
      if (status === '已到期') return 'danger'
      if (status === '正常') return 'success'
      return 'info'
    },
    isExpiringSoon(locker) {
      if (!locker.end_date) return false
      if (isExpired(locker.end_date)) return false
      const days = Math.floor((new Date(locker.end_date) - new Date()) / (1000 * 60 * 60 * 24))
      return days <= 7 && days > 0
    },
    getRemainingTime(locker) {
      if (!locker.end_date) return '-'
      if (isExpired(locker.end_date)) return '已过期'
      return getTimeRemaining(locker.end_date)
    },
    formatDate,
    isExpired
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

.lockers-list {
  height: calc(100vh - 200rpx);
}

.locker-card {
  background: #ffffff;
  border-radius: 24rpx;
  padding: 40rpx;
  margin-bottom: 32rpx;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.05);
}

.locker-header {
  display: flex;
  align-items: center;
  gap: 24rpx;
  margin-bottom: 32rpx;
  padding-bottom: 24rpx;
  border-bottom: 1px solid #f0f0f0;
}

.locker-icon {
  font-size: 64rpx;
}

.locker-number {
  font-size: 40rpx;
  font-weight: 600;
  color: #303133;
}

.locker-info {
  display: flex;
  flex-direction: column;
  gap: 24rpx;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 28rpx;
}

.label {
  color: #909399;
}

.value {
  color: #303133;
  font-weight: 500;
}

.value.warning {
  color: #e6a23c;
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

.status-tag.danger {
  background: #fef0f0;
  color: #f56c6c;
}

.status-tag.info {
  background: #f4f4f5;
  color: #909399;
}
</style>

