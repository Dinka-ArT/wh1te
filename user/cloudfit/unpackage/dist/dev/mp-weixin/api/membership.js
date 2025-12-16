"use strict";
const utils_request = require("../utils/request.js");
const getMembership = () => {
  return utils_request.request({
    url: "/memberships/current",
    method: "GET"
  });
};
exports.getMembership = getMembership;
//# sourceMappingURL=../../.sourcemap/mp-weixin/api/membership.js.map
