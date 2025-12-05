package com.cloudfitness.service;

import java.util.Map;

public interface AdminMembershipService {
    Map<String, Object> getMemberships(Integer userId, String membershipType,
                                     String startDate, String endDate,
                                     Integer page, Integer pageSize);
    Map<String, Object> getMembershipDetail(Integer membershipId);
    Integer createMembership(Integer userId, String membershipType, String startDate, String expiryDate);
    void updateMembership(Integer membershipId, String membershipType, String startDate, String expiryDate);
    void renewMembership(Integer membershipId, String expiryDate, Integer months);
    void deleteMembership(Integer membershipId);
}


