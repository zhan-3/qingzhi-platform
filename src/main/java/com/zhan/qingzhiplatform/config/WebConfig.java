package com.zhan.qingzhiplatform.config;

import com.zhan.qingzhiplatform.interceptor.AdminCheckInterceptor;
import com.zhan.qingzhiplatform.interceptor.LoginCheckInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private LoginCheckInterceptor loginCheckInterceptor;

    @Autowired
    private AdminCheckInterceptor adminCheckInterceptor;


    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginCheckInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/",
                        "/index.html",
                        "/assets/**",
                        "/favicon.svg",
                        "/icons.svg",
                        "/login",
                        "/register",
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                        "/v3/api-docs/**"
                );

        registry.addInterceptor(adminCheckInterceptor)
                .addPathPatterns("/admin/**");
    }
}
