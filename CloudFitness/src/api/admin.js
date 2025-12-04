import request from './request'

// 用户管理
export const getAdminMembers = (params) => {
  return request({
    url: '/admin/users/members',
    method: 'get',
    params
  })
}

export const getAdminMemberDetail = (userId) => {
  return request({
    url: `/admin/users/members/${userId}`,
    method: 'get'
  })
}

export const createAdminMember = (data) => {
  return request({
    url: '/admin/users/members',
    method: 'post',
    data
  })
}

export const updateAdminMember = (userId, data) => {
  return request({
    url: `/admin/users/members/${userId}`,
    method: 'put',
    data
  })
}

export const deleteAdminMember = (userId) => {
  return request({
    url: `/admin/users/members/${userId}`,
    method: 'delete'
  })
}

export const resetAdminMemberPassword = (userId, data) => {
  return request({
    url: `/admin/users/members/${userId}/reset-password`,
    method: 'put',
    data
  })
}

export const updateAdminMemberStatus = (userId, data) => {
  return request({
    url: `/admin/users/members/${userId}/status`,
    method: 'put',
    data
  })
}

export const getAdminCoaches = (params) => {
  return request({
    url: '/admin/users/coaches',
    method: 'get',
    params
  })
}

export const getAdminCoachDetail = (userId) => {
  return request({
    url: `/admin/users/coaches/${userId}`,
    method: 'get'
  })
}

export const createAdminCoach = (data) => {
  return request({
    url: '/admin/users/coaches',
    method: 'post',
    data
  })
}

export const updateAdminCoach = (userId, data) => {
  return request({
    url: `/admin/users/coaches/${userId}`,
    method: 'put',
    data
  })
}

export const deleteAdminCoach = (userId) => {
  return request({
    url: `/admin/users/coaches/${userId}`,
    method: 'delete'
  })
}

export const resetAdminCoachPassword = (userId, data) => {
  return request({
    url: `/admin/users/coaches/${userId}/reset-password`,
    method: 'put',
    data
  })
}

export const updateAdminCoachStatus = (userId, data) => {
  return request({
    url: `/admin/users/coaches/${userId}/status`,
    method: 'put',
    data
  })
}

export const getAdminAdmins = (params) => {
  return request({
    url: '/admin/users/admins',
    method: 'get',
    params
  })
}

export const getAdminAdminDetail = (userId) => {
  return request({
    url: `/admin/users/admins/${userId}`,
    method: 'get'
  })
}

export const createAdminAdmin = (data) => {
  return request({
    url: '/admin/users/admins',
    method: 'post',
    data
  })
}

export const updateAdminAdmin = (userId, data) => {
  return request({
    url: `/admin/users/admins/${userId}`,
    method: 'put',
    data
  })
}

export const deleteAdminAdmin = (userId) => {
  return request({
    url: `/admin/users/admins/${userId}`,
    method: 'delete'
  })
}

export const resetAdminAdminPassword = (userId, data) => {
  return request({
    url: `/admin/users/admins/${userId}/reset-password`,
    method: 'put',
    data
  })
}

export const updateAdminAdminStatus = (userId, data) => {
  return request({
    url: `/admin/users/admins/${userId}/status`,
    method: 'put',
    data
  })
}

// 课程管理
export const getAdminCourses = (params) => {
  return request({
    url: '/admin/courses',
    method: 'get',
    params
  })
}

export const getAdminCourseDetail = (courseId) => {
  return request({
    url: `/admin/courses/${courseId}`,
    method: 'get'
  })
}

export const createAdminCourse = (data) => {
  return request({
    url: '/admin/courses',
    method: 'post',
    data
  })
}

export const updateAdminCourse = (courseId, data) => {
  return request({
    url: `/admin/courses/${courseId}`,
    method: 'put',
    data
  })
}

export const deleteAdminCourse = (courseId) => {
  return request({
    url: `/admin/courses/${courseId}`,
    method: 'delete'
  })
}

// 预约管理
export const getAdminReservations = (params) => {
  return request({
    url: '/admin/reservations',
    method: 'get',
    params
  })
}

export const getAdminReservationDetail = (reservationId) => {
  return request({
    url: `/admin/reservations/${reservationId}`,
    method: 'get'
  })
}

export const createAdminReservation = (data) => {
  return request({
    url: '/admin/reservations',
    method: 'post',
    data
  })
}

export const updateAdminReservation = (reservationId, data) => {
  return request({
    url: `/admin/reservations/${reservationId}`,
    method: 'put',
    data
  })
}

export const deleteAdminReservation = (reservationId) => {
  return request({
    url: `/admin/reservations/${reservationId}`,
    method: 'delete'
  })
}

// 签到管理
export const getAdminAttendance = (params) => {
  return request({
    url: '/admin/attendance',
    method: 'get',
    params
  })
}

export const getAdminAttendanceDetail = (attendanceId) => {
  return request({
    url: `/admin/attendance/${attendanceId}`,
    method: 'get'
  })
}

export const deleteAdminAttendance = (attendanceId) => {
  return request({
    url: `/admin/attendance/${attendanceId}`,
    method: 'delete'
  })
}

// 会员卡管理
export const getAdminMemberships = (params) => {
  return request({
    url: '/admin/memberships',
    method: 'get',
    params
  })
}

