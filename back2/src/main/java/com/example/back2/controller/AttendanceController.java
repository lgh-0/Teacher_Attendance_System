package com.example.back2.controller;

import com.example.back2.entity.Attendance;
import com.example.back2.repository.AttendanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/attendance")
@CrossOrigin(origins = "http://localhost:5174", allowCredentials = "true")

public class AttendanceController {
    private static final Logger logger = LoggerFactory.getLogger(AttendanceController.class);

    @Autowired
    private AttendanceRepository attendanceRepository;  //AttendanceRepository接口的实例

    @PostMapping("/submit/{teacherName}")
    public ResponseEntity<?> submitAttendance(
         @PathVariable String teacherName,
         @RequestBody List<Attendance> attendances) {
        try {
            logger.info("Receiving attendance submission for teacher: {} with {} records", 
                    teacherName, attendances.size());

            if (attendances.isEmpty()) {
                return ResponseEntity.badRequest().body(new ErrorResponse("没有考勤记录"));
            }

            // 获取教师ID和班级
            String teaId = attendances.get(0).getTeaId();
            String className = attendances.get(0).getStuClass();
            int attendanceId = attendances.get(0).getAttendanceId();
            
            logger.info("Processing attendance for class {} with attendance ID {}", 
                    className, attendanceId);

            // 设置当前时间
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            String formattedTime = now.format(formatter);
            LocalDateTime truncatedTime = LocalDateTime.parse(formattedTime, formatter);

            // 为每条记录设置考勤时间和教师名称
            // 直接使用前端传入的attendanceId
            attendances.forEach(attendance -> {
                attendance.setAttendanceTime(String.valueOf(truncatedTime));
                attendance.setTeaName(teacherName);
                logger.debug("Processing attendance record: student={}, class={}, time={}, attendanceId={}", 
                        attendance.getStuName(), 
                        attendance.getStuClass(),
                        attendance.getAttendanceTime(),
                        attendance.getAttendanceId());
            });

            // 保存考勤记录
            List<Attendance> savedAttendances = attendanceRepository.saveAll(attendances);
            logger.info("Successfully saved {} attendance records for class {} with attendance ID {}", 
                    savedAttendances.size(), className, attendanceId);
            
            return ResponseEntity.ok().body(savedAttendances);
        } catch (Exception e) {
            logger.error("Failed to submit attendance", e);
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("提交考勤失败: " + e.getMessage()));
        }
    }

    @GetMapping("/count/{teacherId}/{className}")
    public ResponseEntity<?> getAttendanceCount(
            @PathVariable String teacherId,
            @PathVariable String className) {
        try {
            logger.info("Getting attendance count for teacherId: {} and className: {}", teacherId, className);
            
            // 获取特定班级的最大考勤ID
            Integer maxId = attendanceRepository.findMaxAttendanceId(teacherId, className);
            logger.info("Found max attendance ID: {}", maxId);
            
            // 如果没有考勤记录，返回0
            int count = maxId != null ? maxId : 0;
            logger.info("Returning attendance count: {}", count);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            logger.error("Failed to get attendance count for teacherId: {} and className: {}", teacherId, className, e);
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("获取考勤次数失败: " + e.getMessage()));
        }
    }

    // 错误响应内部类
    private static class ErrorResponse {
        private String message;

        public ErrorResponse(String message) {
            this.message = message;
        }

    }
}
