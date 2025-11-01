package com.ktb3.community.auth.resolver;

import com.ktb3.community.auth.annotation.AuthMemberId;
import com.ktb3.community.common.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class AuthMemberIdResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        // 파라미터 타입이 Long 이고, @AuthMemberId 가 붙어 있으면 지원
        return parameter.hasParameterAnnotation(AuthMemberId.class)
                && Long.class.equals(parameter.getParameterType());
    }

    //실제 @AuthMemberId 어노테이션이 붙은 파라미터를 처리
    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ) throws Exception {

        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        Object memberId = request.getAttribute("memberId");

        if (memberId == null) {
            // 필터에서 세팅이 안 되었거나 인증 실패 케이스
            throw new BusinessException(HttpStatus.UNAUTHORIZED, "인증 정보가 없습니다.");
        }

        return memberId;
    }

}
