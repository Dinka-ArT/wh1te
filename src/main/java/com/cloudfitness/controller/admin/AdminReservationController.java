package com.cloudfitness.controller.admin;

import com.cloudfitness.common.Result;
import com.cloudfitness.service.AdminReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/admin/reservations")
public class AdminReservationController {
    @Autowired
    private AdminReservationService adminReservationService;

    @GetMapping
    public Result<Map<String, Object>> getReservations(
            @RequestParam(required = false) Integer user_id,
            @RequestParam(required = false) Integer course_id,
            @RequestParam(required = false) String start_date,
            @RequestParam(required = false) String end_date,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer page_size) {
        Map<String, Object> data = adminReservationService.getReservations(
            user_id, course_id, start_date, end_date, status, page, page_size);
        return Result.success(data);
    }

    @GetMapping("/{reservation_id}")
    public Result<Map<String, Object>> getReservationDetail(@PathVariable("reservation_id") Integer reservationId) {
        Map<String, Object> data = adminReservationService.getReservationDetail(reservationId);
        return Result.success(data);
    }

    @PutMapping("/{reservation_id}/confirm")
    public Result<Object> confirmReservation(@PathVariable("reservation_id") Integer reservationId) {
        adminReservationService.confirmReservation(reservationId);
        return Result.success("确认成功", null);
    }

    @PutMapping("/{reservation_id}/cancel")
    public Result<Object> cancelReservation(@PathVariable("reservation_id") Integer reservationId) {
        adminReservationService.cancelReservation(reservationId);
        return Result.success("取消成功", null);
    }

    @PutMapping("/{reservation_id}/complete")
    public Result<Object> completeReservation(@PathVariable("reservation_id") Integer reservationId) {
        adminReservationService.completeReservation(reservationId);
        return Result.success("标记成功", null);
    }

    @DeleteMapping("/{reservation_id}")
    public Result<Object> deleteReservation(@PathVariable("reservation_id") Integer reservationId) {
        adminReservationService.deleteReservation(reservationId);
        return Result.success("删除成功", null);
    }
}

