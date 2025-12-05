# CloudFitness 后端项目

## 项目说明

这是云健健身房管理系统的后端服务，基于 Spring Boot 3.x 开发。

## 技术栈

- Java 17
- Spring Boot 3.2.0
- MyBatis-Plus 3.5.5
- MySQL 8.0
- Redis
- Spring Security
- JWT (jjwt 0.12.3)

## 项目结构

```
src/main/java/com/cloudfitness/
├── CloudFitnessApplication.java    # 启动类
├── common/                          # 通用类
│   ├── Result.java                 # 统一响应格式
│   └── GlobalExceptionHandler.java # 全局异常处理
├── config/                          # 配置类
│   └── SecurityConfig.java         # Spring Security配置
├── entity/                          # 实体类
│   ├── User.java
│   ├── Course.java
│   ├── Reservation.java
│   └── ...
├── mapper/                          # Mapper接口
│   ├── UserMapper.java
│   ├── CourseMapper.java
│   └── ...
├── service/                         # 服务层
│   ├── AuthService.java
│   ├── UserService.java
│   └── ...
├── controller/                     # 控制器层
│   ├── AuthController.java
│   ├── UserController.java
│   └── admin/                      # 后台管理接口
│       ├── AdminUserController.java
│       ├── AdminCourseController.java
│       └── ...
├── filter/                          # 过滤器
│   └── JwtAuthenticationFilter.java
└── util/                            # 工具类
    ├── JwtUtil.java
    └── Md5Util.java
```

## 数据库配置

1. 创建数据库 `yunjian`
2. 执行 `yunjian.sql` 创建表结构
3. 修改 `application.yml` 中的数据库连接信息

## 配置说明

### application.yml

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/yunjian?...
    username: root
    password: root
  data:
    redis:
      host: localhost
      port: 6379
```

### JWT配置

```yaml
jwt:
  secret: cloudfitness2024secretkey
  expiration: 86400000  # 24小时
```

## API接口

### C端接口（用户端）

- `/api/auth/login` - 用户登录
- `/api/auth/register` - 用户注册
- `/api/auth/check-username` - 检查用户名是否存在
- `/api/auth/check-phone` - 检查手机号是否存在
- `/api/user/info` - 获取用户信息
- `/api/user/profile` - 更新用户资料
- `/api/user/password` - 修改密码
- `/api/courses` - 获取课程列表
- `/api/reservations` - 预约相关接口
- `/api/attendance` - 签到相关接口
- `/api/memberships/current` - 获取当前会员卡
- `/api/lockers` - 获取储物柜列表

### B端接口（后台管理）

- `/api/admin/users/members` - 会员管理
- `/api/admin/users/coaches` - 教练管理
- `/api/admin/users/admins` - 管理员管理
- `/api/admin/courses` - 课程管理
- `/api/admin/reservations` - 预约管理
- `/api/admin/attendance` - 签到管理
- `/api/admin/memberships` - 会员卡管理
- `/api/admin/lockers` - 储物柜管理
- `/api/admin/roles` - 角色管理
- `/api/admin/permissions` - 权限管理
- `/api/admin/dashboard/statistics` - 数据统计

## 开发说明

### 密码加密

使用 MD5 加密，工具类：`Md5Util`

### JWT Token

- 请求头格式：`Authorization: Bearer {token}`
- Token 包含：userId, username, role

### 统一响应格式

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

## 注意事项

1. 所有接口严格按照 `docs/API设计.md` 文档实现
2. 所有字段严格按照 `docs/数据库设计.md` 文档设计
3. 密码使用 MD5 加密
4. 所有接口都需要实现，不能遗漏

## 待完成

由于代码量较大，以下Service实现类需要补充完整：

- `AdminCourseServiceImpl`
- `AdminReservationServiceImpl`
- `AdminAttendanceServiceImpl`
- `AdminMembershipServiceImpl`
- `AdminLockerServiceImpl`
- `AdminRoleServiceImpl`
- `AdminPermissionServiceImpl`
- `AdminDashboardServiceImpl`

以及对应的 Mapper XML 文件。


