import request from '../utils/request.js'

export const getReservations = () => {
  return request({
    url: '/reservations',
    method: 'GET'
  })
}

export const cancelReservation = (reservationId) => {
  return request({
    url: `/reservations/${reservationId}`,
    method: 'DELETE'
  })
}

