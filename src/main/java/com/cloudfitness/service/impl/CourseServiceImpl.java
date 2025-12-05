package com.cloudfitness.service.impl;

import com.cloudfitness.entity.Course;
import com.cloudfitness.mapper.CourseMapper;
import com.cloudfitness.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CourseServiceImpl implements CourseService {
    @Autowired
    private CourseMapper courseMapper;

    @Override
    public List<Course> getUpcomingCourses() {
        return courseMapper.selectUpcomingCourses();
    }

    @Override
    public Course getCourseById(Integer courseId) {
        return courseMapper.selectById(courseId);
    }
}


