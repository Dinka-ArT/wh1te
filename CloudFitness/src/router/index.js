import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const routes = [
  {
    path: '/',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/home',
    component: () => import('@/layouts/MainLayout.vue'),
    redirect: '/home',
    meta: { requiresAuth: true },
    children: [
      {
        path: 'home',
        name: 'Home',
        component: () => import('@/views/Home.vue'),
        meta: { title: '首页' }
      },
      {
        path: 'courses',
        name: 'Courses',
        component: () => import('@/views/Courses.vue'),
        meta: { title: '课程中心' }
      },
      {
        path: 'reservations',
        name: 'Reservations',
        component: () => import('@/views/Reservations.vue'),
        meta: { title: '我的预约' }
      },
      {
        path: 'attendance',
        name: 'Attendance',
        component: () => import('@/views/Attendance.vue'),
        meta: { title: '签到记录' }
      },
      {
        path: 'lockers',
        name: 'Lockers',
        component: () => import('@/views/Lockers.vue'),
        meta: { title: '储物柜' }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/Profile.vue'),
        meta: { title: '个人中心' }
      }
    ]
  },
  {
    path: '/admin',
    component: () => import('@/layouts/AdminLayout.vue'),
    redirect: '/admin/users/members',
    meta: { requiresAuth: true, requiresAdmin: true },
    children: [
      {
        path: 'users/members',
        name: 'AdminMembers',
        component: () => import('@/views/admin/users/Members.vue'),
        meta: { title: '会员管理' }
      },
      {
        path: 'users/coaches',
        name: 'AdminCoaches',
        component: () => import('@/views/admin/users/Coaches.vue'),
        meta: { title: '教练管理' }
      },
      {
        path: 'users/admins',
        name: 'AdminAdmins',
        component: () => import('@/views/admin/users/Admins.vue'),
        meta: { title: '管理员管理' }
      },
      {
        path: 'courses',
        name: 'AdminCourses',
        component: () => import('@/views/admin/Courses.vue'),
        meta: { title: '课程管理' }
      },
      {
        path: 'reservations',
        name: 'AdminReservations',
        component: () => import('@/views/admin/Reservations.vue'),
        meta: { title: '预约管理' }
      },
      {
        path: 'attendance',
        name: 'AdminAttendance',
        component: () => import('@/views/admin/Attendance.vue'),
        meta: { title: '签到管理' }
      },
      {
        path: 'memberships',
        name: 'AdminMemberships',
        component: () => import('@/views/admin/Memberships.vue'),
        meta: { title: '会员卡管理' }
      },
      {
        path: 'lockers',
        name: 'AdminLockers',
        component: () => import('@/views/admin/Lockers.vue'),
        meta: { title: '储物柜管理' }
      },
      {
        path: 'roles',
        name: 'AdminRoles',
        component: () => import('@/views/admin/system/Roles.vue'),
        meta: { title: '角色管理' }
      },
      {
        path: 'permissions',
        name: 'AdminPermissions',
        component: () => import('@/views/admin/system/Permissions.vue'),
        meta: { title: '权限管理' }
      },
      {
        path: 'dashboard',
        name: 'AdminDashboard',
        component: () => import('@/views/admin/Dashboard.vue'),
        meta: { title: '数据统计' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach(async (to, from, next) => {
  const userStore = useUserStore()

  // 如果有 token 但未拉取用户信息，先拉取一次，保证刷新后角色可用
  if (userStore.isLoggedIn && !userStore.userInfo) {
    try {
      await userStore.fetchUserInfo()
    } catch (e) {
      // 拉取失败则登出并回到登录
      userStore.logout()
      next('/login')
      return
    }
  }
  
  // 检查是否需要登录
  if (to.meta.requiresAuth && !userStore.isLoggedIn) {
    next('/login')
    return
  }
  
  // 检查是否需要管理员权限
  if (to.meta.requiresAdmin) {
    const role = userStore.userInfo?.role
    if (role !== 'admin') {
      ElMessage.error('需要管理员权限')
      next('/')
      return
    }
  }
  
  // 如果已登录，访问登录/注册页则跳转到首页/后台
  if ((to.path === '/login' || to.path === '/register') && userStore.isLoggedIn) {
    const role = userStore.userInfo?.role
    if (role === 'admin') {
      next('/admin')
    } else {
      next('/home')
    }
    return
  }
  
  next()
})

export default router

