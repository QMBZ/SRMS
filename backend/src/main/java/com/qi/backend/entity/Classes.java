package com.qi.backend.entity;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class Classes {
    private Long classId;               // 班级 ID，自增
    private String className;           // 班级名称
    private String classCode;           // 班级编码，比如：2022253106
    private Long majorId;               // 班级对应的专业 ID
    private Long collegeId;             // 班级对于的学院 ID
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
