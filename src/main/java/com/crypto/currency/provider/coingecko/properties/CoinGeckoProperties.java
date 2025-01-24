package com.crypto.currency.provider.coingecko.properties;

import lombok.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Value
@ConfigurationProperties(prefix = "providers.coingecko.api")
public class CoinGeckoProperties {


    private final String apiKey;
    private final String baseUrl;
    private final String simplePrice;
}