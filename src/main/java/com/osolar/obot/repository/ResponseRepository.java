package com.osolar.obot.repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.osolar.obot.entity.Response;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class ResponseRepository {

    private final DynamoDBMapper dynamoDBMapper;

    public ResponseRepository(DynamoDBMapper dynamoDBMapper) {
        this.dynamoDBMapper = dynamoDBMapper;
    }

    // Create
    public Response save(Response response) {
        dynamoDBMapper.save(response);
        return response;
    }

    // Read
    public Optional<Response> findById(String id) {
        return Optional.ofNullable(dynamoDBMapper.load(Response.class, id));
    }

    // Delete
    public void deleteById(String id) {
        Response response = dynamoDBMapper.load(Response.class, id);
        if (response != null) {
            dynamoDBMapper.delete(response);
        }
    }
}
