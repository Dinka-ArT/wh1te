import request from '../utils/request.js'

export const login = (username, password) => {
  return request({
    url: '/auth/login',
    method: 'POST',
    data: { username, password }
  })
}

export const register = (data) => {
  return request({
    url: '/auth/register',
    method: 'POST',
    data
  })
}

export const checkUsernameExists = async (username) => {
  try {
    const res = await request({
      url: '/auth/check-username',
      method: 'POST',
      data: { username }
    })
    return res.exists === true || res.data?.exists === true
  } catch (error) {
    return false
  }
}

export const checkPhoneExists = async (phone_number) => {
  try {
    const res = await request({
      url: '/auth/check-phone',
      method: 'POST',
      data: { phone_number }
    })
    return res.exists === true || res.data?.exists === true
  } catch (error) {
    return false
  }
}

export const getUserInfo = () => {
  return request({
    url: '/user/info',
    method: 'GET'
  })
}

