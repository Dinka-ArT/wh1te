"use strict";
const utils_request = require("../utils/request.js");
const getLockers = () => {
  return utils_request.request({
    url: "/lockers",
    method: "GET"
  });
};
exports.getLockers = getLockers;
//# sourceMappingURL=../../.sourcemap/mp-weixin/api/locker.js.map
