<template>
  <view class="container">
    <view class="page-header">
      <text class="page-title">个人中心</text>
    </view>

    <!-- 用户信息卡片 -->
    <view class="card user-card">
      <view class="user-info">
        <view class="avatar-container" @click="changeAvatar">
          <image v-if="avatarUrl" class="avatar-image" :src="avatarUrl" mode="aspectFill"></image>
          <view v-else class="avatar">
            <text>{{ userInfo?.username?.charAt(0) || 'U' }}</text>
          </view>
          <view class="avatar-mask">
            <text class="avatar-edit-icon">📷</text>
          </view>
        </view>
        <view class="user-details">
          <text class="username">{{ userInfo?.username || '未登录' }}</text>
          <text class="user-role">{{ getRoleText(userInfo?.role) }}</text>
        </view>
      </view>
    </view>

    <!-- 基本信息 -->
    <view class="card profile-card">
      <view class="card-header">
        <text class="card-title">基本信息</text>
      </view>
      <view class="form-section">
        <view class="form-item">
          <text class="form-label">用户名</text>
          <input
            class="form-input"
            :value="userInfo?.username"
            disabled
          />
        </view>
        <view class="form-item">
          <text class="form-label">邮箱</text>
          <input
            class="form-input"
            v-model="profileForm.email"
            placeholder="请输入邮箱"
          />
        </view>
        <view class="form-item">
          <text class="form-label">手机号</text>
          <input
            class="form-input"
            v-model="profileForm.phone_number"
            placeholder="请输入手机号"
            type="number"
          />
        </view>
        <view class="form-item">
          <text class="form-label">注册时间</text>
          <input
            class="form-input"
            :value="formatDate(userInfo?.registration_date)"
            disabled
          />
        </view>
        <view class="form-item">
          <text class="form-label">会员状态</text>
          <text :class="['status-tag', membershipStatus.type]">
            {{ membershipStatus.text }}
          </text>
        </view>
        <button class="save-button" :loading="saving" @click="handleSaveProfile">
          保存修改
        </button>
      </view>
    </view>

    <!-- 修改密码 -->
    <view class="card password-card">
      <view class="card-header">
        <text class="card-title">修改密码</text>
      </view>
      <view class="form-section">
        <view class="form-item">
          <text class="form-label">原密码</text>
          <input
            class="form-input"
            type="password"
            v-model="passwordForm.old_password"
            placeholder="请输入原密码"
          />
        </view>
        <view class="form-item">
          <text class="form-label">新密码</text>
          <input
            class="form-input"
            type="password"
            v-model="passwordForm.new_password"
            placeholder="请输入新密码（至少6位）"
          />
        </view>
        <view class="form-item">
          <text class="form-label">确认密码</text>
          <input
            class="form-input"
            type="password"
            v-model="passwordForm.confirm_password"
            placeholder="请确认新密码"
          />
        </view>
        <button class="save-button" :loading="changingPassword" @click="handleChangePassword">
          修改密码
        </button>
      </view>
    </view>

    <!-- 其他功能 -->
    <view class="card menu-card">
      <view class="menu-item" @click="goToReservations">
        <text class="menu-icon">📅</text>
        <text class="menu-text">我的预约</text>
        <text class="menu-arrow">></text>
      </view>
      <view class="menu-item" @click="goToAttendance">
        <text class="menu-icon">✓</text>
        <text class="menu-text">签到记录</text>
        <text class="menu-arrow">></text>
      </view>
      <view class="menu-item" @click="handleLogout">
        <text class="menu-icon">🚪</text>
        <text class="menu-text">退出登录</text>
        <text class="menu-arrow">></text>
      </view>
    </view>
  </view>
</template>

<script>
import userStore from '../../store/user.js'
import { updateProfile, changePassword } from '../../api/user.js'
import { getMembership } from '../../api/membership.js'
import { formatDate, isExpired } from '../../utils/date.js'
import { chooseImage, saveImageToLocal, getAvatarPath } from '../../utils/upload.js'

