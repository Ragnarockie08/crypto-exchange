package com.crypto.currency.provider.coingecko.properties;

import lombok.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Value
@ConfigurationProperties(prefix = "providers.coingecko.api")
public class CoinGeckoProperties {
    String apiKey;
    String baseUrl;
    String simplePrice;
}