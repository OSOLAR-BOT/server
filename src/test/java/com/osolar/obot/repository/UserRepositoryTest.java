package com.osolar.obot.repository;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.osolar.obot.domain.user.repository.UserRepository;
import com.osolar.obot.domain.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.BDDAssertions.then;

@Tag("Local")
@ExtendWith(SpringExtension.class)
class UserRepositoryTest {

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
        public UserRepository userRepository(DynamoDBMapper dynamoDBMapper) {
            return new UserRepository(dynamoDBMapper);
        }
    }

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AmazonDynamoDB amazonDynamoDb;

    @Autowired
    private DynamoDBMapper dynamoDbMapper;

    @Test
    @DisplayName("DynamoDB 실행 테스트")
    void CRUD_TEST() {

        // Create
        User createdUser = userRepository.save(User.builder().build());
        then(createdUser.getId()).isNotNull();

        // Read
        User readUser = userRepository.findById(createdUser.getId())
                .orElseThrow(IllegalStateException::new);

        then(readUser)
                .hasFieldOrPropertyWithValue("id", createdUser.getId());

        // Delete
        userRepository.deleteById(createdUser.getId());
    }
}