# API 接口设计文档

## 一、C端接口（用户端）

### 1.1 认证相关接口

#### 1.1.1 用户登录
- **接口路径：** `POST /api/auth/login`
- **请求参数：**
  ```json
  {
    "username": "string",
    "password": "string"
  }
  ```
- **响应数据：**
  ```json
  {
    "code": 200,
    "message": "success",
    "data": {
      "token": "string",
      "user_id": 1,
      "username": "string",
      "role": "member"
    }
  }
  ```

#### 1.1.2 用户注册
- **接口路径：** `POST /api/auth/register`
- **请求参数：**
  ```json
  {
    "username": "string",
    "phone_number": "string",
    "password": "string"
  }
  ```
- **响应数据：**
  ```json
  {
    "code": 200,
    "message": "注册成功",
    "data": {
      "user_id": 1
    }
  }
  ```

#### 1.1.3 检查用户名是否存在
- **接口路径：** `POST /api/auth/check-username`
- **请求参数：**
  ```json
  {
    "username": "string"
  }
  ```
- **响应数据：**
  ```json
  {
    "code": 200,
    "message": "success",
    "data": {
      "exists": true
    }
  }
  ```

#### 1.1.4 检查手机号是否存在
- **接口路径：** `POST /api/auth/check-phone`
- **请求参数：**
  ```json
  {
    "phone_number": "string"
  }
  ```
- **响应数据：**
  ```json
  {
    "code": 200,
    "message": "success",
    "data": {
      "exists": true
    }
  }
  ```

#### 1.1.5 获取用户信息
- **接口路径：** `GET /api/user/info`
- **请求头：** `Authorization: Bearer {token}`
- **响应数据：**
  ```json
  {
    "code": 200,
    "message": "success",
    "data": {
      "user_id": 1,
      "username": "string",
      "email": "string",
      "phone_number": "string",
      "role": "member",
      "registration_date": "2024-01-01T00:00:00Z"
    }
  }
  ```

#### 1.1.6 更新用户资料
- **接口路径：** `PUT /api/user/profile`
- **请求头：** `Authorization: Bearer {token}`
- **请求参数：**
  ```json
  {
    "email": "string",
    "phone_number": "string"
  }
  ```
- **响应数据：**
  ```json
  {
    "code": 200,
    "message": "更新成功",
    "data": {}
  }
  ```

#### 1.1.7 修改密码
- **接口路径：** `PUT /api/user/password`
- **请求头：** `Authorization: Bearer {token}`
- **请求参数：**
  ```json
  {
    "old_password": "string",
    "new_password": "string"
  }
  ```
- **响应数据：**
  ```json
  {
    "code": 200,
    "message": "密码修改成功",
    "data": {}
  }
  ```

---

### 1.2 课程相关接口

#### 1.2.1 获取课程列表
- **接口路径：** `GET /api/courses`
- **请求参数（Query）：**
  - `date`: 日期（YYYY-MM-DD，选填）
  - `instructor_id`: 教练ID（选填）
  - `page`: 页码（选填，默认1）
  - `page_size`: 每页数量（选填，默认20）
- **响应数据：**
  ```json
  {
    "code": 200,
    "message": "success",
    "data": {
      "courses": [
        {
          "course_id": 1,
          "course_name": "瑜伽课程",
          "instructor_id": 2,
          "instructor_name": "张教练",
          "schedule": "2024-01-15T10:00:00Z",
          "capacity": 20,
          "reserved_count": 15,
          "description": "课程描述"
        }
      ],
      "total": 100,
      "page": 1,
      "page_size": 20
    }
  }
  ```

#### 1.2.2 获取课程详情
- **接口路径：** `GET /api/courses/{course_id}`
- **响应数据：**
  ```json
  {
    "code": 200,
    "message": "success",
    "data": {
      "course_id": 1,
      "course_name": "瑜伽课程",
      "instructor_id": 2,
      "instructor_name": "张教练",
      "schedule": "2024-01-15T10:00:00Z",
      "capacity": 20,
      "reserved_count": 15,
      "description": "课程描述",
      "status": "未开始"
    }
  }
  ```

---

### 1.3 预约相关接口

#### 1.3.1 获取预约列表
- **接口路径：** `GET /api/reservations`
- **请求头：** `Authorization: Bearer {token}`
- **响应数据：**
  ```json
  {
    "code": 200,
    "message": "success",
    "data": {
      "reservations": [
        {
          "reservation_id": 1,
          "user_id": 1,
          "course_id": 1,
          "course_name": "瑜伽课程",
          "reservation_date": "2024-01-10T08:00:00Z",
          "schedule": "2024-01-15T10:00:00Z",
          "status": "pending",
          "instructor_name": "张教练",
          "attendance": {
            "attendance_id": 1,
            "check_in_time": "2024-01-15T09:55:00Z",
            "is_on_time": true
          }
        }
      ]
    }
  }
  ```

#### 1.3.2 预约课程
- **接口路径：** `POST /api/reservations`
- **请求头：** `Authorization: Bearer {token}`
- **请求参数：**
  ```json
  {
    "course_id": 1
  }
  ```
- **响应数据：**
  ```json
  {
    "code": 200,
    "message": "预约成功",
    "data": {
      "reservation_id": 1
    }
  }
  ```

#### 1.3.3 取消预约
- **接口路径：** `DELETE /api/reservations/{reservation_id}`
- **请求头：** `Authorization: Bearer {token}`
- **响应数据：**
  ```json
  {
    "code": 200,
    "message": "取消成功",
    "data": {}
  }
  ```

---

### 1.4 签到相关接口

#### 1.4.1 获取签到记录
- **接口路径：** `GET /api/attendance`
- **请求头：** `Authorization: Bearer {token}`
- **请求参数（Query）：**
  - `month`: 月份（YYYY-MM，选填）
  - `limit`: 限制数量（选填，默认30）
