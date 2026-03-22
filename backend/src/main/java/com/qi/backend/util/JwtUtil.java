package com.qi.backend.util;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

@Component
public class JwtUtil {
    // 读取 jwt 的配置
    @Value("${jwt.secret}")
    private String secret;  // 密钥
    @Value("${jwt.expire}")
    private Long expire;    // token 有效期

    // 生成 token
    public String createToken(Long userId, Integer roleId) {
        return JWT.create()
                .withClaim("userId", userId)
                .withClaim("roleId", roleId)
                .withExpiresAt(new Date(System.currentTimeMillis() + expire))
                .sign(Algorithm.HMAC256(secret));
    }

    // 获取用户 ID
    public Long getUserId(String token) {
        try {
            return JWT.decode(token).getClaim("userId").asLong();
        } catch (Exception e) {
            return null;
        }
    }

    // 获取是否是管理员（可能不用）
    public Integer getIsAdmin(String token) {
        try {
            return JWT.decode(token).getClaim("roleId").asInt();
        } catch (Exception e) {
            return null;
        }
    }

    // 验证 token 是否有效
    public boolean verify(String token) {
        try {
            JWT.require(Algorithm.HMAC256(secret)).build().verify(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
