package com.cloudfitness.service;

import com.cloudfitness.entity.Course;
import java.util.List;
import java.util.Map;

public interface AdminCourseService {
    Map<String, Object> getCourses(String courseName, Integer instructorId,
                                   String startDate, String endDate, String status,
                                   Integer page, Integer pageSize);
    Map<String, Object> getCourseDetail(Integer courseId);
    Integer createCourse(String courseName, Integer instructorId, String schedule,
                        Integer capacity, String description);
    void updateCourse(Integer courseId, String courseName, Integer instructorId,
                     String schedule, Integer capacity, String description);
    void deleteCourse(Integer courseId);
    void updateCourseStatus(Integer courseId, String status);
}


