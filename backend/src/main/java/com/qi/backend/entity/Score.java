package com.qi.backend.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class Score {
    private Long id;
    private String studentNo;
    private String courseName;
    private BigDecimal score;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
