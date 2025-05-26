package com.osolar.obot.repository;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.osolar.obot.domain.user.repository.UserSessionRepository;
import com.osolar.obot.domain.user.entity.UserSession;
import com.osolar.obot.domain.user.entity.SessionStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.BDDAssertions.then;

@Tag("Local")
@ExtendWith(SpringExtension.class)
class UserSessionRepositoryTest {

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
        public UserSessionRepository sessionRepository(DynamoDBMapper dynamoDBMapper) {
            return new UserSessionRepository(dynamoDBMapper);
        }
    }

    @Autowired
    private UserSessionRepository userSessionRepository;

    @Autowired
    private AmazonDynamoDB amazonDynamoDb;

    @Autowired
    private DynamoDBMapper dynamoDbMapper;

    @Test
    @DisplayName("DynamoDB 실행 테스트")
    void CRUD_TEST() {

        // Create
        UserSession createdUserSession = userSessionRepository.save(UserSession.builder()
                .userId("1234")
                .sessionStatus(SessionStatus.IN_PROGRESS)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build());
        then(createdUserSession.getId()).isNotNull();

        // Read
        UserSession readUserSession = userSessionRepository.findById(createdUserSession.getId())
                .orElseThrow(IllegalStateException::new);
        then(readUserSession)
                .hasFieldOrPropertyWithValue("id", createdUserSession.getId())
                .hasFieldOrPropertyWithValue("sessionStatus", SessionStatus.IN_PROGRESS);

        UserSession foundUserSession = userSessionRepository.findByUserId("1234").orElseThrow();
        then(foundUserSession)
                .hasFieldOrPropertyWithValue("id", createdUserSession.getId())
                .hasFieldOrPropertyWithValue("sessionStatus", SessionStatus.IN_PROGRESS);

        // Update
        readUserSession.update(SessionStatus.COMPLETED, LocalDateTime.now(), LocalDateTime.now());
        UserSession updatedUserSession = userSessionRepository.save(readUserSession);
        then(updatedUserSession)
                .hasFieldOrPropertyWithValue("sessionStatus", SessionStatus.COMPLETED);

        // Delete
        userSessionRepository.deleteById(createdUserSession.getId());
    }
}