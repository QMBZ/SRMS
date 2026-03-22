package com.qi.backend.entity;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class Role {
    private Integer roleId;             // 角色 ID，自增
    private String roleName;            // 角色名称
    private String roleDesc;            // 角色描述
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
