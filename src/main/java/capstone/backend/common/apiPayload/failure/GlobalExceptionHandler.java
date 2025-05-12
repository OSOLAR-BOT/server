package capstone.backend.common.apiPayload.failure;

import capstone.backend.common.apiPayload.failure.customException.JWTException;
import capstone.backend.common.apiPayload.failure.customException.UserException;
import capstone.backend.common.apiPayload.failure.customExceptionStatus.JWTExceptionStatus;
import capstone.backend.common.apiPayload.failure.customExceptionStatus.UserExceptionStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    // [JWT]
    @ExceptionHandler(JWTException.TokenNullException.class)
    public ResponseEntity<ExceptionApiResponse> handleException(JWTException.TokenNullException e){
        log.error("[GlobalExceptionHandler] JWTException.TokenNullException occurred");
        return ResponseEntity
                .status(
                        JWTExceptionStatus.TOKEN_NULL.getHttpStatus()
                )
                .body(
                        new ExceptionApiResponse(
                                false, JWTExceptionStatus.TOKEN_NULL.getCode(), JWTExceptionStatus.TOKEN_NULL.getMessage()
                        )
                );
    }

    @ExceptionHandler(JWTException.TokenExpiredException.class)
    public ResponseEntity<ExceptionApiResponse> handleException(JWTException.TokenExpiredException e){
        log.error("[GlobalExceptionHandler] JWTException.TokenExpiredException occurred");
        return ResponseEntity
                .status(
                        JWTExceptionStatus.TOKEN_EXPIRED.getHttpStatus()
                )
                .body(
                        new ExceptionApiResponse(
                                false, JWTExceptionStatus.TOKEN_EXPIRED.getCode(), JWTExceptionStatus.TOKEN_EXPIRED.getMessage()
                        )
                );
    }

    @ExceptionHandler(JWTException.TokenTypeNotAccessException.class)
    public ResponseEntity<ExceptionApiResponse> handleException(JWTException.TokenTypeNotAccessException e){
        log.error("[GlobalExceptionHandler] JWTException.TokenTypeNotAccessException occurred");
        return ResponseEntity
                .status(
                        JWTExceptionStatus.TOKEN_TYPE_NOT_ACCESS.getHttpStatus()
                )
                .body(
                        new ExceptionApiResponse(
                                false, JWTExceptionStatus.TOKEN_TYPE_NOT_ACCESS.getCode(), JWTExceptionStatus.TOKEN_TYPE_NOT_ACCESS.getMessage()
                        )
                );
    }


    // [AUTH]
    @ExceptionHandler(UserException.UsernameAlreadyExistException.class)
    public ResponseEntity<ExceptionApiResponse> handleException(UserException.UsernameAlreadyExistException e){
        log.error("[GlobalExceptionHandler] UserException.UsernameAlreadyExistException occurred");
        return ResponseEntity
                .status(
                        UserExceptionStatus.USERNAME_ALREADY_EXIST.getHttpStatus()
                )
                .body(
                        new ExceptionApiResponse(
                                false, UserExceptionStatus.USERNAME_ALREADY_EXIST.getCode(), UserExceptionStatus.USERNAME_ALREADY_EXIST.getMessage()
                        )
                );
    }

    @ExceptionHandler(UserException.PasswordNotValidException.class)
    public ResponseEntity<ExceptionApiResponse> handleException(UserException.PasswordNotValidException e){
        log.error("[GlobalExceptionHandler] UserException.PasswordNotValidException occurred");
        return ResponseEntity
                .status(
                        UserExceptionStatus.PASSWORD_NOT_VALID.getHttpStatus()
                )
                .body(
                        new ExceptionApiResponse(
                                false, UserExceptionStatus.PASSWORD_NOT_VALID.getCode(), UserExceptionStatus.PASSWORD_NOT_VALID.getMessage()
                        )
                );
    }

    @ExceptionHandler(UserException.TokenNotValidException.class)
    public ResponseEntity<ExceptionApiResponse> handleException(UserException.TokenNotValidException e){
        log.error("[GlobalExceptionHandler] UserException.TokenNotValidException occurred");
        return ResponseEntity
                .status(
                        UserExceptionStatus.TOKEN_NOT_VALID.getHttpStatus()
                )
                .body(
                        new ExceptionApiResponse(
                                false, UserExceptionStatus.TOKEN_NOT_VALID.getCode(), UserExceptionStatus.TOKEN_NOT_VALID.getMessage()
                        )
                );
    }

    @ExceptionHandler(UserException.UsernameNotExistException.class)
    public ResponseEntity<ExceptionApiResponse> handleException(UserException.UsernameNotExistException e){
        log.error("[GlobalExceptionHandler] UserException.UsernameNotExistException occurred");
        return ResponseEntity
                .status(
                        UserExceptionStatus.USERNAME_NOT_EXIST.getHttpStatus()
                )
                .body(
                        new ExceptionApiResponse(
                                false, UserExceptionStatus.USERNAME_NOT_EXIST.getCode(), UserExceptionStatus.USERNAME_NOT_EXIST.getMessage()
                        )
                );
    }
}