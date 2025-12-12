package com.cloudfitness.service;

public interface CoachReservationService {

    /**
     * 教练为学员的预约进行签到
     * @param reservationId 预约ID
     * @param coachId 当前操作的教练ID，用于权限校验
     */
    void checkIn(Integer reservationId, Integer coachId);

    /**
     * 获取某位教练所有课程的全部预约信息
     * @param coachId 教练的用户ID
     * @return 包含预约列表的Map
     */
    java.util.Map<String, Object> getReservationsForCoach(Integer coachId);
}

