package com.osolar.obot.domain.inquiry.repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.osolar.obot.domain.inquiry.entity.Inquiry;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class InquiryRepository {

    private final DynamoDBMapper dynamoDBMapper;

    public InquiryRepository(DynamoDBMapper dynamoDBMapper) {
        this.dynamoDBMapper = dynamoDBMapper;
    }

    // Create
    public Inquiry save(Inquiry inquiry) {
        dynamoDBMapper.save(inquiry);
        return inquiry;
    }

    // Read
    public Optional<Inquiry> findById(String id) {
        return Optional.ofNullable(dynamoDBMapper.load(Inquiry.class, id));
    }

    // Delete
    public void deleteById(String id) {
        Inquiry inquiry = dynamoDBMapper.load(Inquiry.class, id);
        if (inquiry != null) {
            dynamoDBMapper.delete(inquiry);
        }
    }
}