export const getAdminMembershipDetail = (membershipId) => {
  return request({
    url: `/admin/memberships/${membershipId}`,
    method: 'get'
  })
}

export const createAdminMembership = (data) => {
  return request({
    url: '/admin/memberships',
    method: 'post',
    data
  })
}

export const updateAdminMembership = (membershipId, data) => {
  return request({
    url: `/admin/memberships/${membershipId}`,
    method: 'put',
    data
  })
}

export const deleteAdminMembership = (membershipId) => {
  return request({
    url: `/admin/memberships/${membershipId}`,
    method: 'delete'
  })
}

export const renewAdminMembership = (membershipId, data) => {
  return request({
    url: `/admin/memberships/${membershipId}/renew`,
    method: 'put',
    data
  })
}

// 储物柜管理
export const getAdminLockers = (params) => {
  return request({
    url: '/admin/lockers',
    method: 'get',
    params
  })
}

export const getAdminLockerDetail = (lockerId) => {
  return request({
    url: `/admin/lockers/${lockerId}`,
    method: 'get'
  })
}

export const createAdminLocker = (data) => {
  return request({
    url: '/admin/lockers',
    method: 'post',
    data
  })
}

export const updateAdminLocker = (lockerId, data) => {
  return request({
    url: `/admin/lockers/${lockerId}`,
    method: 'put',
    data
  })
}

export const deleteAdminLocker = (lockerId) => {
  return request({
    url: `/admin/lockers/${lockerId}`,
    method: 'delete'
  })
}

export const assignAdminLocker = (lockerId, data) => {
  return request({
    url: `/admin/lockers/${lockerId}/assign`,
    method: 'put',
    data
  })
}

export const releaseAdminLocker = (lockerId) => {
  return request({
    url: `/admin/lockers/${lockerId}/release`,
    method: 'put'
  })
}

// 角色管理
export const getAdminRoles = (params) => {
  return request({
    url: '/admin/roles',
    method: 'get',
    params
  })
}

export const getAdminRoleDetail = (roleId) => {
  return request({
    url: `/admin/roles/${roleId}`,
    method: 'get'
  })
}

export const createAdminRole = (data) => {
  return request({
    url: '/admin/roles',
    method: 'post',
    data
  })
}

export const updateAdminRole = (roleId, data) => {
  return request({
    url: `/admin/roles/${roleId}`,
    method: 'put',
    data
  })
}

export const deleteAdminRole = (roleId) => {
  return request({
    url: `/admin/roles/${roleId}`,
    method: 'delete'
  })
}

export const updateAdminRolePermissions = (roleId, data) => {
  return request({
    url: `/admin/roles/${roleId}/permissions`,
    method: 'put',
    data
  })
}

// 权限管理
export const getAdminPermissions = (params) => {
  return request({
    url: '/admin/permissions',
    method: 'get',
    params
  })
}

export const getAdminPermissionDetail = (permissionId) => {
  return request({
    url: `/admin/permissions/${permissionId}`,
    method: 'get'
  })
}

export const createAdminPermission = (data) => {
  return request({
    url: '/admin/permissions',
    method: 'post',
    data
  })
}

export const updateAdminPermission = (permissionId, data) => {
  return request({
    url: `/admin/permissions/${permissionId}`,
    method: 'put',
    data
  })
}

export const deleteAdminPermission = (permissionId) => {
  return request({
    url: `/admin/permissions/${permissionId}`,
    method: 'delete'
  })
}

// 仪表盘
export const getAdminDashboard = () => {
  return request({
    url: '/admin/dashboard',
    method: 'get'
  })
}

export const getAdminDashboardUserGrowth = (params) => {
  return request({
    url: '/admin/dashboard/user-growth',
    method: 'get',
    params
  })
}

export const getAdminDashboardCourseReservations = () => {
  return request({
    url: '/admin/dashboard/course-reservations',
    method: 'get'
  })
}

export const getAdminDashboardAttendanceRate = (params) => {
  return request({
    url: '/admin/dashboard/attendance-rate',
    method: 'get',
    params
  })
}

export const getAdminDashboardMembershipDistribution = () => {
  return request({
    url: '/admin/dashboard/membership-distribution',
    method: 'get'
  })
}

export const getAdminDashboardCourseDistribution = () => {
  return request({
    url: '/admin/dashboard/course-distribution',
    method: 'get'
  })
}

// 获取所有角色列表（用于下拉选择）
export const getAllRoles = () => {
  return request({
    url: '/admin/roles',
    method: 'get',
    params: { page: 1, page_size: 1000 }
  })
}

// 获取所有权限列表（用于角色权限分配）
export const getAllPermissions = () => {
  return request({
    url: '/admin/permissions',
    method: 'get',
    params: { page: 1, page_size: 1000 }
  })
}

// 获取所有会员列表（用于下拉选择）
export const getAllMembers = () => {
  return request({
    url: '/admin/users/members',
    method: 'get',
    params: { page: 1, page_size: 1000 }
  })
}

// 获取所有教练列表（用于下拉选择）
export const getAllCoaches = () => {
  return request({
    url: '/admin/users/coaches',
    method: 'get',
    params: { page: 1, page_size: 1000 }
  })
}

// 获取所有课程列表（用于下拉选择）
export const getAllCourses = () => {
  return request({
    url: '/admin/courses',
    method: 'get',
    params: { page: 1, page_size: 1000 }
  })
}

