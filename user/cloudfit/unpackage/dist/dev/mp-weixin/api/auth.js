"use strict";
const utils_request = require("../utils/request.js");
const login = (username, password) => {
  return utils_request.request({
    url: "/auth/login",
    method: "POST",
    data: { username, password }
  });
};
const register = (data) => {
  return utils_request.request({
    url: "/auth/register",
    method: "POST",
    data
  });
};
const checkUsernameExists = async (username) => {
  var _a;
  try {
    const res = await utils_request.request({
      url: "/auth/check-username",
      method: "POST",
      data: { username }
    });
    return res.exists === true || ((_a = res.data) == null ? void 0 : _a.exists) === true;
  } catch (error) {
    return false;
  }
};
const checkPhoneExists = async (phone_number) => {
  var _a;
  try {
    const res = await utils_request.request({
      url: "/auth/check-phone",
      method: "POST",
      data: { phone_number }
    });
    return res.exists === true || ((_a = res.data) == null ? void 0 : _a.exists) === true;
  } catch (error) {
    return false;
  }
};
const getUserInfo = () => {
  return utils_request.request({
    url: "/user/info",
    method: "GET"
  });
};
exports.checkPhoneExists = checkPhoneExists;
exports.checkUsernameExists = checkUsernameExists;
exports.getUserInfo = getUserInfo;
exports.login = login;
exports.register = register;
//# sourceMappingURL=../../.sourcemap/mp-weixin/api/auth.js.map
