// pages/login/login.ts
import { request, setToken, setUserInfo } from "../../utils/request";

Page({
  data: {
    hasUserInfo: false,
    loading: false,
  },

  onLoad() {
    const token = wx.getStorageSync("token");
    if (token) {
      // 已登录则直接跳转
      wx.reLaunch({ url: "/pages/home/home" });
    }
  },

  login() {
    if (this.data.loading) return;
    this.setData({ loading: true });

    wx.login({
      success: async (res) => {
        if (!res.code) {
          wx.showToast({ title: "获取登录凭证失败", icon: "none" });
          this.setData({ loading: false });
          return;
        }
        try {
          const data = await request<{ token: string; user_id?: number; role?: string; username?: string }>({
            url: "/auth/wx-login",
            method: "POST",
            data: { code: res.code },
          });
          setToken(data.token);
          setUserInfo({
            user_id: data.user_id,
            role: data.role,
            username: data.username,
          });
          wx.showToast({ title: "登录成功", icon: "success" });
          wx.reLaunch({ url: "/pages/home/home" });
        } catch (err: any) {
          console.error(err);
          wx.showToast({
            title: err?.message || "登录失败",
            icon: "none",
          });
        } finally {
          this.setData({ loading: false });
        }
      },
      fail: (err) => {
        console.error(err);
        wx.showToast({ title: "微信登录失败", icon: "none" });
        this.setData({ loading: false });
      },
    });
  },
});

