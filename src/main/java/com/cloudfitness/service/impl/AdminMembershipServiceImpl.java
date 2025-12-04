package com.cloudfitness.service.impl;

import com.cloudfitness.entity.Membership;
import com.cloudfitness.entity.User;
import com.cloudfitness.mapper.MembershipMapper;
import com.cloudfitness.mapper.UserMapper;
import com.cloudfitness.service.AdminMembershipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminMembershipServiceImpl implements AdminMembershipService {
    @Autowired
    private MembershipMapper membershipMapper;
    
    @Autowired
    private UserMapper userMapper;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public Map<String, Object> getMemberships(Integer userId, String membershipType,
                                              String startDate, String endDate,
                                              Integer page, Integer pageSize) {
        List<Membership> memberships = membershipMapper.selectList(null);
        List<Map<String, Object>> list = new ArrayList<>();
        
        for (Membership membership : memberships) {
            if (userId != null && !membership.getUserId().equals(userId)) {
                continue;
            }
            if (membershipType != null && !membership.getMembershipType().equals(membershipType)) {
                continue;
            }
            
            User user = userMapper.selectById(membership.getUserId());
            Map<String, Object> item = new HashMap<>();
            item.put("membership_id", membership.getMembershipId());
            item.put("user_id", membership.getUserId());
            if (user != null) {
                item.put("username", user.getUsername());
                item.put("phone_number", user.getPhoneNumber());
            }
            item.put("membership_type", membership.getMembershipType());
            item.put("start_date", membership.getStartDate());
            item.put("expiry_date", membership.getExpiryDate());
            
            String status = "valid";
            if (membership.getExpiryDate().isBefore(LocalDateTime.now())) {
                status = "expired";
            }
            item.put("status", status);
            
            list.add(item);
        }
        
        int total = list.size();
        int fromIndex = (page - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, total);
        if (fromIndex < total) {
            list = list.subList(fromIndex, toIndex);
        } else {
            list = new ArrayList<>();
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        result.put("page", page);
        result.put("page_size", pageSize);
        return result;
    }

    @Override
    public Map<String, Object> getMembershipDetail(Integer membershipId) {
        Membership membership = membershipMapper.selectById(membershipId);
        if (membership == null) {
            throw new RuntimeException("会员卡不存在");
        }
        
        User user = userMapper.selectById(membership.getUserId());
        Map<String, Object> result = new HashMap<>();
        result.put("membership_id", membership.getMembershipId());
        result.put("user_id", membership.getUserId());
        if (user != null) {
            result.put("username", user.getUsername());
        }
        result.put("membership_type", membership.getMembershipType());
        result.put("start_date", membership.getStartDate());
        result.put("expiry_date", membership.getExpiryDate());
        
        String status = "valid";
        if (membership.getExpiryDate().isBefore(LocalDateTime.now())) {
            status = "expired";
        }
        result.put("status", status);
        
        return result;
    }

    @Override
    public Integer createMembership(Integer userId, String membershipType, String startDate, String expiryDate) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        Membership membership = new Membership();
        membership.setUserId(userId);
        membership.setMembershipType(membershipType);
        membership.setStartDate(LocalDateTime.parse(startDate.replace("Z", "").replace("T", " "), FORMATTER));
        membership.setExpiryDate(LocalDateTime.parse(expiryDate.replace("Z", "").replace("T", " "), FORMATTER));
        membership.setCreatedAt(LocalDateTime.now());
        membership.setUpdatedAt(LocalDateTime.now());
        membershipMapper.insert(membership);
        return membership.getMembershipId();
    }

    @Override
    public void updateMembership(Integer membershipId, String membershipType, String startDate, String expiryDate) {
        Membership membership = membershipMapper.selectById(membershipId);
        if (membership == null) {
            throw new RuntimeException("会员卡不存在");
        }
        if (membershipType != null) {
            membership.setMembershipType(membershipType);
        }
        if (startDate != null) {
            membership.setStartDate(LocalDateTime.parse(startDate.replace("Z", "").replace("T", " "), FORMATTER));
        }
        if (expiryDate != null) {
            membership.setExpiryDate(LocalDateTime.parse(expiryDate.replace("Z", "").replace("T", " "), FORMATTER));
        }
        membership.setUpdatedAt(LocalDateTime.now());
        membershipMapper.updateById(membership);
    }

    @Override
    public void renewMembership(Integer membershipId, String expiryDate) {
        Membership membership = membershipMapper.selectById(membershipId);
        if (membership == null) {
            throw new RuntimeException("会员卡不存在");
        }
        membership.setExpiryDate(LocalDateTime.parse(expiryDate.replace("Z", "").replace("T", " "), FORMATTER));
        membership.setUpdatedAt(LocalDateTime.now());
        membershipMapper.updateById(membership);
    }

    @Override
    public void deleteMembership(Integer membershipId) {
        Membership membership = membershipMapper.selectById(membershipId);
        if (membership == null) {
            throw new RuntimeException("会员卡不存在");
        }
        membershipMapper.deleteById(membershipId);
    }
}

