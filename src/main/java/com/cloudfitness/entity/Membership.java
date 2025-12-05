package com.cloudfitness.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("memberships")
public class Membership {
    @TableId(type = IdType.AUTO)
    private Integer membershipId;
    private Integer userId;
    private String membershipType;
    private LocalDateTime startDate;
    private LocalDateTime expiryDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}


