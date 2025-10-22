package com.bytogether.userservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.lambda.LambdaClient;

@Configuration
public class AwsConfig {

    @Value("${spring.cloud.aws.region.static}")
    private String defaultRegion;

    @Value("${spring.cloud.aws.region.avatar}")
    private String avatarRegion;

    @Bean
    public LambdaClient defaultLambdaClient(){
      return LambdaClient.builder()
              .region(Region.of(defaultRegion))
              .build();
    }

    @Bean
    public LambdaClient avatarLambdaClient(){
        return LambdaClient.builder()
                .region(Region.of(avatarRegion))
                .build();
    }
}
