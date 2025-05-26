package com.osolar.obot.domain.chat.dto.response;

import com.osolar.obot.domain.user.entity.SessionStatus;
import com.osolar.obot.domain.user.entity.UserSession;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class SessionResponse {

    private final String sessionId;
    private final SessionStatus sessionStatus;
    private final LocalDateTime createdAt;

    public static SessionResponse toDTO(UserSession session) {
        return SessionResponse.builder()
            .sessionId(session.getId())
            .sessionStatus(session.getSessionStatus())
            .createdAt(session.getCreatedAt())
            .build();
    }

    @Builder
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ChatApiResponse {
        private String answer;
        private String summary;
    }
}