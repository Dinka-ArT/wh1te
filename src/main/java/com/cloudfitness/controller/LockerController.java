package com.cloudfitness.controller;

import com.cloudfitness.common.Result;
import com.cloudfitness.entity.Locker;
import com.cloudfitness.service.LockerService;
import com.cloudfitness.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/lockers")
public class LockerController {
    @Autowired
    private LockerService lockerService;
    
    @Autowired
    private JwtUtil jwtUtil;

    private Integer getUserIdFromRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        return jwtUtil.getUserIdFromToken(token);
    }

    @GetMapping
    public Result<Map<String, List<Locker>>> getLockers(HttpServletRequest request) {
        Integer userId = getUserIdFromRequest(request);
        List<Locker> lockers = lockerService.getLockersByUserId(userId);
        Map<String, List<Locker>> data = new HashMap<>();
        data.put("lockers", lockers);
        return Result.success(data);
    }
}

