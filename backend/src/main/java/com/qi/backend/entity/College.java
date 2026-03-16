package com.qi.backend.entity;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class College {
    private Long collegeId;
    private String collegeName;
    private String collegeCode;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
