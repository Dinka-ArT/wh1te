import request from './request'

export const getLockers = () => {
  return request({
    url: '/lockers',
    method: 'get'
  })
}

