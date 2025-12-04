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

  const loginUser = async (username, password) => {
    const response = await login(username, password)
    token.value = response.token
    localStorage.setItem('token', token.value)
    await fetchUserInfo()
    return { success: true }
  }

  const fetchUserInfo = async () => {
    const data = await getUserInfo()
    userInfo.value = data
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

