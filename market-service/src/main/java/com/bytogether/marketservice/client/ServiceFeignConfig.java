package com.bytogether.marketservice.client;

import feign.RetryableException;
import feign.Retryer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class ServiceFeignConfig {

    @Bean
    public Retryer feignRetryer() {
        // 기본 Retryer(Default): period, maxPeriod, maxAttempts
        // new Retryer.Default(100ms 간격, 최대 1초, 3번 재시도)
        return new Retryer.Default(100, 1000, 3) {
            @Override
            public void continueOrPropagate(RetryableException e) {
                log.warn("Feign retrying request... cause={}", e.getMessage());
                super.continueOrPropagate(e);
            }
        };
    }

}