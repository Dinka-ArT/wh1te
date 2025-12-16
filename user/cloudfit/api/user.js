import request from '../utils/request.js'

export const updateProfile = (data) => {
  return request({
    url: '/user/profile',
    method: 'PUT',
    data
  })
}

export const changePassword = (oldPassword, newPassword) => {
  return request({
    url: '/user/password',
    method: 'PUT',
    data: {
      old_password: oldPassword,
      new_password: newPassword
    }
  })
}

