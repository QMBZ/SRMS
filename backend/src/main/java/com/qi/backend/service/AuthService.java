package com.qi.backend.service;

import com.qi.backend.entity.User;
import com.qi.backend.util.JwtUtil;
import com.qi.backend.util.security.crypto.password.PasswordEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    /**
     * 用户登录
     * @param username 用户名（学号/工号）
     * @param password 明文密码
     * @return 生成的 JWT Token
     */
    public String login(String username, String password) {
        // 根据用户名查询用户
        User user = userService.getUserByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 验证账号状态
        if (user.getStatus() == 0) {
            throw new RuntimeException("账号已被禁用，请联系管理员");
        }

        // 验证密码
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        // 用户类型
        Integer roleId = user.getRoleId();

        // 返回 jwt
        return jwtUtil.createToken(user.getUserId(), roleId);
    }

    /**
     * 用户注册（等同于添加用户，学籍系统专用）
     * @param user 前端传入的用户信息（明文密码）
     * @return 是否注册成功
     */
    public boolean register(User user) {
        // 1. 校验用户名是否已存在
        User existUser = userService.getUserByUsername(user.getUserName());
        if (existUser != null) {
            return false;
        }

        // 2. 密码加密
        String rawPassword = user.getPassword();
        String encodedPassword = passwordEncoder.encode(rawPassword);
        user.setPassword(encodedPassword);

        // 3. 默认状态启用（1）
        if (user.getStatus() == null) {
            user.setStatus(1);
        }

        // 4. 调用 UserService 添加用户
        return userService.addUser(user);
    }

    /**
     * 用户登出
     */
    public void logout() {
        // 扩展：可加入Token黑名单
    }
}