package com.ktb3.community.common.exception;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
@Builder
public class ErrorResponse {
    private final int status;         // HTTP 상태 코드
    private final String code;        // 예: BAD_REQUEST, INTERNAL_SERVER_ERROR
    private final String message;     // 사용자에게 보여줄 메시지
    private final LocalDateTime timestamp;

    public static ErrorResponse of(HttpStatus status, String message) {
        return ErrorResponse.builder()
                .status(status.value())
                .code(status.name())
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
