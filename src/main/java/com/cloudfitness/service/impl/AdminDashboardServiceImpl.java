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
        // The original logic for 'todayCourses' is disabled because Course.schedule is now a String (e.g., '每周三 19:00-20:00')
        // and can no longer be directly compared with today's date.
        int todayCourses = 0;

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
        int range = (days == null || days <= 0) ? 30 : days;
        LocalDate today = LocalDate.now();
        LocalDate start = today.minusDays(range - 1);

        var users = userMapper.selectList(null);
        Map<LocalDate, Long> byDay = users.stream()
                .filter(u -> u.getRegistrationDate() != null)
                .map(u -> u.getRegistrationDate().toLocalDate())
                .filter(d -> !d.isBefore(start) && !d.isAfter(today))
                .collect(java.util.stream.Collectors.groupingBy(d -> d, java.util.stream.Collectors.counting()));

        java.util.List<Map<String, Object>> list = new java.util.ArrayList<>();
        for (LocalDate d = start; !d.isAfter(today); d = d.plusDays(1)) {
            Map<String, Object> row = new HashMap<>();
            row.put("date", d.toString());
            row.put("count", byDay.getOrDefault(d, 0L));
            list.add(row);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("data", list);
        return result;
    }

    @Override
    public Map<String, Object> getCourseReservations(Integer days) {
        int range = (days == null || days <= 0) ? 30 : days;
        LocalDate start = LocalDate.now().minusDays(range - 1);

        var reservations = reservationMapper.selectList(null);
        // 近 range 天的预约，按课程聚合
        Map<Integer, Long> byCourse = reservations.stream()
                .filter(r -> r.getReservationDate() != null && !r.getReservationDate().toLocalDate().isBefore(start))
                .collect(java.util.stream.Collectors.groupingBy(r -> r.getCourseId(), java.util.stream.Collectors.counting()));

        // 课程名称映射
        var courses = courseMapper.selectList(null).stream()
                .collect(java.util.stream.Collectors.toMap(c -> c.getCourseId(), c -> c.getCourseName(), (a, b) -> a));

        java.util.List<Map<String, Object>> list = new java.util.ArrayList<>();
        byCourse.forEach((courseId, cnt) -> {
            Map<String, Object> row = new HashMap<>();
            row.put("course_id", courseId);
            row.put("course_name", courses.getOrDefault(courseId, "未知课程"));
            row.put("count", cnt);
            list.add(row);
        });

        Map<String, Object> result = new HashMap<>();
        result.put("data", list);
        return result;
    }

    @Override
    public Map<String, Object> getAttendanceRate(Integer days) {
        int range = (days == null || days <= 0) ? 7 : days;
        LocalDate today = LocalDate.now();
        LocalDate start = today.minusDays(range - 1);

        var reservations = reservationMapper.selectList(null);
        var attendanceList = attendanceMapper.selectList(null);

        Map<LocalDate, Long> reservationByDay = reservations.stream()
                .filter(r -> r.getReservationDate() != null)
                .map(r -> r.getReservationDate().toLocalDate())
                .filter(d -> !d.isBefore(start) && !d.isAfter(today))
                .collect(java.util.stream.Collectors.groupingBy(d -> d, java.util.stream.Collectors.counting()));

        Map<LocalDate, Long> attendanceByDay = attendanceList.stream()
                .filter(a -> a.getCheckInTime() != null)
                .map(a -> a.getCheckInTime().toLocalDate())
                .filter(d -> !d.isBefore(start) && !d.isAfter(today))
                .collect(java.util.stream.Collectors.groupingBy(d -> d, java.util.stream.Collectors.counting()));

        java.util.List<Map<String, Object>> list = new java.util.ArrayList<>();
        for (LocalDate d = start; !d.isAfter(today); d = d.plusDays(1)) {
            long resCnt = reservationByDay.getOrDefault(d, 0L);
            long attCnt = attendanceByDay.getOrDefault(d, 0L);
            double rate = resCnt == 0 ? 0 : (attCnt * 100.0 / resCnt);
            Map<String, Object> row = new HashMap<>();
            row.put("date", d.toString());
            row.put("rate", rate);
            list.add(row);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("data", list);
        return result;
    }

    @Override
    public Map<String, Object> getMembershipDistribution() {
        var memberships = membershipMapper.selectList(null);
        LocalDateTime now = LocalDateTime.now();
        Map<String, Long> byType = memberships.stream()
                .filter(m -> m.getExpiryDate() != null && m.getExpiryDate().isAfter(now))
                .collect(java.util.stream.Collectors.groupingBy(m -> m.getMembershipType(), java.util.stream.Collectors.counting()));

        java.util.List<Map<String, Object>> list = new java.util.ArrayList<>();
        byType.forEach((type, cnt) -> {
            Map<String, Object> row = new HashMap<>();
            row.put("type", type);
            row.put("count", cnt);
            list.add(row);
        });

        Map<String, Object> result = new HashMap<>();
        result.put("data", list);
        return result;
    }

    @Override
    public Map<String, Object> getCourseDistribution() {
        var courses = courseMapper.selectList(null);
        java.util.List<Map<String, Object>> list = new java.util.ArrayList<>();
        courses.forEach(c -> {
            Map<String, Object> row = new HashMap<>();
            row.put("course_id", c.getCourseId());
            row.put("course_name", c.getCourseName());
            row.put("count", 1); // 若无类型字段，则按课程计数
            list.add(row);
        });

        Map<String, Object> result = new HashMap<>();
        result.put("data", list);
        return result;
    }
}


