package com.qi.backend.entity;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class College {
    private Long collegeId;             // 学院 ID，自增
    private String collegeName;         // 学院名称
    private String collegeCode;         // 学院编码，比如：25
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
