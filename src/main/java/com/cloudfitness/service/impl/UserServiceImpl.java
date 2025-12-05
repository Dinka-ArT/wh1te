package com.cloudfitness.service.impl;

import com.cloudfitness.entity.User;
import com.cloudfitness.mapper.UserMapper;
import com.cloudfitness.service.UserService;
import com.cloudfitness.util.Md5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public User getUserInfo(Integer userId) {
        return userMapper.selectById(userId);
    }

    @Override
    public void updateProfile(Integer userId, String email, String phoneNumber) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (email != null && !email.isEmpty()) {
            User existingUser = userMapper.selectByEmail(email);
            if (existingUser != null && !existingUser.getUserId().equals(userId)) {
                throw new RuntimeException("邮箱已被使用");
            }
            user.setEmail(email);
        }
        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            User existingUser = userMapper.selectByPhoneNumber(phoneNumber);
            if (existingUser != null && !existingUser.getUserId().equals(userId)) {
                throw new RuntimeException("手机号已被使用");
            }
            user.setPhoneNumber(phoneNumber);
        }
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(user);
    }

    @Override
    public void changePassword(Integer userId, String oldPassword, String newPassword) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        String encryptedOldPassword = Md5Util.encrypt(oldPassword);
        if (!encryptedOldPassword.equals(user.getPassword())) {
            throw new RuntimeException("原密码错误");
        }
        user.setPassword(Md5Util.encrypt(newPassword));
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(user);
    }
}


