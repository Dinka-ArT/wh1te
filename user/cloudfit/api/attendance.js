import request from '../utils/request.js'

export const getAttendance = (params) => {
  return request({
    url: '/attendance',
    method: 'GET',
    data: params || {}
  })
}

