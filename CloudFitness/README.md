# 云健健身房会员系统 - 前端

基于 Vue 3 + Pinia + Element Plus 开发的现代化健身房会员管理系统前端。

## 技术栈

- **Vue 3** - 组合式 API
- **Pinia** - 状态管理
- **Element Plus** - UI 组件库
- **Vue Router** - 路由管理
- **Axios** - HTTP 请求
- **Day.js** - 时间处理
- **ECharts** - 数据可视化
- **Vite** - 构建工具

## 功能模块

### 1. 首页（会员仪表盘）
- 欢迎信息与会员基础信息展示
- 会员卡状态卡片
- 今日课程列表
- 预约概览统计
- 训练趋势图表
- 课程类型分布图表
- 快捷功能入口

### 2. 课程中心
- 课程列表展示
- 课程筛选（按日期、教练）
- 课程详情弹窗
- 课程预约功能
- 开课倒计时

### 3. 我的预约
- 预约记录列表
- 预约状态管理
- 预约详情查看
- 取消预约功能
- 签到记录关联

### 4. 签到记录
- 签到明细列表
- 每月出勤率折线图
- 课程类型分布饼图
- 准时/迟到统计

### 5. 储物柜管理
- 储物柜租赁信息展示
- 使用状态显示
- 到期提醒

### 6. 个人中心
- 基本信息编辑
- 密码修改
- 会员状态展示

## 项目结构

```
cloud-fitness-frontend/
├── src/
│   ├── api/              # API 接口
│   │   ├── auth.js      # 认证相关
│   │   ├── course.js    # 课程相关
│   │   ├── reservation.js # 预约相关
│   │   ├── attendance.js  # 签到相关
│   │   ├── locker.js    # 储物柜相关
│   │   ├── membership.js # 会员卡相关
│   │   ├── user.js      # 用户相关
│   │   └── request.js   # Axios 封装
│   ├── assets/          # 静态资源
│   ├── components/      # 公共组件
│   ├── layouts/         # 布局组件
│   │   └── MainLayout.vue
│   ├── router/          # 路由配置
│   │   └── index.js
│   ├── stores/          # Pinia 状态管理
│   │   ├── user.js      # 用户状态
│   │   └── reservation.js # 预约状态
│   ├── styles/          # 全局样式
│   │   └── main.scss
│   ├── utils/           # 工具函数
│   │   ├── date.js      # 日期处理
│   │   └── echarts.js   # ECharts 配置
│   ├── views/           # 页面组件
│   │   ├── Login.vue    # 登录页
│   │   ├── Home.vue     # 首页
│   │   ├── Courses.vue  # 课程中心
│   │   ├── Reservations.vue # 我的预约
│   │   ├── Attendance.vue  # 签到记录
│   │   ├── Lockers.vue  # 储物柜
│   │   └── Profile.vue  # 个人中心
│   ├── App.vue          # 根组件
│   └── main.js          # 入口文件
├── index.html
├── package.json
├── vite.config.js
└── README.md
```

## 安装与运行

### 环境要求

- Node.js >= 16.0.0
- npm >= 7.0.0 或 yarn >= 1.22.0

### 安装依赖

```bash
npm install
# 或
yarn install
```

### 开发模式

```bash
npm run dev
# 或
yarn dev
```

开发服务器将在 `http://localhost:3000` 启动

### 构建生产版本

```bash
npm run build
# 或
yarn build
```

构建产物将输出到 `dist` 目录

### 预览生产构建

```bash
npm run preview
# 或
yarn preview
```

## API 配置

项目使用代理配置连接后端 API，默认配置在 `vite.config.js` 中：

```javascript
server: {
  proxy: {
    '/api': {
      target: 'http://localhost:8080',
      changeOrigin: true,
      rewrite: (path) => path.replace(/^\/api/, '')
    }
  }
}
```

请根据实际后端服务地址修改 `target` 配置。

## 设计规范

### 视觉风格
- 极简现代风格
- 白色背景 + 黑色按钮高对比度
- 柔和渐变点缀
- 精致圆角与多层次阴影
- 流畅微交互

### 交互规范
- 卡片 hover 提升效果
- 列表加载骨架屏
- 简洁的过渡动画
- 清晰的状态反馈

## 主要特性

- ✅ 响应式设计，支持移动端和桌面端
- ✅ 路由守卫，自动登录验证
- ✅ 统一的错误处理和消息提示
- ✅ 数据可视化图表展示
- ✅ 优雅的加载状态和空状态处理
- ✅ TypeScript 友好的代码结构

## 浏览器支持

- Chrome (最新版)
- Firefox (最新版)
- Safari (最新版)
- Edge (最新版)

## 开发注意事项

1. API 接口返回格式应遵循统一规范：
   ```javascript
   {
     code: 200,
     data: {},
     message: 'success'
   }
   ```

2. 认证 token 存储在 localStorage，key 为 `token`

3. 日期时间统一使用 Day.js 处理，格式为 `YYYY-MM-DD HH:mm:ss`

4. 图表组件使用 vue-echarts，需要按需导入 ECharts 模块

## License

MIT

