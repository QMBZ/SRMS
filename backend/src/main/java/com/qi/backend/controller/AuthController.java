package com.qi.backend.controller;

import com.qi.backend.entity.User;
import com.qi.backend.model.LoginRequest;
import com.qi.backend.model.Result;
import com.qi.backend.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "用户认证模块", description = "登录、注册、登出接口")
public class AuthController {

    private final AuthService authService;

    // 登录
    @Operation(
        summary = "用户登录",
        description = "根据用户名和密码进行登录验证，验证成功返回Token令牌"
    )
    @PostMapping("/login")
    public Result<String> login(@RequestBody LoginRequest loginRequest) {
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();
        String token = authService.login(username, password);
        return Result.success("登录成功", token);
    }

    // 注册（添加用户）
    @Operation(
        summary = "用户注册",
        description = "创建新用户，用户名不可重复"
    )
    @PostMapping("/register")
    public Result<Boolean> register(@RequestBody User user) {
        boolean success = authService.register(user);
        if (success) {
            return Result.success("注册成功", true);
        } else {
            return Result.error("注册失败，用户名已存在");
        }
    }

    // 登出
    @Operation(
        summary = "用户登出",
        description = "这个接口目前没有任何效果"
    )
    @PostMapping("/logout")
    public Result<Boolean> logout() {
        authService.logout();
        return Result.success("登出成功", Boolean.TRUE);
    }
}