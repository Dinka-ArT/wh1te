package com.cloudfitness.service.impl;

import com.cloudfitness.entity.Attendance;
import com.cloudfitness.entity.Course;
import com.cloudfitness.entity.Reservation;
import com.cloudfitness.entity.User;
import com.cloudfitness.mapper.AttendanceMapper;
import com.cloudfitness.mapper.CourseMapper;
import com.cloudfitness.mapper.ReservationMapper;
import com.cloudfitness.mapper.UserMapper;
import com.cloudfitness.service.AdminReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminReservationServiceImpl implements AdminReservationService {
    @Autowired
    private ReservationMapper reservationMapper;
    
    @Autowired
    private CourseMapper courseMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private AttendanceMapper attendanceMapper;

    @Override
    public Map<String, Object> getReservations(Integer userId, Integer courseId,
                                               String startDate, String endDate, String status,
                                               Integer page, Integer pageSize) {
        Integer offset = (page - 1) * pageSize;
        List<Reservation> reservations = reservationMapper.selectAdminReservations(
            userId, courseId, startDate, endDate, status, offset, pageSize);
        Integer total = reservationMapper.countAdminReservations(userId, courseId, startDate, endDate, status);
        
        List<Map<String, Object>> list = new ArrayList<>();
        for (Reservation reservation : reservations) {
            Map<String, Object> item = new HashMap<>();
            item.put("reservation_id", reservation.getReservationId());
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
            item.put("reservation_date", reservation.getReservationDate());
            item.put("status", reservation.getStatus());
            
            Attendance attendance = attendanceMapper.selectByReservationId(reservation.getReservationId());
            item.put("has_attendance", attendance != null);
            
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
    public Map<String, Object> getReservationDetail(Integer reservationId) {
        Reservation reservation = reservationMapper.selectById(reservationId);
        if (reservation == null) {
            throw new RuntimeException("预约不存在");
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("reservation_id", reservation.getReservationId());
        result.put("user_id", reservation.getUserId());
        User user = userMapper.selectById(reservation.getUserId());
        if (user != null) {
            result.put("username", user.getUsername());
            result.put("phone_number", user.getPhoneNumber());
        }
        result.put("course_id", reservation.getCourseId());
        Course course = courseMapper.selectById(reservation.getCourseId());
        if (course != null) {
            result.put("course_name", course.getCourseName());
            result.put("schedule", course.getSchedule());
        }
        result.put("reservation_date", reservation.getReservationDate());
        result.put("status", reservation.getStatus());
        
        Attendance attendance = attendanceMapper.selectByReservationId(reservationId);
        if (attendance != null) {
            Map<String, Object> att = new HashMap<>();
            att.put("attendance_id", attendance.getAttendanceId());
            att.put("check_in_time", attendance.getCheckInTime());
            att.put("is_on_time", attendance.getIsOnTime());
            result.put("attendance", att);
        }
        
        return result;
    }

    @Override
    public void confirmReservation(Integer reservationId) {
        Reservation reservation = reservationMapper.selectById(reservationId);
        if (reservation == null) {
            throw new RuntimeException("预约不存在");
        }
        reservation.setStatus("confirmed");
        reservation.setUpdatedAt(LocalDateTime.now());
        reservationMapper.updateById(reservation);
    }

    @Override
    public void cancelReservation(Integer reservationId) {
        Reservation reservation = reservationMapper.selectById(reservationId);
        if (reservation == null) {
            throw new RuntimeException("预约不存在");
        }
        reservation.setStatus("cancelled");
        reservation.setUpdatedAt(LocalDateTime.now());
        reservationMapper.updateById(reservation);
    }

    @Override
    public void completeReservation(Integer reservationId) {
        Reservation reservation = reservationMapper.selectById(reservationId);
        if (reservation == null) {
            throw new RuntimeException("预约不存在");
        }
        reservation.setStatus("completed");
        reservation.setUpdatedAt(LocalDateTime.now());
        reservationMapper.updateById(reservation);
    }

    @Override
    public void deleteReservation(Integer reservationId) {
        Reservation reservation = reservationMapper.selectById(reservationId);
        if (reservation == null) {
            throw new RuntimeException("预约不存在");
        }
        reservationMapper.deleteById(reservationId);
    }
}

