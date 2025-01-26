package com.crypto.currency.service.exchange.impl;

import com.crypto.currency.dto.CurrencyRatesResponse;
import com.crypto.currency.dto.ExchangeForecastDetails;
import com.crypto.currency.dto.ExchangeRequest;
import com.crypto.currency.dto.ExchangeResponse;
import com.crypto.currency.exception.error.ApiException;
import com.crypto.currency.service.rates.CurrencyRateService;
import com.crypto.currency.service.exchange.ExchangeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExchangeServiceImpl implements ExchangeService {

    private final CurrencyRateService currencyRateService;
    private final ExchangeCalculator exchangeCalculator;

    //TODO: validator for exchange amount should vary from currencyId
    // fiat > spot should have min amount 10 etc.
    // spot > fiat should enable granular amounts for exchange (0.005) etc.
    @Override
    public ExchangeResponse calculateExchange(final ExchangeRequest request) {
        log.debug("Starting exchange calculation for source currency: {}, target currencies: {}, amount: {}",
                request.getFrom(), request.getTo(), request.getAmount());

        CurrencyRatesResponse ratesResponse = currencyRateService.getRates(request.getFrom(), request.getTo());
        Map<String, BigDecimal> rateMap = ratesResponse.getRates();

        ExchangeResponse response = populateExchangeResponse(request, rateMap);
        log.debug("Completed exchange calculation for source currency: {}, response: {}", request.getFrom(), response);

        return response;
    }

    //TODO: for more control over thread management and task scheduling possible use for ExecutorService
    private ExchangeResponse populateExchangeResponse(final ExchangeRequest request,
                                                      Map<String, BigDecimal> rateMap) {

        ExchangeResponse response = new ExchangeResponse();
        response.setFrom(request.getFrom());

        // Use parallelStream() for parallel processing
        Map<String, ExchangeForecastDetails> detailsMap = request.getTo().parallelStream()
                .filter(targetCurrency -> rateMap.get(targetCurrency) != null)
                .collect(Collectors.toConcurrentMap(
                        targetCurrency -> targetCurrency,
                        targetCurrency -> calculateExchangeForecast(
                                request.getAmount(),
                                rateMap.get(targetCurrency)
                        )
                ));

        response.setConversions(detailsMap);

        return response;
    }

    private ExchangeForecastDetails calculateExchangeForecast(BigDecimal amount,
                                                              BigDecimal rate) {
        BigDecimal fee = exchangeCalculator.calculateFee(amount);
        BigDecimal netAmount = exchangeCalculator.calculateNetAmount(amount, fee);
        BigDecimal result = exchangeCalculator.calculateResult(netAmount, rate);

        return new ExchangeForecastDetails(rate, amount, result, fee);
    }
}

