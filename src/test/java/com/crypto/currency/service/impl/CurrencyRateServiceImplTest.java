package com.crypto.currency.service.impl;

import com.crypto.currency.dto.CurrencyRatesResponse;
import com.crypto.currency.exception.error.ApiException;
import com.crypto.currency.exception.error.ProviderException;
import com.crypto.currency.provider.CryptoProvider;
import com.crypto.currency.service.rates.impl.CurrencyRateServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CurrencyRateServiceImplTest {

    @Mock
    private CryptoProvider cryptoProvider;
    @InjectMocks
    private CurrencyRateServiceImpl currencyRateService;

    @Test
    void shouldGetRates_whenValidParams() {
        //given
        String coinId = "bitcoin";
        List<String> rates = List.of("usd", "eth");
        Map<String, BigDecimal> mockedResult = Map.of("usd", new BigDecimal("100.0"), "eth", new BigDecimal("30.0"));
        //when
        when(cryptoProvider.getRatesForCurrency(coinId, rates)).thenReturn(mockedResult);

        //then
        CurrencyRatesResponse currencyRatesResponse =  currencyRateService.getRates(coinId, rates);

        assertEquals("bitcoin", currencyRatesResponse.getSource());
        assertEquals(2, currencyRatesResponse.getRates().size());
        assertEquals(new BigDecimal("100.0"), currencyRatesResponse.getRates().get("usd"));
        assertEquals(new BigDecimal("30.0"), currencyRatesResponse.getRates().get("eth"));
    }

    @Test
    void shouldThrowException_whenClientException() {
        //given
        String coinId = "bitcoin";
        List<String> rates = List.of("usd", "eth");

        //when
        when(cryptoProvider.getRatesForCurrency(coinId, rates)).thenThrow(new ProviderException("Failed to retrieve rates for currency: bitcoin"));

        //then
        ProviderException providerException = assertThrows(
                ProviderException.class,
                () -> currencyRateService.getRates(coinId, rates)
        );

        assertEquals("Failed to retrieve rates for currency: bitcoin", providerException.getMessage());
    }
}