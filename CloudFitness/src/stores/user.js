import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login, getUserInfo } from '@/api/auth'

export const useUserStore = defineStore('user', () => {
  const userInfo = ref(null)
  const token = ref(localStorage.getItem('token') || '')

  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => userInfo.value?.role === 'admin')
  const isMember = computed(() => userInfo.value?.role === 'member')
  const isCoach = computed(() => userInfo.value?.role === 'coach')

  // 规范化后端返回的用户信息，兼容驼峰/下划线/数组形式的角色字段
  const normalizeUser = (data) => {
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
  }

  const loginUser = async (username, password) => {
    const response = await login(username, password)
    token.value = response.token
    localStorage.setItem('token', token.value)
    await fetchUserInfo()
    return { success: true }
  }

  const fetchUserInfo = async () => {
    const data = await getUserInfo()
    userInfo.value = normalizeUser(data)
  }

  const logout = () => {
    userInfo.value = null
    token.value = ''
    localStorage.removeItem('token')
  }

  return {
    userInfo,
    token,
    isLoggedIn,
    isAdmin,
    isMember,
    isCoach,
    loginUser,
    fetchUserInfo,
    logout
  }
})

