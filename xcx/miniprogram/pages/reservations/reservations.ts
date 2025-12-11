// pages/reservations/reservations.ts
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
      const data = await request<{ reservations: any[] }>({
        url: "/reservations",
      });
      this.setData({ list: data?.reservations || [] });
    } catch (err: any) {
      console.error(err);
      wx.showToast({ title: err?.message || "加载失败", icon: "none" });
    } finally {
      this.setData({ loading: false });
    }
  },

  async cancel(e: any) {
    const id = e.currentTarget.dataset.id;
    if (!id) return;
    wx.showLoading({ title: "取消中", mask: true });
    try {
      await request({
        url: `/reservations/${id}`,
        method: "DELETE",
      });
      wx.showToast({ title: "已取消", icon: "none" });
      this.fetchList();
    } catch (err: any) {
      console.error(err);
      wx.showToast({ title: err?.message || "取消失败", icon: "none" });
    } finally {
      wx.hideLoading();
    }
  },
});

