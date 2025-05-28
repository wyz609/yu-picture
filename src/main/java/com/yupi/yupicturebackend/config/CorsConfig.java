package com.yupi.yupicturebackend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Class name: CorConfig
 * Package: com.yupi.yupicturebackend.config
 * Description:
 *
 * @Create: 2025/4/29 0:14
 * @Author: jay
 * @Version: 1.0
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 覆盖所有请求
        registry.addMapping("/**")
                // 允许发送cookie
                .allowCredentials(true)
                // 放行哪些域名(必须用 patterns，否则 * 会和 allowCredentials冲突)
                .allowedOriginPatterns("*")
                .allowedOrigins("http://localhost:5173")
                .allowedHeaders("*")
                .exposedHeaders("*")
                .allowedMethods("GET", "HEAD", "POST", "PUT", "DELETE", "OPTIONS");

    }
}

