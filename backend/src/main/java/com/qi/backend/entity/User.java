package com.qi.backend.entity;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class User {
    private Long userId;                // 用户 ID，自增
    private String userName;            // 用户名，学生学号，老师工号
    private String password;            // 用户密码，加密
    private String realName;            // 用户真实姓名
    private Integer roleId;             // 用户的角色类型 ID
    private Integer status;             // 用户的账号状态 0 和 1
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
