package com.cloudfitness.controller.admin;

import com.cloudfitness.common.Result;
import com.cloudfitness.service.AdminUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/users")
public class AdminUserController {
    @Autowired
    private AdminUserService adminUserService;

    @GetMapping("/members")
    public Result<Map<String, Object>> getMembers(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String phone_number,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String start_date,
            @RequestParam(required = false) String end_date,
            @RequestParam(required = false) String membership_status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer page_size) {
        Map<String, Object> data = adminUserService.getMembers(
            username, phone_number, email, start_date, end_date, membership_status, page, page_size);
        return Result.success(data);
    }

    @GetMapping("/members/{user_id}")
    public Result<Map<String, Object>> getMemberDetail(@PathVariable("user_id") Integer userId) {
        Map<String, Object> data = adminUserService.getMemberDetail(userId);
        return Result.success(data);
    }

    @PostMapping("/members")
    public Result<Map<String, Integer>> createMember(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String phoneNumber = request.get("phone_number");
        String email = request.get("email");
        String password = request.get("password");
        Integer userId = adminUserService.createMember(username, phoneNumber, email, password);
        Map<String, Integer> data = Map.of("user_id", userId);
        return Result.success("创建成功", data);
    }

    @PutMapping("/members/{user_id}")
    public Result<Object> updateMember(@PathVariable("user_id") Integer userId, @RequestBody Map<String, String> request) {
        String email = request.get("email");
        String phoneNumber = request.get("phone_number");
        adminUserService.updateMember(userId, email, phoneNumber);
        return Result.success("更新成功", null);
    }

    @DeleteMapping("/members/{user_id}")
    public Result<Object> deleteMember(@PathVariable("user_id") Integer userId) {
        adminUserService.deleteMember(userId);
        return Result.success("删除成功", null);
    }

    @PutMapping("/members/{user_id}/reset-password")
    public Result<Object> resetMemberPassword(@PathVariable("user_id") Integer userId, @RequestBody Map<String, String> request) {
        String newPassword = request.get("new_password");
        adminUserService.resetMemberPassword(userId, newPassword);
        return Result.success("密码重置成功", null);
    }

    @PutMapping("/members/{user_id}/status")
    public Result<Object> updateMemberStatus(@PathVariable("user_id") Integer userId, @RequestBody Map<String, String> request) {
        String status = request.get("status");
        adminUserService.updateMemberStatus(userId, status);
        return Result.success("状态更新成功", null);
    }

    @GetMapping("/coaches")
    public Result<Map<String, Object>> getCoaches(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String phone_number,
            @RequestParam(required = false) String start_date,
            @RequestParam(required = false) String end_date,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer page_size) {
        Map<String, Object> data = adminUserService.getCoaches(
            username, phone_number, start_date, end_date, status, page, page_size);
        return Result.success(data);
    }

    @GetMapping("/coaches/{user_id}")
    public Result<Map<String, Object>> getCoachDetail(@PathVariable("user_id") Integer userId) {
        Map<String, Object> data = adminUserService.getCoachDetail(userId);
        return Result.success(data);
    }

    @PostMapping("/coaches")
    public Result<Map<String, Integer>> createCoach(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String phoneNumber = request.get("phone_number");
        String email = request.get("email");
        String password = request.get("password");
        Integer userId = adminUserService.createCoach(username, phoneNumber, email, password);
        Map<String, Integer> data = Map.of("user_id", userId);
        return Result.success("创建成功", data);
    }

    @PutMapping("/coaches/{user_id}")
    public Result<Object> updateCoach(@PathVariable("user_id") Integer userId, @RequestBody Map<String, String> request) {
        String email = request.get("email");
        adminUserService.updateCoach(userId, email);
        return Result.success("更新成功", null);
    }

    @DeleteMapping("/coaches/{user_id}")
    public Result<Object> deleteCoach(@PathVariable("user_id") Integer userId) {
        adminUserService.deleteCoach(userId);
        return Result.success("删除成功", null);
    }

    @PutMapping("/coaches/{user_id}/reset-password")
    public Result<Object> resetCoachPassword(@PathVariable("user_id") Integer userId, @RequestBody Map<String, String> request) {
        String newPassword = request.get("new_password");
        adminUserService.resetCoachPassword(userId, newPassword);
        return Result.success("密码重置成功", null);
    }

    @PutMapping("/coaches/{user_id}/status")
    public Result<Object> updateCoachStatus(@PathVariable("user_id") Integer userId, @RequestBody Map<String, String> request) {
        String status = request.get("status");
        adminUserService.updateCoachStatus(userId, status);
        return Result.success("状态更新成功", null);
    }

    @GetMapping("/admins")
    public Result<Map<String, Object>> getAdmins(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String phone_number,
            @RequestParam(required = false) String start_date,
            @RequestParam(required = false) String end_date,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer page_size) {
        Map<String, Object> data = adminUserService.getAdmins(
            username, phone_number, start_date, end_date, status, page, page_size);
        return Result.success(data);
    }

    @GetMapping("/admins/{user_id}")
    public Result<Map<String, Object>> getAdminDetail(@PathVariable("user_id") Integer userId) {
        Map<String, Object> data = adminUserService.getAdminDetail(userId);
        return Result.success(data);
    }

    @PostMapping("/admins")
    public Result<Map<String, Integer>> createAdmin(@RequestBody Map<String, Object> request) {
        String username = (String) request.get("username");
        String phoneNumber = (String) request.get("phone_number");
        String email = (String) request.get("email");
        String password = (String) request.get("password");
        @SuppressWarnings("unchecked")
        List<Integer> roleIds = (List<Integer>) request.get("role_ids");
        Integer userId = adminUserService.createAdmin(username, phoneNumber, email, password, roleIds);
        Map<String, Integer> data = Map.of("user_id", userId);
        return Result.success("创建成功", data);
    }

    @PutMapping("/admins/{user_id}")
    public Result<Object> updateAdmin(@PathVariable("user_id") Integer userId, @RequestBody Map<String, Object> request) {
        String email = (String) request.get("email");
        @SuppressWarnings("unchecked")
        List<Integer> roleIds = (List<Integer>) request.get("role_ids");
        adminUserService.updateAdmin(userId, email, roleIds);
        return Result.success("更新成功", null);
    }

    @DeleteMapping("/admins/{user_id}")
    public Result<Object> deleteAdmin(@PathVariable("user_id") Integer userId) {
        adminUserService.deleteAdmin(userId);
        return Result.success("删除成功", null);
    }

    @PutMapping("/admins/{user_id}/reset-password")
    public Result<Object> resetAdminPassword(@PathVariable("user_id") Integer userId, @RequestBody Map<String, String> request) {
        String newPassword = request.get("new_password");
        adminUserService.resetAdminPassword(userId, newPassword);
        return Result.success("密码重置成功", null);
    }

    @PutMapping("/admins/{user_id}/status")
    public Result<Object> updateAdminStatus(@PathVariable("user_id") Integer userId, @RequestBody Map<String, String> request) {
        String status = request.get("status");
        adminUserService.updateAdminStatus(userId, status);
        return Result.success("状态更新成功", null);
    }
}

