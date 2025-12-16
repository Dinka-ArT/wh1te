"use strict";
const common_vendor = require("../../common/vendor.js");
const store_user = require("../../store/user.js");
const api_user = require("../../api/user.js");
const api_membership = require("../../api/membership.js");
const utils_date = require("../../utils/date.js");
const utils_upload = require("../../utils/upload.js");
const _sfc_main = {
  data() {
    return {
      userInfo: null,
      membership: null,
      avatarUrl: "",
      profileForm: {
        email: "",
        phone_number: ""
      },
      passwordForm: {
        old_password: "",
        new_password: "",
        confirm_password: ""
      },
      saving: false,
      changingPassword: false,
      uploadingAvatar: false
    };
  },
  computed: {
    membershipStatus() {
      if (!this.membership) {
        return { type: "info", text: "无会员卡" };
      }
      if (utils_date.isExpired(this.membership.expiry_date)) {
        return { type: "danger", text: "已过期" };
      }
      return { type: "success", text: "有效" };
    }
  },
  onLoad() {
    if (!store_user.userStore.getters.isLoggedIn()) {
      common_vendor.index.reLaunch({
        url: "/pages/login/login"
      });
      return;
    }
    this.userInfo = store_user.userStore.state.userInfo;
    this.loadData();
  },
  onShow() {
    if (!store_user.userStore.getters.isLoggedIn()) {
      common_vendor.index.reLaunch({
        url: "/pages/login/login"
      });
      return;
    }
    this.userInfo = store_user.userStore.state.userInfo;
    if (this.userInfo) {
      this.initProfile();
      this.loadAvatar();
    }
    this.loadData();
  },
  methods: {
    async loadData() {
      if (store_user.userStore.getters.isLoggedIn()) {
        try {
          if (!store_user.userStore.state.userInfo) {
            await store_user.userStore.fetchUserInfo();
          }
          this.userInfo = store_user.userStore.state.userInfo;
          if (this.userInfo) {
            this.initProfile();
            this.loadAvatar();
          }
        } catch (error) {
          common_vendor.index.__f__("error", "at pages/profile/profile.vue:219", "获取用户信息失败:", error);
          this.userInfo = store_user.userStore.state.userInfo;
          if (this.userInfo) {
            this.initProfile();
            this.loadAvatar();
          } else {
            common_vendor.index.showToast({
              title: "获取用户信息失败",
              icon: "none"
            });
          }
        }
      } else {
        this.userInfo = store_user.userStore.state.userInfo;
        if (this.userInfo) {
          this.initProfile();
          this.loadAvatar();
        }
      }
      try {
        const membershipData = await api_membership.getMembership();
        this.membership = membershipData;
      } catch (error) {
        common_vendor.index.__f__("error", "at pages/profile/profile.vue:246", "获取会员卡信息失败:", error);
      }
    },
    initProfile() {
      if (this.userInfo) {
        this.profileForm.email = this.userInfo.email || "";
        this.profileForm.phone_number = this.userInfo.phone_number || "";
      }
    },
    loadAvatar() {
      if (this.userInfo && this.userInfo.user_id) {
        const avatarPath = utils_upload.getAvatarPath(this.userInfo.user_id);
        if (avatarPath) {
          this.avatarUrl = avatarPath;
        }
      }
    },
    async changeAvatar() {
      if (this.uploadingAvatar)
        return;
      try {
        const tempFilePath = await utils_upload.chooseImage();
        if (!this.userInfo || !this.userInfo.user_id) {
          common_vendor.index.showToast({
            title: "请先登录",
            icon: "none"
          });
          return;
        }
        this.uploadingAvatar = true;
        const fileName = `${this.userInfo.user_id}.jpg`;
        const savedPath = await utils_upload.saveImageToLocal(tempFilePath, fileName);
        this.avatarUrl = savedPath;
        common_vendor.index.setStorageSync(`avatar_${this.userInfo.user_id}`, savedPath);
        if (store_user.userStore.state.userInfo) {
          store_user.userStore.state.userInfo.avatar = savedPath;
        }
        this.avatarUrl = savedPath;
        common_vendor.index.showToast({
          title: "头像更新成功",
          icon: "success"
        });
      } catch (error) {
        common_vendor.index.__f__("error", "at pages/profile/profile.vue:303", "更换头像失败:", error);
        if (error.errMsg && !error.errMsg.includes("cancel")) {
          common_vendor.index.showToast({
            title: "更换头像失败",
            icon: "none"
          });
        }
      } finally {
        this.uploadingAvatar = false;
      }
    },
    getRoleText(role) {
      const roleMap = {
        admin: "管理员",
        member: "会员",
        coach: "教练"
      };
      return roleMap[role] || "用户";
    },
    async handleSaveProfile() {
      if (this.profileForm.email && !/^[\w-]+(\.[\w-]+)*@[\w-]+(\.[\w-]+)+$/.test(this.profileForm.email)) {
        common_vendor.index.showToast({
          title: "请输入正确的邮箱地址",
          icon: "none"
        });
        return;
      }
      if (this.profileForm.phone_number && !/^1[3-9]\d{9}$/.test(this.profileForm.phone_number)) {
        common_vendor.index.showToast({
          title: "请输入正确的手机号",
          icon: "none"
        });
        return;
      }
      this.saving = true;
      try {
        await api_user.updateProfile(this.profileForm);
        common_vendor.index.showToast({
          title: "保存成功",
          icon: "success"
        });
        await store_user.userStore.fetchUserInfo();
        this.userInfo = store_user.userStore.state.userInfo;
        this.initProfile();
      } catch (error) {
        common_vendor.index.showToast({
          title: error.message || "保存失败",
          icon: "none"
        });
      } finally {
        this.saving = false;
      }
    },
    async handleChangePassword() {
      if (!this.passwordForm.old_password) {
        common_vendor.index.showToast({
          title: "请输入原密码",
          icon: "none"
        });
        return;
      }
      if (!this.passwordForm.new_password) {
        common_vendor.index.showToast({
          title: "请输入新密码",
          icon: "none"
        });
        return;
      }
      if (this.passwordForm.new_password.length < 6) {
        common_vendor.index.showToast({
          title: "密码长度不能少于6位",
          icon: "none"
        });
        return;
      }
      if (this.passwordForm.new_password !== this.passwordForm.confirm_password) {
        common_vendor.index.showToast({
          title: "两次输入的密码不一致",
          icon: "none"
        });
        return;
      }
      this.changingPassword = true;
      try {
        await api_user.changePassword(this.passwordForm.old_password, this.passwordForm.new_password);
        common_vendor.index.showToast({
          title: "密码修改成功",
          icon: "success"
        });
        this.passwordForm = {
          old_password: "",
          new_password: "",
          confirm_password: ""
        };
      } catch (error) {
        common_vendor.index.showToast({
          title: error.message || "密码修改失败",
          icon: "none"
        });
      } finally {
        this.changingPassword = false;
      }
    },
    goToReservations() {
      common_vendor.index.navigateTo({
        url: "/pages/reservations/reservations"
      });
    },
    goToAttendance() {
      common_vendor.index.navigateTo({
        url: "/pages/attendance/attendance"
      });
    },
    handleLogout() {
      common_vendor.index.showModal({
        title: "确认退出",
        content: "确定要退出登录吗？",
        success: (res) => {
          if (res.confirm) {
            store_user.userStore.logout();
            common_vendor.index.reLaunch({
              url: "/pages/login/login"
            });
          }
        }
      });
    },
    formatDate: utils_date.formatDate,
    isExpired: utils_date.isExpired
  }
};
function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
  var _a, _b, _c, _d, _e, _f;
  return common_vendor.e({
    a: $data.avatarUrl
  }, $data.avatarUrl ? {
    b: $data.avatarUrl
  } : {
    c: common_vendor.t(((_b = (_a = $data.userInfo) == null ? void 0 : _a.username) == null ? void 0 : _b.charAt(0)) || "U")
  }, {
    d: common_vendor.o((...args) => $options.changeAvatar && $options.changeAvatar(...args)),
    e: common_vendor.t(((_c = $data.userInfo) == null ? void 0 : _c.username) || "未登录"),
    f: common_vendor.t($options.getRoleText((_d = $data.userInfo) == null ? void 0 : _d.role)),
    g: (_e = $data.userInfo) == null ? void 0 : _e.username,
    h: $data.profileForm.email,
    i: common_vendor.o(($event) => $data.profileForm.email = $event.detail.value),
    j: $data.profileForm.phone_number,
    k: common_vendor.o(($event) => $data.profileForm.phone_number = $event.detail.value),
    l: $options.formatDate((_f = $data.userInfo) == null ? void 0 : _f.registration_date),
    m: common_vendor.t($options.membershipStatus.text),
    n: common_vendor.n($options.membershipStatus.type),
    o: $data.saving,
    p: common_vendor.o((...args) => $options.handleSaveProfile && $options.handleSaveProfile(...args)),
    q: $data.passwordForm.old_password,
    r: common_vendor.o(($event) => $data.passwordForm.old_password = $event.detail.value),
    s: $data.passwordForm.new_password,
    t: common_vendor.o(($event) => $data.passwordForm.new_password = $event.detail.value),
    v: $data.passwordForm.confirm_password,
    w: common_vendor.o(($event) => $data.passwordForm.confirm_password = $event.detail.value),
    x: $data.changingPassword,
    y: common_vendor.o((...args) => $options.handleChangePassword && $options.handleChangePassword(...args)),
    z: common_vendor.o((...args) => $options.goToReservations && $options.goToReservations(...args)),
    A: common_vendor.o((...args) => $options.goToAttendance && $options.goToAttendance(...args)),
    B: common_vendor.o((...args) => $options.handleLogout && $options.handleLogout(...args))
  });
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render], ["__scopeId", "data-v-dd383ca2"]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/profile/profile.js.map
