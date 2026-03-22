package com.qi.backend.model;

import lombok.Data;

@Data
public class ChangePasswordRequest {
    // 修改密码的请求模型
    Long userId;
    String oldPassword;
    String newPassword;
}
