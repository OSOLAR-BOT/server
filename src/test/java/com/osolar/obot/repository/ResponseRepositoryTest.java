package com.osolar.obot.repository;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.osolar.obot.entity.Response;
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
public class ResponseRepositoryTest {

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
        public ResponseRepository responseRepository(DynamoDBMapper dynamoDBMapper) {
            return new ResponseRepository(dynamoDBMapper);
        }
    }

    @Autowired
    private ResponseRepository responseRepository;

    @Autowired
    private AmazonDynamoDB amazonDynamoDb;

    @Autowired
    private DynamoDBMapper dynamoDbMapper;

    @Test
    @DisplayName("DynamoDB 실행 테스트")
    void CRUD_TEST() {

        // Create
        Response createdResponse = responseRepository.save(Response.builder()
                .createdAt(LocalDateTime.now())
                .output("output")
                .isSatisfied(true)
                .satisfactionReason("reason")
                .build());
        then(createdResponse.getId()).isNotNull();

        // Read
        Response readResponse = responseRepository.findById(createdResponse.getId())
                .orElseThrow(IllegalStateException::new);
        then(readResponse)
                .hasFieldOrPropertyWithValue("id", createdResponse.getId())
                .hasFieldOrPropertyWithValue("output", "output")
                .hasFieldOrPropertyWithValue("isSatisfied", true)
                .hasFieldOrPropertyWithValue("satisfactionReason", "reason");

        // Update
        readResponse.update("업데이트", false, "사유");
        Response updatedInquiry = responseRepository.save(readResponse);
        then(updatedInquiry)
                .hasFieldOrPropertyWithValue("id", createdResponse.getId())
                .hasFieldOrPropertyWithValue("output", "업데이트")
                .hasFieldOrPropertyWithValue("isSatisfied", false)
                .hasFieldOrPropertyWithValue("satisfactionReason", "사유");

        // Delete
        responseRepository.deleteById(createdResponse.getId());
    }

}
