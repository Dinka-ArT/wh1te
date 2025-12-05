package com.cloudfitness.service;

import java.util.Map;

public interface AdminDashboardService {
    Map<String, Object> getStatistics();
    Map<String, Object> getUserGrowth(Integer days);
    Map<String, Object> getCourseReservations(Integer days);
    Map<String, Object> getAttendanceRate(Integer days);
    Map<String, Object> getMembershipDistribution();
    Map<String, Object> getCourseDistribution();
}


