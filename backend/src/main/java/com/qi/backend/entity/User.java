package com.qi.backend.entity;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class User {
    private Long userId;
    private String userName;
    private String password;
    private String realName;
    private Integer roleId;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
