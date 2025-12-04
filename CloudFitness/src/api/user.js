import request from './request'

export const updateProfile = (data) => {
  return request({
    url: '/user/profile',
    method: 'put',
    data
  })
}

export const changePassword = (oldPassword, newPassword) => {
  return request({
    url: '/user/password',
    method: 'put',
    data: { old_password: oldPassword, new_password: newPassword }
  })
}

