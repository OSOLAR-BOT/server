package com.osolar.obot.common.apiPayload.success;

import com.osolar.obot.common.apiPayload.BaseApiResponse;
import com.osolar.obot.domain.chat.dto.response.ChatUserResponse;
import com.osolar.obot.domain.chat.dto.response.SessionResponse;
import com.osolar.obot.domain.user.dto.response.LoginResponse;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class SuccessApiResponse <T> extends BaseApiResponse {
    private final T response;

    public SuccessApiResponse(Boolean isSuccess, String code, String message, T response) {
        super(isSuccess, code, message);
        this.response = response;
    }

    // [AUTH]
    public static SuccessApiResponse<Void> Register(){
        return new SuccessApiResponse<>(true, HttpStatus.CREATED.toString()
                , "회원가입 성공", null);
    }
    public static SuccessApiResponse<LoginResponse> Login(LoginResponse response){
        return new SuccessApiResponse<>(true, HttpStatus.OK.toString()
                , "로그인 성공", response);
    }
    public static SuccessApiResponse<Void> ReissueToken(){
        return new SuccessApiResponse<>(true, HttpStatus.OK.toString()
                , "토큰 재발급 성공", null);
    }

    // [CHAT]
    public static SuccessApiResponse<SessionResponse> CreateSession(SessionResponse sessionResponse) {
        return new SuccessApiResponse<>(true, HttpStatus.CREATED.toString()
                , "채팅 세션 생성 성공", sessionResponse);
    }

    public static SuccessApiResponse<ChatUserResponse> GetResponse(ChatUserResponse chatUserResponse) {
        return new SuccessApiResponse<>(true, HttpStatus.CREATED.toString(),
            "챗봇 응답 생성 성공", chatUserResponse);
    }

    // [INQUIRY]
    public static SuccessApiResponse<Void> GetInquiry() {
        return new SuccessApiResponse<>(true, HttpStatus.CREATED.toString()
        , "사용자 만족도 저장 성공", null);
    }
}