- **响应数据：**
  ```json
  {
    "code": 200,
    "message": "success",
    "data": {
      "attendance": [
        {
          "attendance_id": 1,
          "reservation_id": 1,
          "course_name": "瑜伽课程",
          "schedule": "2024-01-15T10:00:00Z",
          "check_in_time": "2024-01-15T09:55:00Z",
          "is_on_time": true,
          "late_minutes": 0
        }
      ]
    }
  }
  ```

---

### 1.5 会员卡相关接口

#### 1.5.1 获取当前会员卡
- **接口路径：** `GET /api/memberships/current`
- **请求头：** `Authorization: Bearer {token}`
- **响应数据：**
  ```json
  {
    "code": 200,
    "message": "success",
    "data": {
      "membership_id": 1,
      "user_id": 1,
      "membership_type": "annual",
      "start_date": "2024-01-01T00:00:00Z",
      "expiry_date": "2024-12-31T23:59:59Z"
    }
  }
  ```

---

### 1.6 储物柜相关接口

#### 1.6.1 获取储物柜列表
- **接口路径：** `GET /api/lockers`
- **请求头：** `Authorization: Bearer {token}`
- **响应数据：**
  ```json
  {
    "code": 200,
    "message": "success",
    "data": {
      "lockers": [
        {
          "locker_id": 1,
          "user_id": 1,
          "locker_number": 101,
          "start_date": "2024-01-01T00:00:00Z",
          "end_date": "2024-12-31T23:59:59Z"
        }
      ]
    }
  }
  ```
---

## 二、B端接口（后台管理端）

### 2.1 用户管理接口

#### 2.1.1 获取会员列表
- **接口路径：** `GET /api/admin/users/members`
- **请求头：** `Authorization: Bearer {token}`
- **请求参数（Query）：**
  - `username`: 用户名（选填）
  - `phone_number`: 手机号（选填）
  - `email`: 邮箱（选填）
  - `start_date`: 注册开始时间（选填）
  - `end_date`: 注册结束时间（选填）
  - `membership_status`: 会员状态（选填：valid/expired）
  - `page`: 页码（选填，默认1）
  - `page_size`: 每页数量（选填，默认20）
- **响应数据：**
  ```json
  {
    "code": 200,
    "message": "success",
    "data": {
      "list": [
        {
          "user_id": 1,
          "username": "member001",
          "phone_number": "13800138000",
          "email": "member@example.com",
          "registration_date": "2024-01-01T00:00:00Z",
          "membership_type": "annual",
          "expiry_date": "2024-12-31T23:59:59Z",
          "membership_status": "valid"
        }
      ],
      "total": 100,
      "page": 1,
      "page_size": 20
    }
  }
  ```

#### 2.1.2 获取会员详情
- **接口路径：** `GET /api/admin/users/members/{user_id}`
- **请求头：** `Authorization: Bearer {token}`
- **响应数据：**
  ```json
  {
    "code": 200,
    "message": "success",
    "data": {
      "user_id": 1,
      "username": "member001",
      "phone_number": "13800138000",
      "email": "member@example.com",
      "role": "member",
      "registration_date": "2024-01-01T00:00:00Z",
      "membership": {
        "membership_id": 1,
        "membership_type": "annual",
        "start_date": "2024-01-01T00:00:00Z",
        "expiry_date": "2024-12-31T23:59:59Z"
      }
    }
  }
  ```

#### 2.1.3 新增会员
- **接口路径：** `POST /api/admin/users/members`
- **请求头：** `Authorization: Bearer {token}`
- **请求参数：**
  ```json
  {
    "username": "member001",
    "phone_number": "13800138000",
    "email": "member@example.com",
    "password": "123456"
  }
  ```
- **响应数据：**
  ```json
  {
    "code": 200,
    "message": "创建成功",
    "data": {
      "user_id": 1
    }
  }
  ```

#### 2.1.4 编辑会员
- **接口路径：** `PUT /api/admin/users/members/{user_id}`
- **请求头：** `Authorization: Bearer {token}`
- **请求参数：**
  ```json
  {
    "email": "newemail@example.com",
    "phone_number": "13900139000"
  }
  ```
- **响应数据：**
  ```json
  {
    "code": 200,
    "message": "更新成功",
    "data": {}
  }
  ```

#### 2.1.5 删除会员
- **接口路径：** `DELETE /api/admin/users/members/{user_id}`
- **请求头：** `Authorization: Bearer {token}`
- **响应数据：**
  ```json
  {
    "code": 200,
    "message": "删除成功",
    "data": {}
  }
  ```

#### 2.1.6 重置会员密码
- **接口路径：** `PUT /api/admin/users/members/{user_id}/reset-password`
- **请求头：** `Authorization: Bearer {token}`
- **请求参数：**
  ```json
  {
    "new_password": "newpassword123"
  }
  ```
- **响应数据：**
  ```json
  {
    "code": 200,
    "message": "密码重置成功",
    "data": {}
  }
  ```

#### 2.1.7 启用/禁用会员
- **接口路径：** `PUT /api/admin/users/members/{user_id}/status`
- **请求头：** `Authorization: Bearer {token}`
- **请求参数：**
  ```json
  {
    "status": "active"
  }
  ```
- **响应数据：**
  ```json
  {
    "code": 200,
    "message": "状态更新成功",
    "data": {}
  }
  ```

#### 2.1.8 获取教练列表
- **接口路径：** `GET /api/admin/users/coaches`
- **请求头：** `Authorization: Bearer {token}`
- **请求参数（Query）：**
  - `username`: 用户名（选填）
  - `phone_number`: 手机号（选填）
  - `start_date`: 注册开始时间（选填）
  - `end_date`: 注册结束时间（选填）
  - `status`: 状态（选填：active/inactive）
  - `page`: 页码（选填，默认1）
  - `page_size`: 每页数量（选填，默认20）
