package com.cloudfitness.controller;

import com.cloudfitness.common.Result;
import com.cloudfitness.service.AuthService;
import com.cloudfitness.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthService authService;
    
    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");
        String token = authService.login(username, password);
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        String role = jwtUtil.getRoleFromToken(token);
        Integer userId = jwtUtil.getUserIdFromToken(token);
        data.put("user_id", userId);
        data.put("username", username);
        data.put("role", role);
        return Result.success(data);
    }

    @PostMapping("/register")
    public Result<Map<String, Object>> register(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String phoneNumber = request.get("phone_number");
        String password = request.get("password");
        Integer userId = authService.register(username, phoneNumber, password);
        Map<String, Object> data = new HashMap<>();
        data.put("user_id", userId);
        return Result.success("注册成功", data);
    }

    @PostMapping("/check-username")
    public Result<Map<String, Boolean>> checkUsername(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        Boolean exists = authService.checkUsernameExists(username);
        Map<String, Boolean> data = new HashMap<>();
        data.put("exists", exists);
        return Result.success(data);
    }

    @PostMapping("/check-phone")
    public Result<Map<String, Boolean>> checkPhone(@RequestBody Map<String, String> request) {
        String phoneNumber = request.get("phone_number");
        Boolean exists = authService.checkPhoneExists(phoneNumber);
        Map<String, Boolean> data = new HashMap<>();
        data.put("exists", exists);
        return Result.success(data);
    }
}


