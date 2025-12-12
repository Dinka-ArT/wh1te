package com.cloudfitness.service;

import com.cloudfitness.entity.Course;
import java.util.Map;

public interface CoachCourseService {

    /**
     * 根据教练ID获取其所有课程
     * @param coachId 教练的用户ID
     * @return 包含课程列表的Map
     */
    Map<String, Object> getCoursesByCoachId(Integer coachId);

    /**
     * 教练创建新课程
     * @param coachId 教练的用户ID
     * @param payload 包含课程信息的Map
     * @return 新创建的课程ID
     */
    Integer createCourse(Integer coachId, Map<String, Object> payload);

    /**
     * 教练更新自己的课程信息
     * @param courseId 要更新的课程ID
     * @param coachId 当前操作的教练ID，用于权限校验
     * @param payload 包含新课程信息的Map
     */
    void updateCourse(Integer courseId, Integer coachId, Map<String, Object> payload);

    /**
     * 教练删除自己的课程
     * @param courseId 要删除的课程ID
     * @param coachId 当前操作的教练ID，用于权限校验
     */
    void deleteCourse(Integer courseId, Integer coachId);

    /**
     * 教练获取自己某门课程的详细信息
     * @param courseId 课程ID
     * @param coachId 当前操作的教练ID，用于权限校验
     * @return 课程实体
     */
    Course getCourseDetail(Integer courseId, Integer coachId);
}

