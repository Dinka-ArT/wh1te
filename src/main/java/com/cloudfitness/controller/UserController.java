package com.cloudfitness.controller;

import com.cloudfitness.common.Result;
import com.cloudfitness.entity.User;
import com.cloudfitness.service.UserService;
import com.cloudfitness.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtUtil jwtUtil;

    private Integer getUserIdFromRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        return jwtUtil.getUserIdFromToken(token);
    }

    @GetMapping("/info")
    public Result<Map<String, Object>> getUserInfo(HttpServletRequest request) {
        Integer userId = getUserIdFromRequest(request);
        User user = userService.getUserInfo(userId);
        Map<String, Object> data = new HashMap<>();
        data.put("user_id", user.getUserId());
        data.put("username", user.getUsername());
        data.put("email", user.getEmail());
        data.put("phone_number", user.getPhoneNumber());
        data.put("role", user.getRole());
        data.put("registration_date", user.getRegistrationDate());
        return Result.success(data);
    }

    @PutMapping("/profile")
    public Result<Object> updateProfile(@RequestBody Map<String, String> request, HttpServletRequest httpRequest) {
        Integer userId = getUserIdFromRequest(httpRequest);
        String email = request.get("email");
        String phoneNumber = request.get("phone_number");
        userService.updateProfile(userId, email, phoneNumber);
        return Result.success("更新成功", null);
    }

    @PutMapping("/password")
    public Result<Object> changePassword(@RequestBody Map<String, String> request, HttpServletRequest httpRequest) {
        Integer userId = getUserIdFromRequest(httpRequest);
        String oldPassword = request.get("old_password");
        String newPassword = request.get("new_password");
        userService.changePassword(userId, oldPassword, newPassword);
        return Result.success("密码修改成功", null);
    }
}


