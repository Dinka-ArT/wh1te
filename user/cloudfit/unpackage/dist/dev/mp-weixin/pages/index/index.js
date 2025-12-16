"use strict";
const common_vendor = require("../../common/vendor.js");
const store_user = require("../../store/user.js");
const api_membership = require("../../api/membership.js");
const api_course = require("../../api/course.js");
const api_reservation = require("../../api/reservation.js");
const utils_date = require("../../utils/date.js");
const _sfc_main = {
  data() {
    return {
      userInfo: null,
      membership: null,
      todayCourses: [],
      reservations: [],
      currentDate: ""
    };
  },
  computed: {
    reservationStats() {
      return {
        total: this.reservations.length,
        pending: this.reservations.filter((r) => r.status === "pending" || r.status === "confirmed").length,
        completed: this.reservations.filter((r) => r.status === "completed").length
      };
    }
  },
  onLoad() {
    if (!store_user.userStore.getters.isLoggedIn()) {
      common_vendor.index.reLaunch({
        url: "/pages/login/login"
      });
      return;
    }
    this.initDate();
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
    if (!this.userInfo && store_user.userStore.getters.isLoggedIn()) {
      store_user.userStore.fetchUserInfo().then(() => {
        this.userInfo = store_user.userStore.state.userInfo;
      }).catch((err) => {
        common_vendor.index.__f__("error", "at pages/index/index.vue:150", "获取用户信息失败:", err);
      });
    }
    this.loadData();
  },
  methods: {
    initDate() {
      const now = /* @__PURE__ */ new Date();
      const weekdays = ["星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"];
      const year = now.getFullYear();
      const month = String(now.getMonth() + 1).padStart(2, "0");
      const day = String(now.getDate()).padStart(2, "0");
      const weekday = weekdays[now.getDay()];
      this.currentDate = `${year}年${month}月${day}日 ${weekday}`;
    },
    async loadData() {
      if (store_user.userStore.getters.isLoggedIn()) {
        try {
          if (!store_user.userStore.state.userInfo) {
            await store_user.userStore.fetchUserInfo();
          }
          this.userInfo = store_user.userStore.state.userInfo;
        } catch (error) {
          common_vendor.index.__f__("error", "at pages/index/index.vue:176", "获取用户信息失败:", error);
          this.userInfo = store_user.userStore.state.userInfo;
        }
      }
      try {
        const membershipData = await api_membership.getMembership();
        this.membership = membershipData;
      } catch (error) {
        common_vendor.index.__f__("error", "at pages/index/index.vue:187", "获取会员卡信息失败:", error);
      }
      try {
        const today = (/* @__PURE__ */ new Date()).toISOString().split("T")[0];
        const coursesData = await api_course.getCourses({ date: today });
        const list = coursesData.courses || coursesData.list || coursesData || [];
        const todayStr = today;
        this.todayCourses = list.filter((course) => {
          const courseDate = new Date(course.schedule).toISOString().split("T")[0];
          return courseDate === todayStr;
        }).slice(0, 3);
      } catch (error) {
        common_vendor.index.__f__("error", "at pages/index/index.vue:201", "获取今日课程失败:", error);
      }
      try {
        const reservationsData = await api_reservation.getReservations();
        this.reservations = Array.isArray(reservationsData) ? reservationsData : reservationsData.reservations || [];
      } catch (error) {
        common_vendor.index.__f__("error", "at pages/index/index.vue:209", "获取预约列表失败:", error);
      }
    },
    goToCourses() {
      common_vendor.index.switchTab({
        url: "/pages/courses/courses"
      });
    },
    goToLockers() {
      common_vendor.index.navigateTo({
        url: "/pages/lockers/lockers"
      });
    },
    goToProfile() {
      common_vendor.index.switchTab({
        url: "/pages/profile/profile"
      });
    },
    formatDate: utils_date.formatDate,
    isExpired: utils_date.isExpired
  }
};
function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
  var _a;
  return common_vendor.e({
    a: common_vendor.t(((_a = $data.userInfo) == null ? void 0 : _a.username) || "会员"),
    b: common_vendor.t($data.currentDate),
    c: $data.membership
  }, $data.membership ? {
    d: common_vendor.t($data.membership.membership_type || "标准会员"),
    e: common_vendor.t($options.formatDate($data.membership.expiry_date, "YYYY-MM-DD")),
    f: common_vendor.t($options.isExpired($data.membership.expiry_date) ? "已过期" : "有效"),
    g: common_vendor.n($options.isExpired($data.membership.expiry_date) ? "expired" : "valid")
  } : {}, {
    h: $data.todayCourses.length > 0
  }, $data.todayCourses.length > 0 ? {
    i: common_vendor.f($data.todayCourses, (course, k0, i0) => {
      return {
        a: common_vendor.t(course.course_name),
        b: common_vendor.t($options.formatDate(course.schedule, "HH:mm")),
        c: course.course_id,
        d: common_vendor.o((...args) => $options.goToCourses && $options.goToCourses(...args), course.course_id)
      };
    })
  } : {}, {
    j: common_vendor.t($options.reservationStats.total),
    k: common_vendor.t($options.reservationStats.pending),
    l: common_vendor.t($options.reservationStats.completed),
    m: common_vendor.o((...args) => $options.goToCourses && $options.goToCourses(...args)),
    n: common_vendor.o((...args) => $options.goToLockers && $options.goToLockers(...args)),
    o: common_vendor.o((...args) => $options.goToProfile && $options.goToProfile(...args))
  });
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render], ["__scopeId", "data-v-1cf27b2a"]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/index/index.js.map
