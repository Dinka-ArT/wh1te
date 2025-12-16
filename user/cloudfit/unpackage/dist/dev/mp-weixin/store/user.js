"use strict";
const common_vendor = require("../common/vendor.js");
const api_auth = require("../api/auth.js");
const userStore = {
  state: {
    userInfo: null,
    token: common_vendor.index.getStorageSync("token") || ""
  },
  getters: {
    isLoggedIn() {
      return !!this.state.token;
    },
    isAdmin() {
      var _a;
      return ((_a = this.state.userInfo) == null ? void 0 : _a.role) === "admin";
    },
    isMember() {
      var _a;
      return ((_a = this.state.userInfo) == null ? void 0 : _a.role) === "member";
    },
    isCoach() {
      var _a;
      return ((_a = this.state.userInfo) == null ? void 0 : _a.role) === "coach";
    }
  },
  // 规范化后端返回的用户信息
  normalizeUser(data) {
    if (!data)
      return null;
    const roleFromArray = Array.isArray(data.roles) && data.roles.length > 0 ? data.roles[0].role ?? data.roles[0].role_name ?? data.roles[0].roleName : void 0;
    const role = data.role ?? data.role_name ?? data.roleName ?? roleFromArray;
    return {
      ...data,
      role,
      user_id: data.user_id ?? data.userId,
      username: data.username,
      phone_number: data.phone_number ?? data.phoneNumber,
      email: data.email,
      status: data.status ?? data.userStatus,
      registration_date: data.registration_date ?? data.registrationDate
    };
  },
  // 登录
  async loginUser(username, password) {
    const response = await api_auth.login(username, password);
    this.state.token = response.token;
    common_vendor.index.setStorageSync("token", this.state.token);
    await this.fetchUserInfo();
    return { success: true };
  },
  // 获取用户信息
  async fetchUserInfo() {
    const data = await api_auth.getUserInfo();
    this.state.userInfo = this.normalizeUser(data);
  },
  // 登出
  logout() {
    this.state.userInfo = null;
    this.state.token = "";
    common_vendor.index.removeStorageSync("token");
  }
};
exports.userStore = userStore;
//# sourceMappingURL=../../.sourcemap/mp-weixin/store/user.js.map
