package com.cloudfitness.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("users")
public class User {
    @TableId(type = IdType.AUTO)
    private Integer userId;
    private String username;
    private String password;
    private String email;
    private String phoneNumber;
    private String role;
    private String status;
    private LocalDateTime lastLoginTime;
    private LocalDateTime registrationDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String description; // 教练/用户简介
    private String openid;
}


