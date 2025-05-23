package com.osolar.obot.domain.chat.controller;

import com.osolar.obot.common.apiPayload.success.SuccessApiResponse;
import com.osolar.obot.domain.chat.dto.request.ChatUserRequest;
import com.osolar.obot.domain.chat.dto.response.ChatUserResponse;
import com.osolar.obot.domain.chat.dto.response.SessionResponse;
import com.osolar.obot.domain.chat.service.StreamingService;
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
    private final StreamingService streamingService;

    @PostMapping("/chat/session")
    public SuccessApiResponse<SessionResponse> createChatSession(
            @RequestParam String userId
    ) {
        SessionResponse sessionResponse = streamingService.createChatSession(userId);
        return SuccessApiResponse.CreateSession(sessionResponse);
    }

    @GetMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamResponseByPrompt(
            @RequestParam String prompt,
            @RequestParam String access) {
        log.info("[StreamingController - streamResponse]");

        SseEmitter emitter = new SseEmitter(180_000L); // 3분으로 설정
        geminiService.streamResponseByPrompt(prompt, emitter, access);

        return emitter;
    }

    @PostMapping("/chat/basic")
    public SuccessApiResponse<ChatUserResponse> getBasicResponse(
        @RequestParam String sessionId, @RequestBody ChatUserRequest chatUserRequest
    ) {
        ChatUserResponse chatUserResponse = streamingService.getBasicResponse(sessionId, chatUserRequest);
        return SuccessApiResponse.GetResponse(chatUserResponse);
    }


}
