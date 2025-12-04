package com.cloudfitness.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloudfitness.entity.RolePermission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface RolePermissionMapper extends BaseMapper<RolePermission> {
    List<Integer> selectPermissionIdsByRoleId(@Param("roleId") Integer roleId);
    void deleteByRoleId(@Param("roleId") Integer roleId);
}

