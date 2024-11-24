package com.example.back2.controller;

import com.example.back2.entity.Teacher;
import com.example.back2.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")

public class ProfileController {

    @Autowired
    private TeacherRepository teacherRepository;

    @GetMapping("/profile/{teaId}")
    public ResponseEntity<?> getTeacherProfile(@PathVariable String teaId) {

        System.out.println("con-back2-profile");

        Teacher teacher = teacherRepository.findByTeaId(teaId);
        if (teacher != null) {
            // 不返回密码
            teacher.setTeaPassword(null);
            return ResponseEntity.ok(teacher);
        }
        return ResponseEntity.notFound().build();
    }

}
