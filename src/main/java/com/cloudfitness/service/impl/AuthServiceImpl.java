package com.cloudfitness.service.impl;

import com.cloudfitness.entity.User;
import com.cloudfitness.mapper.UserMapper;
import com.cloudfitness.service.AuthService;
import com.cloudfitness.util.JwtUtil;
import com.cloudfitness.util.Md5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public String login(String username, String password) {
        User user = userMapper.selectByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户名或密码错误");
        }
        if (!"active".equals(user.getStatus())) {
            throw new RuntimeException("账户已被禁用");
        }
        String encryptedPassword = Md5Util.encrypt(password);
        if (!encryptedPassword.equals(user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }
        user.setLastLoginTime(LocalDateTime.now());
        userMapper.updateById(user);
        return jwtUtil.generateToken(user.getUserId(), user.getUsername(), user.getRole());
    }

    @Override
    public Integer register(String username, String phoneNumber, String password) {
        if (userMapper.selectByUsername(username) != null) {
            throw new RuntimeException("用户名已存在");
        }
        if (userMapper.selectByPhoneNumber(phoneNumber) != null) {
            throw new RuntimeException("手机号已存在");
        }
        User user = new User();
        user.setUsername(username);
        user.setPhoneNumber(phoneNumber);
        user.setPassword(Md5Util.encrypt(password));
        user.setRole("member");
        user.setStatus("active");
        user.setRegistrationDate(LocalDateTime.now());
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.insert(user);
        return user.getUserId();
    }

    @Override
    public Boolean checkUsernameExists(String username) {
        return userMapper.selectByUsername(username) != null;
    }

    @Override
    public Boolean checkPhoneExists(String phoneNumber) {
        return userMapper.selectByPhoneNumber(phoneNumber) != null;
    }
}

