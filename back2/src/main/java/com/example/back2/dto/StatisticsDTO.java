package com.example.back2.dto;

import lombok.Data;

@Data
public class StatisticsDTO {
    private String stuId;
    private String stuName;
    private String courseId;
    private String courseName;
    private String teaId;
    private String teaName;
    private int presentCount;  // 出勤次数
    private int absentCount;   // 缺勤次数
    private int leaveCount;    // 请假次数
    private double attendanceRate;  // 出勤率
}


//
//后端修改：
//创建了 StatisticsDTO 类来封装统计信息
//在 AttendanceRepository 中添加了新的查询方法：
//findMaxAttendanceId: 获取特定班级的最大考勤ID
//findAllByTeaIdAndStuClass: 获取特定班级的所有考勤记录
//findByTeaIdAndStuClassAndStuId: 获取特定学生的所有考勤记录
//实现了 StatisticsController 的 /api/statistics/class 接口，提供：
//总考勤次数
//每个学生的详细考勤统计（出勤次数、缺勤次数、请假次数、出勤率）
//前端修改：
//更新了 Statistics.vue 组件：
//添加了显示总考勤次数的区域
//        更新表格列以显示完整的学生考勤信息
//添加了加载状态指示器
//        实现了与后端API的数据交互
//现在系统可以：
//
//显示班级已进行的总考勤次数
//显示每个学生的详细考勤信息，包括：
//基本信息（学号、姓名、课程信息、教师信息）
//考勤统计（出勤次数、缺勤次数、请假次数）
//出勤率（精确到小数点后两位）
//系统会自动计算这些统计信息，并在用户选择不同班级时动态更新数据。

