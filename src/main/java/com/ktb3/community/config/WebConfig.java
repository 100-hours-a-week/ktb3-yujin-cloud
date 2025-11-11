package com.ktb3.community.config;
import com.ktb3.community.auth.resolver.AuthMemberIdResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final AuthMemberIdResolver authMemberIdResolver;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**") // CORS 허용할 URL 패턴
                .allowedOrigins(
                        "http://localhost:3000",   // express 서버 사용
                        "http://127.0.0.1:5500"
                )
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowCredentials(true) // 쿠키나 인증정보 포함 시 true
                .maxAge(3600); // preflight 캐시 유지 시간(초)
    }

    // 리졸버 등록
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(authMemberIdResolver);
    }
}