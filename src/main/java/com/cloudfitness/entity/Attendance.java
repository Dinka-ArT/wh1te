package com.cloudfitness.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("attendance")
public class Attendance {
    @TableId(type = IdType.AUTO)
    private Integer attendanceId;
    private Integer userId;
    private Integer courseId;
    private Integer reservationId;
    private LocalDateTime checkInTime;
    private Boolean isOnTime;
    private Integer lateMinutes;
    private LocalDateTime createdAt;
}


