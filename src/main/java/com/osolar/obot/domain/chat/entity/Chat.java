package com.osolar.obot.domain.chat.entity;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAutoGeneratedKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverted;
import com.osolar.obot.common.config.DynamoDBConfig;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "chatting_content")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
@DynamoDBTable(tableName = "chat")
public class Chat {

    @DynamoDBHashKey
    @DynamoDBAutoGeneratedKey
    private String id;

    @DynamoDBAttribute
    @DynamoDBIndexHashKey(globalSecondaryIndexName = "bySessionId")
    private String sessionId;

    @DynamoDBAttribute
    private String username;

    @DynamoDBAttribute
    private String question;

    @DynamoDBAttribute
    private String answer;

    @DynamoDBAttribute
    private String summary;

    @DynamoDBAttribute
    @DynamoDBTypeConverted(converter = DynamoDBConfig.LocalDateTimeConverter.class)
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