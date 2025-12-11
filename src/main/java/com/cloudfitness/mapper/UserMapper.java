package com.cloudfitness.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloudfitness.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    User selectByUsername(@Param("username") String username);

    User selectByPhoneNumber(@Param("phoneNumber") String phoneNumber);

    User selectByEmail(@Param("email") String email);

    List<User> selectMembers(@Param("username") String username,
                             @Param("phoneNumber") String phoneNumber,
                             @Param("email") String email,
                             @Param("startDate") String startDate,
                             @Param("endDate") String endDate,
                             @Param("membershipStatus") String membershipStatus,
                             @Param("offset") Integer offset,
                             @Param("pageSize") Integer pageSize);

    Integer countMembers(@Param("username") String username,
                         @Param("phoneNumber") String phoneNumber,
                         @Param("email") String email,
                         @Param("startDate") String startDate,
                         @Param("endDate") String endDate,
                         @Param("membershipStatus") String membershipStatus);

    List<User> selectCoaches(@Param("username") String username,
                             @Param("phoneNumber") String phoneNumber,
                             @Param("startDate") String startDate,
                             @Param("endDate") String endDate,
                             @Param("status") String status,
                             @Param("offset") Integer offset,
                             @Param("pageSize") Integer pageSize);

    Integer countCoaches(@Param("username") String username,
                         @Param("phoneNumber") String phoneNumber,
                         @Param("startDate") String startDate,
                         @Param("endDate") String endDate,
                         @Param("status") String status);

    List<User> selectAdmins(@Param("username") String username,
                            @Param("phoneNumber") String phoneNumber,
                            @Param("startDate") String startDate,
                            @Param("endDate") String endDate,
                            @Param("status") String status,
                            @Param("offset") Integer offset,
                            @Param("pageSize") Integer pageSize);

    Integer countAdmins(@Param("username") String username,
                        @Param("phoneNumber") String phoneNumber,
                        @Param("startDate") String startDate,
                        @Param("endDate") String endDate,
                        @Param("status") String status);

    User selectByOpenid(@Param("openid") String openid);
}


