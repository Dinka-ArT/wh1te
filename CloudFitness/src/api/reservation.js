import request from './request'

export const getReservations = () => {
  return request({
    url: '/reservations',
    method: 'get'
  })
}

export const cancelReservation = (reservationId) => {
  return request({
    url: `/reservations/${reservationId}`,
    method: 'delete'
  })
}

