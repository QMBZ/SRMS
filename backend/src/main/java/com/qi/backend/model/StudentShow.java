package com.qi.backend.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class StudentShow {
    private Long studentId;             // 学生 ID，自增
    private String StudentNo;           // 学号
    private String realName;            // 学生真实姓名
    private String gender;              // 学生性别
    private String nation;              // 学生民族
    private String nativePlace;         // 学生籍贯
    private String phone;               // 学生电话
    private String email;               // 学生邮箱
    private String photoUrl;            // 学生头像 URL
    private Long collegeId;             // 学生的学院 ID
    private Long majorId;               // 学生的专业 ID
    private Long classId;               // 学生的班级 ID
    private LocalDate enrollmentTime;   // 入学时间
    private LocalDate graduationTime;   // 毕业时间
    private String studentStatus;       // 学籍状态：在读/休学/复学/保留/毕业/结业/退学/...
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
