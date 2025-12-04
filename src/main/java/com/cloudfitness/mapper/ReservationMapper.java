package com.cloudfitness.mapper;

import com.cloudfitness.entity.Reservation;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ReservationMapper extends BaseMapper<Reservation> {
    List<Reservation> selectByUserId(@Param("userId") Integer userId);
    List<Reservation> selectByCourseId(@Param("courseId") Integer courseId);
    Reservation selectByUserIdAndCourseId(@Param("userId") Integer userId, @Param("courseId") Integer courseId);
    Integer countByCourseId(@Param("courseId") Integer courseId);
    List<Reservation> selectAdminReservations(@Param("userId") Integer userId,
                                            @Param("courseId") Integer courseId,
                                            @Param("startDate") String startDate,
                                            @Param("endDate") String endDate,
                                            @Param("status") String status,
                                            @Param("offset") Integer offset,
                                            @Param("pageSize") Integer pageSize);
    Integer countAdminReservations(@Param("userId") Integer userId,
                                 @Param("courseId") Integer courseId,
                                 @Param("startDate") String startDate,
                                 @Param("endDate") String endDate,
                                 @Param("status") String status);
}

