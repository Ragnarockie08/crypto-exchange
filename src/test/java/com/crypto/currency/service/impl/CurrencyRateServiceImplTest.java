package com.crypto.currency.service.impl;

import com.crypto.currency.dto.CurrencyRatesResponse;
import com.crypto.currency.exception.error.ApiException;
import com.crypto.currency.exception.error.ProviderException;
import com.crypto.currency.provider.CryptoRateProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CurrencyRateServiceImplTest {

    @Mock
    private CryptoRateProvider cryptoRateProvider;
    @InjectMocks
    private CurrencyRateServiceImpl currencyRateService;

    @Test
    void shouldGetRates_whenValidParams() {
        //given
        String coinId = "bitcoin";
        List<String> rates = List.of("usd", "eth");
        Map<String, Double> mockedResult = Map.of("usd", 100.0, "eth", 30.0);
        //when
        when(cryptoRateProvider.getRatesForCurrency(coinId, rates)).thenReturn(mockedResult);

        //then
        CurrencyRatesResponse currencyRatesResponse =  currencyRateService.getRates(coinId, rates);

        assertEquals("bitcoin", currencyRatesResponse.getSource());
        assertEquals(2, currencyRatesResponse.getRates().size());
        assertEquals(100.0, currencyRatesResponse.getRates().get("usd"));
        assertEquals(30.0, currencyRatesResponse.getRates().get("eth"));
    }

    @Test
    void shouldThrowException_whenClientException() {
        //given
        String coinId = "bitcoin";
        List<String> rates = List.of("usd", "eth");
        Map<String, Double> mockedResult = Map.of("usd", 100.0, "eth", 30.0);
        //when
        when(cryptoRateProvider.getRatesForCurrency(coinId, rates)).thenThrow(new RuntimeException());

        //then
        ProviderException providerException = assertThrows(
                ProviderException.class,
                () -> currencyRateService.getRates(coinId, rates)
        );

        assertEquals("Failed to retrieve rates for currency: bitcoin", providerException.getMessage());
    }

    @Test
    void shouldThrowApiException_whenFiltersRemoveAllRates() {
        //given
        String baseSymbol = "bitcoin";
        List<String> requestedRates = List.of("usd", "eth");
        Map<String, Double> providerRates = Map.of("btc", 123.45, "ada", 2.34);

        //when
        when(cryptoRateProvider.getRatesForCurrency(baseSymbol, requestedRates))
                .thenReturn(providerRates);

        //then
        ApiException exception = assertThrows(
                ApiException.class,
                () -> currencyRateService.getRates(baseSymbol, requestedRates)
        );

        assertEquals("No matching filtered rates found for currency: " + baseSymbol, exception.getMessage());
    }

    @Test
    void shouldReturnPartiallyFilteredRates_whenSomeRatesMatch() {
        //given
        String baseSymbol = "bitcoin";
        List<String> requestedRates = List.of("usd", "eth", "unknown");
        Map<String, Double> providerRates = Map.of("usd", 100.0, "eth", 200.0, "btc", 300.0);

        //when
        when(cryptoRateProvider.getRatesForCurrency(baseSymbol, requestedRates))
                .thenReturn(providerRates);

        //then
        CurrencyRatesResponse response = currencyRateService.getRates(baseSymbol, requestedRates);

        assertEquals(baseSymbol, response.getSource());
        assertEquals(2, response.getRates().size());
        assertEquals(100.0, response.getRates().get("usd"));
        assertEquals(200.0, response.getRates().get("eth"));
    }
}