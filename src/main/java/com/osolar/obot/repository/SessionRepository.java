package com.osolar.obot.repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBSaveExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ExpectedAttributeValue;
import com.osolar.obot.entity.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class SessionRepository {

    @Autowired
    private DynamoDBMapper dynamoDBMapper;

    // Create
    public Session save(Session session) {
        dynamoDBMapper.save(session);
        return session;
    }

    // Read
    public Session findSessionById(String id) {
        return dynamoDBMapper.load(Session.class, id);
    }

    // Update
    public Session update(Session session) {
        dynamoDBMapper.save(session,
                new DynamoDBSaveExpression()
                        .withExpectedEntry("id",
                                new ExpectedAttributeValue(
                                        new AttributeValue().withS(session.getId())
                                )));
        return session;
    }

    // Delete
    public void deleteSessionById(String id) {
        dynamoDBMapper.delete(dynamoDBMapper.load(Session.class, id));
    }
}
