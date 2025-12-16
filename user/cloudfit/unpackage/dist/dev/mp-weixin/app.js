"use strict";
Object.defineProperty(exports, Symbol.toStringTag, { value: "Module" });
const common_vendor = require("./common/vendor.js");
const store_user = require("./store/user.js");
if (!Math) {
  "./pages/login/login.js";
  "./pages/index/index.js";
  "./pages/courses/courses.js";
  "./pages/profile/profile.js";
  "./pages/register/register.js";
  "./pages/reservations/reservations.js";
  "./pages/lockers/lockers.js";
  "./pages/attendance/attendance.js";
}
const _sfc_main = {
  onLaunch: function() {
    common_vendor.index.__f__("log", "at App.vue:6", "App Launch");
    const token = common_vendor.index.getStorageSync("token");
    if (token) {
      store_user.userStore.fetchUserInfo().catch((err) => {
        common_vendor.index.__f__("error", "at App.vue:12", "获取用户信息失败:", err);
        store_user.userStore.logout();
      });
    }
  },
  onShow: function() {
    common_vendor.index.__f__("log", "at App.vue:19", "App Show");
  },
  onHide: function() {
    common_vendor.index.__f__("log", "at App.vue:22", "App Hide");
  }
};
function createApp() {
  const app = common_vendor.createSSRApp(_sfc_main);
  return {
    app
  };
}
createApp().app.mount("#app");
exports.createApp = createApp;
//# sourceMappingURL=../.sourcemap/mp-weixin/app.js.map
