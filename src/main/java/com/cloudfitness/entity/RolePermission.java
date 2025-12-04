package com.cloudfitness.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("role_permissions")
public class RolePermission {
    private Integer roleId;
    private Integer permissionId;
    private LocalDateTime createdAt;
}

