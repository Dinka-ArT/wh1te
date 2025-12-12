package com.cloudfitness.service.impl;

import com.cloudfitness.entity.Attendance;
import com.cloudfitness.entity.Course;
import com.cloudfitness.entity.Reservation;
import com.cloudfitness.mapper.AttendanceMapper;
import com.cloudfitness.mapper.CourseMapper;
import com.cloudfitness.mapper.ReservationMapper;
import com.cloudfitness.service.CoachReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class CoachReservationServiceImpl implements CoachReservationService {

    @Autowired
    private ReservationMapper reservationMapper;

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private AttendanceMapper attendanceMapper;

    @Override
    @Transactional
    public void checkIn(Integer reservationId, Integer coachId) {
        // 1. 查找预约信息
        Reservation reservation = reservationMapper.selectById(reservationId);
        if (reservation == null) {
            throw new RuntimeException("预约不存在");
        }

        // 2. 检查预约状态，防止重复签到
        if ("completed".equalsIgnoreCase(reservation.getStatus()) || "attended".equalsIgnoreCase(reservation.getStatus())) {
            throw new RuntimeException("该预约已完成，请勿重复签到");
        }

        // 3. 查找课程信息，并校验教练权限
        Course course = courseMapper.selectById(reservation.getCourseId());
        if (course == null) {
            throw new RuntimeException("预约对应的课程不存在");
        }
        if (!Objects.equals(course.getInstructorId(), coachId)) {
            throw new RuntimeException("无权为该课程的预约签到");
        }

        // 4. 更新预约状态为 'completed'
        Reservation reservationToUpdate = new Reservation();
        reservationToUpdate.setReservationId(reservationId);
        reservationToUpdate.setStatus("completed");
        reservationMapper.updateById(reservationToUpdate);

        // 5. 在签到表中创建记录
        Attendance attendance = new Attendance();
        attendance.setUserId(reservation.getUserId());
        attendance.setCourseId(reservation.getCourseId());
        attendance.setReservationId(reservationId);
        attendance.setCheckInTime(LocalDateTime.now());
        attendance.setIsOnTime(true); // 默认为准时
        attendanceMapper.insert(attendance);
    }

    @Override
    public Map<String, Object> getReservationsForCoach(Integer coachId) {
        // 1. 查找教练的所有课程
        List<Course> coachCourses = courseMapper.selectByInstructorId(coachId);
        if (coachCourses == null || coachCourses.isEmpty()) {
            return Map.of("reservations", java.util.Collections.emptyList());
        }

        // 2. 获取所有课程的ID
        List<Integer> courseIds = coachCourses.stream().map(Course::getCourseId).collect(java.util.stream.Collectors.toList());

        // 3. 一次性查询所有相关课程的预约详情
        List<com.cloudfitness.dto.ReservationDetailDto> reservations = reservationMapper.selectReservationDetailsByCourseIds(courseIds);

        Map<String, Object> data = new HashMap<>();
        data.put("reservations", reservations);
        return data;
    }
}

