"use strict";
const common_vendor = require("../../common/vendor.js");
const api_locker = require("../../api/locker.js");
const utils_date = require("../../utils/date.js");
const _sfc_main = {
  data() {
    return {
      lockers: [],
      loading: false
    };
  },
  onLoad() {
    this.loadLockers();
  },
  methods: {
    async loadLockers() {
      this.loading = true;
      try {
        const data = await api_locker.getLockers();
        this.lockers = Array.isArray(data) ? data : data.lockers || [];
      } catch (error) {
        common_vendor.index.showToast({
          title: "获取储物柜信息失败",
          icon: "none"
        });
      } finally {
        this.loading = false;
      }
    },
    getStatus(locker) {
      if (!locker.end_date)
        return "使用中";
      if (utils_date.isExpired(locker.end_date))
        return "已到期";
      return "正常";
    },
    getStatusType(locker) {
      const status = this.getStatus(locker);
      if (status === "已到期")
        return "danger";
      if (status === "正常")
        return "success";
      return "info";
    },
    isExpiringSoon(locker) {
      if (!locker.end_date)
        return false;
      if (utils_date.isExpired(locker.end_date))
        return false;
      const days = Math.floor((new Date(locker.end_date) - /* @__PURE__ */ new Date()) / (1e3 * 60 * 60 * 24));
      return days <= 7 && days > 0;
    },
    getRemainingTime(locker) {
      if (!locker.end_date)
        return "-";
      if (utils_date.isExpired(locker.end_date))
        return "已过期";
      return utils_date.getTimeRemaining(locker.end_date);
    },
    formatDate: utils_date.formatDate,
    isExpired: utils_date.isExpired
  }
};
function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
  return common_vendor.e({
    a: $data.loading
  }, $data.loading ? {} : $data.lockers.length === 0 ? {} : {
    c: common_vendor.f($data.lockers, (locker, k0, i0) => {
      return common_vendor.e({
        a: common_vendor.t(locker.locker_number),
        b: common_vendor.t($options.formatDate(locker.start_date, "YYYY-MM-DD")),
        c: common_vendor.t($options.formatDate(locker.end_date, "YYYY-MM-DD")),
        d: common_vendor.t($options.getStatus(locker)),
        e: common_vendor.n($options.getStatusType(locker)),
        f: locker.end_date
      }, locker.end_date ? {
        g: common_vendor.t($options.getRemainingTime(locker)),
        h: $options.isExpiringSoon(locker) ? 1 : ""
      } : {}, {
        i: locker.locker_id
      });
    })
  }, {
    b: $data.lockers.length === 0
  });
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render], ["__scopeId", "data-v-5006335d"]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/lockers/lockers.js.map
