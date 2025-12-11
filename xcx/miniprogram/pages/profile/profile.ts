// pages/profile/profile.ts
import { clearToken, getToken, isLoggedIn } from "../../utils/request";

Page({
  data: {
    token: "",
  },

  onShow() {
    if (!isLoggedIn()) {
      wx.reLaunch({ url: "/pages/login/login" });
      return;
    }
    this.setData({ token: getToken() });
  },

  logout() {
    clearToken();
    wx.showToast({ title: "已退出", icon: "none" });
    wx.reLaunch({ url: "/pages/login/login" });
  },
});

