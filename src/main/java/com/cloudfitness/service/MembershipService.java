package com.cloudfitness.service;

import com.cloudfitness.entity.Membership;

public interface MembershipService {
    Membership getCurrentMembership(Integer userId);
}


