package com.osolar.obot.domain.chat.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.osolar.obot.common.apiPayload.failure.customException.UserException;
import com.osolar.obot.common.handler.ChatWebSocketHandler;
import com.osolar.obot.domain.chat.dto.request.ChatStreamApiRequest;
import com.osolar.obot.domain.chat.dto.request.ChatUserRequest;
import com.osolar.obot.domain.chat.dto.response.SessionResponse;
import com.osolar.obot.domain.chat.repository.ChatRepository;
import com.osolar.obot.domain.user.entity.SessionStatus;
import com.osolar.obot.domain.user.entity.User;
import com.osolar.obot.domain.user.entity.UserSession;
import com.osolar.obot.domain.user.jwt.JWTUtil;
import com.osolar.obot.domain.user.repository.UserRepository;
import com.osolar.obot.domain.user.repository.UserSessionRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.socket.client.WebSocketConnectionManager;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

@Service
@Slf4j
@RequiredArgsConstructor
public class StreamingService {

    private final UserSessionRepository userSessionRepository;
    private final WebClient webClient;
    private final UserRepository userRepository;
    private final JWTUtil jwtUtil;
    private final ChatRepository chatRepository;

    //    @Value("${BASE_API_URL}")
//    private String apiUrl;
    private static String ws_uri = "wss://85mvqp4j76.execute-api.us-east-1.amazonaws.com/dev/";

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
        try {
            // 1. 유저 인증 및 조회
            User user = userRepository.findByUsername(username).orElseThrow(UserException.UsernameNotExistException::new);

            // 2. (수정 필요) 유저데이터 객체 생성 (현: 목업)
            ChatStreamApiRequest.UserData userData = getMockUserData();

            // 3. (수정 필요) 채팅내역 객체 생성 (현: 목업)
            List<String> chatLog = getMockHistoryList();

            // 4. 요청 객체 생성
            ChatStreamApiRequest chatStreamApiRequest = ChatStreamApiRequest.builder()
                    .action("$default")
                    .question(chatUserRequest.getQuestion())
                    .user(userData)
                    .history(chatLog)
                    .build();

            // 5. 요청 json 직렬화
            ObjectMapper mapper = new ObjectMapper();
            String requestJson = mapper.writeValueAsString(chatStreamApiRequest);

            // 6. WebSocket 핸들러 세팅
            ChatWebSocketHandler handler = new ChatWebSocketHandler(sseEmitter, requestJson);

            // 7. WebSocket 연결 시작
            StandardWebSocketClient client = new StandardWebSocketClient();
            WebSocketConnectionManager manager = new WebSocketConnectionManager(client, handler, ws_uri);
            manager.start();

        } catch (Exception e) {
            log.error("[StreamingService] WebSocket 연결 실패", e);
            sseEmitter.completeWithError(e);
        }
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


//    public ChatUserResponse getBasicResponse(String sessionId, ChatUserRequest chatUserRequest) {
//        //User 데이터 가져오는 로직 추후 구현 예정
//        User user = userRepository.findByUsername(jwtUtil.getUsername(chatUserRequest.getAccessKey()))
//            .orElseThrow(UserException.UsernameNotExistException::new);
//
//        ArrayList<String> mockHistory = new ArrayList<>();
//        mockHistory.add("지난달 매출은 1,200,000원이었어요.");
//        mockHistory.add("계약 대상은 한국전력공사입니다.");
//        ChatApiRequest mockRequest = ChatApiRequest.builder()
//            .question(chatUserRequest.getQuestion())
//            .user(UserData.builder()
//                .solarspaceId(13232017)
//                .businessNumber("5650102273")
//                .firmName("이봉금태양광발전소")
//                .representativePhone("0614731261")
//                .plantName("이봉금 태양광 발전소")
//                .facilityConfirm(true)
//                .facilityCode("PV15-S-01234")
//                .facilityCapa("99.63")
//                .facilityWeight("1.5")
//                .permitNumber("영암제1234호")
//                .permitCapa("98.44")
//                .ppaContractTarget("한국전력공사")
//                .ppaContractNumber("5001012345")
//                .taxRegistrationId("0519")
//                .ppaBranchOffice("한국전력공사 영암지사")
//                .ppaContactPhone("062-260-5101")
//                .recTradingType("FIXED")
//                .recContractTarget("한국수력원자력(주)")
//                .commercialDate("2023-06-23")
//                .recStartDate("2023-06-23")
//                .subscription("ACTIVE")
//                .certificateExpiresAt("2025-03-24T23:59:59")
//                .representativeName("최파나")
//                .contactName("조나아")
//                .contactPhone("01037652656")
//                .contactEmail("x22e45gf@kakao.com")
//                .build())
//            .history(mockHistory)
//            .build();
//
//        ChatApiResponse response = webClient.post()
//            .uri(apiUrl + "/query")
//            .contentType(MediaType.APPLICATION_JSON)
//            .bodyValue(mockRequest)
//            .retrieve()
//            .bodyToMono(ChatApiResponse.class)
//            .block();
//
//        Chat savedChat = chatRepository.save(Chat.builder()
//            .username(user.getUsername())
//            .sessionId(sessionId)
//            .question(chatUserRequest.getQuestion())
//            .answer(response.getAnswer())
//            .summary(response.getSummary())
//            .createdAt(LocalDateTime.now())
//            .build());
//
//        return ChatUserResponse.builder()
//            .chatId(savedChat.getId())
//            .answer(savedChat.getAnswer())
//            .createdAt(savedChat.getCreatedAt())
//            .build();
//
//    }
