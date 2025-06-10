package com.osolar.obot.domain.chat.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.osolar.obot.common.handler.ChatWebSocketHandler;
import com.osolar.obot.domain.chat.dto.request.ChatStreamApiRequest;
import com.osolar.obot.domain.chat.dto.request.ChatUserRequest;
import com.osolar.obot.domain.chat.dto.response.SessionResponse;
import com.osolar.obot.domain.chat.entity.Chat;
import com.osolar.obot.domain.chat.repository.ChatRepository;
import com.osolar.obot.domain.user.entity.SessionStatus;
import com.osolar.obot.domain.user.entity.User;
import com.osolar.obot.domain.user.entity.UserSession;
import com.osolar.obot.domain.user.repository.UserRepository;
import com.osolar.obot.domain.user.repository.UserSessionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.socket.client.WebSocketConnectionManager;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class StreamingService {

    private final UserSessionRepository userSessionRepository;
    private final UserRepository userRepository;
    private final ChatRepository chatRepository;

    @Value("${external.ws-uri}")
    private String WS_URI;

    public SessionResponse createChatSession(String userId) {
        UserSession userSession = UserSession.builder()
                .userId(userId)
                .sessionStatus(SessionStatus.IN_PROGRESS)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        return SessionResponse.toDTO(userSessionRepository.save(userSession));
    }

    public void getStreamResponse(SseEmitter sseEmitter, String sessionId, ChatUserRequest chatUserRequest, String username) {
        log.info("[StreamingService - getStreamResponse]");
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // 1. 요청 JSON 구성
        ChatStreamApiRequest chatStreamApiRequest = ChatStreamApiRequest.builder()
                .action("$default")
                .question(chatUserRequest.getQuestion())
                .user(getMockUserData())
                .history(getMockHistoryList())
                .build();

        String requestJson;
        try {
            requestJson = new ObjectMapper().writeValueAsString(chatStreamApiRequest);
        } catch (Exception e) {
            sseEmitter.completeWithError(e);
            return;
        }

        // 2. WebSocket Handler 세팅
        ChatWebSocketHandler[] handlerRef = new ChatWebSocketHandler[1];
        Runnable onComplete = () -> {
            String finalAnswer = handlerRef[0].getFullAnswer();
            log.info("[StreamingService] DB 저장 - 최종 응답: {}", finalAnswer);
            chatRepository.save(Chat.builder()
                    .username(user.getUsername())
                    .sessionId(sessionId)
                    .question(chatUserRequest.getQuestion())
                    .answer(finalAnswer)
                    .summary(null)
                    .createdAt(LocalDateTime.now())
                    .build());

            sseEmitter.complete();
        };

        ChatWebSocketHandler handler = new ChatWebSocketHandler(sseEmitter, requestJson, onComplete);
        handlerRef[0] = handler;

        WebSocketConnectionManager manager = new WebSocketConnectionManager(
                new StandardWebSocketClient(),
                handler,
                WS_URI
        );
        manager.start();
    }

    private List<String> getMockHistoryList() {
        return new ArrayList<>();
    }

    private ChatStreamApiRequest.UserData getMockUserData() {
        return ChatStreamApiRequest.UserData.builder()
                .solarspaceId(13232017)
                .businessNumber("5650102273")
                .firmName("이봉금태양광발전소")
                .representativePhone("0614731261")
                .plantName("이봉금 태양광 발전소")
                .facilityConfirm(true)
                .facilityCode("PV15-S-01234")
                .facilityCapa("99.63")
                .facilityWeight("1.5")
                .permitNumber("영암제1234호")
                .permitCapa("98.44")
                .ppaContractTarget("한국전력공사")
                .ppaContractNumber("5001012345")
                .taxRegistrationId("0519")
                .ppaBranchOffice("한국전력공사 영암지사")
                .ppaContactPhone("062-260-5101")
                .recTradingType("FIXED")
                .recContractTarget("한국수력원자력(주)")
                .commercialDate("2023-06-23")
                .recStartDate("2023-06-23")
                .subscription("ACTIVE")
                .certificateExpiresAt("2025-03-24T23:59:59")
                .representativeName("최파나")
                .contactName("조나아")
                .contactPhone("01037652656")
                .contactEmail("x22e45gf@kakao.com")
                .build();
    }


}



