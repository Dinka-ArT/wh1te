# 快速启动指南

## 第一步：安装依赖

```bash
npm install
```

## 第二步：启动开发服务器

```bash
npm run dev
```

访问 `http://localhost:3000` 查看应用

## 第三步：配置后端 API

编辑 `vite.config.js`，修改代理配置中的 `target` 为你的后端服务地址：

```javascript
server: {
  proxy: {
    '/api': {
      target: 'http://localhost:8080', // 修改为你的后端地址
      changeOrigin: true,
      rewrite: (path) => path.replace(/^\/api/, '')
    }
  }
}
```

## API 接口说明

项目需要以下后端 API 接口：

### 认证相关
- `POST /api/auth/login` - 用户登录
- `GET /api/user/info` - 获取用户信息

### 课程相关
- `GET /api/courses` - 获取课程列表
- `GET /api/courses/:id` - 获取课程详情
- `POST /api/reservations` - 预约课程

### 预约相关
- `GET /api/reservations` - 获取预约列表
- `DELETE /api/reservations/:id` - 取消预约

### 会员卡相关
- `GET /api/memberships/current` - 获取当前会员卡

### 储物柜相关
- `GET /api/lockers` - 获取储物柜列表

### 签到相关
- `GET /api/attendance` - 获取签到记录

### 用户相关
- `PUT /api/user/profile` - 更新用户资料
- `PUT /api/user/password` - 修改密码

## 注意事项

1. 后端 API 返回格式应统一为：
   ```json
   {
     "code": 200,
     "data": {},
     "message": "success"
   }
   ```

2. 登录成功后，后端应返回 token，前端会存储在 localStorage

3. 所有需要认证的接口，前端会自动在请求头中添加 `Authorization: Bearer {token}`

4. 如果后端接口格式不同，需要修改 `src/api/request.js` 中的响应拦截器

## 常见问题

### 1. 端口被占用
修改 `vite.config.js` 中的 `server.port` 配置

### 2. API 请求失败
- 检查后端服务是否启动
- 检查代理配置是否正确
- 查看浏览器控制台的网络请求

### 3. 样式不生效
确保已安装 `sass` 依赖：
```bash
npm install sass -D
```

