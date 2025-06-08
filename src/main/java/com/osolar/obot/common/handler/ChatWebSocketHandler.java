package com.osolar.obot.common.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final SseEmitter sseEmitter;
    private final String requestJson;

    public ChatWebSocketHandler(SseEmitter sseEmitter, String requestJson) {
        this.sseEmitter = sseEmitter;
        this.requestJson = requestJson;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("[WebSocket] 연결 성공: {}", session.getId());
        session.sendMessage(new TextMessage(requestJson));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        log.info("[WebSocket] 메시지 수신: {}", message.getPayload());
        try {
            sseEmitter.send(SseEmitter.event().data(message.getPayload()));
        } catch (Exception e) {
            log.error("SSE 전송 오류", e);
            sseEmitter.completeWithError(e);
            session.close();
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("[WebSocket] 오류 발생", exception);
        sseEmitter.completeWithError(exception);
        session.close();
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        log.info("[WebSocket] 연결 종료: {}", status);
        sseEmitter.complete();
    }
}
