package com.example.back2.entity;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "teacher")
@Data
public class Teacher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

   @Column(name = "tea_id")
    private String teaId;

    @Column(name = "tea_password")
    private String teaPassword;

    @Column(name = "tea_name")
    private String teaName;

    @Column(name ="tea_age")
    private Integer teaAge;

    @Column(name ="tea_gender")
    private String teaGender;

    @Column(name = "tea_email")
    private String teaEmail;

    @Column(name = "tea_class1")
    private String teaClass1;

    @Column(name = "tea_class2")
    private String teaClass2;

}
