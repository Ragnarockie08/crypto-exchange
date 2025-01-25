package com.crypto.currency.service.impl;

import com.crypto.currency.dto.CurrencyRatesResponse;
import com.crypto.currency.dto.ExchangeForecastDetails;
import com.crypto.currency.dto.ExchangeRequest;
import com.crypto.currency.dto.ExchangeResponse;
import com.crypto.currency.exception.error.ApiException;
import com.crypto.currency.service.rates.CurrencyRateService;
import com.crypto.currency.service.exchange.impl.ExchangeCalculator;
import com.crypto.currency.service.exchange.impl.ExchangeServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExchangeServiceImplTest {

    @Mock
    private CurrencyRateService currencyRateService;
    @Spy
    private ExchangeCalculator exchangeCalculator;
    @InjectMocks
    private ExchangeServiceImpl exchangeService;

    @Test
    void shouldCalculateExchangeForecast_WhenValidData() {
        //given
        ExchangeRequest request = new ExchangeRequest(
                "usd",
                List.of("eur", "btc"),
                new BigDecimal("100.0")
        );

        Map<String, BigDecimal> mockRates = Map.of(
                "eur", new BigDecimal("0.84"),
                "btc", new BigDecimal("0.00000954")
        );
        CurrencyRatesResponse ratesResponse = new CurrencyRatesResponse();
        ratesResponse.setRates(mockRates);

        //when
        when(currencyRateService.getRates("usd", List.of("eur", "btc"))).thenReturn(ratesResponse);

        ExchangeResponse response = exchangeService.calculateExchange(request);

        //then
        assertEquals("usd", response.getFrom());
        assertEquals(2, response.getConversions().size());

        ExchangeForecastDetails eurDetails = response.getConversions().get("eur");
        assertEquals(0, eurDetails.getRate().compareTo(new BigDecimal("0.84")));
        assertEquals(0, eurDetails.getAmount().compareTo(new BigDecimal("100.0")));
        assertEquals(0, eurDetails.getFee().compareTo(new BigDecimal("1.000")));
        assertEquals(0, eurDetails.getResult().compareTo(new BigDecimal("83.16000")));

        ExchangeForecastDetails btcDetails = response.getConversions().get("btc");
        assertEquals(0, btcDetails.getRate().compareTo(new BigDecimal("0.00000954")));
        assertEquals(0, btcDetails.getAmount().compareTo(new BigDecimal("100.0")));
        assertEquals(0, btcDetails.getFee().compareTo(new BigDecimal("1.000")));
        assertEquals(0, btcDetails.getResult().compareTo(new BigDecimal("0.00094446")));

        verify(currencyRateService).getRates("usd", List.of("eur", "btc"));
    }

    @Test
    void shouldThrowApiException_WhenBaseCurrencyIsBlank() {
        //given
        ExchangeRequest request = new ExchangeRequest("", List.of("eur", "btc"), new BigDecimal("1.0"));

        //when
        when(currencyRateService.getRates("", List.of("eur", "btc")))
                .thenThrow(new ApiException("Base currency symbol must not be null or empty."));

        //then
        ApiException exception = assertThrows(ApiException.class,
                () -> exchangeService.calculateExchange(request));
        assertEquals("Base currency symbol must not be null or empty.", exception.getMessage());
    }
}

