package com.osolar.obot.domain.chat.controller;

import com.osolar.obot.common.annotation.CurrentUser;
import com.osolar.obot.common.apiPayload.success.SuccessApiResponse;
import com.osolar.obot.domain.chat.dto.response.SessionResponse;
import com.osolar.obot.domain.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.osolar.obot.domain.chat.dto.request.ChatUserRequest;
import com.osolar.obot.domain.chat.service.StreamingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "챗봇 응답 스트리밍 API", description = "챗봇 응답 스트리밍 API")
public class StreamingController {

    private final StreamingService streamingService;

    @Operation(summary = "[세션 관리] 챗봇 세션 생성 API")
    @PostMapping("/chat/session")
    public SuccessApiResponse<SessionResponse> createChatSession(
            @RequestParam String userId
    ) {
        SessionResponse sessionResponse = streamingService.createChatSession(userId);
        return SuccessApiResponse.CreateSession(sessionResponse);
    }

    @Operation(summary = "[WebSocket] 챗봇 스트리밍 응답 반환 API")
    @PostMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @PreAuthorize("isAuthenticated()")
    public SseEmitter streamResponseByPrompt(
            @RequestParam String sessionId,
            @RequestBody ChatUserRequest chatUserRequest,
            @CurrentUser User currentUser
    ) {
        log.info("[StreamingController - streamResponseByPrompt] sessionId = {}, question = {}", sessionId, chatUserRequest.getQuestion());

        SseEmitter emitter = new SseEmitter(180_000L); // 3분으로 설정
        streamingService.getStreamResponse(emitter, sessionId, chatUserRequest, currentUser.getUsername());

        return emitter;
    }

}
