package com.qi.backend.entity;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class Major {
    private Long majorId;
    private String majorName;
    private String majorCode;
    private Long collegeId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
