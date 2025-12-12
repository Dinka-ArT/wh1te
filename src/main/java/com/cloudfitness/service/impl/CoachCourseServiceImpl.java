package com.cloudfitness.service.impl;

import com.cloudfitness.entity.Course;
import com.cloudfitness.mapper.CourseMapper;
import com.cloudfitness.service.CoachCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class CoachCourseServiceImpl implements CoachCourseService {

    @Autowired
    private CourseMapper courseMapper;

    @Override
    public Map<String, Object> getCoursesByCoachId(Integer coachId) {
        List<Course> courses = courseMapper.selectByInstructorId(coachId);
        Map<String, Object> data = new HashMap<>();
        data.put("courses", courses);
        return data;
    }

    @Override
    @Transactional
    public Integer createCourse(Integer coachId, Map<String, Object> payload) {
        Course course = new Course();
        course.setInstructorId(coachId); // 强制使用 token 中的教练ID
        course.setCourseName((String) payload.get("course_name"));
        course.setSchedule((String) payload.get("schedule"));
        course.setCapacity((Integer) payload.get("capacity"));
        course.setDescription((String) payload.get("description"));
        course.setStatus("scheduled"); // 默认状态
        courseMapper.insert(course);
        return course.getCourseId();
    }

    @Override
    @Transactional
    public void updateCourse(Integer courseId, Integer coachId, Map<String, Object> payload) {
        Course existingCourse = courseMapper.selectById(courseId);
        if (existingCourse == null) {
            throw new RuntimeException("课程不存在");
        }
        if (!Objects.equals(existingCourse.getInstructorId(), coachId)) {
            throw new RuntimeException("无权修改该课程");
        }

        Course courseToUpdate = new Course();
        courseToUpdate.setCourseId(courseId);
        courseToUpdate.setCourseName((String) payload.get("course_name"));
        courseToUpdate.setSchedule((String) payload.get("schedule"));
        courseToUpdate.setCapacity((Integer) payload.get("capacity"));
        courseToUpdate.setDescription((String) payload.get("description"));
        // 注意：instructor_id 和 status 不应在此处被随意修改

        courseMapper.updateById(courseToUpdate);
    }

    @Override
    @Transactional
    public void deleteCourse(Integer courseId, Integer coachId) {
        Course existingCourse = courseMapper.selectById(courseId);
        if (existingCourse == null) {
            // 如果课程本就不存在，可以静默处理或返回成功，幂等性
            return;
        }
        if (!Objects.equals(existingCourse.getInstructorId(), coachId)) {
            throw new RuntimeException("无权删除该课程");
        }
        courseMapper.deleteById(courseId);
    }

    @Override
    public Course getCourseDetail(Integer courseId, Integer coachId) {
        Course course = courseMapper.selectById(courseId);
        if (course == null) {
            throw new RuntimeException("课程不存在");
        }
        if (!Objects.equals(course.getInstructorId(), coachId)) {
            throw new RuntimeException("无权查看该课程");
        }
        return course;
    }
}

