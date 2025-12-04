package com.cloudfitness.service;

import com.cloudfitness.entity.User;

public interface AuthService {
    String login(String username, String password);
    Integer register(String username, String phoneNumber, String password);
    Boolean checkUsernameExists(String username);
    Boolean checkPhoneExists(String phoneNumber);
}

