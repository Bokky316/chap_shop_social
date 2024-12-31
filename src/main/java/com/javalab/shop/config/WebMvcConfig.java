package com.javalab.shop.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 정적 리소스 패핑을 위한 설정
 * WebMvcConfigurer 인터페이스를 구현하여 정적 리소스 매핑을 설정
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${uploadPath}")
    String uploadPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 1. 동적인 이미지(상품이미지) /images/** 요청이 오면 uploadPath로 매핑
        registry.addResourceHandler("/images/**") // /images/** 요청이 오면 uploadPath로 매핑
                .addResourceLocations(uploadPath); // 로컬 컴퓨터에 저장된 파일을 읽어올 root 경로를 설정

        // 2. 정적 이미지(로고, 아이콘) /static-images/** 요청이 오면 classpath:/static/images/ 경로로 매핑
        registry.addResourceHandler("/static-images/**")
                .addResourceLocations("classpath:/static/images/"); // 정적 리소스

    }
}
