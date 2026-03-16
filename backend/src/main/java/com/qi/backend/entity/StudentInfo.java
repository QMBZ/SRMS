package com.qi.backend.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class StudentInfo {
    private Long studentId;
    private String StudentNo;
    private String realName;
    private String gender;
    private String idCard;
    private String nation;
    private String nativePlace;
    private String phone;
    private String email;
    private String photoUrl;
    private Long collegeId;
    private Long majorId;
    private Long classId;
    private LocalDate enrollmentTime;
    private LocalDate graduationTime;
    private String studentStatus;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
