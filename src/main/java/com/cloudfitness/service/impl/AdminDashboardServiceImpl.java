package com.cloudfitness.service.impl;

import com.cloudfitness.entity.*;
import com.cloudfitness.mapper.*;
import com.cloudfitness.service.AdminDashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class AdminDashboardServiceImpl implements AdminDashboardService {
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private CourseMapper courseMapper;
    
    @Autowired
    private ReservationMapper reservationMapper;
    
    @Autowired
    private AttendanceMapper attendanceMapper;
    
    @Autowired
    private MembershipMapper membershipMapper;
    
    @Autowired
    private LockerMapper lockerMapper;

    @Override
    public Map<String, Object> getStatistics() {
        Map<String, Object> result = new HashMap<>();
        
        int totalMembers = userMapper.selectList(null).stream()
            .filter(u -> "member".equals(u.getRole()))
            .mapToInt(u -> 1)
            .sum();
        
        int totalCoaches = userMapper.selectList(null).stream()
            .filter(u -> "coach".equals(u.getRole()))
            .mapToInt(u -> 1)
            .sum();
        
        int totalCourses = courseMapper.selectList(null).size();
        
        int totalReservations = reservationMapper.selectList(null).size();
        
        int totalAttendance = attendanceMapper.selectList(null).size();
        
        int activeMemberships = membershipMapper.selectList(null).stream()
            .filter(m -> m.getExpiryDate().isAfter(LocalDateTime.now()))
            .mapToInt(m -> 1)
            .sum();
        
        int inUseLockers = lockerMapper.selectList(null).stream()
            .filter(l -> "in_use".equals(l.getStatus()))
            .mapToInt(l -> 1)
            .sum();
        
        result.put("total_members", totalMembers);
        result.put("total_coaches", totalCoaches);
        result.put("total_courses", totalCourses);
        result.put("total_reservations", totalReservations);
        result.put("total_attendance", totalAttendance);
        result.put("active_memberships", activeMemberships);
        result.put("in_use_lockers", inUseLockers);
        
        return result;
    }
}


