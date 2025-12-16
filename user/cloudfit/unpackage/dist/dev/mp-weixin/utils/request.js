"use strict";
const common_vendor = require("../common/vendor.js");
const BASE_URL = "http://localhost:8080/api";
const request = (options) => {
  return new Promise((resolve, reject) => {
    const token = common_vendor.index.getStorageSync("token") || "";
    const header = {
      "Content-Type": "application/json",
      ...options.header
    };
    if (token) {
      header["Authorization"] = `Bearer ${token}`;
    }
    let url = BASE_URL + options.url;
    let requestData = options.data || {};
    if ((options.method || "GET").toUpperCase() === "GET" && Object.keys(requestData).length > 0) {
      const query = Object.keys(requestData).map((key) => `${encodeURIComponent(key)}=${encodeURIComponent(requestData[key])}`).join("&");
      url += (url.includes("?") ? "&" : "?") + query;
      requestData = {};
    }
    common_vendor.index.request({
      url,
      method: options.method || "GET",
      data: requestData,
      header,
      success: (res) => {
        var _a, _b;
        if (res.statusCode === 200) {
          const data = res.data;
          if (data.code !== void 0 && data.code !== 200) {
            common_vendor.index.showToast({
              title: data.message || "请求失败",
              icon: "none"
            });
            reject(new Error(data.message || "请求失败"));
            return;
          }
          resolve(data.data !== void 0 ? data.data : data);
        } else if (res.statusCode === 401) {
          common_vendor.index.removeStorageSync("token");
          common_vendor.index.reLaunch({
            url: "/pages/login/login"
          });
          common_vendor.index.showToast({
            title: "登录已过期，请重新登录",
            icon: "none"
          });
          reject(new Error("未授权"));
        } else if (res.statusCode === 403) {
          common_vendor.index.showToast({
            title: "没有权限访问",
            icon: "none"
          });
          reject(new Error("没有权限"));
        } else {
          common_vendor.index.showToast({
            title: ((_a = res.data) == null ? void 0 : _a.message) || "请求失败",
            icon: "none"
          });
          reject(new Error(((_b = res.data) == null ? void 0 : _b.message) || "请求失败"));
        }
      },
      fail: (err) => {
        common_vendor.index.showToast({
          title: "网络错误，请检查网络连接",
          icon: "none"
        });
        reject(err);
      }
    });
  });
};
exports.request = request;
//# sourceMappingURL=../../.sourcemap/mp-weixin/utils/request.js.map
