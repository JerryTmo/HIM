package com.example.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        // 只為 Controller 層的類添加 /api 前綴
        configurer.addPathPrefix("/api", c -> c.getPackageName().startsWith("com.example.controller"));
    }
}
