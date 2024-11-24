package com.example.back2.controller;

import com.example.back2.dto.StatisticsDTO;
import com.example.back2.entity.Attendance;
import com.example.back2.entity.Teacher;
import com.example.back2.repository.AttendanceRepository;
import com.example.back2.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/statistics")
@CrossOrigin(origins = "http://localhost:5174", allowCredentials = "true")
public class StatisticsController {
    private static final Logger logger = LoggerFactory.getLogger(StatisticsController.class);

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @GetMapping("/class")
    public ResponseEntity<?> getStatistics(
            @RequestParam String teaId,
            @RequestParam String className) {
        try {
            logger.info("Getting statistics for teacher ID: {} and class: {}", teaId, className);

            // 获取该班级的所有考勤记录
            List<Attendance> attendances = attendanceRepository.findAllByTeaIdAndStuClass(teaId, className);
            logger.info("Found {} attendance records", attendances.size());

            // 按学生ID分组统计
            Map<String, List<Attendance>> studentAttendances = attendances.stream()
                    .collect(Collectors.groupingBy(Attendance::getStuId));

            List<StatisticsDTO> statistics = new ArrayList<>();
            
            // 计算每个学生的考勤统计
            for (Map.Entry<String, List<Attendance>> entry : studentAttendances.entrySet()) {
                List<Attendance> studentRecords = entry.getValue();
                if (studentRecords.isEmpty()) continue;

                Attendance firstRecord = studentRecords.get(0);
                StatisticsDTO dto = new StatisticsDTO();
                
                // 设置基本信息
                dto.setStuId(firstRecord.getStuId());
                dto.setStuName(firstRecord.getStuName());
                dto.setCourseId(firstRecord.getCourseId());
                dto.setCourseName(firstRecord.getCourseName());
                dto.setTeaId(firstRecord.getTeaId());
                dto.setTeaName(firstRecord.getTeaName());

                // 计算出勤统计
                int presentCount = 0;
                int absentCount = 0;
                int leaveCount = 0;

                for (Attendance record : studentRecords) {
                    switch (record.getStuStatue()) {
                        case 1: presentCount++; break;
                        case 2: absentCount++; break;
                        case 3: leaveCount++; break;
                    }
                }

                dto.setPresentCount(presentCount);
                dto.setAbsentCount(absentCount);
                dto.setLeaveCount(leaveCount);
                
                // 计算出勤率
                int totalAttendances = presentCount + absentCount + leaveCount;
                double attendanceRate = totalAttendances > 0 
                    ? (double) presentCount / totalAttendances * 100 
                    : 0.0;
                dto.setAttendanceRate(Math.round(attendanceRate * 100.0) / 100.0);  // 保留两位小数

                statistics.add(dto);
            }

            // 获取最大考勤次数
            Integer maxAttendanceId = attendanceRepository.findMaxAttendanceId(teaId, className);
            
            Map<String, Object> response = new HashMap<>();
            response.put("statistics", statistics);
            response.put("totalAttendances", maxAttendanceId != null ? maxAttendanceId : 0);
            
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error generating statistics", e);
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("获取统计数据失败: " + e.getMessage()));
        }
    }

    private static class ErrorResponse {
        private final String message;

        public ErrorResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
}