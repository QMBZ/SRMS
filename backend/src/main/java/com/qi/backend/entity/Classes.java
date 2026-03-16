package com.qi.backend.entity;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class Classes {
    private Long classId;
    private String className;
    private String classCode;
    private Long majorId;
    private Long collegeId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
