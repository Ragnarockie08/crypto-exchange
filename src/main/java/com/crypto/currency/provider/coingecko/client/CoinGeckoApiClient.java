package com.crypto.currency.provider.coingecko.client;

import com.crypto.currency.exception.error.ProviderException;
import com.crypto.currency.utils.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

@Component
public class CoinGeckoApiClient {

    private final RestTemplate restTemplate;
    private final String apiKey;
    private final String baseUrl;

    public CoinGeckoApiClient(RestTemplate restTemplate,
                              @Value("${coingecko.api.key}") String apiKey,
                              @Value("${coingecko.api.base-url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
    }

    public Map<String, Map<String, Double>> getSimplePrice(String coinId, List<String> vsCurrencies) {

        String url = buildSimplePriceUri(baseUrl, coinId, vsCurrencies);

        HttpHeaders headers = new HttpHeaders();
        headers.set("x-cg-demo-api-key", apiKey);
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

    public static String buildSimplePriceUri(String baseUrl, String coinId, List<String> vsCurrencies) {
        return UriComponentsBuilder
                .fromHttpUrl(baseUrl)
                .path("/simple/price")
                .queryParam("ids", coinId)
                .queryParam("vs_currencies", StringUtils.toCommaSeparated(vsCurrencies))
                .toUriString();
    }
}

