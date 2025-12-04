package com.cloudfitness.controller.admin;

import com.cloudfitness.common.Result;
import com.cloudfitness.service.AdminCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/admin/courses")
public class AdminCourseController {
    @Autowired
    private AdminCourseService adminCourseService;

    @GetMapping
    public Result<Map<String, Object>> getCourses(
            @RequestParam(required = false) String course_name,
            @RequestParam(required = false) Integer instructor_id,
            @RequestParam(required = false) String start_date,
            @RequestParam(required = false) String end_date,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer page_size) {
        Map<String, Object> data = adminCourseService.getCourses(
            course_name, instructor_id, start_date, end_date, status, page, page_size);
        return Result.success(data);
    }

    @GetMapping("/{course_id}")
    public Result<Map<String, Object>> getCourseDetail(@PathVariable("course_id") Integer courseId) {
        Map<String, Object> data = adminCourseService.getCourseDetail(courseId);
        return Result.success(data);
    }

    @PostMapping
    public Result<Map<String, Integer>> createCourse(@RequestBody Map<String, Object> request) {
        String courseName = (String) request.get("course_name");
        Integer instructorId = (Integer) request.get("instructor_id");
        String schedule = (String) request.get("schedule");
        Integer capacity = (Integer) request.get("capacity");
        String description = (String) request.get("description");
        Integer courseId = adminCourseService.createCourse(courseName, instructorId, schedule, capacity, description);
        Map<String, Integer> data = Map.of("course_id", courseId);
        return Result.success("创建成功", data);
    }

    @PutMapping("/{course_id}")
    public Result<Object> updateCourse(@PathVariable("course_id") Integer courseId, @RequestBody Map<String, Object> request) {
        String courseName = (String) request.get("course_name");
        Integer instructorId = (Integer) request.get("instructor_id");
        String schedule = (String) request.get("schedule");
        Integer capacity = (Integer) request.get("capacity");
        String description = (String) request.get("description");
        adminCourseService.updateCourse(courseId, courseName, instructorId, schedule, capacity, description);
        return Result.success("更新成功", null);
    }

    @DeleteMapping("/{course_id}")
    public Result<Object> deleteCourse(@PathVariable("course_id") Integer courseId) {
        adminCourseService.deleteCourse(courseId);
        return Result.success("删除成功", null);
    }

    @PutMapping("/{course_id}/status")
    public Result<Object> updateCourseStatus(@PathVariable("course_id") Integer courseId, @RequestBody Map<String, String> request) {
        String status = request.get("status");
        adminCourseService.updateCourseStatus(courseId, status);
        return Result.success("状态更新成功", null);
    }
}

