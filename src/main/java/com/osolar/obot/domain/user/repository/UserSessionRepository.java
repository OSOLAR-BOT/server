package com.osolar.obot.domain.user.repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.osolar.obot.domain.user.entity.User;
import com.osolar.obot.domain.user.entity.UserSession;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class UserSessionRepository {

    private final DynamoDBMapper dynamoDBMapper;

    public UserSessionRepository(DynamoDBMapper dynamoDBMapper) {
        this.dynamoDBMapper = dynamoDBMapper;
    }

    // Create
    public UserSession save(UserSession userSession) {
        dynamoDBMapper.save(userSession);
        return userSession;
    }

    // Read
    public Optional<UserSession> findById(String id) {
        return Optional.ofNullable(dynamoDBMapper.load(UserSession.class, id));
    }

    public Optional<UserSession> findByUserId(String userId) {
        DynamoDBQueryExpression<UserSession> queryExpression = new DynamoDBQueryExpression<UserSession>()
                .withIndexName("byUserId")
                .withConsistentRead(false)  // GSI -> Eventually Consistent Read
                .withKeyConditionExpression("userId = :userId")
                .withExpressionAttributeValues(Map.of(":userId", new AttributeValue().withS(userId)));

        List<UserSession> result = dynamoDBMapper.query(UserSession.class, queryExpression);
        if (result.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(result.get(0));
    }

    // Delete
    public void deleteById(String id) {
        UserSession userSession = dynamoDBMapper.load(UserSession.class, id);
        if (userSession != null) {
            dynamoDBMapper.delete(userSession);
        }
    }
}
