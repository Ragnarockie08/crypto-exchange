package com.crypto.currency.provider.coingecko.client;

import com.crypto.currency.exception.error.ProviderException;
import com.crypto.currency.provider.coingecko.properties.CoinGeckoProperties;
import com.crypto.currency.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CoinGeckoApiClient {

    private final RestTemplate restTemplate;
    private final CoinGeckoProperties coinGeckoProperties;

    public Map<String, Map<String, Double>> getSimplePrice(String coinId, List<String> vsCurrencies) {

        String url = buildSimplePriceUri(coinGeckoProperties.getBaseUrl(), coinId, vsCurrencies);

        HttpHeaders headers = new HttpHeaders();
        headers.set("x-cg-demo-api-key", coinGeckoProperties.getApiKey());
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map<String, Map<String, Double>>> response =
                    restTemplate.exchange(
                            url,
                            HttpMethod.GET,
                            requestEntity,
                            new ParameterizedTypeReference<Map<String, Map<String, Double>>>() {}
                    );

            if (response.getStatusCode() != HttpStatus.OK) {
                throw new ProviderException("Failed to fetch data from CoinGecko. HTTP code: " + response.getStatusCode());
            }
            return response.getBody();
        } catch (RestClientException ex) {
            throw new ProviderException(
                    "Failed to fetch data from CoinGecko",
                    ex
            );
        }
    }

    private String buildSimplePriceUri(String baseUrl, String coinId, List<String> vsCurrencies) {
        return UriComponentsBuilder
                .fromHttpUrl(baseUrl)
                .path(coinGeckoProperties.getSimplePrice())
                .queryParam("ids", coinId)
                .queryParam("vs_currencies", StringUtils.toCommaSeparated(vsCurrencies))
                .toUriString();
    }
}

