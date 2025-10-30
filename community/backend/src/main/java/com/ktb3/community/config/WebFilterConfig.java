package com.ktb3.community.config;

import com.ktb3.community.common.filter.JwtAuthFilter;
import jakarta.servlet.Filter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// 서블릿필터를 애플리케이션에 등록하는 스프링 설정 클래스
@Configuration
@RequiredArgsConstructor
public class WebFilterConfig {

    private final JwtAuthFilter jwtAuthFilter;

    // 커스텀 필터를 서블릿 컨테이너에 명시적으로 등록
    // 컨트롤러 진입 전 요청을 가로채 토큰 추출·검증을 수행하고, 유효 시 사용자 정보를 컨텍스트/요청 속성에 저장
    @Bean
    public FilterRegistrationBean<Filter> jwtFilter() {
        FilterRegistrationBean<Filter> filterFilterRegistrationBean = new FilterRegistrationBean<>();
        filterFilterRegistrationBean.setFilter(jwtAuthFilter);
        filterFilterRegistrationBean.addUrlPatterns("/*");
        filterFilterRegistrationBean.setOrder(1);
        return filterFilterRegistrationBean;
    }
}
