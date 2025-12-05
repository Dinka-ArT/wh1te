package com.cloudfitness.service;

import java.util.Map;

public interface AdminReservationService {
    Map<String, Object> getReservations(Integer userId, Integer courseId,
                                       String startDate, String endDate, String status,
                                       Integer page, Integer pageSize);
    Map<String, Object> getReservationDetail(Integer reservationId);
    Integer createReservation(Integer userId, Integer courseId, String status);
    void updateReservation(Integer reservationId, Integer userId, Integer courseId, String status);
    void confirmReservation(Integer reservationId);
    void cancelReservation(Integer reservationId);
    void completeReservation(Integer reservationId);
    void deleteReservation(Integer reservationId);
}


