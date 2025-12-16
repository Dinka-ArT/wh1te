"use strict";
const utils_request = require("../utils/request.js");
const getAttendance = (params) => {
  return utils_request.request({
    url: "/attendance",
    method: "GET",
    data: params || {}
  });
};
exports.getAttendance = getAttendance;
//# sourceMappingURL=../../.sourcemap/mp-weixin/api/attendance.js.map
