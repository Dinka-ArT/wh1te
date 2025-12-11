package com.cloudfitness.service.impl;

import com.cloudfitness.entity.User;
import com.cloudfitness.mapper.UserMapper;
import com.cloudfitness.service.AuthService;
import com.cloudfitness.util.JwtUtil;
import com.cloudfitness.util.Md5Util;
import org.springframework.beans.factory.annotation.Autowired;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${wx.appid}")
    private String appid;

    @Value("${wx.appsecret}")
    private String appsecret;

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

    @Override
    public String wxLogin(String code) {
        String url = "https://api.weixin.qq.com/sns/jscode2session?appid=" + appid +
                     "&secret=" + appsecret + "&js_code=" + code + "&grant_type=authorization_code";

        String jsonResponse = restTemplate.getForObject(url, String.class);
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> responseMap;
        try {
            responseMap = objectMapper.readValue(jsonResponse, Map.class);
        } catch (IOException e) {
            throw new RuntimeException("解析微信响应失败", e);
        }

        String openid = responseMap.get("openid");
        if (openid == null) {
            throw new RuntimeException("微信登录失败: " + responseMap.get("errmsg"));
        }

        User user = userMapper.selectByOpenid(openid);
        if (user == null) {
            // 用户不存在，自动注册
            user = new User();
            user.setOpenid(openid);
            // 微信小程序登录默认角色为 member
            user.setRole("member");
            user.setStatus("active");
            // 初始用户名可以设置为 "微信用户" + 部分 openid 或随机数
            user.setUsername("wx_" + openid.substring(openid.length() - 6));
            user.setRegistrationDate(LocalDateTime.now());
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());
            userMapper.insert(user);
        }

        user.setLastLoginTime(LocalDateTime.now());
        userMapper.updateById(user);

        return jwtUtil.generateToken(user.getUserId(), user.getUsername(), user.getRole());
    }
}