- **响应数据：**
  ```json
  {
    "code": 200,
    "message": "success",
    "data": {
      "list": [
        {
          "user_id": 2,
          "username": "coach001",
          "phone_number": "13800138001",
          "email": "coach@example.com",
          "registration_date": "2024-01-01T00:00:00Z",
          "course_count": 10,
          "status": "active"
        }
      ],
      "total": 20,
      "page": 1,
      "page_size": 20
    }
  }
  ```

#### 2.1.9 获取教练详情
- **接口路径：** `GET /api/admin/users/coaches/{user_id}`
- **请求头：** `Authorization: Bearer {token}`
- **响应数据：**
  ```json
  {
    "code": 200,
    "message": "success",
    "data": {
      "user_id": 2,
      "username": "coach001",
      "phone_number": "13800138001",
      "email": "coach@example.com",
      "role": "coach",
      "registration_date": "2024-01-01T00:00:00Z",
      "description": "资深瑜伽教练",
      "courses": [
        {
          "course_id": 1,
          "course_name": "瑜伽课程",
          "schedule": "2024-01-15T10:00:00Z"
        }
      ]
    }
  }
  ```

#### 2.1.10 新增教练
- **接口路径：** `POST /api/admin/users/coaches`
- **请求头：** `Authorization: Bearer {token}`
- **请求参数：**
  ```json
  {
    "username": "coach001",
    "phone_number": "13800138001",
    "email": "coach@example.com",
    "password": "123456",
    "description": "资深瑜伽教练"
  }
  ```
- **响应数据：**
  ```json
  {
    "code": 200,
    "message": "创建成功",
    "data": {
      "user_id": 2
    }
  }
  ```

#### 2.1.11 编辑教练
- **接口路径：** `PUT /api/admin/users/coaches/{user_id}`
- **请求头：** `Authorization: Bearer {token}`
- **请求参数：**
  ```json
  {
    "email": "newemail@example.com",
    "description": "更新后的简介"
  }
  ```
- **响应数据：**
  ```json
  {
    "code": 200,
    "message": "更新成功",
    "data": {}
  }
  ```

#### 2.1.12 删除教练
- **接口路径：** `DELETE /api/admin/users/coaches/{user_id}`
- **请求头：** `Authorization: Bearer {token}`
- **响应数据：**
  ```json
  {
    "code": 200,
    "message": "删除成功",
    "data": {}
  }
  ```

#### 2.1.13 重置教练密码
- **接口路径：** `PUT /api/admin/users/coaches/{user_id}/reset-password`
- **请求头：** `Authorization: Bearer {token}`
- **请求参数：**
  ```json
  {
    "new_password": "newpassword123"
  }
  ```
- **响应数据：**
  ```json
  {
    "code": 200,
    "message": "密码重置成功",
    "data": {}
  }
  ```

#### 2.1.14 启用/禁用教练
- **接口路径：** `PUT /api/admin/users/coaches/{user_id}/status`
- **请求头：** `Authorization: Bearer {token}`
- **请求参数：**
  ```json
  {
    "status": "active"
  }
  ```
- **响应数据：**
  ```json
  {
    "code": 200,
    "message": "状态更新成功",
    "data": {}
  }
  ```

#### 2.1.15 获取管理员列表
- **接口路径：** `GET /api/admin/users/admins`
- **请求头：** `Authorization: Bearer {token}`
- **请求参数（Query）：**
  - `username`: 用户名（选填）
  - `phone_number`: 手机号（选填）
  - `start_date`: 注册开始时间（选填）
  - `end_date`: 注册结束时间（选填）
  - `status`: 状态（选填：active/inactive）
  - `page`: 页码（选填，默认1）
  - `page_size`: 每页数量（选填，默认20）
- **响应数据：**
  ```json
  {
    "code": 200,
    "message": "success",
    "data": {
      "list": [
        {
          "user_id": 3,
          "username": "admin001",
          "phone_number": "13800138002",
          "email": "admin@example.com",
          "registration_date": "2024-01-01T00:00:00Z",
          "last_login_time": "2024-01-15T10:00:00Z",
          "status": "active",
          "role_name": "超级管理员"
        }
      ],
      "total": 5,
      "page": 1,
      "page_size": 20
    }
  }
  ```

#### 2.1.16 获取管理员详情
- **接口路径：** `GET /api/admin/users/admins/{user_id}`
- **请求头：** `Authorization: Bearer {token}`
- **响应数据：**
  ```json
  {
    "code": 200,
    "message": "success",
    "data": {
      "user_id": 3,
      "username": "admin001",
      "phone_number": "13800138002",
      "email": "admin@example.com",
      "role": "admin",
      "registration_date": "2024-01-01T00:00:00Z",
      "last_login_time": "2024-01-15T10:00:00Z",
      "status": "active",
      "roles": [
        {
          "role_id": 1,
          "role_name": "超级管理员",
          "permissions": [
            {
              "permission_id": 1,
              "permission_name": "manage_users"
            }
          ]
        }
      ]
    }
  }
  ```

#### 2.1.17 新增管理员
- **接口路径：** `POST /api/admin/users/admins`
- **请求头：** `Authorization: Bearer {token}`
- **请求参数：**
  ```json
  {
    "username": "admin001",
    "phone_number": "13800138002",
    "email": "admin@example.com",
    "password": "123456",
    "role_ids": [1, 2]
  }
  ```
- **响应数据：**
  ```json
  {
    "code": 200,
    "message": "创建成功",
    "data": {
      "user_id": 3
    }
  }
  ```

#### 2.1.18 编辑管理员
- **接口路径：** `PUT /api/admin/users/admins/{user_id}`
- **请求头：** `Authorization: Bearer {token}`
- **请求参数：**
  ```json
  {
    "email": "newemail@example.com",
    "role_ids": [1, 2, 3]
  }
  ```
- **响应数据：**
  ```json
  {
    "code": 200,
    "message": "更新成功",
    "data": {}
  }
  ```

