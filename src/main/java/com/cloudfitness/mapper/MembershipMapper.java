package com.cloudfitness.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloudfitness.entity.Membership;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface MembershipMapper extends BaseMapper<Membership> {
    Membership selectCurrentByUserId(@Param("userId") Integer userId);
    List<Membership> selectByUserId(@Param("userId") Integer userId);
}


