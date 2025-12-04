package com.cloudfitness.controller.admin;

import com.cloudfitness.common.Result;
import com.cloudfitness.service.AdminAttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/admin/attendance")
public class AdminAttendanceController {
    @Autowired
    private AdminAttendanceService adminAttendanceService;

    @GetMapping
    public Result<Map<String, Object>> getAttendance(
            @RequestParam(required = false) Integer user_id,
            @RequestParam(required = false) Integer course_id,
            @RequestParam(required = false) String start_date,
            @RequestParam(required = false) String end_date,
            @RequestParam(required = false) Boolean is_on_time,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer page_size) {
        Map<String, Object> data = adminAttendanceService.getAttendance(
            user_id, course_id, start_date, end_date, is_on_time, page, page_size);
        return Result.success(data);
    }

    @GetMapping("/{attendance_id}")
    public Result<Map<String, Object>> getAttendanceDetail(@PathVariable("attendance_id") Integer attendanceId) {
        Map<String, Object> data = adminAttendanceService.getAttendanceDetail(attendanceId);
        return Result.success(data);
    }

    @DeleteMapping("/{attendance_id}")
    public Result<Object> deleteAttendance(@PathVariable("attendance_id") Integer attendanceId) {
        adminAttendanceService.deleteAttendance(attendanceId);
        return Result.success("删除成功", null);
    }

    @GetMapping("/statistics")
    public Result<Map<String, Object>> getStatistics() {
        Map<String, Object> data = adminAttendanceService.getStatistics();
        return Result.success(data);
    }
}