#### 2.1.19 删除管理员
- **接口路径：** `DELETE /api/admin/users/admins/{user_id}`
- **请求头：** `Authorization: Bearer {token}`
- **响应数据：**
  ```json
  {
    "code": 200,
    "message": "删除成功",
    "data": {}
  }
  ```

#### 2.1.20 重置管理员密码
- **接口路径：** `PUT /api/admin/users/admins/{user_id}/reset-password`
- **请求头：** `Authorization: Bearer {token}`
- **请求参数：**
  ```json
  {
    "new_password": "newpassword123"
  }
  ```
- **响应数据：**
  ```json
  {
    "code": 200,
    "message": "密码重置成功",
    "data": {}
  }
  ```

#### 2.1.21 启用/禁用管理员
- **接口路径：** `PUT /api/admin/users/admins/{user_id}/status`
- **请求头：** `Authorization: Bearer {token}`
- **请求参数：**
  ```json
  {
    "status": "active"
  }
  ```
- **响应数据：**
  ```json
  {
    "code": 200,
    "message": "状态更新成功",
    "data": {}
  }
  ```

---

### 2.2 课程管理接口

#### 2.2.1 获取课程列表
- **接口路径：** `GET /api/admin/courses`
- **请求头：** `Authorization: Bearer {token}`
- **请求参数（Query）：**
  - `course_name`: 课程名称（选填）
  - `instructor_id`: 教练ID（选填）
  - `start_date`: 开课开始时间（选填）
  - `end_date`: 开课结束时间（选填）
  - `status`: 课程状态（选填：upcoming/ongoing/completed）
  - `page`: 页码（选填，默认1）
  - `page_size`: 每页数量（选填，默认20）
- **响应数据：**
  ```json
  {
    "code": 200,
    "message": "success",
    "data": {
      "list": [
        {
          "course_id": 1,
          "course_name": "瑜伽课程",
          "instructor_id": 2,
          "instructor_name": "张教练",
          "schedule": "2024-01-15T10:00:00Z",
          "capacity": 20,
          "reserved_count": 15,
          "status": "upcoming"
        }
      ],
      "total": 100,
      "page": 1,
      "page_size": 20
    }
  }
  ```

#### 2.2.2 获取课程详情
- **接口路径：** `GET /api/admin/courses/{course_id}`
- **请求头：** `Authorization: Bearer {token}`
- **响应数据：**
  ```json
  {
    "code": 200,
    "message": "success",
    "data": {
      "course_id": 1,
      "course_name": "瑜伽课程",
      "instructor_id": 2,
      "instructor_name": "张教练",
      "schedule": "2024-01-15T10:00:00Z",
      "capacity": 20,
      "reserved_count": 15,
      "description": "课程描述",
      "status": "upcoming",
      "reservations": [
        {
          "reservation_id": 1,
          "user_id": 1,
          "username": "member001",
          "phone_number": "13800138000",
          "reservation_date": "2024-01-10T08:00:00Z",
          "status": "confirmed"
        }
      ]
    }
  }
  ```

#### 2.2.3 新增课程
- **接口路径：** `POST /api/admin/courses`
- **请求头：** `Authorization: Bearer {token}`
- **请求参数：**
  ```json
  {
    "course_name": "瑜伽课程",
    "instructor_id": 2,
    "schedule": "2024-01-15T10:00:00Z",
    "capacity": 20,
    "description": "课程描述"
  }
  ```
- **响应数据：**
  ```json
  {
    "code": 200,
    "message": "创建成功",
    "data": {
      "course_id": 1
    }
  }
  ```

#### 2.2.4 编辑课程
- **接口路径：** `PUT /api/admin/courses/{course_id}`
- **请求头：** `Authorization: Bearer {token}`
- **请求参数：**
  ```json
  {
    "course_name": "瑜伽课程（更新）",
    "instructor_id": 2,
    "schedule": "2024-01-15T11:00:00Z",
    "capacity": 25,
    "description": "更新后的课程描述"
  }
  ```
- **响应数据：**
  ```json
  {
    "code": 200,
    "message": "更新成功",
    "data": {}
  }
  ```

#### 2.2.5 删除课程
- **接口路径：** `DELETE /api/admin/courses/{course_id}`
- **请求头：** `Authorization: Bearer {token}`
- **响应数据：**
  ```json
  {
    "code": 200,
    "message": "删除成功",
    "data": {}
  }
  ```

#### 2.2.6 更新课程状态
- **接口路径：** `PUT /api/admin/courses/{course_id}/status`
- **请求头：** `Authorization: Bearer {token}`
- **请求参数：**
  ```json
  {
    "status": "ongoing"
  }
  ```
- **响应数据：**
  ```json
  {
    "code": 200,
    "message": "状态更新成功",
    "data": {}
  }
  ```

---

### 2.3 预约管理接口

#### 2.3.1 获取预约列表
- **接口路径：** `GET /api/admin/reservations`
- **请求头：** `Authorization: Bearer {token}`
- **请求参数（Query）：**
  - `username`: 用户名（选填）
  - `phone_number`: 手机号（选填）
  - `course_name`: 课程名称（选填）
  - `start_date`: 预约开始时间（选填）
  - `end_date`: 预约结束时间（选填）
  - `status`: 预约状态（选填：pending/confirmed/completed/cancelled）
  - `page`: 页码（选填，默认1）
  - `page_size`: 每页数量（选填，默认20）
- **响应数据：**
  ```json
  {
    "code": 200,
    "message": "success",
    "data": {
      "list": [
        {
          "reservation_id": 1,
          "user_id": 1,
          "username": "member001",
          "phone_number": "13800138000",
          "course_id": 1,
          "course_name": "瑜伽课程",
          "reservation_date": "2024-01-10T08:00:00Z",
          "schedule": "2024-01-15T10:00:00Z",
          "status": "confirmed",
          "has_attendance": true
        }
      ],
      "total": 200,
      "page": 1,
      "page_size": 20
    }
  }
  ```

