package com.crypto.currency.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ApiClientConfig {

    @Bean
    public RestTemplate restTemplate() {
        // e.g. configure interceptors to add the x-cg-demo-api-key header automatically
        return new RestTemplate();
    }
}

