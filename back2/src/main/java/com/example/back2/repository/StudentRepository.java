package com.example.back2.repository;

import com.example.back2.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {//JpaRepository 提供了对 student 表的基本 CRUD 操作

}






