package com.osolar.obot.common.apiPayload.failure.customExceptionStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum JWTExceptionStatus {
    TOKEN_NULL(HttpStatus.BAD_REQUEST, "40010", "토큰은 NULL 값일 수 없습니다."),
    TOKEN_EXPIRED(HttpStatus.BAD_REQUEST, "40011", "만료기간이 지난 토큰입니다."),
    TOKEN_TYPE_NOT_ACCESS(HttpStatus.BAD_REQUEST, "40012", "토큰의 타입이 인증용이 아닙니다."),

    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}