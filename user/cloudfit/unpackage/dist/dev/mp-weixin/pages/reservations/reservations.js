"use strict";
const common_vendor = require("../../common/vendor.js");
const api_reservation = require("../../api/reservation.js");
const utils_date = require("../../utils/date.js");
const _sfc_main = {
  data() {
    return {
      reservations: [],
      loading: false,
      detailVisible: false,
      selectedReservation: null
    };
  },
  onLoad() {
    this.loadReservations();
  },
  onShow() {
    this.loadReservations();
  },
  methods: {
    async loadReservations() {
      this.loading = true;
      try {
        const data = await api_reservation.getReservations();
        this.reservations = Array.isArray(data) ? data : data.reservations || [];
      } catch (error) {
        common_vendor.index.showToast({
          title: "获取预约列表失败",
          icon: "none"
        });
      } finally {
        this.loading = false;
      }
    },
    getStatusText(status) {
      const statusMap = {
        pending: "待开始",
        confirmed: "已确认",
        completed: "已完成",
        cancelled: "已取消"
      };
      return statusMap[status] || status;
    },
    getStatusType(status) {
      const typeMap = {
        pending: "warning",
        confirmed: "success",
        completed: "info",
        cancelled: "info"
      };
      return typeMap[status] || "info";
    },
    canCancel(reservation) {
      if (!reservation)
        return false;
      if (reservation.status === "cancelled" || reservation.status === "completed") {
        return false;
      }
      const courseTime = new Date(reservation.schedule);
      const now = /* @__PURE__ */ new Date();
      return courseTime > now;
    },
    showDetail(reservation) {
      this.selectedReservation = reservation;
      this.detailVisible = true;
    },
    closeDetail() {
      this.detailVisible = false;
      this.selectedReservation = null;
    },
    async handleCancel(reservation) {
      if (!this.canCancel(reservation)) {
        common_vendor.index.showToast({
          title: "无法取消此预约",
          icon: "none"
        });
        return;
      }
      common_vendor.index.showModal({
        title: "确认取消",
        content: `确定要取消预约"${reservation.course_name}"吗？`,
        success: async (res) => {
          if (res.confirm) {
            try {
              await api_reservation.cancelReservation(reservation.reservation_id);
              common_vendor.index.showToast({
                title: "取消成功",
                icon: "success"
              });
              await this.loadReservations();
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
    },
    formatDate: utils_date.formatDate
  }
};
function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
  var _a;
  return common_vendor.e({
    a: $data.loading
  }, $data.loading ? {} : $data.reservations.length === 0 ? {} : {
    c: common_vendor.f($data.reservations, (reservation, k0, i0) => {
      return common_vendor.e({
        a: common_vendor.t(reservation.course_name),
        b: common_vendor.t($options.getStatusText(reservation.status)),
        c: common_vendor.n($options.getStatusType(reservation.status)),
        d: common_vendor.t($options.formatDate(reservation.reservation_date)),
        e: common_vendor.t($options.formatDate(reservation.schedule)),
        f: common_vendor.t(reservation.instructor_name || "待定"),
        g: $options.canCancel(reservation)
      }, $options.canCancel(reservation) ? {
        h: common_vendor.o(($event) => $options.handleCancel(reservation), reservation.reservation_id)
      } : {}, {
        i: reservation.reservation_id,
        j: common_vendor.o(($event) => $options.showDetail(reservation), reservation.reservation_id)
      });
    })
  }, {
    b: $data.reservations.length === 0,
    d: $data.detailVisible
  }, $data.detailVisible ? common_vendor.e({
    e: common_vendor.t((_a = $data.selectedReservation) == null ? void 0 : _a.course_name),
    f: common_vendor.o((...args) => $options.closeDetail && $options.closeDetail(...args)),
    g: $data.selectedReservation
  }, $data.selectedReservation ? common_vendor.e({
    h: common_vendor.t($data.selectedReservation.course_name),
    i: common_vendor.t($options.getStatusText($data.selectedReservation.status)),
    j: common_vendor.n($options.getStatusType($data.selectedReservation.status)),
    k: common_vendor.t($options.formatDate($data.selectedReservation.reservation_date)),
    l: common_vendor.t($options.formatDate($data.selectedReservation.schedule)),
    m: common_vendor.t($data.selectedReservation.instructor_name || "待定"),
    n: common_vendor.t($data.selectedReservation.attendance ? "已签到" : "未签到"),
    o: common_vendor.n($data.selectedReservation.attendance ? "success" : "info"),
    p: $data.selectedReservation.attendance
  }, $data.selectedReservation.attendance ? {
    q: common_vendor.t($options.formatDate($data.selectedReservation.attendance.check_in_time)),
    r: common_vendor.t($data.selectedReservation.attendance.is_on_time ? "准时" : "迟到"),
    s: common_vendor.n($data.selectedReservation.attendance.is_on_time ? "success" : "warning")
  } : {}) : {}, {
    t: common_vendor.o((...args) => $options.closeDetail && $options.closeDetail(...args)),
    v: $options.canCancel($data.selectedReservation)
  }, $options.canCancel($data.selectedReservation) ? {
    w: common_vendor.o(($event) => $options.handleCancel($data.selectedReservation))
  } : {}, {
    x: common_vendor.o(() => {
    }),
    y: common_vendor.o((...args) => $options.closeDetail && $options.closeDetail(...args))
  }) : {});
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render], ["__scopeId", "data-v-217a793d"]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/reservations/reservations.js.map
