<template>
  <view class="container">
    <view class="page-header">
      <text class="page-title">课程中心</text>
      <text class="page-subtitle">可预约的课程</text>
    </view>

    <view v-if="loading" class="loading">
      <text>加载中...</text>
    </view>

    <view v-else-if="courses.length === 0" class="empty">
      <text>暂无课程</text>
    </view>

    <scroll-view v-else scroll-y class="courses-list">
      <view
        v-for="course in courses"
        :key="course.course_id"
        class="course-card"
        @click="showCourseDetail(course)"
      >
        <view class="course-header">
          <text class="course-name">{{ course.course_name }}</text>
          <text :class="['status-tag', getCourseStatusType(course)]">
            {{ getCourseStatus(course) }}
          </text>
        </view>
        <view class="course-info">
          <view class="info-item">
            <text class="label">时间：</text>
            <text class="value">{{ formatDate(course.schedule, 'YYYY-MM-DD HH:mm') }}</text>
          </view>
          <view class="info-item">
            <text class="label">教练：</text>
            <text class="value">{{ course.instructor_name || '待定' }}</text>
          </view>
          <view class="info-item">
            <text class="label">人数：</text>
            <text class="value">{{ course.reserved_count || 0 }} / {{ course.capacity || 20 }} 人</text>
          </view>
        </view>
        <view class="course-actions">
          <button
            class="action-button"
            :class="{ disabled: !canReserve(course) && !isReserved(course) }"
            @click.stop="handleReserve(course)"
          >
            {{ isReserved(course) ? '取消预约' : '立即预约' }}
          </button>
        </view>
      </view>
    </scroll-view>

    <!-- 课程详情弹窗 -->
    <view v-if="detailVisible" class="modal" @click="closeDetail">
      <view class="modal-content" @click.stop>
        <view class="modal-header">
          <text class="modal-title">{{ selectedCourse?.course_name }}</text>
          <text class="close-btn" @click="closeDetail">×</text>
        </view>
        <view v-if="selectedCourse" class="modal-body">
          <view class="detail-item">
            <text class="detail-label">开课时间：</text>
            <text class="detail-value">{{ formatDate(selectedCourse.schedule, 'YYYY-MM-DD HH:mm') }}</text>
          </view>
          <view class="detail-item">
            <text class="detail-label">课程状态：</text>
            <text :class="['status-tag', getCourseStatusType(selectedCourse)]">
              {{ getCourseStatus(selectedCourse) }}
            </text>
          </view>
          <view class="detail-item">
            <text class="detail-label">教练：</text>
            <text class="detail-value">{{ selectedCourse.instructor_name || '待定' }}</text>
          </view>
          <view class="detail-item">
            <text class="detail-label">预约人数：</text>
            <text class="detail-value">{{ selectedCourse.reserved_count || 0 }} / {{ selectedCourse.capacity || 20 }}</text>
          </view>
          <view v-if="selectedCourse.description" class="detail-item">
            <text class="detail-label">课程介绍：</text>
            <text class="detail-value">{{ selectedCourse.description }}</text>
          </view>
        </view>
        <view class="modal-footer">
          <button class="modal-button" @click="closeDetail">关闭</button>
          <button
            class="modal-button primary"
            :class="{ disabled: !canReserve(selectedCourse) }"
            @click="handleReserve(selectedCourse)"
          >
            {{ isReserved(selectedCourse) ? '已预约' : '立即预约' }}
          </button>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import userStore from '../../store/user.js'
import { getCourses, reserveCourse } from '../../api/course.js'
import { getReservations, cancelReservation } from '../../api/reservation.js'
import { formatDate } from '../../utils/date.js'

