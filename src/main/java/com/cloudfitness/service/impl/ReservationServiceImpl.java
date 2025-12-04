package com.cloudfitness.service.impl;

import com.cloudfitness.entity.Course;
import com.cloudfitness.entity.Reservation;
import com.cloudfitness.mapper.CourseMapper;
import com.cloudfitness.mapper.ReservationMapper;
import com.cloudfitness.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservationServiceImpl implements ReservationService {
    @Autowired
    private ReservationMapper reservationMapper;
    
    @Autowired
    private CourseMapper courseMapper;

    @Override
    public List<Reservation> getReservationsByUserId(Integer userId) {
        return reservationMapper.selectByUserId(userId);
    }

    @Override
    public Integer createReservation(Integer userId, Integer courseId) {
        Course course = courseMapper.selectById(courseId);
        if (course == null) {
            throw new RuntimeException("课程不存在");
        }
        if (course.getSchedule().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("课程已过期");
        }
        Reservation existing = reservationMapper.selectByUserIdAndCourseId(userId, courseId);
        if (existing != null) {
            throw new RuntimeException("您已预约过该课程");
        }
        Integer reservedCount = reservationMapper.countByCourseId(courseId);
        if (reservedCount >= course.getCapacity()) {
            throw new RuntimeException("课程已满员");
        }
        Reservation reservation = new Reservation();
        reservation.setUserId(userId);
        reservation.setCourseId(courseId);
        reservation.setReservationDate(LocalDateTime.now());
        reservation.setStatus("pending");
        reservation.setCreatedAt(LocalDateTime.now());
        reservation.setUpdatedAt(LocalDateTime.now());
        reservationMapper.insert(reservation);
        return reservation.getReservationId();
    }

    @Override
    public void cancelReservation(Integer reservationId, Integer userId) {
        Reservation reservation = reservationMapper.selectById(reservationId);
        if (reservation == null) {
            throw new RuntimeException("预约不存在");
        }
        if (!reservation.getUserId().equals(userId)) {
            throw new RuntimeException("无权取消该预约");
        }
        if ("completed".equals(reservation.getStatus()) || "cancelled".equals(reservation.getStatus())) {
            throw new RuntimeException("该预约无法取消");
        }
        reservation.setStatus("cancelled");
        reservation.setUpdatedAt(LocalDateTime.now());
        reservationMapper.updateById(reservation);
    }
}

