package com.cloudfitness.controller.admin;

import com.cloudfitness.common.Result;
import com.cloudfitness.service.AdminDashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/admin/dashboard")
public class AdminDashboardController {
    @Autowired
    private AdminDashboardService adminDashboardService;

    // 兼容前端调用：GET /admin/dashboard
    @GetMapping
    public Result<Map<String, Object>> getStatisticsCompat() {
        Map<String, Object> data = adminDashboardService.getStatistics();
        return Result.success(data);
    }

    @GetMapping("/statistics")
    public Result<Map<String, Object>> getStatistics() {
        Map<String, Object> data = adminDashboardService.getStatistics();
        return Result.success(data);
    }

    @GetMapping("/user-growth")
    public Result<Map<String, Object>> getUserGrowth(@RequestParam(defaultValue = "30") Integer days) {
        return Result.success(adminDashboardService.getUserGrowth(days));
    }

    @GetMapping("/course-reservations")
    public Result<Map<String, Object>> getCourseReservations() {
        return Result.success(adminDashboardService.getCourseReservations());
    }

    @GetMapping("/attendance-rate")
    public Result<Map<String, Object>> getAttendanceRate(@RequestParam(defaultValue = "7") Integer days) {
        return Result.success(adminDashboardService.getAttendanceRate(days));
    }

    @GetMapping("/membership-distribution")
    public Result<Map<String, Object>> getMembershipDistribution() {
        return Result.success(adminDashboardService.getMembershipDistribution());
    }

    @GetMapping("/course-distribution")
    public Result<Map<String, Object>> getCourseDistribution() {
        return Result.success(adminDashboardService.getCourseDistribution());
    }
}


