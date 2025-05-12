package com.osolar.obot.domain.chat.repository;

import com.osolar.obot.domain.chat.entity.Chat;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChatRepository extends MongoRepository<Chat, String> {
}
