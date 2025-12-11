// pages/coach/courses.ts
import { getUserInfo, isLoggedIn, request } from "../../utils/request";

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
    const info = getUserInfo();
    if (!info || info.role !== "coach") {
      wx.showToast({ title: "仅教练可访问", icon: "none" });
      wx.reLaunch({ url: "/pages/home/home" });
      return;
    }
    this.fetchList(info.user_id);
  },

  async fetchList(userId?: number) {
    this.setData({ loading: true });
    try {
      const data = await request<{ courses: any[] }>({ url: "/courses" });
      const courses = data && Array.isArray((data as any).courses) ? (data as any).courses : [];
      const list = courses.filter((c) => {
        return userId ? Number(c.instructor_id) === Number(userId) : false;
      });
      this.setData({ list });
    } catch (err: any) {
      console.error(err);
      const msg = (err && err.message) ? err.message : "加载失败";
      wx.showToast({ title: msg, icon: "none" });
    } finally {
      this.setData({ loading: false });
    }
  },
});

