package com.osolar.obot.repository;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.osolar.obot.domain.inquiry.entity.Inquiry;
import com.osolar.obot.domain.inquiry.repository.InquiryRepository;
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
public class InquiryRepositoryTest {

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
        public InquiryRepository inquiryRepository(DynamoDBMapper dynamoDBMapper) {
            return new InquiryRepository(dynamoDBMapper);
        }
    }

    @Autowired
    private InquiryRepository inquiryRepository;

    @Autowired
    private AmazonDynamoDB amazonDynamoDb;

    @Autowired
    private DynamoDBMapper dynamoDbMapper;

    @Test
    @DisplayName("DynamoDB 실행 테스트")
    void CRUD_TEST() {

        // Create
//        Inquiry createdInquiry = inquiryRepository.save(Inquiry.builder()
//                .createdAt(LocalDateTime.now())
//                .prompt("프롬프트")
//                .output("결과")
//                .build());
//        then(createdInquiry.getId()).isNotNull();

//        // Read
//        Inquiry readInquiry = inquiryRepository.findById(createdInquiry.getId())
//                .orElseThrow(IllegalStateException::new);
//        then(readInquiry)
//                .hasFieldOrPropertyWithValue("id", createdInquiry.getId())
//                .hasFieldOrPropertyWithValue("prompt", "프롬프트")
//                .hasFieldOrPropertyWithValue("output", "결과");
//
//        // Delete
//        inquiryRepository.deleteById(createdInquiry.getId());
    }

}
