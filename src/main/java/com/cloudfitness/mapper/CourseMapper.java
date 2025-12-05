package com.cloudfitness.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloudfitness.entity.Course;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface CourseMapper extends BaseMapper<Course> {
    List<Course> selectUpcomingCourses();
    List<Course> selectByInstructorId(@Param("instructorId") Integer instructorId);
    List<Course> selectAdminCourses(@Param("courseName") String courseName,
                                    @Param("instructorId") Integer instructorId,
                                    @Param("startDate") String startDate,
                                    @Param("endDate") String endDate,
                                    @Param("status") String status,
                                    @Param("offset") Integer offset,
                                    @Param("pageSize") Integer pageSize);
    Integer countAdminCourses(@Param("courseName") String courseName,
                             @Param("instructorId") Integer instructorId,
                             @Param("startDate") String startDate,
                             @Param("endDate") String endDate,
                             @Param("status") String status);
}


