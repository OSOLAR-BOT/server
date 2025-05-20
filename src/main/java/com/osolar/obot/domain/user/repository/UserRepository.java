package com.osolar.obot.domain.user.repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.osolar.obot.domain.user.entity.User;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class UserRepository {

    private final DynamoDBMapper dynamoDBMapper;

    public UserRepository(DynamoDBMapper dynamoDBMapper) {
        this.dynamoDBMapper = dynamoDBMapper;
    }

    // Create
    public User save(User user) {
        dynamoDBMapper.save(user);
        return user;
    }

    // Read
    public Optional<User> findById(String id) {
        return Optional.ofNullable(dynamoDBMapper.load(User.class, id));
    }

    public Optional<User> findByUsername(String username) {
        DynamoDBQueryExpression<User> queryExpression = new DynamoDBQueryExpression<User>()
                .withIndexName("byUsername")
                .withConsistentRead(false)  // GSI -> Eventually Consistent Read
                .withKeyConditionExpression("username = :username")
                .withExpressionAttributeValues(Map.of(":username", new AttributeValue().withS(username)));

        List<User> result = dynamoDBMapper.query(User.class, queryExpression);
        if (result.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(result.get(0));
    }

    public Boolean existsByUsername(String username) {
        DynamoDBQueryExpression<User> queryExpression = new DynamoDBQueryExpression<User>()
                .withIndexName("byUsername")
                .withConsistentRead(false)  // GSI -> Eventually Consistent Read
                .withKeyConditionExpression("username = :username")
                .withExpressionAttributeValues(Map.of(":username", new AttributeValue().withS(username)));

        List<User> result = dynamoDBMapper.query(User.class, queryExpression);
        return !result.isEmpty();
    }

    // Delete
    public void deleteById(String id) {
        User user = dynamoDBMapper.load(User.class, id);
        if (user != null) {
            dynamoDBMapper.delete(user);
        }
    }

}
