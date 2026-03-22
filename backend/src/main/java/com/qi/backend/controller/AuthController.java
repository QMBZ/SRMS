package com.qi.backend.controller;

import com.qi.backend.entity.User;
import com.qi.backend.service.AuthService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // 登录
    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password) {
        return authService.login(username, password);
    }

    // 注册（添加用户）
    @PostMapping("/register")
    public boolean register(@RequestBody User user) {
        return authService.register(user);
    }

    // 登出
    @PostMapping("/logout")
    public void logout() {
        authService.logout();
    }
}