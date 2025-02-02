package com.crypto.currency;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@TestConfiguration
public class TestConfig {

    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
