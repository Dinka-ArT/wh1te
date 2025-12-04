import request from './request'

export const login = (username, password) => {
  return request({
    url: '/auth/login',
    method: 'post',
    data: { username, password }
  })
}

export const register = (data) => {
  return request({
    url: '/auth/register',
    method: 'post',
    data
  })
}

export const checkUsernameExists = async (username) => {
  try {
    const res = await request({
      url: '/auth/check-username',
      method: 'post',
      data: { username }
    })
    return res.exists === true || res.data?.exists === true
  } catch (error) {
    // API失败时返回false，不阻止用户继续注册
    return false
  }
}

export const checkPhoneExists = async (phone_number) => {
  try {
    const res = await request({
      url: '/auth/check-phone',
      method: 'post',
      data: { phone_number }
    })
    return res.exists === true || res.data?.exists === true
  } catch (error) {
    // API失败时返回false，不阻止用户继续注册
    return false
  }
}

export const getUserInfo = () => {
  return request({
    url: '/user/info',
    method: 'get'
  })
}