#### 2.3.2 获取预约详情
- **接口路径：** `GET /api/admin/reservations/{reservation_id}`
- **请求头：** `Authorization: Bearer {token}`
- **响应数据：**
  ```json
  {
    "code": 200,
    "message": "success",
    "data": {
      "reservation_id": 1,
      "user_id": 1,
      "username": "member001",
      "phone_number": "13800138000",
      "course_id": 1,
      "course_name": "瑜伽课程",
      "reservation_date": "2024-01-10T08:00:00Z",
      "schedule": "2024-01-15T10:00:00Z",
      "status": "confirmed",
      "attendance": {
        "attendance_id": 1,
        "check_in_time": "2024-01-15T09:55:00Z",
        "is_on_time": true
      }
    }
  }
  ```

#### 2.3.3 新增预约
- **接口路径：** `POST /api/admin/reservations`
- **请求头：** `Authorization: Bearer {token}`
- **请求参数：**
  ```json
  {
    "user_id": 1,
    "course_id": 1,
    "status": "confirmed"
  }
  ```
- **响应数据：**
  ```json
  {
    "code": 200,
    "message": "创建成功",
    "data": {
      "reservation_id": 1
    }
  }
  ```

#### 2.3.4 编辑预约
- **接口路径：** `PUT /api/admin/reservations/{reservation_id}`
- **请求头：** `Authorization: Bearer {token}`
- **请求参数：**
  ```json
  {
    "status": "confirmed"
  }
  ```
- **响应数据：**
  ```json
  {
    "code": 200,
    "message": "更新成功",
    "data": {}
  }
  ```

#### 2.3.5 删除预约
- **接口路径：** `DELETE /api/admin/reservations/{reservation_id}`
- **请求头：** `Authorization: Bearer {token}`
- **响应数据：**
  ```json
  {
    "code": 200,
    "message": "删除成功",
    "data": {}
  }
  ```

#### 2.3.6 确认预约
- **接口路径：** `PUT /api/admin/reservations/{reservation_id}/confirm`
- **请求头：** `Authorization: Bearer {token}`
- **响应数据：**
  ```json
  {
    "code": 200,
    "message": "确认成功",
    "data": {}
  }
  ```

#### 2.3.7 取消预约
- **接口路径：** `PUT /api/admin/reservations/{reservation_id}/cancel`
- **请求头：** `Authorization: Bearer {token}`
- **响应数据：**
  ```json
  {
    "code": 200,
    "message": "取消成功",
    "data": {}
  }
  ```

#### 2.3.8 标记完成
- **接口路径：** `PUT /api/admin/reservations/{reservation_id}/complete`
- **请求头：** `Authorization: Bearer {token}`
- **响应数据：**
  ```json
  {
    "code": 200,
    "message": "标记成功",
    "data": {}
  }
  ```

---

### 2.4 签到管理接口

#### 2.4.1 获取签到记录列表
- **接口路径：** `GET /api/admin/attendance`
- **请求头：** `Authorization: Bearer {token}`
- **请求参数（Query）：**
  - `username`: 用户名（选填）
  - `phone_number`: 手机号（选填）
  - `course_name`: 课程名称（选填）
  - `start_date`: 签到开始时间（选填）
  - `end_date`: 签到结束时间（选填）
  - `is_on_time`: 是否准时（选填：true/false）
  - `page`: 页码（选填，默认1）
  - `page_size`: 每页数量（选填，默认20）
- **响应数据：**
  ```json
  {
    "code": 200,
    "message": "success",
    "data": {
      "list": [
        {
          "attendance_id": 1,
          "reservation_id": 1,
          "user_id": 1,
          "username": "member001",
          "phone_number": "13800138000",
          "course_id": 1,
          "course_name": "瑜伽课程",
          "schedule": "2024-01-15T10:00:00Z",
          "check_in_time": "2024-01-15T09:55:00Z",
          "is_on_time": true,
          "late_minutes": 0
        }
      ],
      "total": 500,
      "page": 1,
      "page_size": 20
    }
  }
  ```

#### 2.4.2 获取签到详情
- **接口路径：** `GET /api/admin/attendance/{attendance_id}`
- **请求头：** `Authorization: Bearer {token}`
- **响应数据：**
  ```json
  {
    "code": 200,
    "message": "success",
    "data": {
      "attendance_id": 1,
      "reservation_id": 1,
      "user_id": 1,
      "username": "member001",
      "course_id": 1,
      "course_name": "瑜伽课程",
      "schedule": "2024-01-15T10:00:00Z",
      "check_in_time": "2024-01-15T09:55:00Z",
      "is_on_time": true,
      "late_minutes": 0
    }
  }
  ```

#### 2.4.3 删除签到记录
- **接口路径：** `DELETE /api/admin/attendance/{attendance_id}`
- **请求头：** `Authorization: Bearer {token}`
- **响应数据：**
  ```json
  {
    "code": 200,
    "message": "删除成功",
    "data": {}
  }
  ```

#### 2.4.4 获取每日签到统计
- **接口路径：** `GET /api/admin/attendance/statistics/daily`
- **请求头：** `Authorization: Bearer {token}`
- **请求参数（Query）：**
  - `start_date`: 开始日期（必填，YYYY-MM-DD）
  - `end_date`: 结束日期（必填，YYYY-MM-DD）
- **响应数据：**
  ```json
  {
    "code": 200,
    "message": "success",
    "data": {
      "statistics": [
        {
          "date": "2024-01-15",
          "total": 50,
          "on_time": 45,
          "late": 5
        }
      ]
    }
  }
  ```

#### 2.4.5 获取每月出勤率统计
- **接口路径：** `GET /api/admin/attendance/statistics/monthly`
- **请求头：** `Authorization: Bearer {token}`
- **请求参数（Query）：**
  - `start_month`: 开始月份（必填，YYYY-MM）
  - `end_month`: 结束月份（必填，YYYY-MM）
