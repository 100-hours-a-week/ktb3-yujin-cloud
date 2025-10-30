package com.ktb3.community.auth.annotation;

import java.lang.annotation.*;

@Target(ElementType.PARAMETER) // 파라미터에만 붙일수 있음
@Retention(RetentionPolicy.RUNTIME) // 런타임에 정보 유지
public @interface AuthMemberId { } // 이 클래스를 annotation 타입으로 사용하겠다 선언
