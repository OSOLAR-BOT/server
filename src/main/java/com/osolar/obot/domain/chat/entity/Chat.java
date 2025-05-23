package com.osolar.obot.domain.chat.entity;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "chatting_content")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Chat {
    @Id
    private String id;
    private String sessionId;
    private String username;
    private String question;
    private String answer;
    private String summary;
    private LocalDateTime createdAt;

    @Builder
    public Chat(String username, String sessionId, String question, String answer, String summary, LocalDateTime createdAt) {
        this.username = username;
        this.sessionId = sessionId;
        this.question = question;
        this.answer = answer;
        this.summary = summary;
        this.createdAt = createdAt;
    }
}