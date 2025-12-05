package com.cloudfitness.service;

import java.util.Map;

public interface AdminLockerService {
    Map<String, Object> getLockers(Integer lockerNumber, Integer userId, String status,
                                  Integer page, Integer pageSize);
    Map<String, Object> getLockerDetail(Integer lockerId);
    Integer createLocker(Integer lockerNumber);
    void assignLocker(Integer lockerId, Integer userId, String endDate);
    void updateLocker(Integer lockerId, Integer lockerNumber, Integer userId, String endDate);
    void releaseLocker(Integer lockerId);
    void deleteLocker(Integer lockerId);
}


