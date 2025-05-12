package capstone.backend.domain.chat.repository;

import capstone.backend.domain.chat.entity.Chat;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChatRepository extends MongoRepository<Chat, String> {
}
