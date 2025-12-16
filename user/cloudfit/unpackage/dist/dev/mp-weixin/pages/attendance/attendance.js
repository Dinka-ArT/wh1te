"use strict";
const common_vendor = require("../../common/vendor.js");
const api_attendance = require("../../api/attendance.js");
const utils_date = require("../../utils/date.js");
const _sfc_main = {
  data() {
    return {
      attendanceList: [],
      loading: false
    };
  },
  onLoad() {
    this.loadAttendance();
  },
  methods: {
    async loadAttendance() {
      this.loading = true;
      try {
        const data = await api_attendance.getAttendance({ limit: 50 });
        this.attendanceList = Array.isArray(data) ? data : data.attendance || [];
      } catch (error) {
        common_vendor.index.showToast({
          title: "获取签到记录失败",
          icon: "none"
        });
      } finally {
        this.loading = false;
      }
    },
    formatDate: utils_date.formatDate
  }
};
function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
  return common_vendor.e({
    a: $data.loading
  }, $data.loading ? {} : $data.attendanceList.length === 0 ? {} : {
    c: common_vendor.f($data.attendanceList, (item, k0, i0) => {
      return common_vendor.e({
        a: common_vendor.t(item.course_name || "自由训练"),
        b: common_vendor.t(item.is_on_time ? "准时" : "迟到"),
        c: common_vendor.n(item.is_on_time ? "success" : "warning"),
        d: common_vendor.t($options.formatDate(item.check_in_time)),
        e: item.check_out_time
      }, item.check_out_time ? {
        f: common_vendor.t($options.formatDate(item.check_out_time))
      } : {}, {
        g: item.attendance_id
      });
    })
  }, {
    b: $data.attendanceList.length === 0
  });
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render], ["__scopeId", "data-v-a1b796e8"]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/attendance/attendance.js.map
