package com.cloudfitness.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloudfitness.entity.Locker;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface LockerMapper extends BaseMapper<Locker> {
    Locker selectByLockerNumber(@Param("lockerNumber") Integer lockerNumber);
    List<Locker> selectByUserId(@Param("userId") Integer userId);
    List<Locker> selectAdminLockers(@Param("lockerNumber") Integer lockerNumber,
                                    @Param("userId") Integer userId,
                                    @Param("status") String status,
                                    @Param("offset") Integer offset,
                                    @Param("pageSize") Integer pageSize);
    Integer countAdminLockers(@Param("lockerNumber") Integer lockerNumber,
                             @Param("userId") Integer userId,
                             @Param("status") String status);
}

