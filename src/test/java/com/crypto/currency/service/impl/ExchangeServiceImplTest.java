package com.crypto.currency.service.impl;

import com.crypto.currency.dto.CurrencyRatesResponse;
import com.crypto.currency.dto.ExchangeRateDetails;
import com.crypto.currency.dto.ExchangeRequest;
import com.crypto.currency.dto.ExchangeResponse;
import com.crypto.currency.service.CurrencyRateService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExchangeServiceImplTest {

    @Mock
    private CurrencyRateService currencyRateService;

    @InjectMocks
    private ExchangeServiceImpl exchangeService;

    @Test
    void shouldCalculateExchangeForecast_WhenValidData() {
        //given
        ExchangeRequest request = new ExchangeRequest("usd", List.of("eur", "btc"), 100.0);

        Map<String, Double> mockRates = Map.of(
                "eur", 0.84,
                "btc", 0.00000954
        );
        CurrencyRatesResponse ratesResponse = new CurrencyRatesResponse();
        ratesResponse.setRates(mockRates);

        //when
        when(currencyRateService.getRates("usd", List.of("eur", "btc"))).thenReturn(ratesResponse);

        ExchangeResponse response = exchangeService.calculateExchange(request);

        //then
        assertEquals("usd", response.getFrom());
        assertEquals(2, response.getConversions().size());

        ExchangeRateDetails eurDetails = response.getConversions().get("eur");

        assertEquals(0.84, eurDetails.getRate());
        assertEquals(100.0, eurDetails.getAmount());
        assertEquals(1.0, eurDetails.getFee());
        assertEquals(83.16, eurDetails.getResult());

        ExchangeRateDetails btcDetails = response.getConversions().get("btc");

        assertEquals(0.00000954, btcDetails.getRate());
        assertEquals(100.0, btcDetails.getAmount());
        assertEquals(1.0, btcDetails.getFee());
        assertEquals(0.00094446, btcDetails.getResult());

        verify(currencyRateService).getRates("usd", List.of("eur", "btc"));
    }
}
