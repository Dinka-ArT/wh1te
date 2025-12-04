package com.cloudfitness.service;

import java.util.Map;

public interface AdminAttendanceService {
    Map<String, Object> getAttendance(Integer userId, Integer courseId,
                                     String startDate, String endDate, Boolean isOnTime,
                                     Integer page, Integer pageSize);
    Map<String, Object> getAttendanceDetail(Integer attendanceId);
    void deleteAttendance(Integer attendanceId);
    Map<String, Object> getStatistics();
}

