// API 请求封装
const BASE_URL = 'http://localhost:8080/api' // 根据实际情况修改

// 请求拦截器
const request = (options) => {
  return new Promise((resolve, reject) => {
    // 获取 token
    const token = uni.getStorageSync('token') || ''
    
    // 设置请求头
    const header = {
      'Content-Type': 'application/json',
      ...options.header
    }
    
    if (token) {
      header['Authorization'] = `Bearer ${token}`
    }
    
    // GET 请求使用 data 作为 query 参数
    let url = BASE_URL + options.url
    let requestData = options.data || {}
    
    // GET 请求将 data 转为 query 参数
    if ((options.method || 'GET').toUpperCase() === 'GET' && Object.keys(requestData).length > 0) {
      const query = Object.keys(requestData)
        .map(key => `${encodeURIComponent(key)}=${encodeURIComponent(requestData[key])}`)
        .join('&')
      url += (url.includes('?') ? '&' : '?') + query
      requestData = {}
    }
    
    // 发起请求
    uni.request({
      url: url,
      method: options.method || 'GET',
      data: requestData,
      header: header,
      success: (res) => {
        // 处理响应
        if (res.statusCode === 200) {
          const data = res.data
          // 后端返回格式：{ code: 200, message: "success", data: {...} }
          if (data.code !== undefined && data.code !== 200) {
            uni.showToast({
              title: data.message || '请求失败',
              icon: 'none'
            })
            reject(new Error(data.message || '请求失败'))
            return
          }
          // 返回 data 字段，如果 data 为 null 则返回整个响应
          resolve(data.data !== undefined ? data.data : data)
        } else if (res.statusCode === 401) {
          // 未授权，清除 token 并跳转登录
          uni.removeStorageSync('token')
          uni.reLaunch({
            url: '/pages/login/login'
          })
          uni.showToast({
            title: '登录已过期，请重新登录',
            icon: 'none'
          })
          reject(new Error('未授权'))
        } else if (res.statusCode === 403) {
          uni.showToast({
            title: '没有权限访问',
            icon: 'none'
          })
          reject(new Error('没有权限'))
        } else {
          uni.showToast({
            title: res.data?.message || '请求失败',
            icon: 'none'
          })
          reject(new Error(res.data?.message || '请求失败'))
        }
      },
      fail: (err) => {
        uni.showToast({
          title: '网络错误，请检查网络连接',
          icon: 'none'
        })
        reject(err)
      }
    })
  })
}

export default request

