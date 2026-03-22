package com.qi.backend.config;

import com.qi.backend.util.JwtUtil;

import lombok.RequiredArgsConstructor;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
@RequiredArgsConstructor
public class LoginInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;

    /**
     * 在请求进入 Controller 之前执行
     */
    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        // 1. 获取请求头里的 token（约定前端传 Authorization: Bearer token）
        String token = request.getHeader("Authorization");

        // 2. 判断 token 是否为空
        if (token == null || token.isEmpty()) {
            response.setStatus(401);
            response.getWriter().write("未登录，请先登录");
            return false;
        }

        // 3. 去掉 Bearer 前缀（标准格式）
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // 4. 验证 token 是否有效
        boolean verify = jwtUtil.verify(token);
        if (!verify) {
            response.setStatus(401);
            response.getWriter().write("token 无效或已过期");
            return false;
        }

        // 5. 验证通过，放行
        return true;
    }
}
