// 图片上传工具
export const chooseImage = () => {
  return new Promise((resolve, reject) => {
    uni.chooseImage({
      count: 1,
      sizeType: ['compressed'],
      sourceType: ['album', 'camera'],
      success: (res) => {
        resolve(res.tempFilePaths[0])
      },
      fail: (err) => {
        reject(err)
      }
    })
  })
}

// 保存图片到本地
export const saveImageToLocal = (tempFilePath, fileName) => {
  return new Promise((resolve, reject) => {
    // 小程序环境使用 uni.saveFile 保存文件
    // #ifdef MP-WEIXIN || MP-ALIPAY || MP-BAIDU || MP-TOUTIAO || MP-QQ
    uni.saveFile({
      tempFilePath: tempFilePath,
      success: (res) => {
        // 保存成功，返回保存的文件路径
        const savedFilePath = res.savedFilePath
        resolve(savedFilePath)
      },
      fail: (err) => {
        // 如果保存失败，尝试使用 base64
        saveAsBase64(tempFilePath, fileName).then(resolve).catch(reject)
      }
    })
    // #endif
    
    // #ifndef MP-WEIXIN || MP-ALIPAY || MP-BAIDU || MP-TOUTIAO || MP-QQ
    // 其他平台使用 base64 存储
    saveAsBase64(tempFilePath, fileName).then(resolve).catch(reject)
    // #endif
  })
}

// 保存为 base64
const saveAsBase64 = (tempFilePath, fileName) => {
  return new Promise((resolve, reject) => {
    const fs = uni.getFileSystemManager()
    fs.readFile({
      filePath: tempFilePath,
      encoding: 'base64',
      success: (res) => {
        const base64Data = `data:image/jpeg;base64,${res.data}`
        // 保存到本地存储
        uni.setStorageSync(`avatar_base64_${fileName}`, base64Data)
        resolve(base64Data)
      },
      fail: (err) => {
        reject(err)
      }
    })
  })
}

// 获取本地头像路径
export const getAvatarPath = (userId) => {
  if (!userId) return null
  
  // 先从本地存储获取路径
  const storedPath = uni.getStorageSync(`avatar_${userId}`)
  if (storedPath) {
    return storedPath
  }
  
  // 检查是否有 base64 存储（兼容旧版本）
  const base64Data = uni.getStorageSync(`avatar_base64_${userId}.jpg`)
  if (base64Data) {
    uni.setStorageSync(`avatar_${userId}`, base64Data)
    return base64Data
  }
  
  return null
}

