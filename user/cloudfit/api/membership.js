import request from '../utils/request.js'

export const getMembership = () => {
  return request({
    url: '/memberships/current',
    method: 'GET'
  })
}

