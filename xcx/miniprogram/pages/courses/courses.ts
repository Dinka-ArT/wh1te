// pages/courses/courses.ts
import { isLoggedIn, request } from "../../utils/request";

Page({
  data: {
    loading: false,
    list: [] as any[],
  },

  onShow() {
    if (!isLoggedIn()) {
      wx.reLaunch({ url: "/pages/login/login" });
      return;
    }
    this.fetchList();
  },

  async fetchList() {
    this.setData({ loading: true });
    try {
      const data = await request<{ courses: any[] }>({ url: "/courses" });
      this.setData({ list: data?.courses || [] });
    } catch (err: any) {
      console.error(err);
      wx.showToast({ title: err?.message || "加载失败", icon: "none" });
    } finally {
      this.setData({ loading: false });
    }
  },

  async reserve(e: any) {
    const courseId = e.currentTarget.dataset.id;
    if (!courseId) return;
    wx.showLoading({ title: "预约中", mask: true });
    try {
      await request({
        url: "/reservations",
        method: "POST",
        data: { course_id: courseId },
      });
      wx.showToast({ title: "预约成功", icon: "success" });
      this.fetchList();
    } catch (err: any) {
      console.error(err);
      wx.showToast({ title: err?.message || "预约失败", icon: "none" });
    } finally {
      wx.hideLoading();
    }
  },
});

