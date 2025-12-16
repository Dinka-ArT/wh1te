"use strict";
const common_vendor = require("../common/vendor.js");
const chooseImage = () => {
  return new Promise((resolve, reject) => {
    common_vendor.index.chooseImage({
      count: 1,
      sizeType: ["compressed"],
      sourceType: ["album", "camera"],
      success: (res) => {
        resolve(res.tempFilePaths[0]);
      },
      fail: (err) => {
        reject(err);
      }
    });
  });
};
const saveImageToLocal = (tempFilePath, fileName) => {
  return new Promise((resolve, reject) => {
    common_vendor.index.saveFile({
      tempFilePath,
      success: (res) => {
        const savedFilePath = res.savedFilePath;
        resolve(savedFilePath);
      },
      fail: (err) => {
        saveAsBase64(tempFilePath, fileName).then(resolve).catch(reject);
      }
    });
  });
};
const saveAsBase64 = (tempFilePath, fileName) => {
  return new Promise((resolve, reject) => {
    const fs = common_vendor.index.getFileSystemManager();
    fs.readFile({
      filePath: tempFilePath,
      encoding: "base64",
      success: (res) => {
        const base64Data = `data:image/jpeg;base64,${res.data}`;
        common_vendor.index.setStorageSync(`avatar_base64_${fileName}`, base64Data);
        resolve(base64Data);
      },
      fail: (err) => {
        reject(err);
      }
    });
  });
};
const getAvatarPath = (userId) => {
  if (!userId)
    return null;
  const storedPath = common_vendor.index.getStorageSync(`avatar_${userId}`);
  if (storedPath) {
    return storedPath;
  }
  const base64Data = common_vendor.index.getStorageSync(`avatar_base64_${userId}.jpg`);
  if (base64Data) {
    common_vendor.index.setStorageSync(`avatar_${userId}`, base64Data);
    return base64Data;
  }
  return null;
};
exports.chooseImage = chooseImage;
exports.getAvatarPath = getAvatarPath;
exports.saveImageToLocal = saveImageToLocal;
//# sourceMappingURL=../../.sourcemap/mp-weixin/utils/upload.js.map
