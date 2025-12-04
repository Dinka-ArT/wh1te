package com.cloudfitness.service.impl;

import com.cloudfitness.entity.Membership;
import com.cloudfitness.entity.User;
import com.cloudfitness.mapper.MembershipMapper;
import com.cloudfitness.mapper.UserMapper;
import com.cloudfitness.service.AdminUserService;
import com.cloudfitness.util.Md5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminUserServiceImpl implements AdminUserService {
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private MembershipMapper membershipMapper;

    @Override
    public Map<String, Object> getMembers(String username, String phoneNumber, String email,
                                         String startDate, String endDate, String membershipStatus,
                                         Integer page, Integer pageSize) {
        Integer offset = (page - 1) * pageSize;
        List<User> users = userMapper.selectMembers(username, phoneNumber, email, startDate, endDate, membershipStatus, offset, pageSize);
        Integer total = userMapper.countMembers(username, phoneNumber, email, startDate, endDate, membershipStatus);
        
        Map<String, Object> result = new HashMap<>();
        result.put("list", users);
        result.put("total", total);
        result.put("page", page);
        result.put("page_size", pageSize);
        return result;
    }

    @Override
    public Map<String, Object> getMemberDetail(Integer userId) {
        User user = userMapper.selectById(userId);
        Membership membership = membershipMapper.selectCurrentByUserId(userId);
        Map<String, Object> result = new HashMap<>();
        result.put("user_id", user.getUserId());
        result.put("username", user.getUsername());
        result.put("phone_number", user.getPhoneNumber());
        result.put("email", user.getEmail());
        result.put("role", user.getRole());
        result.put("registration_date", user.getRegistrationDate());
        if (membership != null) {
            result.put("membership", membership);
        }
        return result;
    }

    @Override
    public Integer createMember(String username, String phoneNumber, String email, String password) {
        if (userMapper.selectByUsername(username) != null) {
            throw new RuntimeException("用户名已存在");
        }
        if (userMapper.selectByPhoneNumber(phoneNumber) != null) {
            throw new RuntimeException("手机号已存在");
        }
        User user = new User();
        user.setUsername(username);
        user.setPhoneNumber(phoneNumber);
        user.setEmail(email);
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
    public void updateMember(Integer userId, String email, String phoneNumber) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (email != null) {
            user.setEmail(email);
        }
        if (phoneNumber != null) {
            user.setPhoneNumber(phoneNumber);
        }
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(user);
    }

    @Override
    public void deleteMember(Integer userId) {
        userMapper.deleteById(userId);
    }

    @Override
    public void resetMemberPassword(Integer userId, String newPassword) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        user.setPassword(Md5Util.encrypt(newPassword));
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(user);
    }

    @Override
    public void updateMemberStatus(Integer userId, String status) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        user.setStatus(status);
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(user);
    }

    @Override
    public Map<String, Object> getCoaches(String username, String phoneNumber,
                                         String startDate, String endDate, String status,
                                         Integer page, Integer pageSize) {
        Integer offset = (page - 1) * pageSize;
        List<User> users = userMapper.selectCoaches(username, phoneNumber, startDate, endDate, status, offset, pageSize);
        Integer total = userMapper.countCoaches(username, phoneNumber, startDate, endDate, status);
        
        Map<String, Object> result = new HashMap<>();
        result.put("list", users);
        result.put("total", total);
        result.put("page", page);
        result.put("page_size", pageSize);
        return result;
    }

    @Override
    public Map<String, Object> getCoachDetail(Integer userId) {
        User user = userMapper.selectById(userId);
        Map<String, Object> result = new HashMap<>();
        result.put("user_id", user.getUserId());
        result.put("username", user.getUsername());
        result.put("phone_number", user.getPhoneNumber());
        result.put("email", user.getEmail());
        result.put("role", user.getRole());
        result.put("registration_date", user.getRegistrationDate());
        return result;
    }

    @Override
    public Integer createCoach(String username, String phoneNumber, String email, String password) {
        if (userMapper.selectByUsername(username) != null) {
            throw new RuntimeException("用户名已存在");
        }
        if (userMapper.selectByPhoneNumber(phoneNumber) != null) {
            throw new RuntimeException("手机号已存在");
        }
        User user = new User();
        user.setUsername(username);
        user.setPhoneNumber(phoneNumber);
        user.setEmail(email);
        user.setPassword(Md5Util.encrypt(password));
        user.setRole("coach");
        user.setStatus("active");
        user.setRegistrationDate(LocalDateTime.now());
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.insert(user);
        return user.getUserId();
    }

    @Override
    public void updateCoach(Integer userId, String email) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (email != null) {
            user.setEmail(email);
        }
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(user);
    }

    @Override
    public void deleteCoach(Integer userId) {
        userMapper.deleteById(userId);
    }

    @Override
    public void resetCoachPassword(Integer userId, String newPassword) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        user.setPassword(Md5Util.encrypt(newPassword));
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(user);
    }

    @Override
    public void updateCoachStatus(Integer userId, String status) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        user.setStatus(status);
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(user);
    }

    @Override
    public Map<String, Object> getAdmins(String username, String phoneNumber,
                                         String startDate, String endDate, String status,
                                         Integer page, Integer pageSize) {
        Integer offset = (page - 1) * pageSize;
        List<User> users = userMapper.selectAdmins(username, phoneNumber, startDate, endDate, status, offset, pageSize);
        Integer total = userMapper.countAdmins(username, phoneNumber, startDate, endDate, status);
        
        Map<String, Object> result = new HashMap<>();
        result.put("list", users);
        result.put("total", total);
        result.put("page", page);
        result.put("page_size", pageSize);
        return result;
    }

    @Override
    public Map<String, Object> getAdminDetail(Integer userId) {
        User user = userMapper.selectById(userId);
        Map<String, Object> result = new HashMap<>();
        result.put("user_id", user.getUserId());
        result.put("username", user.getUsername());
        result.put("phone_number", user.getPhoneNumber());
        result.put("email", user.getEmail());
        result.put("role", user.getRole());
        result.put("registration_date", user.getRegistrationDate());
        result.put("last_login_time", user.getLastLoginTime());
        result.put("status", user.getStatus());
        return result;
    }

    @Override
    public Integer createAdmin(String username, String phoneNumber, String email, String password, List<Integer> roleIds) {
        if (userMapper.selectByUsername(username) != null) {
            throw new RuntimeException("用户名已存在");
        }
        if (userMapper.selectByPhoneNumber(phoneNumber) != null) {
            throw new RuntimeException("手机号已存在");
        }
        User user = new User();
        user.setUsername(username);
        user.setPhoneNumber(phoneNumber);
        user.setEmail(email);
        user.setPassword(Md5Util.encrypt(password));
        user.setRole("admin");
        user.setStatus("active");
        user.setRegistrationDate(LocalDateTime.now());
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.insert(user);
        return user.getUserId();
    }

    @Override
    public void updateAdmin(Integer userId, String email, List<Integer> roleIds) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (email != null) {
            user.setEmail(email);
        }
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(user);
    }

    @Override
    public void deleteAdmin(Integer userId) {
        userMapper.deleteById(userId);
    }

    @Override
    public void resetAdminPassword(Integer userId, String newPassword) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        user.setPassword(Md5Util.encrypt(newPassword));
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(user);
    }

    @Override
    public void updateAdminStatus(Integer userId, String status) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        user.setStatus(status);
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(user);
    }
}

