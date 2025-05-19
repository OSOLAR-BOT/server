package com.osolar.obot.repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.osolar.obot.entity.User;
import org.springframework.stereotype.Repository;

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
    public Optional<User> findUserById(String id) {
        return Optional.ofNullable(dynamoDBMapper.load(User.class, id));
    }

    // Delete
    public void deleteUserById(String id) {
        User user = dynamoDBMapper.load(User.class, id);
        if (user != null) {
            dynamoDBMapper.delete(user);
        }
    }

}
