package capstone.backend.common.apiPayload.failure.customExceptionStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum UserExceptionStatus {
    USERNAME_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "40000", "이미 존재하는 사용자 아이디입니다."),
    PASSWORD_NOT_VALID(HttpStatus.BAD_REQUEST, "40001", "비밀번호가 일치하지 않습니다."),
    TOKEN_NOT_VALID(HttpStatus.BAD_REQUEST, "40002", "토큰이 유효하지 않습니다."),
    USERNAME_NOT_EXIST(HttpStatus.BAD_REQUEST, "40003", "존재하지 않는 사용자 아이디입니다."),

    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}