export default {
  data() {
    return {
      courses: [],
      reservations: [],
      loading: false,
      detailVisible: false,
      selectedCourse: null
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
    this.loadReservations()
    this.loadCourses()
  },
  onShow() {
    if (!userStore.getters.isLoggedIn()) {
      uni.reLaunch({
        url: '/pages/login/login'
      })
      return
    }
    this.loadReservations()
  },
  methods: {
    async loadReservations() {
      try {
        const data = await getReservations()
        this.reservations = Array.isArray(data) ? data : (data.reservations || [])
      } catch (error) {
        console.error('获取预约列表失败:', error)
      }
    },
    async loadCourses() {
      this.loading = true
      try {
        const data = await getCourses()
        const list = data.courses || data.list || data || []
        const now = new Date()
        
        // 只显示未开始的课程
        const allCourses = list.map(item => ({
          ...item,
          course_id: item.course_id ?? item.courseId,
          course_name: item.course_name ?? item.courseName,
          instructor_name: item.instructor_name ?? item.instructorName,
          reserved_count: item.reserved_count ?? item.reservedCount
        }))
        
        // 过滤出未开始的课程
        this.courses = allCourses.filter(course => {
          const courseTime = new Date(course.schedule)
          return courseTime > now
        })
      } catch (error) {
        uni.showToast({
          title: '获取课程列表失败',
          icon: 'none'
        })
      } finally {
        this.loading = false
      }
    },
    canReserve(course) {
      if (!course) return false
      const now = new Date()
      const courseTime = new Date(course.schedule)
      if (courseTime < now) return false
      if (this.isReserved(course)) return false
      if (course.reserved_count >= (course.capacity || 20)) return false
      return true
    },
    isReserved(course) {
      if (!course) return false
      return this.reservations.some(
        r => r.course_id === course.course_id && (r.status === 'pending' || r.status === 'confirmed')
      )
    },
    getReservationForCourse(course) {
      if (!course) return null
      return this.reservations.find(
        r => r.course_id === course.course_id && (r.status === 'pending' || r.status === 'confirmed')
      )
    },
    getCourseStatus(course) {
      if (!course) return '未知'
      const now = new Date()
      const courseTime = new Date(course.schedule)
      if (courseTime < now) return '已结束'
      const diffHours = (courseTime - now) / (1000 * 60 * 60)
      if (diffHours < 1) return '即将开始'
      return '未开始'
    },
    getCourseStatusType(course) {
      const status = this.getCourseStatus(course)
      if (status === '已结束') return 'info'
      if (status === '即将开始') return 'warning'
      return 'success'
    },
    showCourseDetail(course) {
      this.selectedCourse = course
      this.detailVisible = true
    },
    closeDetail() {
      this.detailVisible = false
      this.selectedCourse = null
    },
    async handleReserve(course) {
      const existing = this.getReservationForCourse(course)
      if (existing) {
        // 取消预约
        uni.showModal({
          title: '取消预约',
          content: `确定取消课程 "${course.course_name}" 的预约吗？`,
          success: async (res) => {
            if (res.confirm) {
              try {
                await cancelReservation(existing.reservation_id)
                uni.showToast({
                  title: '已取消预约',
                  icon: 'success'
                })
                await this.loadReservations()
                await this.loadCourses()
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
        return
      }

      if (!this.canReserve(course)) {
        uni.showToast({
          title: '无法预约此课程',
          icon: 'none'
        })
        return
      }

      uni.showModal({
        title: '确认预约',
        content: `确定要预约课程"${course.course_name}"吗？`,
        success: async (res) => {
          if (res.confirm) {
            try {
              await reserveCourse(course.course_id)
              uni.showToast({
                title: '预约成功',
                icon: 'success'
              })
              await this.loadReservations()
              await this.loadCourses()
              this.closeDetail()
            } catch (error) {
              uni.showToast({
                title: error.message || '预约失败',
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
  margin-bottom: 16rpx;
  display: block;
}

.page-subtitle {
  font-size: 28rpx;
  color: #909399;
  display: block;
}

.loading,
.empty {
  text-align: center;
  padding: 120rpx 0;
  color: #909399;
  font-size: 28rpx;
}

.courses-list {
  height: calc(100vh - 240rpx);
}

.course-card {
  background: #ffffff;
  border-radius: 24rpx;
  padding: 40rpx;
  margin-bottom: 32rpx;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.05);
}

.course-header {
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

.course-info {
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

.action-button {
  width: 100%;
  height: 80rpx;
  background: #667eea;
  color: #ffffff;
  border-radius: 16rpx;
  font-size: 28rpx;
  border: none;
}

.action-button.disabled {
  background: #c0c4cc;
  color: #ffffff;
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
  max-height: 60vh;
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

.modal-button.primary {
  background: #667eea;
  color: #ffffff;
}

.modal-button.disabled {
  background: #c0c4cc;
  color: #ffffff;
}
</style>