- **响应数据：**
  ```json
  {
    "code": 200,
    "message": "success",
    "data": {
      "statistics": [
        {
          "month": "2024-01",
          "total": 500,
          "on_time": 450,
          "attendance_rate": 0.9
        }
      ]
    }
  }
  ```

#### 2.4.6 获取课程出勤率统计
- **接口路径：** `GET /api/admin/attendance/statistics/course`
- **请求头：** `Authorization: Bearer {token}`
- **请求参数（Query）：**
  - `course_id`: 课程ID（选填）
  - `start_date`: 开始日期（选填）
  - `end_date`: 结束日期（选填）
- **响应数据：**
  ```json
  {
    "code": 200,
    "message": "success",
    "data": {
      "statistics": [
        {
          "course_id": 1,
          "course_name": "瑜伽课程",
          "total_reservations": 100,
          "total_attendance": 90,
          "attendance_rate": 0.9
        }
      ]
    }
  }
  ```

#### 2.4.7 获取用户出勤率排名
- **接口路径：** `GET /api/admin/attendance/statistics/user-ranking`
- **请求头：** `Authorization: Bearer {token}`
- **请求参数（Query）：**
  - `limit`: 排名数量（选填，默认10）
  - `start_date`: 开始日期（选填）
  - `end_date`: 结束日期（选填）
- **响应数据：**
  ```json
  {
    "code": 200,
    "message": "success",
    "data": {
      "rankings": [
        {
          "user_id": 1,
          "username": "member001",
          "total_attendance": 50,
          "attendance_rate": 0.95
        }
      ]
    }
  }
  ```

---

### 2.5 会员卡管理接口

#### 2.5.1 获取会员卡列表
- **接口路径：** `GET /api/admin/memberships`
- **请求头：** `Authorization: Bearer {token}`
- **请求参数（Query）：**
  - `username`: 用户名（选填）
  - `phone_number`: 手机号（选填）
  - `membership_type`: 会员卡类型（选填：monthly/annual/other）
  - `start_date`: 到期开始时间（选填）
  - `end_date`: 到期结束时间（选填）
  - `status`: 会员状态（选填：valid/expired）
  - `page`: 页码（选填，默认1）
  - `page_size`: 每页数量（选填，默认20）
- **响应数据：**
  ```json
  {
    "code": 200,
    "message": "success",
    "data": {
      "list": [
        {
          "membership_id": 1,
          "user_id": 1,
          "username": "member001",
          "phone_number": "13800138000",
          "membership_type": "annual",
          "start_date": "2024-01-01T00:00:00Z",
          "expiry_date": "2024-12-31T23:59:59Z",
          "status": "valid"
        }
      ],
      "total": 100,
      "page": 1,
      "page_size": 20
    }
  }
  ```

#### 2.5.2 获取会员卡详情
- **接口路径：** `GET /api/admin/memberships/{membership_id}`
- **请求头：** `Authorization: Bearer {token}`
- **响应数据：**
  ```json
  {
    "code": 200,
    "message": "success",
    "data": {
      "membership_id": 1,
      "user_id": 1,
      "username": "member001",
      "membership_type": "annual",
      "start_date": "2024-01-01T00:00:00Z",
      "expiry_date": "2024-12-31T23:59:59Z",
      "status": "valid"
    }
  }
  ```

#### 2.5.3 新增会员卡
- **接口路径：** `POST /api/admin/memberships`
- **请求头：** `Authorization: Bearer {token}`
- **请求参数：**
  ```json
  {
    "user_id": 1,
    "membership_type": "annual",
    "start_date": "2024-01-01T00:00:00Z",
    "expiry_date": "2024-12-31T23:59:59Z"
  }
  ```
- **响应数据：**
  ```json
  {
    "code": 200,
    "message": "创建成功",
    "data": {
      "membership_id": 1
    }
  }
  ```

#### 2.5.4 编辑会员卡
- **接口路径：** `PUT /api/admin/memberships/{membership_id}`
- **请求头：** `Authorization: Bearer {token}`
- **请求参数：**
  ```json
  {
    "membership_type": "annual",
    "start_date": "2024-01-01T00:00:00Z",
    "expiry_date": "2024-12-31T23:59:59Z"
  }
  ```
- **响应数据：**
  ```json
  {
    "code": 200,
    "message": "更新成功",
    "data": {}
  }
  ```

#### 2.5.5 删除会员卡
- **接口路径：** `DELETE /api/admin/memberships/{membership_id}`
- **请求头：** `Authorization: Bearer {token}`
- **响应数据：**
  ```json
  {
    "code": 200,
    "message": "删除成功",
    "data": {}
  }
  ```

#### 2.5.6 续费会员卡
- **接口路径：** `PUT /api/admin/memberships/{membership_id}/renew`
- **请求头：** `Authorization: Bearer {token}`
- **请求参数：**
  ```json
  {
    "months": 12
  }
  ```
- **响应数据：**
  ```json
  {
    "code": 200,
    "message": "续费成功",
    "data": {
      "new_expiry_date": "2025-12-31T23:59:59Z"
    }
  }
  ```

---

### 2.6 储物柜管理接口

#### 2.6.1 获取储物柜列表
- **接口路径：** `GET /api/admin/lockers`
- **请求头：** `Authorization: Bearer {token}`
- **请求参数（Query）：**
  - `locker_number`: 储物柜编号（选填）
  - `username`: 用户名（选填）
  - `phone_number`: 手机号（选填）
  - `status`: 使用状态（选填：in_use/free/expired）
  - `page`: 页码（选填，默认1）
  - `page_size`: 每页数量（选填，默认20）
- **响应数据：**
  ```json
  {
    "code": 200,
    "message": "success",
    "data": {
      "list": [
        {
          "locker_id": 1,
          "locker_number": 101,
          "user_id": 1,
          "username": "member001",
          "phone_number": "13800138000",
          "start_date": "2024-01-01T00:00:00Z",
          "end_date": "2024-12-31T23:59:59Z",
          "status": "in_use"
        }
      ],
      "total": 50,
      "page": 1,
      "page_size": 20
    }
  }
  ```

