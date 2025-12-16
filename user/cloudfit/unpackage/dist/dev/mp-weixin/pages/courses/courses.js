"use strict";
const common_vendor = require("../../common/vendor.js");
const store_user = require("../../store/user.js");
const api_course = require("../../api/course.js");
const api_reservation = require("../../api/reservation.js");
const utils_date = require("../../utils/date.js");
const _sfc_main = {
  data() {
    return {
      courses: [],
      reservations: [],
      loading: false,
      detailVisible: false,
      selectedCourse: null
    };
  },
  onLoad() {
    if (!store_user.userStore.getters.isLoggedIn()) {
      common_vendor.index.reLaunch({
        url: "/pages/login/login"
      });
      return;
    }
    this.loadReservations();
    this.loadCourses();
  },
  onShow() {
    if (!store_user.userStore.getters.isLoggedIn()) {
      common_vendor.index.reLaunch({
        url: "/pages/login/login"
      });
      return;
    }
    this.loadReservations();
  },
  methods: {
    async loadReservations() {
      try {
        const data = await api_reservation.getReservations();
        this.reservations = Array.isArray(data) ? data : data.reservations || [];
      } catch (error) {
        common_vendor.index.__f__("error", "at pages/courses/courses.vue:143", "获取预约列表失败:", error);
      }
    },
    async loadCourses() {
      this.loading = true;
      try {
        const data = await api_course.getCourses();
        const list = data.courses || data.list || data || [];
        const now = /* @__PURE__ */ new Date();
        const allCourses = list.map((item) => ({
          ...item,
          course_id: item.course_id ?? item.courseId,
          course_name: item.course_name ?? item.courseName,
          instructor_name: item.instructor_name ?? item.instructorName,
          reserved_count: item.reserved_count ?? item.reservedCount
        }));
        this.courses = allCourses.filter((course) => {
          const courseTime = new Date(course.schedule);
          return courseTime > now;
        });
      } catch (error) {
        common_vendor.index.showToast({
          title: "获取课程列表失败",
          icon: "none"
        });
      } finally {
        this.loading = false;
      }
    },
    canReserve(course) {
      if (!course)
        return false;
      const now = /* @__PURE__ */ new Date();
      const courseTime = new Date(course.schedule);
      if (courseTime < now)
        return false;
      if (this.isReserved(course))
        return false;
      if (course.reserved_count >= (course.capacity || 20))
        return false;
      return true;
    },
    isReserved(course) {
      if (!course)
        return false;
      return this.reservations.some(
        (r) => r.course_id === course.course_id && (r.status === "pending" || r.status === "confirmed")
      );
    },
    getReservationForCourse(course) {
      if (!course)
        return null;
      return this.reservations.find(
        (r) => r.course_id === course.course_id && (r.status === "pending" || r.status === "confirmed")
      );
    },
    getCourseStatus(course) {
      if (!course)
        return "未知";
      const now = /* @__PURE__ */ new Date();
      const courseTime = new Date(course.schedule);
      if (courseTime < now)
        return "已结束";
      const diffHours = (courseTime - now) / (1e3 * 60 * 60);
      if (diffHours < 1)
        return "即将开始";
      return "未开始";
    },
    getCourseStatusType(course) {
      const status = this.getCourseStatus(course);
      if (status === "已结束")
        return "info";
      if (status === "即将开始")
        return "warning";
      return "success";
    },
    showCourseDetail(course) {
      this.selectedCourse = course;
      this.detailVisible = true;
    },
    closeDetail() {
      this.detailVisible = false;
      this.selectedCourse = null;
    },
    async handleReserve(course) {
      const existing = this.getReservationForCourse(course);
      if (existing) {
        common_vendor.index.showModal({
          title: "取消预约",
          content: `确定取消课程 "${course.course_name}" 的预约吗？`,
          success: async (res) => {
            if (res.confirm) {
              try {
                await api_reservation.cancelReservation(existing.reservation_id);
                common_vendor.index.showToast({
                  title: "已取消预约",
                  icon: "success"
                });
                await this.loadReservations();
                await this.loadCourses();
                this.closeDetail();
              } catch (error) {
                common_vendor.index.showToast({
                  title: error.message || "取消失败",
                  icon: "none"
                });
              }
            }
          }
        });
        return;
      }
      if (!this.canReserve(course)) {
        common_vendor.index.showToast({
          title: "无法预约此课程",
          icon: "none"
        });
        return;
      }
      common_vendor.index.showModal({
        title: "确认预约",
        content: `确定要预约课程"${course.course_name}"吗？`,
        success: async (res) => {
          if (res.confirm) {
            try {
              await api_course.reserveCourse(course.course_id);
              common_vendor.index.showToast({
                title: "预约成功",
                icon: "success"
              });
              await this.loadReservations();
              await this.loadCourses();
              this.closeDetail();
            } catch (error) {
              common_vendor.index.showToast({
                title: error.message || "预约失败",
                icon: "none"
              });
            }
          }
        }
      });
    },
    formatDate: utils_date.formatDate
  }
};
function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
  var _a;
  return common_vendor.e({
    a: $data.loading
  }, $data.loading ? {} : $data.courses.length === 0 ? {} : {
    c: common_vendor.f($data.courses, (course, k0, i0) => {
      return {
        a: common_vendor.t(course.course_name),
        b: common_vendor.t($options.getCourseStatus(course)),
        c: common_vendor.n($options.getCourseStatusType(course)),
        d: common_vendor.t($options.formatDate(course.schedule, "YYYY-MM-DD HH:mm")),
        e: common_vendor.t(course.instructor_name || "待定"),
        f: common_vendor.t(course.reserved_count || 0),
        g: common_vendor.t(course.capacity || 20),
        h: common_vendor.t($options.isReserved(course) ? "取消预约" : "立即预约"),
        i: !$options.canReserve(course) && !$options.isReserved(course) ? 1 : "",
        j: common_vendor.o(($event) => $options.handleReserve(course), course.course_id),
        k: course.course_id,
        l: common_vendor.o(($event) => $options.showCourseDetail(course), course.course_id)
      };
    })
  }, {
    b: $data.courses.length === 0,
    d: $data.detailVisible
  }, $data.detailVisible ? common_vendor.e({
    e: common_vendor.t((_a = $data.selectedCourse) == null ? void 0 : _a.course_name),
    f: common_vendor.o((...args) => $options.closeDetail && $options.closeDetail(...args)),
    g: $data.selectedCourse
  }, $data.selectedCourse ? common_vendor.e({
    h: common_vendor.t($options.formatDate($data.selectedCourse.schedule, "YYYY-MM-DD HH:mm")),
    i: common_vendor.t($options.getCourseStatus($data.selectedCourse)),
    j: common_vendor.n($options.getCourseStatusType($data.selectedCourse)),
    k: common_vendor.t($data.selectedCourse.instructor_name || "待定"),
    l: common_vendor.t($data.selectedCourse.reserved_count || 0),
    m: common_vendor.t($data.selectedCourse.capacity || 20),
    n: $data.selectedCourse.description
  }, $data.selectedCourse.description ? {
    o: common_vendor.t($data.selectedCourse.description)
  } : {}) : {}, {
    p: common_vendor.o((...args) => $options.closeDetail && $options.closeDetail(...args)),
    q: common_vendor.t($options.isReserved($data.selectedCourse) ? "已预约" : "立即预约"),
    r: !$options.canReserve($data.selectedCourse) ? 1 : "",
    s: common_vendor.o(($event) => $options.handleReserve($data.selectedCourse)),
    t: common_vendor.o(() => {
    }),
    v: common_vendor.o((...args) => $options.closeDetail && $options.closeDetail(...args))
  }) : {});
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render], ["__scopeId", "data-v-5043c84e"]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/courses/courses.js.map
