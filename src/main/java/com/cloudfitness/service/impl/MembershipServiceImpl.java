package com.cloudfitness.service.impl;

import com.cloudfitness.entity.Membership;
import com.cloudfitness.mapper.MembershipMapper;
import com.cloudfitness.service.MembershipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class MembershipServiceImpl implements MembershipService {
    @Autowired
    private MembershipMapper membershipMapper;

    @Override
    public Membership getCurrentMembership(Integer userId) {
        Membership membership = membershipMapper.selectCurrentByUserId(userId);
        if (membership != null && membership.getExpiryDate().isBefore(LocalDateTime.now())) {
            return null;
        }
        return membership;
    }
}

