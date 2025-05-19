package com.osolar.obot.repository;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.osolar.obot.entity.Session;
import com.osolar.obot.entity.enums.SessionStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.BDDAssertions.then;

@ExtendWith(SpringExtension.class)
class SessionRepositoryTest {

    @TestConfiguration
    static class DynamoDBTestConfig {

        @Bean
        public AmazonDynamoDB amazonDynamoDB() {
            AwsClientBuilder.EndpointConfiguration endpointConfiguration =
                    new AwsClientBuilder.EndpointConfiguration("http://localhost:8000", "ap-northeast-2");

            return AmazonDynamoDBClientBuilder.standard()
                    .withEndpointConfiguration(endpointConfiguration)
                    .build();
        }

        @Bean
        public DynamoDBMapper dynamoDBMapper(AmazonDynamoDB amazonDynamoDB) {
            return new DynamoDBMapper(amazonDynamoDB);
        }

        @Bean
        public SessionRepository sessionRepository(DynamoDBMapper dynamoDBMapper) {
            return new SessionRepository(dynamoDBMapper);
        }
    }

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private AmazonDynamoDB amazonDynamoDb;

    @Autowired
    private DynamoDBMapper dynamoDbMapper;

    @Test
    @DisplayName("DynamoDB 실행 테스트")
    void CRUD_TEST() {

        // Create
        Session createdSession = sessionRepository.save(Session.builder()
                .sessionStatus(SessionStatus.IN_PROGRESS)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build());
        then(createdSession.getId()).isNotNull();

        // Read
        Session readSession = sessionRepository.findById(createdSession.getId())
                .orElseThrow(IllegalStateException::new);
        then(readSession)
                .hasFieldOrPropertyWithValue("id", createdSession.getId())
                .hasFieldOrPropertyWithValue("sessionStatus", SessionStatus.IN_PROGRESS);

        // Update
        readSession.update(SessionStatus.COMPLETED, LocalDateTime.now(), LocalDateTime.now());
        Session updatedSession = sessionRepository.save(readSession);
        then(updatedSession)
                .hasFieldOrPropertyWithValue("sessionStatus", SessionStatus.COMPLETED);

        // Delete
        sessionRepository.deleteById(createdSession.getId());
    }
}