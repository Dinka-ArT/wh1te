package com.cloudfitness.service;

import com.cloudfitness.entity.Attendance;
import java.util.List;

public interface AttendanceService {
    List<Attendance> getAttendanceByUserId(Integer userId, String month, Integer limit);
}


