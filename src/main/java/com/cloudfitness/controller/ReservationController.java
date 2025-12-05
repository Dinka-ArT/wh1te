package com.cloudfitness.controller;

import com.cloudfitness.common.Result;
import com.cloudfitness.entity.Reservation;
import com.cloudfitness.service.ReservationService;
import com.cloudfitness.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/reservations")
public class ReservationController {
    @Autowired
    private ReservationService reservationService;
    
    @Autowired
    private JwtUtil jwtUtil;

    private Integer getUserIdFromRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        return jwtUtil.getUserIdFromToken(token);
    }

    @GetMapping
    public Result<Map<String, List<Reservation>>> getReservations(HttpServletRequest request) {
        Integer userId = getUserIdFromRequest(request);
        List<Reservation> reservations = reservationService.getReservationsByUserId(userId);
        Map<String, List<Reservation>> data = new HashMap<>();
        data.put("reservations", reservations);
        return Result.success(data);
    }

    @PostMapping
    public Result<Map<String, Integer>> createReservation(@RequestBody Map<String, Integer> request, HttpServletRequest httpRequest) {
        Integer userId = getUserIdFromRequest(httpRequest);
        Integer courseId = request.get("course_id");
        Integer reservationId = reservationService.createReservation(userId, courseId);
        Map<String, Integer> data = new HashMap<>();
        data.put("reservation_id", reservationId);
        return Result.success("预约成功", data);
    }

    @DeleteMapping("/{reservation_id}")
    public Result<Object> cancelReservation(@PathVariable("reservation_id") Integer reservationId, HttpServletRequest request) {
        Integer userId = getUserIdFromRequest(request);
        reservationService.cancelReservation(reservationId, userId);
        return Result.success("取消成功", null);
    }
}


