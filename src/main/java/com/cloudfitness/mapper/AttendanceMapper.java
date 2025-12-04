package com.cloudfitness.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloudfitness.entity.Attendance;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface AttendanceMapper extends BaseMapper<Attendance> {
    Attendance selectByReservationId(@Param("reservationId") Integer reservationId);
    List<Attendance> selectByUserId(@Param("userId") Integer userId, @Param("month") String month, @Param("limit") Integer limit);
    List<Attendance> selectAdminAttendance(@Param("userId") Integer userId,
                                           @Param("courseId") Integer courseId,
                                           @Param("startDate") String startDate,
                                           @Param("endDate") String endDate,
                                           @Param("isOnTime") Boolean isOnTime,
                                           @Param("offset") Integer offset,
                                           @Param("pageSize") Integer pageSize);
    Integer countAdminAttendance(@Param("userId") Integer userId,
                                @Param("courseId") Integer courseId,
                                @Param("startDate") String startDate,
                                @Param("endDate") String endDate,
                                @Param("isOnTime") Boolean isOnTime);
}

