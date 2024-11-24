package com.example.back2.controller;

import com.example.back2.entity.StudentCourse;
import com.example.back2.entity.Teacher;
import com.example.back2.entity.Attendance;
import com.example.back2.repository.AttendanceRepository;
import com.example.back2.repository.StudentCourseRepository;
import com.example.back2.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

// 渲染学生列表控制器
@RestController
@RequestMapping("/api")
public class StudentController {

    @Autowired
    private StudentCourseRepository studentCourseRepository;

    @Autowired
    private TeacherRepository teacherRepository;


    // 通过前端登录教师的id          获取教师所教的班级列表
    @GetMapping("/teacher/classes/{teacherId}")
    public ResponseEntity<?> getTeacherClasses(@PathVariable String teacherId) {
        System.out.println("Received request for teacher classes with teacherId: " + teacherId);

        Teacher teacher = teacherRepository.findByTeaId(teacherId);

        if (teacher == null) {
            System.out.println("Teacher not found with teacherId: " + teacherId);
            return ResponseEntity.notFound().build();
        }

        System.out.println("Found teacher: " + teacher.getTeaName() + ", Class1: " + teacher.getTeaClass1() + ", Class2: " + teacher.getTeaClass2());

        List<String> classes = new ArrayList<>();
        if (teacher.getTeaClass1() != null && !teacher.getTeaClass1().isEmpty()) {
            classes.add(teacher.getTeaClass1());
        }
        if (teacher.getTeaClass2() != null && !teacher.getTeaClass2().isEmpty()) {
            classes.add(teacher.getTeaClass2());
        }

        System.out.println("Returning classes: " + classes);
        return ResponseEntity.ok(classes);
    }

    // 通过教师的id和className就可以在 StudentCourse 中获取某个班级的学生列表
    @GetMapping("/students/class/{teacherId}/{className}")
    public ResponseEntity<?> getStudentsByTeacherAndClass(
            @PathVariable String teacherId,
            @PathVariable String className) {
        System.out.println("Received request for students with teacherId: " + teacherId + ", className: " + className);
        
        List<StudentCourse> students = studentCourseRepository.findByCourseTeaIdAndCourseClass(teacherId, className);
        System.out.println("Found " + students.size() + " students");
        
        // 构建响应数据
        Map<String, Object> response = new HashMap<>();
        response.put("totalStudents", students.size());
        response.put("students", students);
        
        return ResponseEntity.ok(response);
    }
    private static class MaxThAttendancesResponse {
        private Integer maxThAttendances;

    }
}
