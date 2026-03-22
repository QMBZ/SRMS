package com.qi.backend.model;

import lombok.Data;

@Data
public class LoginRequest {
    // 登陆请求模型
    String username;
    String password;
}
