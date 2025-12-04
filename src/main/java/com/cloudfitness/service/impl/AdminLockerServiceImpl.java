package com.cloudfitness.service.impl;

import com.cloudfitness.entity.Locker;
import com.cloudfitness.entity.User;
import com.cloudfitness.mapper.LockerMapper;
import com.cloudfitness.mapper.UserMapper;
import com.cloudfitness.service.AdminLockerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminLockerServiceImpl implements AdminLockerService {
    @Autowired
    private LockerMapper lockerMapper;
    
    @Autowired
    private UserMapper userMapper;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public Map<String, Object> getLockers(Integer lockerNumber, Integer userId, String status,
                                         Integer page, Integer pageSize) {
        Integer offset = (page - 1) * pageSize;
        List<Locker> lockers = lockerMapper.selectAdminLockers(lockerNumber, userId, status, offset, pageSize);
        Integer total = lockerMapper.countAdminLockers(lockerNumber, userId, status);
        
        List<Map<String, Object>> list = new ArrayList<>();
        for (Locker locker : lockers) {
            Map<String, Object> item = new HashMap<>();
            item.put("locker_id", locker.getLockerId());
            item.put("locker_number", locker.getLockerNumber());
            item.put("user_id", locker.getUserId());
            if (locker.getUserId() != null) {
                User user = userMapper.selectById(locker.getUserId());
                if (user != null) {
                    item.put("username", user.getUsername());
                    item.put("phone_number", user.getPhoneNumber());
                }
            }
            item.put("start_date", locker.getStartDate());
            item.put("end_date", locker.getEndDate());
            item.put("status", locker.getStatus());
            list.add(item);
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        result.put("page", page);
        result.put("page_size", pageSize);
        return result;
    }

    @Override
    public Map<String, Object> getLockerDetail(Integer lockerId) {
        Locker locker = lockerMapper.selectById(lockerId);
        if (locker == null) {
            throw new RuntimeException("储物柜不存在");
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("locker_id", locker.getLockerId());
        result.put("locker_number", locker.getLockerNumber());
        result.put("user_id", locker.getUserId());
        if (locker.getUserId() != null) {
            User user = userMapper.selectById(locker.getUserId());
            if (user != null) {
                result.put("username", user.getUsername());
            }
        }
        result.put("start_date", locker.getStartDate());
        result.put("end_date", locker.getEndDate());
        result.put("status", locker.getStatus());
        
        return result;
    }

    @Override
    public Integer createLocker(Integer lockerNumber) {
        Locker existing = lockerMapper.selectByLockerNumber(lockerNumber);
        if (existing != null) {
            throw new RuntimeException("储物柜编号已存在");
        }
        
        Locker locker = new Locker();
        locker.setLockerNumber(lockerNumber);
        locker.setStatus("free");
        locker.setCreatedAt(LocalDateTime.now());
        locker.setUpdatedAt(LocalDateTime.now());
        lockerMapper.insert(locker);
        return locker.getLockerId();
    }

    @Override
    public void assignLocker(Integer lockerId, Integer userId, String endDate) {
        Locker locker = lockerMapper.selectById(lockerId);
        if (locker == null) {
            throw new RuntimeException("储物柜不存在");
        }
        if (!"free".equals(locker.getStatus())) {
            throw new RuntimeException("储物柜已被使用");
        }
        
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        locker.setUserId(userId);
        locker.setStartDate(LocalDateTime.now());
        if (endDate != null) {
            locker.setEndDate(LocalDateTime.parse(endDate.replace("Z", "").replace("T", " "), FORMATTER));
        }
        locker.setStatus("in_use");
        locker.setUpdatedAt(LocalDateTime.now());
        lockerMapper.updateById(locker);
    }

    @Override
    public void updateLocker(Integer lockerId, Integer lockerNumber, Integer userId, String endDate) {
        Locker locker = lockerMapper.selectById(lockerId);
        if (locker == null) {
            throw new RuntimeException("储物柜不存在");
        }
        if (lockerNumber != null) {
            Locker existing = lockerMapper.selectByLockerNumber(lockerNumber);
            if (existing != null && !existing.getLockerId().equals(lockerId)) {
                throw new RuntimeException("储物柜编号已存在");
            }
            locker.setLockerNumber(lockerNumber);
        }
        if (userId != null) {
            locker.setUserId(userId);
        }
        if (endDate != null) {
            locker.setEndDate(LocalDateTime.parse(endDate.replace("Z", "").replace("T", " "), FORMATTER));
        }
        locker.setUpdatedAt(LocalDateTime.now());
        lockerMapper.updateById(locker);
    }

    @Override
    public void releaseLocker(Integer lockerId) {
        Locker locker = lockerMapper.selectById(lockerId);
        if (locker == null) {
            throw new RuntimeException("储物柜不存在");
        }
        locker.setUserId(null);
        locker.setEndDate(LocalDateTime.now());
        locker.setStatus("free");
        locker.setUpdatedAt(LocalDateTime.now());
        lockerMapper.updateById(locker);
    }

    @Override
    public void deleteLocker(Integer lockerId) {
        Locker locker = lockerMapper.selectById(lockerId);
        if (locker == null) {
            throw new RuntimeException("储物柜不存在");
        }
        lockerMapper.deleteById(lockerId);
    }
}

