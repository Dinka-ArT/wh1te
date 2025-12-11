// pages/home/home.ts
import { isLoggedIn } from "../../utils/request";

Page({
  data: {},

  onShow() {
    // 未登录跳回登录页
    if (!isLoggedIn()) {
      wx.reLaunch({ url: "/pages/login/login" });
      return;
    }
    // 此处可按需拉取概览数据
  },

  goCourses() {
    wx.navigateTo({ url: "/pages/courses/courses" });
  },
  goReservations() {
    wx.navigateTo({ url: "/pages/reservations/reservations" });
  },
  goProfile() {
    wx.navigateTo({ url: "/pages/profile/profile" });
  },
});

