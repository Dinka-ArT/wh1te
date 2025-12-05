package com.cloudfitness.controller;

import com.cloudfitness.common.Result;
import com.cloudfitness.entity.Attendance;
import com.cloudfitness.service.AttendanceService;
import com.cloudfitness.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/attendance")
public class AttendanceController {
    @Autowired
    private AttendanceService attendanceService;
    
    @Autowired
    private JwtUtil jwtUtil;

    private Integer getUserIdFromRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        return jwtUtil.getUserIdFromToken(token);
    }

    @GetMapping
    public Result<Map<String, List<Attendance>>> getAttendance(
            @RequestParam(required = false) String month,
            @RequestParam(required = false) Integer limit,
            HttpServletRequest request) {
        Integer userId = getUserIdFromRequest(request);
        List<Attendance> attendance = attendanceService.getAttendanceByUserId(userId, month, limit);
        Map<String, List<Attendance>> data = new HashMap<>();
        data.put("attendance", attendance);
        return Result.success(data);
    }
}


