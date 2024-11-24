package com.example.back2.repository;

import com.example.back2.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
// 从教师表teacher2中查找教师信息
public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    Teacher findByTeaId(String teaId);
    Teacher findByTeaName(String teaName);
}
