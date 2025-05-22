package com.osolar.obot.domain.inquiry.entity.controller;

import com.osolar.obot.common.apiPayload.success.SuccessApiResponse;
import com.osolar.obot.domain.inquiry.entity.dto.response.SessionResponse;
import com.osolar.obot.external.gemini.GeminiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
public class StreamingController {

    private final GeminiService geminiService;

    @PostMapping("/chat/session")
    public SuccessApiResponse<SessionResponse> createChatSession(
            @RequestParam String userId
    ) {
        SessionResponse sessionResponse = geminiService.createChatSession(userId);
        return SuccessApiResponse.CreateSession(sessionResponse);
    }

    @GetMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamResponseByPrompt(
            @RequestParam String prompt,
            @RequestParam String access
    ) {
        log.info("[StreamingController - streamResponse]");

        SseEmitter emitter = new SseEmitter(180_000L); // 3분으로 설정
        geminiService.streamResponseByPrompt(prompt, emitter, access);

        return emitter;
    }
}
