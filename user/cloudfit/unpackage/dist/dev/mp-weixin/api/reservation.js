"use strict";
const utils_request = require("../utils/request.js");
const getReservations = () => {
  return utils_request.request({
    url: "/reservations",
    method: "GET"
  });
};
const cancelReservation = (reservationId) => {
  return utils_request.request({
    url: `/reservations/${reservationId}`,
    method: "DELETE"
  });
};
exports.cancelReservation = cancelReservation;
exports.getReservations = getReservations;
//# sourceMappingURL=../../.sourcemap/mp-weixin/api/reservation.js.map
