package com.qi.backend.entity;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class Role {
    private Integer roleId;
    private String roleName;
    private String roleDesc;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
