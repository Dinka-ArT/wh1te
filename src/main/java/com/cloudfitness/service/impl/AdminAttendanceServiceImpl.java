package com.cloudfitness.service.impl;

import com.cloudfitness.entity.Attendance;
import com.cloudfitness.entity.Course;
import com.cloudfitness.entity.Reservation;
import com.cloudfitness.entity.User;
import com.cloudfitness.mapper.AttendanceMapper;
import com.cloudfitness.mapper.CourseMapper;
import com.cloudfitness.mapper.ReservationMapper;
import com.cloudfitness.mapper.UserMapper;
import com.cloudfitness.service.AdminAttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminAttendanceServiceImpl implements AdminAttendanceService {
    @Autowired
    private AttendanceMapper attendanceMapper;
    
    @Autowired
    private ReservationMapper reservationMapper;
    
    @Autowired
    private CourseMapper courseMapper;
    
    @Autowired
    private UserMapper userMapper;

    @Override
    public Map<String, Object> getAttendance(Integer userId, Integer courseId,
                                            String startDate, String endDate, Boolean isOnTime,
                                            Integer page, Integer pageSize) {
        Integer offset = (page - 1) * pageSize;
        List<Attendance> attendanceList = attendanceMapper.selectAdminAttendance(
            userId, courseId, startDate, endDate, isOnTime, offset, pageSize);
        Integer total = attendanceMapper.countAdminAttendance(userId, courseId, startDate, endDate, isOnTime);
        
        List<Map<String, Object>> list = new ArrayList<>();
        for (Attendance attendance : attendanceList) {
            Map<String, Object> item = new HashMap<>();
            item.put("attendance_id", attendance.getAttendanceId());
            item.put("reservation_id", attendance.getReservationId());
            
            Reservation reservation = reservationMapper.selectById(attendance.getReservationId());
            if (reservation != null) {
                item.put("user_id", reservation.getUserId());
                User user = userMapper.selectById(reservation.getUserId());
                if (user != null) {
                    item.put("username", user.getUsername());
                    item.put("phone_number", user.getPhoneNumber());
                }
                item.put("course_id", reservation.getCourseId());
                Course course = courseMapper.selectById(reservation.getCourseId());
                if (course != null) {
                    item.put("course_name", course.getCourseName());
                    item.put("schedule", course.getSchedule());
                }
            }
            
            item.put("check_in_time", attendance.getCheckInTime());
            item.put("is_on_time", attendance.getIsOnTime());
            item.put("late_minutes", attendance.getLateMinutes());
            
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
    public Map<String, Object> getAttendanceDetail(Integer attendanceId) {
        Attendance attendance = attendanceMapper.selectById(attendanceId);
        if (attendance == null) {
            throw new RuntimeException("签到记录不存在");
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("attendance_id", attendance.getAttendanceId());
        result.put("reservation_id", attendance.getReservationId());
        
        Reservation reservation = reservationMapper.selectById(attendance.getReservationId());
        if (reservation != null) {
            result.put("user_id", reservation.getUserId());
            User user = userMapper.selectById(reservation.getUserId());
            if (user != null) {
                result.put("username", user.getUsername());
            }
            result.put("course_id", reservation.getCourseId());
            Course course = courseMapper.selectById(reservation.getCourseId());
            if (course != null) {
                result.put("course_name", course.getCourseName());
                result.put("schedule", course.getSchedule());
            }
        }
        
        result.put("check_in_time", attendance.getCheckInTime());
        result.put("is_on_time", attendance.getIsOnTime());
        result.put("late_minutes", attendance.getLateMinutes());
        
        return result;
    }

    @Override
    public void deleteAttendance(Integer attendanceId) {
        Attendance attendance = attendanceMapper.selectById(attendanceId);
        if (attendance == null) {
            throw new RuntimeException("签到记录不存在");
        }
        attendanceMapper.deleteById(attendanceId);
    }

    @Override
    public Map<String, Object> getStatistics() {
        Map<String, Object> result = new HashMap<>();
        
        List<Attendance> allAttendance = attendanceMapper.selectList(null);
        int totalCount = allAttendance.size();
        int onTimeCount = 0;
        int lateCount = 0;
        
        for (Attendance att : allAttendance) {
            if (att.getIsOnTime() != null && att.getIsOnTime()) {
                onTimeCount++;
            } else {
                lateCount++;
            }
        }
        
        result.put("total_attendance", totalCount);
        result.put("on_time_count", onTimeCount);
        result.put("late_count", lateCount);
        result.put("attendance_rate", totalCount > 0 ? (double) onTimeCount / totalCount : 0.0);
        
        return result;
    }
}

