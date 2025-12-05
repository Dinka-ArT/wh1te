package com.cloudfitness.controller.admin;

import com.cloudfitness.common.Result;
import com.cloudfitness.service.AdminLockerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/admin/lockers")
public class AdminLockerController {
    @Autowired
    private AdminLockerService adminLockerService;

    @GetMapping
    public Result<Map<String, Object>> getLockers(
            @RequestParam(required = false) Integer locker_number,
            @RequestParam(required = false) Integer user_id,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer page_size) {
        Map<String, Object> data = adminLockerService.getLockers(
            locker_number, user_id, status, page, page_size);
        return Result.success(data);
    }

    @GetMapping("/{locker_id}")
    public Result<Map<String, Object>> getLockerDetail(@PathVariable("locker_id") Integer lockerId) {
        Map<String, Object> data = adminLockerService.getLockerDetail(lockerId);
        return Result.success(data);
    }

    @PostMapping
    public Result<Map<String, Integer>> createLocker(@RequestBody Map<String, Object> request) {
        Integer lockerNumber = request.get("locker_number") instanceof Number ? ((Number) request.get("locker_number")).intValue() : null;
        Integer lockerId = adminLockerService.createLocker(lockerNumber);
        Map<String, Integer> data = Map.of("locker_id", lockerId);
        return Result.success("创建成功", data);
    }

    @PutMapping("/{locker_id}/assign")
    public Result<Object> assignLocker(@PathVariable("locker_id") Integer lockerId, @RequestBody Map<String, Object> request) {
        Integer userId = (Integer) request.get("user_id");
        String endDate = (String) request.get("end_date");
        adminLockerService.assignLocker(lockerId, userId, endDate);
        return Result.success("分配成功", null);
    }

    @PutMapping("/{locker_id}")
    public Result<Object> updateLocker(@PathVariable("locker_id") Integer lockerId, @RequestBody Map<String, Object> request) {
        Integer lockerNumber = (Integer) request.get("locker_number");
        Integer userId = (Integer) request.get("user_id");
        String endDate = (String) request.get("end_date");
        adminLockerService.updateLocker(lockerId, lockerNumber, userId, endDate);
        return Result.success("更新成功", null);
    }

    @PutMapping("/{locker_id}/release")
    public Result<Object> releaseLocker(@PathVariable("locker_id") Integer lockerId) {
        adminLockerService.releaseLocker(lockerId);
        return Result.success("释放成功", null);
    }

    @DeleteMapping("/{locker_id}")
    public Result<Object> deleteLocker(@PathVariable("locker_id") Integer lockerId) {
        adminLockerService.deleteLocker(lockerId);
        return Result.success("删除成功", null);
    }
}


