import request from './request'

export const getCourses = (params) => {
  return request({
    url: '/courses',
    method: 'get',
    params
  })
}

export const getCourseDetail = (courseId) => {
  return request({
    url: `/courses/${courseId}`,
    method: 'get'
  })
}

export const reserveCourse = (courseId) => {
  return request({
    url: '/reservations',
    method: 'post',
    data: { course_id: courseId }
  })
}

