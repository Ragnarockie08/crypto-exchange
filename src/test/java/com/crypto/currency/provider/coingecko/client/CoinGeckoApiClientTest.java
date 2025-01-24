package com.crypto.currency.provider.coingecko.client;

import com.crypto.currency.TestConfig;
import com.crypto.currency.exception.error.ProviderException;
import com.crypto.currency.provider.coingecko.properties.CoinGeckoProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(CoinGeckoApiClient.class)
@EnableConfigurationProperties(CoinGeckoProperties.class)
@Import(TestConfig.class)
class CoinGeckoApiClientTest {

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private MockRestServiceServer mockServer;
    @Autowired
    private CoinGeckoProperties coinGeckoProperties;
    @Autowired
    private CoinGeckoApiClient coinGeckoApiClient;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockServer = MockRestServiceServer.bindTo(restTemplate).build();
    }

    @Test
    void testGetPrices() throws Exception {

        // given
        String coinId = "bitcoin";
        List<String> rates = List.of("usd", "eth");

        Map<String, Map<String, Double>> mockResponse = new HashMap<>();
        mockResponse.put(coinId, Map.of("usd", 27000.0, "eth", 15.2));
        //when
        mockServer.expect(
                requestTo("https://api.coingecko.com/api/v3/simple/price?ids=bitcoin&vs_currencies=usd,eth"))
                .andRespond(withSuccess(objectMapper.writeValueAsString(mockResponse), MediaType.APPLICATION_JSON));

        //then
        Map<String, Map<String, Double>> result = coinGeckoApiClient.getSimplePrice(coinId, rates);

        assertEquals(27000.0, result.get("bitcoin").get("usd"));
        assertEquals(15.2, result.get("bitcoin").get("eth"));
    }

    @Test
    void shouldThrowHttpClientErrorException_whenResponseIs404() {
        // given
        String coinId = "bitcoin";
        List<String> rates = List.of("usd", "eth");

        // when
        mockServer.expect(requestTo("https://api.coingecko.com/api/v3/simple/price?ids=bitcoin&vs_currencies=usd,eth"))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        // then
        ProviderException exception = assertThrows(
                ProviderException.class,
                () -> coinGeckoApiClient.getSimplePrice(coinId, rates)
        );

        assertEquals( "Failed to fetch data from CoinGecko", exception.getMessage());
    }
}
