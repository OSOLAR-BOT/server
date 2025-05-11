package com.osolar.obot.repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.osolar.obot.entity.Session;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class SessionRepository {

    private final DynamoDBMapper dynamoDBMapper;

    public SessionRepository(DynamoDBMapper dynamoDBMapper) {
        this.dynamoDBMapper = dynamoDBMapper;
    }

    // Create
    public Session save(Session session) {
        dynamoDBMapper.save(session);
        return session;
    }

    // Read
    public Optional<Session> findById(String id) {
        return Optional.ofNullable(dynamoDBMapper.load(Session.class, id));
    }

    // Delete
    public void deleteById(String id) {
        Session session = dynamoDBMapper.load(Session.class, id);
        if (session != null) {
            dynamoDBMapper.delete(session);
        }
    }
}
