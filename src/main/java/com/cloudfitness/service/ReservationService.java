package com.cloudfitness.service;

import com.cloudfitness.entity.Reservation;
import java.util.List;

public interface ReservationService {
    List<Reservation> getReservationsByUserId(Integer userId);
    Integer createReservation(Integer userId, Integer courseId);
    void cancelReservation(Integer reservationId, Integer userId);
}

