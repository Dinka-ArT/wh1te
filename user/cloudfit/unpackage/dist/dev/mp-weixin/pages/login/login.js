"use strict";
const common_vendor = require("../../common/vendor.js");
const store_user = require("../../store/user.js");
const _sfc_main = {
  data() {
    return {
      loginForm: {
        username: "",
        password: ""
      },
      loading: false
    };
  },
  methods: {
    async handleLogin() {
      if (!this.loginForm.username) {
        common_vendor.index.showToast({
          title: "请输入用户名",
          icon: "none"
        });
        return;
      }
      if (!this.loginForm.password) {
        common_vendor.index.showToast({
          title: "请输入密码",
          icon: "none"
        });
        return;
      }
      this.loading = true;
      try {
        await store_user.userStore.loginUser(this.loginForm.username, this.loginForm.password);
        common_vendor.index.showToast({
          title: "登录成功",
          icon: "success"
        });
        setTimeout(() => {
          common_vendor.index.switchTab({
            url: "/pages/index/index"
          });
        }, 1500);
      } catch (error) {
        common_vendor.index.showToast({
          title: error.message || "登录失败",
          icon: "none"
        });
      } finally {
        this.loading = false;
      }
    },
    goToRegister() {
      common_vendor.index.navigateTo({
        url: "/pages/register/register"
      });
    }
  }
};
function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
  return {
    a: $data.loginForm.username,
    b: common_vendor.o(($event) => $data.loginForm.username = $event.detail.value),
    c: common_vendor.o((...args) => $options.handleLogin && $options.handleLogin(...args)),
    d: $data.loginForm.password,
    e: common_vendor.o(($event) => $data.loginForm.password = $event.detail.value),
    f: $data.loading,
    g: common_vendor.o((...args) => $options.handleLogin && $options.handleLogin(...args)),
    h: common_vendor.o((...args) => $options.goToRegister && $options.goToRegister(...args))
  };
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render], ["__scopeId", "data-v-e4e4508d"]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/login/login.js.map
