package com.qi.backend.entity;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class AdminCollege {
    private Long id;
    private Long userId;
    private Long collegeId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
