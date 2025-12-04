import request from './request'

export const getAttendance = (params) => {
  return request({
    url: '/attendance',
    method: 'get',
    params
  })
}

