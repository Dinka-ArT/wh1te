import request from '../utils/request.js'

export const getCourses = (params) => {
  return request({
    url: '/courses',
    method: 'GET',
    data: params || {}
  })
}

export const getCourseDetail = (courseId) => {
  return request({
    url: `/courses/${courseId}`,
    method: 'GET'
  })
}

export const reserveCourse = (courseId) => {
  return request({
    url: '/reservations',
    method: 'POST',
    data: { course_id: courseId }
  })
}

