package com.osolar.obot.repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBSaveExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ExpectedAttributeValue;
import com.osolar.obot.entity.User;
import org.springframework.stereotype.Repository;

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
    public User findUserById(String id) {
        return dynamoDBMapper.load(User.class, id);
    }

    // Update
    public User update(User user) {
        dynamoDBMapper.save(user,
                new DynamoDBSaveExpression()
                        .withExpectedEntry("id",
                                new ExpectedAttributeValue(
                                        new AttributeValue().withS(user.getId())
                                )));
        return user;
    }

    // Delete
    public void deleteUserById(String id) {
        dynamoDBMapper.delete(dynamoDBMapper.load(User.class, id));
    }

}
