package com.cloudfitness.service;

import com.cloudfitness.entity.User;
import java.util.List;
import java.util.Map;

public interface AdminUserService {
    Map<String, Object> getMembers(String username, String phoneNumber, String email, 
                                   String startDate, String endDate, String membershipStatus,
                                   Integer page, Integer pageSize);
    Map<String, Object> getMemberDetail(Integer userId);
    Integer createMember(String username, String phoneNumber, String email, String password);
    void updateMember(Integer userId, String email, String phoneNumber);
    void deleteMember(Integer userId);
    void resetMemberPassword(Integer userId, String newPassword);
    void updateMemberStatus(Integer userId, String status);
    
    Map<String, Object> getCoaches(String username, String phoneNumber, 
                                  String startDate, String endDate, String status,
                                  Integer page, Integer pageSize);
    Map<String, Object> getCoachDetail(Integer userId);
    Integer createCoach(String username, String phoneNumber, String email, String password, String description);
    void updateCoach(Integer userId, String email, String description);
    void deleteCoach(Integer userId);
    void resetCoachPassword(Integer userId, String newPassword);
    void updateCoachStatus(Integer userId, String status);
    
    Map<String, Object> getAdmins(String username, String phoneNumber,
                                   String startDate, String endDate, String status,
                                   Integer page, Integer pageSize);
    Map<String, Object> getAdminDetail(Integer userId);
    Integer createAdmin(String username, String phoneNumber, String email, String password, List<Integer> roleIds);
    void updateAdmin(Integer userId, String email, List<Integer> roleIds);
    void deleteAdmin(Integer userId);
    void resetAdminPassword(Integer userId, String newPassword);
    void updateAdminStatus(Integer userId, String status);
}


