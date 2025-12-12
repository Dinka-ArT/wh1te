package com.cloudfitness.controller;

import com.cloudfitness.common.Result;
import com.cloudfitness.service.CoachReservationService;
import com.cloudfitness.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/coach/reservations")
public class CoachReservationController {

    @Autowired
    private CoachReservationService coachReservationService;

    @Autowired
    private JwtUtil jwtUtil;

    private Integer getCoachIdFromRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        return jwtUtil.getUserIdFromToken(token);
    }

    @GetMapping
    public Result<Map<String, Object>> getReservations(HttpServletRequest request) {
        Integer coachId = getCoachIdFromRequest(request);
        Map<String, Object> data = coachReservationService.getReservationsForCoach(coachId);
        return Result.success(data);
    }

    @PostMapping("/{reservationId}/check-in")
    public Result<Object> checkIn(@PathVariable Integer reservationId, HttpServletRequest request) {
        Integer coachId = getCoachIdFromRequest(request);
        coachReservationService.checkIn(reservationId, coachId);
        return Result.success("签到成功", null);
    }
}

