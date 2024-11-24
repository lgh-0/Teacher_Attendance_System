package com.example.back2.controller;

import com.example.back2.entity.Teacher;
import com.example.back2.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class LoginController {

    @Autowired
    private TeacherRepository teacherRepository;


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        System.out.println("调用的是这个函数");
        String teaId = credentials.get("username"); // 
        String password = credentials.get("password");

        Teacher teacher = teacherRepository.findByTeaId(teaId);
        
        if (teacher != null && teacher.getTeaPassword().equals(password)) {
            // 
            Teacher safeTeacher = new Teacher();
            safeTeacher.setId(teacher.getId());
            safeTeacher.setTeaId(teacher.getTeaId());
            safeTeacher.setTeaName(teacher.getTeaName());
            safeTeacher.setTeaEmail(teacher.getTeaEmail());
            safeTeacher.setTeaClass1(teacher.getTeaClass1());
            safeTeacher.setTeaClass2(teacher.getTeaClass2());
            
            return ResponseEntity.ok(safeTeacher);
        }
        
        return ResponseEntity.badRequest().body("Invalid credentials");
    }
}
