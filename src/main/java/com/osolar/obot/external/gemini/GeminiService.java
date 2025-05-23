package com.osolar.obot.external.gemini;

import com.osolar.obot.common.apiPayload.failure.customException.UserException;
import com.osolar.obot.domain.chat.dto.response.SessionResponse;
import com.osolar.obot.domain.inquiry.entity.Inquiry;
import com.osolar.obot.domain.inquiry.entity.repository.InquiryRepository;
import com.osolar.obot.domain.user.entity.SessionStatus;
import com.osolar.obot.domain.user.entity.User;
import com.osolar.obot.domain.user.entity.UserSession;
import com.osolar.obot.domain.user.jwt.JWTUtil;
import com.osolar.obot.domain.user.repository.UserRepository;
import com.osolar.obot.domain.user.repository.UserSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class GeminiService {

    private final WebClient webClient;
    private final UserRepository userRepository;
    private final UserSessionRepository userSessionRepository;
    private final InquiryRepository inquiryRepository;
    private final JWTUtil jwtUtil;

    @Value("${external.gemini.api-key}")
    private String apiKey;

    @Value("${external.gemini.api-url}")
    private String apiUrl;

    public SessionResponse createChatSession(String userId){
        UserSession userSession = UserSession.builder()
                .userId(userId)
                .sessionStatus(SessionStatus.IN_PROGRESS)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        return SessionResponse.toDTO(userSessionRepository.save(userSession));
    }

    /**
     *g
     * @param prompt 요청 프롬트프 내용
     * @param emitter
     * @param access
    1. 요청 프롬프트 기반으로 응답 스트리밍 전송
    2. 응답 스트리밍 합본 비동기로 mongoDB에 저장
     */
    public void streamResponseByPrompt(String prompt, SseEmitter emitter, String access) {
        log.info("[GeminiService - streamResponseByPrompt]");
        Map<String, Object> requestBody = Map.of(
                "contents", List.of(Map.of(
                        "parts", List.of(Map.of("text", prompt))
                ))
        );

        StringBuilder answer = new StringBuilder();

        // 비동기 스트리밍 요청
        webClient.post()
                .uri(apiUrl.replace(":generateContent", ":streamGenerateContent") + "?key=" + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToFlux(String.class)  // Flux를 통해 스트리밍 데이터를 수신
                .doOnNext(chunk -> {
                    try {
                        String token = chunk.trim();
                        if (token.startsWith("\"text\":")) {
                            String[] parts = token.split(":", 2);
                            if (parts.length > 1) {
                                String text = parts[1].trim();
                                answer.append(text);
                                emitter.send(SseEmitter.event().data(text));  // 클라이언트로 실시간 스트리밍 송신
                            }
                        }
                    } catch (Exception e) {
                        emitter.completeWithError(e);  // 에러가 발생하면 스트리밍 송신 종료
                    }
                })
                .doOnComplete(() -> {
                    // 스트리밍 완료 후 DB 저장 비동기 처리
                    String finalAnswer = answer.toString().trim();
                    if (!finalAnswer.isEmpty()) {
                        // 비동기적으로 DB에 저장
                        saveToDatabase(prompt, finalAnswer, access)
                                .subscribe();  // subscribe()를 호출하여 비동기 처리
                    } else {
                        log.warn("Empty answer not saved to DB.");
                    }

                    emitter.complete();  // 스트리밍 완료 후 emitter 종료
                })
                .doOnError(e -> {
                    emitter.completeWithError(e);  // 에러 발생 시 스트리밍 종료
                    log.error("Stream error", e);
                })
                .subscribe();  // 비동기 스트리밍 요청 시작
    }

    private Mono<Void> saveToDatabase(String prompt, String finalAnswer, String access) {
        return Mono.fromRunnable(() -> {
            try {
                User user = userRepository.findByUsername(jwtUtil.getUsername(access))
                            .orElseThrow(UserException.UsernameNotExistException::new);
                UserSession session = userSessionRepository.findByUserId(user.getId())
                            .orElseThrow(UserException.UserSessionNotExistException::new);

                inquiryRepository.save(
                        Inquiry.builder()
                                .sessionId(session.getId())
                                .createdAt(LocalDateTime.now())
                                .prompt(prompt)
                                .output(finalAnswer)
                                .build()
                );
            } catch (Exception e) {
                log.error("Error saving content", e);
            }
        });
    }
}