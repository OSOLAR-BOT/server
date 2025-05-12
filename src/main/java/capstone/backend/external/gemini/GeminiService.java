package capstone.backend.external.gemini;

import com.osolar.obot.domain.chat.entity.Chat;
import com.osolar.obot.domain.chat.repository.ChatRepository;
import com.osolar.obot.domain.user.jwt.JWTUtil;
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
    private final ChatRepository chatRepository;
    private final JWTUtil jwtUtil;

    @Value("${external.gemini.api-key}")
    private String apiKey;

    @Value("${external.gemini.api-url}")
    private String apiUrl;

    /**
     *
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
                chatRepository.save(
                        Chat.builder()
                                .username(jwtUtil.getUsername(access))
                                .question(prompt)
                                .answer(finalAnswer)
                                .createdAt(LocalDateTime.now())
                                .build()
                );
            } catch (Exception e) {
                log.error("Error saving content", e);
            }
        });
    }

}