export default {
  data() {
    return {
      userInfo: null,
      membership: null,
      avatarUrl: '',
      profileForm: {
        email: '',
        phone_number: ''
      },
      passwordForm: {
        old_password: '',
        new_password: '',
        confirm_password: ''
      },
      saving: false,
      changingPassword: false,
      uploadingAvatar: false
    }
  },
  computed: {
    membershipStatus() {
      if (!this.membership) {
        return { type: 'info', text: '无会员卡' }
      }
      if (isExpired(this.membership.expiry_date)) {
        return { type: 'danger', text: '已过期' }
      }
      return { type: 'success', text: '有效' }
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
    // 先设置用户信息，再加载数据
    this.userInfo = userStore.state.userInfo
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
    // 每次显示时更新用户信息
    this.userInfo = userStore.state.userInfo
    if (this.userInfo) {
      this.initProfile()
      this.loadAvatar()
    }
    this.loadData()
  },
  methods: {
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
          if (this.userInfo) {
            this.initProfile()
            this.loadAvatar()
          }
        } catch (error) {
          console.error('获取用户信息失败:', error)
          // 如果获取失败，尝试使用 store 中的信息
          this.userInfo = userStore.state.userInfo
          if (this.userInfo) {
            this.initProfile()
            this.loadAvatar()
          } else {
            uni.showToast({
              title: '获取用户信息失败',
              icon: 'none'
            })
          }
        }
      } else {
        // 如果没有登录，尝试从 store 获取
        this.userInfo = userStore.state.userInfo
        if (this.userInfo) {
          this.initProfile()
          this.loadAvatar()
        }
      }

      // 获取会员卡信息
      try {
        const membershipData = await getMembership()
        this.membership = membershipData
      } catch (error) {
        console.error('获取会员卡信息失败:', error)
      }
    },
    initProfile() {
      if (this.userInfo) {
        this.profileForm.email = this.userInfo.email || ''
        this.profileForm.phone_number = this.userInfo.phone_number || ''
      }
    },
    loadAvatar() {
      if (this.userInfo && this.userInfo.user_id) {
        const avatarPath = getAvatarPath(this.userInfo.user_id)
        if (avatarPath) {
          this.avatarUrl = avatarPath
        }
      }
    },
    async changeAvatar() {
      if (this.uploadingAvatar) return
      
      try {
        // 选择图片
        const tempFilePath = await chooseImage()
        
        if (!this.userInfo || !this.userInfo.user_id) {
          uni.showToast({
            title: '请先登录',
            icon: 'none'
          })
          return
        }
        
        this.uploadingAvatar = true
        
        // 保存到本地
        const fileName = `${this.userInfo.user_id}.jpg`
        const savedPath = await saveImageToLocal(tempFilePath, fileName)
        
        // 更新显示
        this.avatarUrl = savedPath
        
        // 保存头像路径到本地存储
        uni.setStorageSync(`avatar_${this.userInfo.user_id}`, savedPath)
        
        // 更新用户信息中的头像路径
        if (userStore.state.userInfo) {
          userStore.state.userInfo.avatar = savedPath
        }
        
        // 更新当前页面的头像显示
        this.avatarUrl = savedPath
        
        uni.showToast({
          title: '头像更新成功',
          icon: 'success'
        })
      } catch (error) {
        console.error('更换头像失败:', error)
        if (error.errMsg && !error.errMsg.includes('cancel')) {
          uni.showToast({
            title: '更换头像失败',
            icon: 'none'
          })
        }
      } finally {
        this.uploadingAvatar = false
      }
    },
    getRoleText(role) {
      const roleMap = {
        admin: '管理员',
        member: '会员',
        coach: '教练'
      }
      return roleMap[role] || '用户'
    },
    async handleSaveProfile() {
      // 验证邮箱
      if (this.profileForm.email && !/^[\w-]+(\.[\w-]+)*@[\w-]+(\.[\w-]+)+$/.test(this.profileForm.email)) {
        uni.showToast({
          title: '请输入正确的邮箱地址',
          icon: 'none'
        })
        return
      }
      // 验证手机号
      if (this.profileForm.phone_number && !/^1[3-9]\d{9}$/.test(this.profileForm.phone_number)) {
        uni.showToast({
          title: '请输入正确的手机号',
          icon: 'none'
        })
        return
      }

      this.saving = true
      try {
        await updateProfile(this.profileForm)
        uni.showToast({
          title: '保存成功',
          icon: 'success'
        })
        await userStore.fetchUserInfo()
        this.userInfo = userStore.state.userInfo
        this.initProfile()
      } catch (error) {
        uni.showToast({
          title: error.message || '保存失败',
          icon: 'none'
        })
      } finally {
        this.saving = false
      }
    },
    async handleChangePassword() {
      if (!this.passwordForm.old_password) {
        uni.showToast({
          title: '请输入原密码',
          icon: 'none'
        })
        return
      }
      if (!this.passwordForm.new_password) {
        uni.showToast({
          title: '请输入新密码',
          icon: 'none'
        })
        return
      }
      if (this.passwordForm.new_password.length < 6) {
        uni.showToast({
          title: '密码长度不能少于6位',
          icon: 'none'
        })
        return
      }
      if (this.passwordForm.new_password !== this.passwordForm.confirm_password) {
        uni.showToast({
          title: '两次输入的密码不一致',
          icon: 'none'
        })
        return
      }

      this.changingPassword = true
      try {
        await changePassword(this.passwordForm.old_password, this.passwordForm.new_password)
        uni.showToast({
          title: '密码修改成功',
          icon: 'success'
        })
        this.passwordForm = {
          old_password: '',
          new_password: '',
          confirm_password: ''
        }
      } catch (error) {
        uni.showToast({
          title: error.message || '密码修改失败',
          icon: 'none'
        })
      } finally {
        this.changingPassword = false
      }
    },
    goToReservations() {
      uni.navigateTo({
        url: '/pages/reservations/reservations'
      })
    },
    goToAttendance() {
      uni.navigateTo({
        url: '/pages/attendance/attendance'
      })
    },
    handleLogout() {
      uni.showModal({
        title: '确认退出',
        content: '确定要退出登录吗？',
        success: (res) => {
          if (res.confirm) {
            userStore.logout()
            uni.reLaunch({
              url: '/pages/login/login'
            })
          }
        }
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

.page-header {
  margin-bottom: 40rpx;
}

.page-title {
  font-size: 48rpx;
  font-weight: 600;
  color: #303133;
}

.card {
  background: #ffffff;
  border-radius: 24rpx;
  padding: 40rpx;
  margin-bottom: 32rpx;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.05);
}

.user-card {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 32rpx;
  width: 100%;
}

.avatar-container {
  position: relative;
  width: 120rpx;
  height: 120rpx;
  border-radius: 60rpx;
  overflow: hidden;
}

.avatar {
  width: 120rpx;
  height: 120rpx;
  border-radius: 60rpx;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #ffffff;
  font-size: 48rpx;
  font-weight: 600;
}

.avatar-image {
  width: 120rpx;
  height: 120rpx;
  border-radius: 60rpx;
}

.avatar-mask {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.3);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s;
}

.avatar-container:active .avatar-mask {
  opacity: 1;
}

.avatar-edit-icon {
  font-size: 48rpx;
  color: #ffffff;
}

.user-details {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
  flex: 1;
}

.username {
  font-size: 36rpx;
  font-weight: 600;
  color: #303133;
}

.user-role {
  font-size: 28rpx;
  color: #909399;
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

.form-section {
  display: flex;
  flex-direction: column;
  gap: 32rpx;
}

.form-item {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.form-label {
  font-size: 28rpx;
  color: #606266;
}

.form-input {
  padding: 24rpx;
  background: #f5f7fa;
  border-radius: 16rpx;
  font-size: 28rpx;
  color: #303133;
}

.status-tag {
  padding: 8rpx 24rpx;
  border-radius: 8rpx;
  font-size: 24rpx;
  align-self: flex-start;
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

.save-button {
  width: 100%;
  height: 80rpx;
  background: #667eea;
  color: #ffffff;
  border-radius: 16rpx;
  font-size: 28rpx;
  border: none;
  margin-top: 16rpx;
}

.menu-item {
  display: flex;
  align-items: center;
  padding: 32rpx 0;
  border-bottom: 1px solid #f0f0f0;
}

.menu-item:last-child {
  border-bottom: none;
}

.menu-icon {
  font-size: 48rpx;
  margin-right: 24rpx;
}

.menu-text {
  flex: 1;
  font-size: 28rpx;
  color: #303133;
}

.menu-arrow {
  font-size: 32rpx;
  color: #c0c4cc;
}
</style>

