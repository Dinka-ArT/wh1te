package com.cloudfitness.controller;

import com.cloudfitness.common.Result;
import com.cloudfitness.entity.Course;
import com.cloudfitness.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/courses")
public class CourseController {
    @Autowired
    private CourseService courseService;

    @GetMapping
    public Result<Map<String, List<Course>>> getCourses() {
        List<Course> courses = courseService.getUpcomingCourses();
        Map<String, List<Course>> data = new HashMap<>();
        data.put("courses", courses);
        return Result.success(data);
    }
}

