package com.osolar.obot.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.TimeZone;

@Configuration
public class DynamoDBConfig {

    @Value("${aws.dynamodb.endpoint}")
    private String endPoint;

    @Value("${aws.region}")
    private String region;

    @Bean(name = "dynamoDBMapper")
    public DynamoDBMapper dynamoDBMapper() {
        DynamoDBMapperConfig config = DynamoDBMapperConfig.builder()
                .withSaveBehavior(DynamoDBMapperConfig.SaveBehavior.CLOBBER)
                .withConsistentReads(DynamoDBMapperConfig.ConsistentReads.CONSISTENT)
                .withTableNameOverride(null)
                .withPaginationLoadingStrategy(DynamoDBMapperConfig.PaginationLoadingStrategy.EAGER_LOADING)
                .build();
        return new DynamoDBMapper(amazonDynamoDB(), config);
    }

    @Bean(name = "amazonDynamoDB")
    public AmazonDynamoDB amazonDynamoDB() {
        return AmazonDynamoDBClientBuilder.standard()
                .withRegion(Regions.fromName(region)) // AWS Region
                .withCredentials(DefaultAWSCredentialsProviderChain.getInstance()) // IAM Role
                .build();
    }

    public static class LocalDateTimeConverter implements DynamoDBTypeConverter<Date, LocalDateTime> {

        @Override
        public Date convert(LocalDateTime source) {
            return Date.from(source.toInstant(ZoneOffset.UTC));
        }

        @Override
        public LocalDateTime unconvert(Date source) {
            return source.toInstant().atZone(TimeZone.getDefault().toZoneId()).toLocalDateTime();
        }
    }

}
