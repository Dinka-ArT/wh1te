<template>
  <view class="login-container">
    <view class="login-box">
      <view class="login-header">
        <text class="title">云健健身</text>
        <text class="subtitle">会员登录</text>
      </view>
      <view class="login-form">
        <view class="form-item">
          <input
            class="input"
            type="text"
            placeholder="请输入用户名"
            v-model="loginForm.username"
          />
        </view>
        <view class="form-item">
          <input
            class="input"
            type="password"
            placeholder="请输入密码"
            v-model="loginForm.password"
            @confirm="handleLogin"
          />
        </view>
        <button class="login-button" :loading="loading" @click="handleLogin">
          登录
        </button>
        <view class="register-link">
          <text>还没有账号？</text>
          <text class="link" @click="goToRegister">立即注册</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import userStore from '../../store/user.js'
import { login } from '../../api/auth.js'

export default {
  data() {
    return {
      loginForm: {
        username: '',
        password: ''
      },
      loading: false
    }
  },
  methods: {
    async handleLogin() {
      if (!this.loginForm.username) {
        uni.showToast({
          title: '请输入用户名',
          icon: 'none'
        })
        return
      }
      if (!this.loginForm.password) {
        uni.showToast({
          title: '请输入密码',
          icon: 'none'
        })
        return
      }
      
      this.loading = true
      try {
        await userStore.loginUser(this.loginForm.username, this.loginForm.password)
        uni.showToast({
          title: '登录成功',
          icon: 'success'
        })
        setTimeout(() => {
          uni.switchTab({
            url: '/pages/index/index'
          })
        }, 1500)
      } catch (error) {
        uni.showToast({
          title: error.message || '登录失败',
          icon: 'none'
        })
      } finally {
        this.loading = false
      }
    },
    goToRegister() {
      uni.navigateTo({
        url: '/pages/register/register'
      })
    }
  }
}
</script>

<style scoped>
.login-container {
  width: 100%;
  height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40rpx;
}

.login-box {
  width: 100%;
  max-width: 600rpx;
  background: #ffffff;
  border-radius: 32rpx;
  padding: 80rpx 60rpx;
  box-shadow: 0 20rpx 80rpx rgba(0, 0, 0, 0.1);
}

.login-header {
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

.login-form {
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

.login-button {
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

.register-link {
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

