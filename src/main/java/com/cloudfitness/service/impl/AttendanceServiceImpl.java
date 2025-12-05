package com.cloudfitness.service.impl;

import com.cloudfitness.entity.Attendance;
import com.cloudfitness.mapper.AttendanceMapper;
import com.cloudfitness.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AttendanceServiceImpl implements AttendanceService {
    @Autowired
    private AttendanceMapper attendanceMapper;

    @Override
    public List<Attendance> getAttendanceByUserId(Integer userId, String month, Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 30;
        }
        return attendanceMapper.selectByUserId(userId, month, limit);
    }
}


