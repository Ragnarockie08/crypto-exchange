package com.crypto.currency.provider.coingecko;

import com.crypto.currency.provider.coingecko.client.CoinGeckoApiClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CoinGeckoProviderTest {

    @Mock
    private CoinGeckoApiClient apiClient;
    @InjectMocks
    private CoinGeckoProvider coinGeckoProvider;

    @Test
    void shouldReturnRatesForCurrency_WhenValidResponse() {
        //given
        String baseSymbol = "BTC";
        List<String> rates = List.of("usd", "eth");

        String coinGeckoId = "bitcoin";
        Map<String, Map<String, BigDecimal>> mockResponse = Map.of(
                coinGeckoId, Map.of("usd", new BigDecimal("27000.0"), "eth", new BigDecimal("0.074"))
        );

        //when
        when(apiClient.getSimplePrice(coinGeckoId, rates)).thenReturn(mockResponse);
        Map<String, BigDecimal> result = coinGeckoProvider.getRatesForCurrency(baseSymbol, rates);

        //then
        assertEquals(2, result.size());
        assertEquals(new BigDecimal("27000.0"), result.get("usd"));
        assertEquals(new BigDecimal("0.074"), result.get("eth"));

        verify(apiClient, times(1)).getSimplePrice(coinGeckoId, rates);
    }

    @Test
    void shouldReturnDefaultRate_WhenNoRatesProvided() {
        //given
        String baseSymbol = "BTC";
        List<String> rates = Collections.emptyList();

        String coinGeckoId = "bitcoin";
        Map<String, Map<String, BigDecimal>> mockResponse = Map.of(
                coinGeckoId, Map.of("usd", new BigDecimal("27000.0"))
        );

        //when
        when(apiClient.getSimplePrice(coinGeckoId, List.of("usd"))).thenReturn(mockResponse);
        Map<String, BigDecimal> result = coinGeckoProvider.getRatesForCurrency(baseSymbol, rates);

        //then
        assertEquals(1, result.size());
        assertEquals(new BigDecimal("27000.0"), result.get("usd"));

        verify(apiClient, times(1)).getSimplePrice(coinGeckoId, List.of("usd"));
    }

    @Test
    void shouldReturnEmptyMap_WhenApiResponseIsNull() {
        //given
        String baseSymbol = "BTC";
        List<String> rates = List.of("usd", "eth");

        String coinGeckoId = "bitcoin";

        //when
        when(apiClient.getSimplePrice(coinGeckoId, rates)).thenReturn(null);
        Map<String, BigDecimal> result = coinGeckoProvider.getRatesForCurrency(baseSymbol, rates);

        //then
        assertEquals(0, result.size());
        verify(apiClient, times(1)).getSimplePrice(coinGeckoId, rates);
    }

    @Test
    void shouldReturnEmptyMap_WhenApiResponseDoesNotContainCoinGeckoId() {
        //given
        String baseSymbol = "BTC";
        List<String> rates = List.of("usd", "eth");

        String coinGeckoId = "bitcoin";
        Map<String, Map<String, BigDecimal>> mockResponse = Map.of(
                "another-coin", Map.of("usd", new BigDecimal("100.0"), "eth", new BigDecimal("0.02"))
        );

        //when
        when(apiClient.getSimplePrice(coinGeckoId, rates)).thenReturn(mockResponse);
        Map<String, BigDecimal> result = coinGeckoProvider.getRatesForCurrency(baseSymbol, rates);

        //then
        assertEquals(0, result.size());
        verify(apiClient, times(1)).getSimplePrice(coinGeckoId, rates);
    }
}
