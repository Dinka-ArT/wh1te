<template>
  <view class="container">
    <view class="welcome-section">
      <text class="welcome-title">欢迎回来，{{ userInfo?.username || '会员' }}</text>
      <text class="welcome-desc">今天是 {{ currentDate }}</text>
    </view>

    <!-- 会员卡状态卡片 -->
    <view class="card membership-card">
      <view class="card-header">
        <text class="card-title">我的会员卡</text>
      </view>
      <view class="card-content">
        <view v-if="membership" class="membership-info">
          <text class="membership-type">{{ membership.membership_type || '标准会员' }}</text>
          <view class="membership-date">
            <text>有效期至：{{ formatDate(membership.expiry_date, 'YYYY-MM-DD') }}</text>
            <text :class="['status-tag', isExpired(membership.expiry_date) ? 'expired' : 'valid']">
              {{ isExpired(membership.expiry_date) ? '已过期' : '有效' }}
            </text>
          </view>
        </view>
        <view v-else class="empty">
          <text>暂无会员卡信息</text>
        </view>
      </view>
    </view>

    <!-- 今日课程卡片 -->
    <view class="card today-course-card">
      <view class="card-header">
        <text class="card-title">今日课程</text>
      </view>
      <view class="card-content">
        <view v-if="todayCourses.length > 0" class="course-list">
          <view
            v-for="course in todayCourses"
            :key="course.course_id"
            class="course-item"
            @click="goToCourses"
          >
            <text class="course-name">{{ course.course_name }}</text>
            <text class="course-time">{{ formatDate(course.schedule, 'HH:mm') }}</text>
          </view>
        </view>
        <view v-else class="empty">
          <text>今日暂无课程</text>
        </view>
      </view>
    </view>

    <!-- 预约概览卡片 -->
    <view class="card reservation-card">
      <view class="card-header">
        <text class="card-title">预约概览</text>
      </view>
      <view class="card-content">
        <view class="reservation-stats">
          <view class="stat-item">
            <text class="stat-value">{{ reservationStats.total }}</text>
            <text class="stat-label">总预约</text>
          </view>
          <view class="stat-item">
            <text class="stat-value primary">{{ reservationStats.pending }}</text>
            <text class="stat-label">待开始</text>
          </view>
          <view class="stat-item">
            <text class="stat-value success">{{ reservationStats.completed }}</text>
            <text class="stat-label">已完成</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 快捷入口 -->
    <view class="card quick-actions-card">
      <view class="card-header">
        <text class="card-title">快捷入口</text>
      </view>
      <view class="quick-actions">
        <view class="action-item" @click="goToCourses">
          <text class="action-icon">📅</text>
          <text class="action-text">课程预约</text>
        </view>
        <view class="action-item" @click="goToLockers">
          <text class="action-icon">📦</text>
          <text class="action-text">储物柜</text>
        </view>
        <view class="action-item" @click="goToProfile">
          <text class="action-icon">👤</text>
          <text class="action-text">个人中心</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import userStore from '../../store/user.js'
import { getMembership } from '../../api/membership.js'
import { getCourses } from '../../api/course.js'
import { getReservations } from '../../api/reservation.js'
import { formatDate, isExpired } from '../../utils/date.js'

