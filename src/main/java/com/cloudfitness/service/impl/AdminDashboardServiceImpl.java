package com.cloudfitness.service.impl;

import com.cloudfitness.mapper.AttendanceMapper;
import com.cloudfitness.mapper.CourseMapper;
import com.cloudfitness.mapper.LockerMapper;
import com.cloudfitness.mapper.MembershipMapper;
import com.cloudfitness.mapper.ReservationMapper;
import com.cloudfitness.mapper.UserMapper;
import com.cloudfitness.service.AdminDashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
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

        LocalDate today = LocalDate.now();

        var users = userMapper.selectList(null);
        int totalUsers = users.size();
        int totalMembers = (int) users.stream().filter(u -> "member".equals(u.getRole())).count();
        int totalCoaches = (int) users.stream().filter(u -> "coach".equals(u.getRole())).count();
        int totalAdmins = (int) users.stream().filter(u -> "admin".equals(u.getRole())).count();
        int todayNewUsers = (int) users.stream()
                .filter(u -> u.getRegistrationDate() != null && u.getRegistrationDate().toLocalDate().isEqual(today))
                .count();

        var courses = courseMapper.selectList(null);
        int totalCourses = courses.size();
        int todayCourses = (int) courses.stream()
                .filter(c -> c.getSchedule() != null && c.getSchedule().toLocalDate().isEqual(today))
                .count();

        var reservations = reservationMapper.selectList(null);
        int totalReservations = reservations.size();
        int todayReservations = (int) reservations.stream()
                .filter(r -> r.getReservationDate() != null && r.getReservationDate().toLocalDate().isEqual(today))
                .count();

        var attendanceList = attendanceMapper.selectList(null);
        int totalAttendance = attendanceList.size();
        int todayAttendance = (int) attendanceList.stream()
                .filter(a -> a.getCheckInTime() != null && a.getCheckInTime().toLocalDate().isEqual(today))
                .count();

        var memberships = membershipMapper.selectList(null);
        int activeMemberships = (int) memberships.stream()
                .filter(m -> m.getExpiryDate() != null && m.getExpiryDate().isAfter(LocalDateTime.now()))
                .count();

        int inUseLockers = (int) lockerMapper.selectList(null).stream()
                .filter(l -> "in_use".equals(l.getStatus()))
                .count();

        // 使用前端兼容的蛇形字段命名
        result.put("total_users", totalUsers);
        result.put("total_members", totalMembers);
        result.put("total_coaches", totalCoaches);
        result.put("total_admins", totalAdmins);
        result.put("today_new_users", todayNewUsers);
        result.put("total_courses", totalCourses);
        result.put("today_courses", todayCourses);
        result.put("total_reservations", totalReservations);
        result.put("today_reservations", todayReservations);
        result.put("total_attendance", totalAttendance);
        result.put("today_attendance", todayAttendance);
        result.put("valid_memberships", activeMemberships);
        result.put("in_use_lockers", inUseLockers);

        return result;
    }

    @Override
    public Map<String, Object> getUserGrowth(Integer days) {
        Map<String, Object> result = new HashMap<>();
        result.put("data", java.util.Collections.emptyList());
        return result;
    }

    @Override
    public Map<String, Object> getCourseReservations() {
        Map<String, Object> result = new HashMap<>();
        result.put("data", java.util.Collections.emptyList());
        return result;
    }

    @Override
    public Map<String, Object> getAttendanceRate(Integer days) {
        Map<String, Object> result = new HashMap<>();
        result.put("data", java.util.Collections.emptyList());
        return result;
    }

    @Override
    public Map<String, Object> getMembershipDistribution() {
        Map<String, Object> result = new HashMap<>();
        result.put("data", java.util.Collections.emptyList());
        return result;
    }

    @Override
    public Map<String, Object> getCourseDistribution() {
        Map<String, Object> result = new HashMap<>();
        result.put("data", java.util.Collections.emptyList());
        return result;
    }
}


