package com.osolar.obot.common.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final SseEmitter sseEmitter;
    private final String requestJson;
    private final Runnable onComplete;
    private final StringBuilder fullAnswer = new StringBuilder();

    // 스트림 조각을 누적하는 버퍼
    private final StringBuilder streamBuffer = new StringBuilder();

    public ChatWebSocketHandler(SseEmitter sseEmitter, String requestJson, Runnable onComplete) {
        this.sseEmitter = sseEmitter;
        this.requestJson = requestJson;
        this.onComplete = onComplete;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("[WebSocket] 연결 성공: {}", session.getId());
        // WebSocket 연결이 열리면 요청 메시지 전송
        session.sendMessage(new TextMessage(requestJson));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        // 1) 수신한 조각을 버퍼에 누적
        streamBuffer.append(payload);

        // 2) 버퍼 내에 [DONE] 메시지가 포함되어 있는지 검사
        int doneIndex = streamBuffer.indexOf("[DONE]");

        if (doneIndex != -1) {
            // [DONE] 발견 시

            // [DONE] 앞까지 문자열만 잘라서 fullAnswer에 추가
            String beforeDone = streamBuffer.substring(0, doneIndex);
            fullAnswer.append(beforeDone);

            // 클라이언트에 [DONE] 이전까지만 전송
            try {
                sseEmitter.send(SseEmitter.event().data(beforeDone));
            } catch (Exception e) {
                log.error("SSE 전송 오류 발생", e);
                safeCompleteWithError(e);
                closeSession(session);
                return;
            }

            // WebSocket 세션 닫기 (스트림 종료)
            if (session.isOpen()) {
                session.close();
            }

            // 버퍼는 [DONE] 이후부터 남겨둠 (만약 추가 데이터 올 경우 대비)
            streamBuffer.delete(0, doneIndex + "[DONE]".length());

            return;
        }

        // 3) [DONE]이 아직 없으면 지금 받은 payload만 SSE로 전송
        fullAnswer.append(payload);

        try {
            sseEmitter.send(SseEmitter.event().data(payload));
        } catch (Exception e) {
            log.error("SSE 전송 오류 발생", e);
            safeCompleteWithError(e);
            closeSession(session);
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("[WebSocket] 오류 발생", exception);
        safeCompleteWithError(new Exception(exception));
        closeSession(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        log.info("[WebSocket] 연결 종료: {}", status);
        try {
            sseEmitter.complete();
        } catch (IllegalStateException ise) {
            log.warn("SSEEmitter가 이미 완료된 상태입니다.");
        }

        // 연결 종료 시 DB 저장
        if (onComplete != null) {
            onComplete.run();
        }
    }

    /**
     * 최종 누적된 전체 답변 반환
     */
    public String getFullAnswer() {
        return fullAnswer.toString();
    }

    /**
     * SSEEmitter를 안전하게 completeWithError 호출
     */
    private void safeCompleteWithError(Exception e) {
        try {
            sseEmitter.completeWithError(e);
        } catch (IllegalStateException ise) {
            log.warn("SSEEmitter가 이미 완료된 상태입니다.");
        } catch (Exception ex) {
            log.error("SSEEmitter completeWithError 호출 중 에러", ex);
        }
    }

    /**
     * WebSocketSession을 안전하게 닫기
     */
    private void closeSession(WebSocketSession session) {
        try {
            if (session.isOpen()) {
                session.close();
            }
        } catch (Exception ex) {
            log.error("WebSocketSession 닫는 중 오류 발생", ex);
        }
    }
}
