package capstone.backend.domain.chat.controller;

import capstone.backend.external.gemini.GeminiService;
import org.springframework.http.MediaType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
public class ChatController {

    private final GeminiService geminiService;

    @GetMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamResponseByPrompt(
            @RequestParam String prompt,
            @RequestParam String access) {
        log.info("[ChatController - streamResponse]");
        SseEmitter emitter = new SseEmitter(180_000L); // 3분으로 설정

        geminiService.streamResponseByPrompt(prompt, emitter, access);

        return emitter;
    }
}
