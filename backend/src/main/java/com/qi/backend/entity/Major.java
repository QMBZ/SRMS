package com.qi.backend.entity;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class Major {
    private Long majorId;               // 专业 ID，自增
    private String majorName;           // 专业名称
    private String majorCode;           // 专业编码，比如：2531
    private Long collegeId;             // 专业所属的学院的 ID
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
