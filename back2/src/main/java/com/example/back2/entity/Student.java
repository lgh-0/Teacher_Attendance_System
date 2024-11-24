package com.example.back2.entity;

import jakarta.persistence.*;
import lombok.Data;
@Entity
@Table(name="student")
@Data
public class Student {

    @Id // 指定主键
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 自增策略
    @Column(name = "id")
    private Long id;

    @Column(name = "stu_id")
    private String stuId;

    @Column(name = "stu_name")
    private String stuName;

    @Column(name = "stu_age")
    private String stuAge;

    @Column(name="stu_gender")
    private String stuGender;
}
