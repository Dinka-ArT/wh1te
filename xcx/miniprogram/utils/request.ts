const BASE_URL = "http://localhost:8080/api"; // 如真机需访问本机，请改为局域网IP
const TOKEN_KEY = "token";
const USER_INFO_KEY = "user_info";

export function getToken() {
  return wx.getStorageSync(TOKEN_KEY) || "";
}

export function setToken(token: string) {
  wx.setStorageSync(TOKEN_KEY, token || "");
}

export function clearToken() {
  wx.removeStorageSync(TOKEN_KEY);
}

export function isLoggedIn() {
  return !!getToken();
}

export function setUserInfo(info: any) {
  wx.setStorageSync(USER_INFO_KEY, info || {});
}

export function getUserInfo() {
  return wx.getStorageSync(USER_INFO_KEY) || null;
}

export function clearUserInfo() {
  wx.removeStorageSync(USER_INFO_KEY);
}

interface RequestOptions<T> {
  url: string;
  method?: "GET" | "POST" | "PUT" | "DELETE";
  data?: any;
  header?: Record<string, string>;
}

interface ApiResult<T> {
  code: number;
  message: string;
  data: T;
}

export function request<T>(options: RequestOptions<T>): Promise<T> {
  const { url, method = "GET", data = {}, header = {} } = options;
  const token = getToken();

  return new Promise((resolve, reject) => {
    wx.request<ApiResult<T>>({
      url: `${BASE_URL}${url}`,
      method,
      data,
      header: {
        "Content-Type": "application/json",
        ...(token ? { Authorization: `Bearer ${token}` } : {}),
        ...header,
      },
      success(res) {
        if (res.statusCode !== 200) {
          reject(new Error(`HTTP ${res.statusCode}`));
          return;
        }
        const body = res.data;
        if (body?.code === 200) {
          resolve(body.data);
        } else {
          reject(new Error(body?.message || "请求失败"));
        }
      },
      fail(err) {
        reject(err);
      },
    });
  });
}

