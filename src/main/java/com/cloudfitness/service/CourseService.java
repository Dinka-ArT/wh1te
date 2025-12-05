package com.cloudfitness.service;

import com.cloudfitness.entity.Course;
import java.time.LocalDateTime;
import java.util.List;

public interface CourseService {
    List<Course> getUpcomingCourses();
    Course getCourseById(Integer courseId);
}