#### 2.6.2 获取储物柜详情
- **接口路径：** `GET /api/admin/lockers/{locker_id}`
- **请求头：** `Authorization: Bearer {token}`
- **响应数据：**
  ```json
  {
    "code": 200,
    "message": "success",
    "data": {
      "locker_id": 1,
      "locker_number": 101,
      "user_id": 1,
      "username": "member001",
      "start_date": "2024-01-01T00:00:00Z",
      "end_date": "2024-12-31T23:59:59Z",
      "status": "in_use"
    }
  }
  ```

#### 2.6.3 新增储物柜
- **接口路径：** `POST /api/admin/lockers`
- **请求头：** `Authorization: Bearer {token}`
- **请求参数：**
  ```json
  {
    "locker_number": 101,
    "user_id": 1,
    "start_date": "2024-01-01T00:00:00Z",
    "end_date": "2024-12-31T23:59:59Z"
  }
  ```
- **响应数据：**
  ```json
  {
    "code": 200,
    "message": "创建成功",
    "data": {
      "locker_id": 1
    }
  }
  ```

#### 2.6.4 编辑储物柜
- **接口路径：** `PUT /api/admin/lockers/{locker_id}`
- **请求头：** `Authorization: Bearer {token}`
- **请求参数：**
  ```json
  {
    "user_id": 1,
    "start_date": "2024-01-01T00:00:00Z",
    "end_date": "2024-12-31T23:59:59Z"
  }
  ```
- **响应数据：**
  ```json
  {
    "code": 200,
    "message": "更新成功",
    "data": {}
  }
  ```

#### 2.6.5 删除储物柜
- **接口路径：** `DELETE /api/admin/lockers/{locker_id}`
- **请求头：** `Authorization: Bearer {token}`
- **响应数据：**
  ```json
  {
    "code": 200,
    "message": "删除成功",
    "data": {}
  }
  ```

#### 2.6.6 分配储物柜
- **接口路径：** `PUT /api/admin/lockers/{locker_id}/assign`
- **请求头：** `Authorization: Bearer {token}`
- **请求参数：**
  ```json
  {
    "user_id": 1,
    "start_date": "2024-01-01T00:00:00Z",
    "end_date": "2024-12-31T23:59:59Z"
  }
  ```
- **响应数据：**
  ```json
  {
    "code": 200,
    "message": "分配成功",
    "data": {}
  }
  ```

#### 2.6.7 释放储物柜
- **接口路径：** `PUT /api/admin/lockers/{locker_id}/release`
- **请求头：** `Authorization: Bearer {token}`
- **响应数据：**
  ```json
  {
    "code": 200,
    "message": "释放成功",
    "data": {}
  }
  ```

---

### 2.7 角色权限管理接口

#### 2.7.1 获取角色列表
- **接口路径：** `GET /api/admin/roles`
- **请求头：** `Authorization: Bearer {token}`
- **请求参数（Query）：**
  - `role_name`: 角色名称（选填）
  - `page`: 页码（选填，默认1）
  - `page_size`: 每页数量（选填，默认20）
- **响应数据：**
  ```json
  {
    "code": 200,
    "message": "success",
    "data": {
      "list": [
        {
          "role_id": 1,
          "role_name": "超级管理员",
          "permission_count": 10,
          "created_at": "2024-01-01T00:00:00Z"
        }
      ],
      "total": 5,
      "page": 1,
      "page_size": 20
    }
  }
  ```

#### 2.7.2 获取角色详情
- **接口路径：** `GET /api/admin/roles/{role_id}`
- **请求头：** `Authorization: Bearer {token}`
- **响应数据：**
  ```json
  {
    "code": 200,
    "message": "success",
    "data": {
      "role_id": 1,
      "role_name": "超级管理员",
      "permissions": [
        {
          "permission_id": 1,
          "permission_name": "manage_users",
          "permission_description": "用户管理"
        }
      ],
      "created_at": "2024-01-01T00:00:00Z"
    }
  }
  ```

#### 2.7.3 新增角色
- **接口路径：** `POST /api/admin/roles`
- **请求头：** `Authorization: Bearer {token}`
- **请求参数：**
  ```json
  {
    "role_name": "普通管理员"
  }
  ```
- **响应数据：**
  ```json
  {
    "code": 200,
    "message": "创建成功",
    "data": {
      "role_id": 2
    }
  }
  ```

#### 2.7.4 编辑角色
- **接口路径：** `PUT /api/admin/roles/{role_id}`
- **请求头：** `Authorization: Bearer {token}`
- **请求参数：**
  ```json
  {
    "role_name": "普通管理员（更新）"
  }
  ```
- **响应数据：**
  ```json
  {
    "code": 200,
    "message": "更新成功",
    "data": {}
  }
  ```

#### 2.7.5 删除角色
- **接口路径：** `DELETE /api/admin/roles/{role_id}`
- **请求头：** `Authorization: Bearer {token}`
- **响应数据：**
  ```json
  {
    "code": 200,
    "message": "删除成功",
    "data": {}
  }
  ```

#### 2.7.6 分配权限
- **接口路径：** `PUT /api/admin/roles/{role_id}/permissions`
- **请求头：** `Authorization: Bearer {token}`
- **请求参数：**
  ```json
  {
    "permission_ids": [1, 2, 3]
  }
  ```
- **响应数据：**
  ```json
  {
    "code": 200,
    "message": "分配成功",
    "data": {}
  }
  ```

#### 2.7.7 获取权限列表
- **接口路径：** `GET /api/admin/permissions`
- **请求头：** `Authorization: Bearer {token}`
- **请求参数（Query）：**
  - `permission_name`: 权限名称（选填）
  - `page`: 页码（选填，默认1）
  - `page_size`: 每页数量（选填，默认20）
