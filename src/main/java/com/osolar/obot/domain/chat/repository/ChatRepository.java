package com.osolar.obot.domain.chat.repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.osolar.obot.domain.chat.entity.Chat;
import org.springframework.stereotype.Repository;

@Repository
public class ChatRepository {

    private final DynamoDBMapper dynamoDBMapper;

    public ChatRepository(DynamoDBMapper dynamoDBMapper) {
        this.dynamoDBMapper = dynamoDBMapper;
    }

    // Create
    public Chat save(Chat chat) {
        dynamoDBMapper.save(chat);
        return chat;
    }


}
