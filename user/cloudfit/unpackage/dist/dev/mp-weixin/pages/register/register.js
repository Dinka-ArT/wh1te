"use strict";
const common_vendor = require("../../common/vendor.js");
const api_auth = require("../../api/auth.js");
const _sfc_main = {
  data() {
    return {
      registerForm: {
        username: "",
        phone_number: "",
        password: "",
        confirmPassword: ""
      },
      loading: false
    };
  },
  methods: {
    async checkUsername() {
      if (!this.registerForm.username || this.registerForm.username.length < 3) {
        return;
      }
      try {
        const exists = await api_auth.checkUsernameExists(this.registerForm.username);
        if (exists) {
          common_vendor.index.showToast({
            title: "该用户名已被使用",
            icon: "none"
          });
        }
      } catch (error) {
        common_vendor.index.__f__("warn", "at pages/register/register.vue:84", "检查用户名失败:", error);
      }
    },
    async checkPhone() {
      if (!this.registerForm.phone_number || !/^1[3-9]\d{9}$/.test(this.registerForm.phone_number)) {
        return;
      }
      try {
        const exists = await api_auth.checkPhoneExists(this.registerForm.phone_number);
        if (exists) {
          common_vendor.index.showToast({
            title: "该手机号已被注册",
            icon: "none"
          });
        }
      } catch (error) {
        common_vendor.index.__f__("warn", "at pages/register/register.vue:100", "检查手机号失败:", error);
      }
    },
    async handleRegister() {
      if (!this.registerForm.username) {
        common_vendor.index.showToast({
          title: "请输入用户名",
          icon: "none"
        });
        return;
      }
      if (this.registerForm.username.length < 3 || this.registerForm.username.length > 20) {
        common_vendor.index.showToast({
          title: "用户名长度为3-20位",
          icon: "none"
        });
        return;
      }
      if (!/^[a-zA-Z0-9_]+$/.test(this.registerForm.username)) {
        common_vendor.index.showToast({
          title: "用户名只能包含字母、数字和下划线",
          icon: "none"
        });
        return;
      }
      if (!this.registerForm.phone_number) {
        common_vendor.index.showToast({
          title: "请输入手机号",
          icon: "none"
        });
        return;
      }
      if (!/^1[3-9]\d{9}$/.test(this.registerForm.phone_number)) {
        common_vendor.index.showToast({
          title: "请输入正确的手机号",
          icon: "none"
        });
        return;
      }
      if (!this.registerForm.password) {
        common_vendor.index.showToast({
          title: "请输入密码",
          icon: "none"
        });
        return;
      }
      if (this.registerForm.password.length < 6) {
        common_vendor.index.showToast({
          title: "密码长度不能少于6位",
          icon: "none"
        });
        return;
      }
      if (this.registerForm.password !== this.registerForm.confirmPassword) {
        common_vendor.index.showToast({
          title: "两次输入的密码不一致",
          icon: "none"
        });
        return;
      }
      this.loading = true;
      try {
        await api_auth.register({
          username: this.registerForm.username,
          phone_number: this.registerForm.phone_number,
          password: this.registerForm.password
        });
        common_vendor.index.showToast({
          title: "注册成功，请登录",
          icon: "success"
        });
        setTimeout(() => {
          common_vendor.index.navigateBack();
        }, 1500);
      } catch (error) {
        common_vendor.index.showToast({
          title: error.message || "注册失败，请重试",
          icon: "none"
        });
      } finally {
        this.loading = false;
      }
    },
    goToLogin() {
      common_vendor.index.navigateBack();
    }
  }
};
function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
  return {
    a: common_vendor.o((...args) => $options.checkUsername && $options.checkUsername(...args)),
    b: $data.registerForm.username,
    c: common_vendor.o(($event) => $data.registerForm.username = $event.detail.value),
    d: common_vendor.o((...args) => $options.checkPhone && $options.checkPhone(...args)),
    e: $data.registerForm.phone_number,
    f: common_vendor.o(($event) => $data.registerForm.phone_number = $event.detail.value),
    g: $data.registerForm.password,
    h: common_vendor.o(($event) => $data.registerForm.password = $event.detail.value),
    i: $data.registerForm.confirmPassword,
    j: common_vendor.o(($event) => $data.registerForm.confirmPassword = $event.detail.value),
    k: $data.loading,
    l: common_vendor.o((...args) => $options.handleRegister && $options.handleRegister(...args)),
    m: common_vendor.o((...args) => $options.goToLogin && $options.goToLogin(...args))
  };
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render], ["__scopeId", "data-v-bac4a35d"]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/register/register.js.map
