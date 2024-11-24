package com.example.back2.repository;

import com.example.back2.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    // 通过教师ID、课程ID和班级查询考勤记录
    List<Attendance> findByTeaIdAndCourseIdAndStuClass(String teaId, String courseId, String stuClass);
    
    // 获取特定班级的最大考勤ID（即已考勤次数）
    @Query("SELECT COALESCE(MAX(a.attendanceId), 0) FROM Attendance a WHERE a.teaId = :teaId AND a.stuClass = :stuClass")
    Integer findMaxAttendanceId(@Param("teaId") String teaId, @Param("stuClass") String stuClass);
    
    // 获取特定班级的所有考勤记录
    @Query("SELECT a FROM Attendance a WHERE a.teaId = :teaId AND a.stuClass = :stuClass")
    List<Attendance> findAllByTeaIdAndStuClass(@Param("teaId") String teaId, @Param("stuClass") String stuClass);
}
