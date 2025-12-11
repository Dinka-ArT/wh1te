// pages/coach/home.ts
import { getUserInfo, isLoggedIn } from "../../utils/request";

Page({
  onShow() {
    if (!isLoggedIn()) {
      wx.reLaunch({ url: "/pages/login/login" });
      return;
    }
    const info = getUserInfo();
    if (!info || info.role !== "coach") {
      wx.showToast({ title: "仅教练可访问", icon: "none" });
      wx.reLaunch({ url: "/pages/home/home" });
    }
  },

  goCourses() {
    wx.navigateTo({ url: "/pages/coach/courses" });
  },

  goReservations() {
    wx.navigateTo({ url: "/pages/coach/reservations" });
  },
});

