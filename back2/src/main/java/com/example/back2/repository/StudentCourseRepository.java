package com.example.back2.repository;

import com.example.back2.entity.StudentCourse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentCourseRepository extends JpaRepository<StudentCourse, Long> {


    // 通过教师ID和班级查询学生列表  要在学生选课表里查询学生列表
    List<StudentCourse> findByCourseTeaIdAndCourseClass(String courseTeaId, String courseClass);
}
