package com.cloudfitness.service;

import com.cloudfitness.entity.User;

public interface UserService {
    User getUserInfo(Integer userId);
    void updateProfile(Integer userId, String email, String phoneNumber);
    void changePassword(Integer userId, String oldPassword, String newPassword);
}


