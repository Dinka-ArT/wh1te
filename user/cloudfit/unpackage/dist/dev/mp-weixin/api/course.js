"use strict";
const utils_request = require("../utils/request.js");
const getCourses = (params) => {
  return utils_request.request({
    url: "/courses",
    method: "GET",
    data: params || {}
  });
};
const reserveCourse = (courseId) => {
  return utils_request.request({
    url: "/reservations",
    method: "POST",
    data: { course_id: courseId }
  });
};
exports.getCourses = getCourses;
exports.reserveCourse = reserveCourse;
//# sourceMappingURL=../../.sourcemap/mp-weixin/api/course.js.map
