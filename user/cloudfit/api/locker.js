import request from '../utils/request.js'

export const getLockers = () => {
  return request({
    url: '/lockers',
    method: 'GET'
  })
}

