"use strict";
const utils_request = require("../utils/request.js");
const updateProfile = (data) => {
  return utils_request.request({
    url: "/user/profile",
    method: "PUT",
    data
  });
};
const changePassword = (oldPassword, newPassword) => {
  return utils_request.request({
    url: "/user/password",
    method: "PUT",
    data: {
      old_password: oldPassword,
      new_password: newPassword
    }
  });
};
exports.changePassword = changePassword;
exports.updateProfile = updateProfile;
//# sourceMappingURL=../../.sourcemap/mp-weixin/api/user.js.map
