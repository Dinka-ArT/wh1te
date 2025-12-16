<template>
  <view class="register-container">
    <view class="register-box">
      <view class="register-header">
        <text class="title">云健健身</text>
        <text class="subtitle">会员注册</text>
      </view>
      <view class="register-form">
        <view class="form-item">
          <input
            class="input"
            type="text"
            placeholder="请输入用户名（3-20位字母、数字、下划线）"
            v-model="registerForm.username"
            @blur="checkUsername"
          />
        </view>
        <view class="form-item">
          <input
            class="input"
            type="number"
            placeholder="请输入手机号"
            v-model="registerForm.phone_number"
            @blur="checkPhone"
          />
        </view>
        <view class="form-item">
          <input
            class="input"
            type="password"
            placeholder="请输入密码（至少6位）"
            v-model="registerForm.password"
          />
        </view>
        <view class="form-item">
          <input
            class="input"
            type="password"
            placeholder="请确认密码"
            v-model="registerForm.confirmPassword"
          />
        </view>
        <button class="register-button" :loading="loading" @click="handleRegister">
          注册
        </button>
        <view class="login-link">
          <text>已有账号？</text>
          <text class="link" @click="goToLogin">立即登录</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { register, checkUsernameExists, checkPhoneExists } from '../../api/auth.js'

export default {
  data() {
    return {
      registerForm: {
        username: '',
        phone_number: '',
        password: '',
        confirmPassword: ''
      },
      loading: false
    }
  },
  methods: {
    async checkUsername() {
      if (!this.registerForm.username || this.registerForm.username.length < 3) {
        return
      }
      try {
        const exists = await checkUsernameExists(this.registerForm.username)
        if (exists) {
          uni.showToast({
            title: '该用户名已被使用',
            icon: 'none'
          })
        }
      } catch (error) {
        console.warn('检查用户名失败:', error)
      }
    },
    async checkPhone() {
      if (!this.registerForm.phone_number || !/^1[3-9]\d{9}$/.test(this.registerForm.phone_number)) {
        return
      }
      try {
        const exists = await checkPhoneExists(this.registerForm.phone_number)
        if (exists) {
          uni.showToast({
            title: '该手机号已被注册',
            icon: 'none'
          })
        }
      } catch (error) {
        console.warn('检查手机号失败:', error)
      }
    },
    async handleRegister() {
      // 验证
      if (!this.registerForm.username) {
        uni.showToast({
          title: '请输入用户名',
          icon: 'none'
        })
        return
      }
      if (this.registerForm.username.length < 3 || this.registerForm.username.length > 20) {
        uni.showToast({
          title: '用户名长度为3-20位',
          icon: 'none'
        })
        return
      }
      if (!/^[a-zA-Z0-9_]+$/.test(this.registerForm.username)) {
        uni.showToast({
          title: '用户名只能包含字母、数字和下划线',
          icon: 'none'
        })
        return
      }
      if (!this.registerForm.phone_number) {
        uni.showToast({
          title: '请输入手机号',
          icon: 'none'
        })
        return
      }
      if (!/^1[3-9]\d{9}$/.test(this.registerForm.phone_number)) {
        uni.showToast({
          title: '请输入正确的手机号',
          icon: 'none'
        })
        return
      }
      if (!this.registerForm.password) {
        uni.showToast({
          title: '请输入密码',
          icon: 'none'
        })
        return
      }
      if (this.registerForm.password.length < 6) {
        uni.showToast({
          title: '密码长度不能少于6位',
          icon: 'none'
        })
        return
      }
      if (this.registerForm.password !== this.registerForm.confirmPassword) {
        uni.showToast({
          title: '两次输入的密码不一致',
          icon: 'none'
        })
        return
      }
      
      this.loading = true
      try {
        await register({
          username: this.registerForm.username,
          phone_number: this.registerForm.phone_number,
          password: this.registerForm.password
        })
        uni.showToast({
          title: '注册成功，请登录',
          icon: 'success'
        })
        setTimeout(() => {
          uni.navigateBack()
        }, 1500)
      } catch (error) {
        uni.showToast({
          title: error.message || '注册失败，请重试',
          icon: 'none'
        })
      } finally {
        this.loading = false
      }
    },
    goToLogin() {
      uni.navigateBack()
    }
  }
}
</script>

<style scoped>
.register-container {
  width: 100%;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40rpx;
}

.register-box {
  width: 100%;
  max-width: 600rpx;
  background: #ffffff;
  border-radius: 32rpx;
  padding: 80rpx 60rpx;
  box-shadow: 0 20rpx 80rpx rgba(0, 0, 0, 0.1);
}

.register-header {
  text-align: center;
  margin-bottom: 80rpx;
}

.title {
  display: block;
  font-size: 64rpx;
  font-weight: 600;
  color: #303133;
  margin-bottom: 16rpx;
}

.subtitle {
  display: block;
  font-size: 28rpx;
  color: #909399;
}

.register-form {
  width: 100%;
}

.form-item {
  margin-bottom: 40rpx;
}

.input {
  width: 100%;
  height: 88rpx;
  padding: 0 24rpx;
  background: #f5f7fa;
  border-radius: 16rpx;
  font-size: 28rpx;
  color: #303133;
}

.register-button {
  width: 100%;
  height: 88rpx;
  background: #667eea;
  color: #ffffff;
  border-radius: 16rpx;
  font-size: 32rpx;
  font-weight: 500;
  margin-top: 40rpx;
  border: none;
}

.login-link {
  text-align: center;
  margin-top: 40rpx;
  font-size: 28rpx;
  color: #909399;
}

.link {
  color: #667eea;
  margin-left: 8rpx;
}
</style>

