package com.ktb3.community.config;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // CORS 허용할 URL 패턴
                .allowedOrigins(
                        "http://localhost:3000",   // express 서버 사용
                        "http://127.0.0.1:5500"
                )
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowCredentials(true) // 쿠키나 인증정보 포함 시 true
                .maxAge(3600); // preflight 캐시 유지 시간(초)
    }
}