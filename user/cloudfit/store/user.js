// 用户状态管理
import { login, getUserInfo } from '../api/auth.js'

export default {
  state: {
    userInfo: null,
    token: uni.getStorageSync('token') || ''
  },
  
  getters: {
    isLoggedIn() {
      return !!this.state.token
    },
    isAdmin() {
      return this.state.userInfo?.role === 'admin'
    },
    isMember() {
      return this.state.userInfo?.role === 'member'
    },
    isCoach() {
      return this.state.userInfo?.role === 'coach'
    }
  },
  
  // 规范化后端返回的用户信息
  normalizeUser(data) {
    if (!data) return null
    const roleFromArray = Array.isArray(data.roles) && data.roles.length > 0
      ? (data.roles[0].role ?? data.roles[0].role_name ?? data.roles[0].roleName)
      : undefined
    const role =
      data.role ??
      data.role_name ??
      data.roleName ??
      roleFromArray

    return {
      ...data,
      role,
      user_id: data.user_id ?? data.userId,
      username: data.username,
      phone_number: data.phone_number ?? data.phoneNumber,
      email: data.email,
      status: data.status ?? data.userStatus,
      registration_date: data.registration_date ?? data.registrationDate,
    }
  },
  
  // 登录
  async loginUser(username, password) {
    const response = await login(username, password)
    this.state.token = response.token
    uni.setStorageSync('token', this.state.token)
    await this.fetchUserInfo()
    return { success: true }
  },
  
  // 获取用户信息
  async fetchUserInfo() {
    const data = await getUserInfo()
    this.state.userInfo = this.normalizeUser(data)
  },
  
  // 登出
  logout() {
    this.state.userInfo = null
    this.state.token = ''
    uni.removeStorageSync('token')
  }
}
