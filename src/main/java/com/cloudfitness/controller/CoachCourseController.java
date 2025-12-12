package com.cloudfitness.controller;

import com.cloudfitness.common.Result;
import com.cloudfitness.service.CoachCourseService;
import com.cloudfitness.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/coach/courses")
public class CoachCourseController {

    @Autowired
    private CoachCourseService coachCourseService;

    @Autowired
    private JwtUtil jwtUtil;

    private Integer getCoachIdFromRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        // 在实际应用中，这里可能还需要校验角色是否为 coach
        return jwtUtil.getUserIdFromToken(token);
    }

    @GetMapping
    public Result<Map<String, Object>> getMyCourses(HttpServletRequest request) {
        Integer coachId = getCoachIdFromRequest(request);
        Map<String, Object> data = coachCourseService.getCoursesByCoachId(coachId);
        return Result.success(data);
    }

    @PostMapping
    public Result<Map<String, Integer>> createCourse(@RequestBody Map<String, Object> payload, HttpServletRequest request) {
        Integer coachId = getCoachIdFromRequest(request);
        Integer courseId = coachCourseService.createCourse(coachId, payload);
        Map<String, Integer> data = Map.of("course_id", courseId);
        return Result.success("创建成功", data);
    }

    @PutMapping("/{courseId}")
    public Result<Object> updateCourse(@PathVariable Integer courseId, @RequestBody Map<String, Object> payload, HttpServletRequest request) {
        Integer coachId = getCoachIdFromRequest(request);
        coachCourseService.updateCourse(courseId, coachId, payload);
        return Result.success("更新成功", null);
    }

    @DeleteMapping("/{courseId}")
    public Result<Object> deleteCourse(@PathVariable Integer courseId, HttpServletRequest request) {
        Integer coachId = getCoachIdFromRequest(request);
        coachCourseService.deleteCourse(courseId, coachId);
        return Result.success("删除成功", null);
    }

    @GetMapping("/{courseId}")
    public Result<com.cloudfitness.entity.Course> getCourseDetail(@PathVariable Integer courseId, HttpServletRequest request) {
        Integer coachId = getCoachIdFromRequest(request);
        com.cloudfitness.entity.Course course = coachCourseService.getCourseDetail(courseId, coachId);
        return Result.success(course);
    }
}

