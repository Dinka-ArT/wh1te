package com.cloudfitness.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReservationDetailDto {
    private Integer reservationId;
    private Integer userId;
    private String username;
    private Integer courseId;
    private String courseName;
    private String schedule;
    private String status;
    private LocalDateTime createdAt;
}

