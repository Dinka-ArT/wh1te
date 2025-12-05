package com.cloudfitness.service.impl;

import com.cloudfitness.entity.Course;
import com.cloudfitness.entity.Reservation;
import com.cloudfitness.entity.User;
import com.cloudfitness.mapper.CourseMapper;
import com.cloudfitness.mapper.ReservationMapper;
import com.cloudfitness.mapper.UserMapper;
import com.cloudfitness.service.AdminCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminCourseServiceImpl implements AdminCourseService {
    @Autowired
    private CourseMapper courseMapper;
    
    @Autowired
    private ReservationMapper reservationMapper;
    
    @Autowired
    private UserMapper userMapper;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public Map<String, Object> getCourses(String courseName, Integer instructorId,
                                         String startDate, String endDate, String status,
                                         Integer page, Integer pageSize) {
        Integer offset = (page - 1) * pageSize;
        List<Course> courses = courseMapper.selectAdminCourses(courseName, instructorId, startDate, endDate, status, offset, pageSize);
        Integer total = courseMapper.countAdminCourses(courseName, instructorId, startDate, endDate, status);
        
        List<Map<String, Object>> list = new ArrayList<>();
        for (Course course : courses) {
            Map<String, Object> item = new HashMap<>();
            item.put("course_id", course.getCourseId());
            item.put("course_name", course.getCourseName());
            item.put("instructor_id", course.getInstructorId());
            if (course.getInstructorId() != null) {
                User instructor = userMapper.selectById(course.getInstructorId());
                if (instructor != null) {
                    item.put("instructor_name", instructor.getUsername());
                }
            }
            item.put("schedule", course.getSchedule());
            item.put("capacity", course.getCapacity());
            item.put("status", course.getStatus());
            Integer reservedCount = reservationMapper.countByCourseId(course.getCourseId());
            item.put("reserved_count", reservedCount);
            list.add(item);
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        result.put("page", page);
        result.put("page_size", pageSize);
        return result;
    }

    @Override
    public Map<String, Object> getCourseDetail(Integer courseId) {
        Course course = courseMapper.selectById(courseId);
        if (course == null) {
            throw new RuntimeException("课程不存在");
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("course_id", course.getCourseId());
        result.put("course_name", course.getCourseName());
        result.put("instructor_id", course.getInstructorId());
        if (course.getInstructorId() != null) {
            User instructor = userMapper.selectById(course.getInstructorId());
            if (instructor != null) {
                result.put("instructor_name", instructor.getUsername());
            }
        }
        result.put("schedule", course.getSchedule());
        result.put("capacity", course.getCapacity());
        result.put("description", course.getDescription());
        result.put("status", course.getStatus());
        
        Integer reservedCount = reservationMapper.countByCourseId(courseId);
        result.put("reserved_count", reservedCount);
        
        List<Reservation> reservations = reservationMapper.selectByCourseId(courseId);
        List<Map<String, Object>> reservationList = new ArrayList<>();
        for (Reservation reservation : reservations) {
            Map<String, Object> res = new HashMap<>();
            res.put("reservation_id", reservation.getReservationId());
            res.put("user_id", reservation.getUserId());
            User user = userMapper.selectById(reservation.getUserId());
            if (user != null) {
                res.put("username", user.getUsername());
                res.put("phone_number", user.getPhoneNumber());
            }
            res.put("reservation_date", reservation.getReservationDate());
            res.put("status", reservation.getStatus());
            reservationList.add(res);
        }
        result.put("reservations", reservationList);
        
        return result;
    }

    @Override
    public Integer createCourse(String courseName, Integer instructorId, String schedule,
                               Integer capacity, String description) {
        Course course = new Course();
        course.setCourseName(courseName);
        course.setInstructorId(instructorId);
        course.setSchedule(LocalDateTime.parse(schedule.replace("Z", "").replace("T", " "), FORMATTER));
        course.setCapacity(capacity);
        course.setDescription(description);
        course.setStatus("upcoming");
        course.setCreatedAt(LocalDateTime.now());
        course.setUpdatedAt(LocalDateTime.now());
        courseMapper.insert(course);
        return course.getCourseId();
    }

    @Override
    public void updateCourse(Integer courseId, String courseName, Integer instructorId,
                            String schedule, Integer capacity, String description) {
        Course course = courseMapper.selectById(courseId);
        if (course == null) {
            throw new RuntimeException("课程不存在");
        }
        if (courseName != null) {
            course.setCourseName(courseName);
        }
        if (instructorId != null) {
            course.setInstructorId(instructorId);
        }
        if (schedule != null) {
            course.setSchedule(LocalDateTime.parse(schedule.replace("Z", "").replace("T", " "), FORMATTER));
        }
        if (capacity != null) {
            course.setCapacity(capacity);
        }
        if (description != null) {
            course.setDescription(description);
        }
        course.setUpdatedAt(LocalDateTime.now());
        courseMapper.updateById(course);
    }

    @Override
    public void deleteCourse(Integer courseId) {
        Course course = courseMapper.selectById(courseId);
        if (course == null) {
            throw new RuntimeException("课程不存在");
        }
        courseMapper.deleteById(courseId);
    }

    @Override
    public void updateCourseStatus(Integer courseId, String status) {
        Course course = courseMapper.selectById(courseId);
        if (course == null) {
            throw new RuntimeException("课程不存在");
        }
        course.setStatus(status);
        course.setUpdatedAt(LocalDateTime.now());
        courseMapper.updateById(course);
    }
}


