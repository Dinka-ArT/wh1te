import request from './request'

export const getMembership = () => {
  return request({
    url: '/memberships/current',
    method: 'get'
  })
}

