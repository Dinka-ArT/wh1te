// pages/coach/reservations.ts
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
    this.fetchReservations(info.user_id);
  },

  async fetchReservations(userId?: number) {
    if (!userId) {
      wx.showToast({ title: "未获取到教练ID", icon: "none" });
      return;
    }
    this.setData({ loading: true });
    try {
      // 先拉取课程，筛出教练自己的课
      const courseData = await request<{ courses: any[] }>({ url: "/courses" });
      const courseList = courseData && Array.isArray((courseData as any).courses)
        ? (courseData as any).courses
        : [];
      const myCourses = courseList.filter((c) => Number(c.instructor_id) === Number(userId));
      const courseIds = myCourses.map((c) => c.course_id);

      let all: any[] = [];
      // 简化处理：逐个课程查询预约（使用 admin 接口过滤 course_id）
      for (const cid of courseIds) {
        try {
          const res = await request<{ reservations: any[] }>({
            url: `/admin/reservations?course_id=${cid}&page=1&page_size=200`,
          });
          all = all.concat(res?.reservations || []);
        } catch (innerErr) {
          console.warn("查询课程预约失败", cid, innerErr);
        }
      }
      this.setData({ list: all });
    } catch (err: any) {
      console.error(err);
      wx.showToast({ title: err?.message || "加载失败", icon: "none" });
    } finally {
      this.setData({ loading: false });
    }
  },
});