export default {
  data() {
    return {
      userInfo: null,
      membership: null,
      todayCourses: [],
      reservations: [],
      currentDate: ''
    }
  },
  computed: {
    reservationStats() {
      return {
        total: this.reservations.length,
        pending: this.reservations.filter(r => r.status === 'pending' || r.status === 'confirmed').length,
        completed: this.reservations.filter(r => r.status === 'completed').length
      }
    }
  },
  onLoad() {
    // 检查登录状态
    if (!userStore.getters.isLoggedIn()) {
      uni.reLaunch({
        url: '/pages/login/login'
      })
      return
    }
    this.initDate()
    this.loadData()
  },
  onShow() {
    // 检查登录状态
    if (!userStore.getters.isLoggedIn()) {
      uni.reLaunch({
        url: '/pages/login/login'
      })
      return
    }
    // 更新用户信息
    this.userInfo = userStore.state.userInfo
    // 如果还没有用户信息，尝试获取
    if (!this.userInfo && userStore.getters.isLoggedIn()) {
      userStore.fetchUserInfo().then(() => {
        this.userInfo = userStore.state.userInfo
      }).catch(err => {
        console.error('获取用户信息失败:', err)
      })
    }
    this.loadData()
  },
  methods: {
    initDate() {
      const now = new Date()
      const weekdays = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六']
      const year = now.getFullYear()
      const month = String(now.getMonth() + 1).padStart(2, '0')
      const day = String(now.getDate()).padStart(2, '0')
      const weekday = weekdays[now.getDay()]
      this.currentDate = `${year}年${month}月${day}日 ${weekday}`
    },
    async loadData() {
      // 获取用户信息
      if (userStore.getters.isLoggedIn()) {
        try {
          // 如果 store 中没有用户信息，先获取
          if (!userStore.state.userInfo) {
            await userStore.fetchUserInfo()
          }
          // 更新本地用户信息
          this.userInfo = userStore.state.userInfo
        } catch (error) {
          console.error('获取用户信息失败:', error)
          // 如果获取失败，尝试使用 store 中的信息
          this.userInfo = userStore.state.userInfo
        }
      }

      // 获取会员卡信息
      try {
        const membershipData = await getMembership()
        this.membership = membershipData
      } catch (error) {
        console.error('获取会员卡信息失败:', error)
      }

      // 获取今日课程
      try {
        const today = new Date().toISOString().split('T')[0]
        const coursesData = await getCourses({ date: today })
        const list = coursesData.courses || coursesData.list || coursesData || []
        const todayStr = today
        this.todayCourses = list.filter(course => {
          const courseDate = new Date(course.schedule).toISOString().split('T')[0]
          return courseDate === todayStr
        }).slice(0, 3) // 只显示前3个
      } catch (error) {
        console.error('获取今日课程失败:', error)
      }

      // 获取预约列表
      try {
        const reservationsData = await getReservations()
        this.reservations = Array.isArray(reservationsData) ? reservationsData : (reservationsData.reservations || [])
      } catch (error) {
        console.error('获取预约列表失败:', error)
      }
    },
    goToCourses() {
      uni.switchTab({
        url: '/pages/courses/courses'
      })
    },
    goToLockers() {
      uni.navigateTo({
        url: '/pages/lockers/lockers'
      })
    },
    goToProfile() {
      uni.switchTab({
        url: '/pages/profile/profile'
      })
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

.welcome-section {
  margin-bottom: 40rpx;
}

.welcome-title {
  display: block;
  font-size: 56rpx;
  font-weight: 600;
  color: #303133;
  margin-bottom: 16rpx;
}

.welcome-desc {
  display: block;
  font-size: 28rpx;
  color: #909399;
}

.card {
  background: #ffffff;
  border-radius: 24rpx;
  padding: 40rpx;
  margin-bottom: 32rpx;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.05);
}

.card-header {
  margin-bottom: 32rpx;
  padding-bottom: 24rpx;
  border-bottom: 1px solid #f0f0f0;
}

.card-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #303133;
}

.card-content {
  min-height: 120rpx;
}

.membership-info {
  display: flex;
  flex-direction: column;
  gap: 24rpx;
}

.membership-type {
  font-size: 48rpx;
  font-weight: 600;
  color: #303133;
}

.membership-date {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 28rpx;
  color: #606266;
}

.status-tag {
  padding: 8rpx 24rpx;
  border-radius: 8rpx;
  font-size: 24rpx;
}

.status-tag.valid {
  background: #f0f9ff;
  color: #67c23a;
}

.status-tag.expired {
  background: #fef0f0;
  color: #f56c6c;
}

.course-list {
  display: flex;
  flex-direction: column;
  gap: 24rpx;
}

.course-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24rpx 0;
  border-bottom: 1px solid #f0f0f0;
}

.course-item:last-child {
  border-bottom: none;
}

.course-name {
  font-size: 28rpx;
  color: #303133;
  font-weight: 500;
}

.course-time {
  font-size: 28rpx;
  color: #909399;
}

.reservation-stats {
  display: flex;
  justify-content: space-around;
  align-items: center;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16rpx;
}

.stat-value {
  font-size: 64rpx;
  font-weight: 600;
  color: #303133;
}

.stat-value.primary {
  color: #667eea;
}

.stat-value.success {
  color: #67c23a;
}

.stat-label {
  font-size: 28rpx;
  color: #909399;
}

.quick-actions {
  display: flex;
  justify-content: space-around;
  align-items: center;
  gap: 32rpx;
}

.action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16rpx;
  flex: 1;
}

.action-icon {
  font-size: 64rpx;
}

.action-text {
  font-size: 28rpx;
  color: #303133;
}

.empty {
  text-align: center;
  padding: 60rpx 0;
  color: #909399;
  font-size: 28rpx;
}
</style>
