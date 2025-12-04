import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getReservations, cancelReservation } from '@/api/reservation'

export const useReservationStore = defineStore('reservation', () => {
  const reservations = ref([])
  const loading = ref(false)

  const fetchReservations = async () => {
    loading.value = true
    try {
      const data = await getReservations()
      reservations.value = Array.isArray(data) ? data : (data.reservations || [])
    } finally {
      loading.value = false
    }
  }

  const cancel = async (reservationId) => {
    try {
      await cancelReservation(reservationId)
      await fetchReservations()
      return { success: true }
    } catch (error) {
      return { success: false, message: error.message || '取消失败' }
    }
  }

  return {
    reservations,
    loading,
    fetchReservations,
    cancel
  }
})