- **响应数据：**
  ```json
  {
    "code": 200,
    "message": "success",
    "data": {
      "list": [
        {
          "permission_id": 1,
          "permission_name": "manage_users",
          "permission_description": "用户管理",
          "created_at": "2024-01-01T00:00:00Z"
        }
      ],
      "total": 20,
      "page": 1,
      "page_size": 20
    }
  }
  ```

#### 2.7.8 获取权限详情
- **接口路径：** `GET /api/admin/permissions/{permission_id}`
- **请求头：** `Authorization: Bearer {token}`
- **响应数据：**
  ```json
  {
    "code": 200,
    "message": "success",
    "data": {
      "permission_id": 1,
      "permission_name": "manage_users",
      "permission_description": "用户管理",
      "created_at": "2024-01-01T00:00:00Z"
    }
  }
  ```

#### 2.7.9 新增权限
- **接口路径：** `POST /api/admin/permissions`
- **请求头：** `Authorization: Bearer {token}`
- **请求参数：**
  ```json
  {
    "permission_name": "manage_courses",
    "permission_description": "课程管理"
  }
  ```
- **响应数据：**
  ```json
  {
    "code": 200,
    "message": "创建成功",
    "data": {
      "permission_id": 2
    }
  }
  ```

#### 2.7.10 编辑权限
- **接口路径：** `PUT /api/admin/permissions/{permission_id}`
- **请求头：** `Authorization: Bearer {token}`
- **请求参数：**
  ```json
  {
    "permission_name": "manage_courses",
    "permission_description": "课程管理（更新）"
  }
  ```
- **响应数据：**
  ```json
  {
    "code": 200,
    "message": "更新成功",
    "data": {}
  }
  ```

#### 2.7.11 删除权限
- **接口路径：** `DELETE /api/admin/permissions/{permission_id}`
- **请求头：** `Authorization: Bearer {token}`
- **响应数据：**
  ```json
  {
    "code": 200,
    "message": "删除成功",
    "data": {}
  }
  ```

---

### 2.8 数据统计接口

#### 2.8.1 获取数据仪表盘
- **接口路径：** `GET /api/admin/dashboard`
- **请求头：** `Authorization: Bearer {token}`
- **响应数据：**
  ```json
  {
    "code": 200,
    "message": "success",
    "data": {
      "total_users": 1000,
      "total_members": 800,
      "total_coaches": 20,
      "total_admins": 5,
      "today_new_users": 10,
      "total_courses": 500,
      "today_courses": 5,
      "total_reservations": 2000,
      "today_reservations": 50,
      "total_attendance": 1500,
      "today_attendance": 30,
      "valid_memberships": 750,
      "in_use_lockers": 200
    }
  }
  ```

#### 2.8.2 获取用户增长趋势
- **接口路径：** `GET /api/admin/dashboard/user-growth`
- **请求头：** `Authorization: Bearer {token}`
- **请求参数（Query）：**
  - `start_date`: 开始日期（必填，YYYY-MM-DD）
  - `end_date`: 结束日期（必填，YYYY-MM-DD）
- **响应数据：**
  ```json
  {
    "code": 200,
    "message": "success",
    "data": {
      "growth": [
        {
          "date": "2024-01-15",
          "total": 1000,
          "new": 10
        }
      ]
    }
  }
  ```

#### 2.8.3 获取课程预约统计
- **接口路径：** `GET /api/admin/dashboard/course-reservations`
- **请求头：** `Authorization: Bearer {token}`
- **请求参数（Query）：**
  - `start_date`: 开始日期（选填）
  - `end_date`: 结束日期（选填）
- **响应数据：**
  ```json
  {
    "code": 200,
    "message": "success",
    "data": {
      "statistics": [
        {
          "course_id": 1,
          "course_name": "瑜伽课程",
          "reservation_count": 100
        }
      ]
    }
  }
  ```

#### 2.8.4 获取签到出勤率
- **接口路径：** `GET /api/admin/dashboard/attendance-rate`
- **请求头：** `Authorization: Bearer {token}`
- **请求参数（Query）：**
  - `start_date`: 开始日期（选填）
  - `end_date`: 结束日期（选填）
- **响应数据：**
  ```json
  {
    "code": 200,
    "message": "success",
    "data": {
      "attendance_rate": [
        {
          "date": "2024-01-15",
          "rate": 0.9
        }
      ]
    }
  }
  ```

#### 2.8.5 获取会员卡类型分布
- **接口路径：** `GET /api/admin/dashboard/membership-distribution`
- **请求头：** `Authorization: Bearer {token}`
- **响应数据：**
  ```json
  {
    "code": 200,
    "message": "success",
    "data": {
      "distribution": [
        {
          "membership_type": "annual",
          "count": 500
        },
        {
          "membership_type": "monthly",
          "count": 300
        }
      ]
    }
  }
  ```

#### 2.8.6 获取课程类型分布
- **接口路径：** `GET /api/admin/dashboard/course-distribution`
- **请求头：** `Authorization: Bearer {token}`
- **响应数据：**
  ```json
  {
    "code": 200,
    "message": "success",
    "data": {
      "distribution": [
        {
          "course_name": "瑜伽课程",
          "count": 200
        }
      ]
    }
  }
  ```

---

## 三、通用响应格式

所有接口统一使用以下响应格式：

**成功响应：**
```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

**错误响应：**
```json
{
  "code": 400,
  "message": "错误信息",
  "data": null
}
```

**常见错误码：**
- 200: 成功
- 400: 请求参数错误
- 401: 未授权（需要登录）
- 403: 无权限
- 404: 资源不存在
- 500: 服务器内部错误

---

## 四、认证说明

所有B端接口（`/api/admin/*`）都需要在请求头中携带认证token：

```
Authorization: Bearer {token}
```

token通过登录接口获取，有效期为24小时。

