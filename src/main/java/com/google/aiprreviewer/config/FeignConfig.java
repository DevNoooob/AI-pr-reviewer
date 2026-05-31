package com.google.aiprreviewer.config;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {
    @Value("${github.token}")
    private String token;

    /**
     * 设置拦截器，在请求头中加入我的github Token，防止匿名请求限流
     * @return
     */
    @Bean
    public RequestInterceptor githubInterceptor() {
        return template -> {
            template.header(
                    "Authorization",
                    token
            );
        };
    }
}
