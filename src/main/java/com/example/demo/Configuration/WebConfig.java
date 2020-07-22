package com.example.demo.Configuration;

import com.example.demo.interceptor.ProjectLockedInterceptor;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // registry.addInterceptor(new
        // ProjectLockedInterceptor()).addPathPatterns("/mobile/**")
        // .excludePathPatterns("/mobile/project/all", "/mobile/me");
    }
}