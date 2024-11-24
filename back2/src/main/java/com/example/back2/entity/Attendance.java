package com.example.back2.entity;


// 考勤表2
import jakarta.persistence.*;
import lombok.Data;
@Entity
@Table(name = "attendance")
@Data
public class Attendance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "stu_id")
    private String stuId;

    @Column(name = "stu_name")
    private String stuName;

    @Column(name = "stu_class")
    private String stuClass;

    @Column(name = "course_id")
    private String courseId;

    @Column(name = "course_name")
    private String courseName;

    @Column(name = "tea_id")
    private String teaId;

    @Column(name = "tea_name")
    private String teaName;

    @Column(name = "stu_statue")
    private int stuStatue;

    //第几次考勤
    @Column(name = "attendance_id")
    private int attendanceId;

    //某次考勤的时间
    @Column(name = "attendance_time")
    private String attendanceTime;
}
