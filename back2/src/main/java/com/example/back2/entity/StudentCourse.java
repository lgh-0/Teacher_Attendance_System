package com.example.back2.entity;

//学生选课表
import jakarta.persistence.*;
import lombok.Data;
// 假设只有软工和编译原理两门课
@Entity
@Table(name = "student_course")
@Data

public class StudentCourse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "stu_id") // 学生学号
    private String stuId;

    @Column(name = "stu_name") // 学生姓名
    private String stuName;

    @Column(name = "course_id") // 课程号
    private String courseId;

    @Column(name = "course_name") // 课程名
    private String courseName;

    @Column(name="course_tea_id") // 课程老师的id
    private String courseTeaId;

    @Column(name = "course_teacher_name") // 课程老师
    private String courseTeaName;


    @Column(name = "course_class") // 课程班级
    private String courseClass;

}
