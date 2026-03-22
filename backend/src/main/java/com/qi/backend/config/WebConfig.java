package com.qi.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    // 拦截器
    private final LoginInterceptor loginInterceptor;

    // 配置跨域请求
    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        registry.addMapping("/**")
                // 这里是测试，所以就允许全部域名
                // .allowedOrigins("http://localhost:*")
                .allowedOriginPatterns("*")
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

    /**
     * 配置拦截规则
     */
    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        if (loginInterceptor == null) {
            return;
        }

        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/**")         // 拦截所有请求
                .excludePathPatterns(           // 放行接口
                        "/auth/login",          // 登录
                        "/auth/register"        // 注册
                );
    }
}
