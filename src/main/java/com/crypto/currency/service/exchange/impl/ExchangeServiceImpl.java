package com.crypto.currency.service.exchange.impl;

import com.crypto.currency.dto.CurrencyRatesResponse;
import com.crypto.currency.dto.ExchangeForecastDetails;
import com.crypto.currency.dto.ExchangeRequest;
import com.crypto.currency.dto.ExchangeResponse;
import com.crypto.currency.service.rates.CurrencyRateService;
import com.crypto.currency.service.exchange.ExchangeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExchangeServiceImpl implements ExchangeService {

    private final CurrencyRateService currencyRateService;
    private final ExchangeRateCalculator exchangeRateCalculator;

    @Override
    public ExchangeResponse calculateExchange(final ExchangeRequest request) {
        log.debug("Starting exchange calculation for source currency: {}, target currencies: {}, amount: {}",
                request.getFrom(), request.getTo(), request.getAmount());

        CurrencyRatesResponse ratesResponse = currencyRateService.getRates(request.getFrom(), request.getTo());

        Map<String, Double> rateMap = ratesResponse.getRates();

        ExchangeResponse response = populateExchangeDetails(request, rateMap);
        log.debug("Completed exchange calculation for source currency: {}, response: {}", request.getFrom(), response);

        return response;
    }

    private ExchangeResponse populateExchangeDetails(final ExchangeRequest request,
                                         Map<String, Double> rateMap) {

        ExchangeResponse response = new ExchangeResponse();
        response.setFrom(request.getFrom());

        Map<String, ExchangeForecastDetails> detailsMap = request.getTo().stream()
                .filter(targetCurrency -> rateMap.get(targetCurrency) != null) // Skip invalid currencies
                .collect(Collectors.toMap(
                        targetCurrency -> targetCurrency, // Key: target currency name
                        targetCurrency -> buildExchangeForecastDetails(
                                request.getAmount(),
                                rateMap.get(targetCurrency)
                        )
                ));
        response.setConversions(detailsMap);

        return response;
    }

    private ExchangeForecastDetails buildExchangeForecastDetails(double amount,
                                                             double rate) {
        double fee = exchangeRateCalculator.calculateFee(amount);
        double netAmount = exchangeRateCalculator.calculateNetAmount(amount, fee);
        double result = exchangeRateCalculator.calculateResult(netAmount, rate);

        ExchangeForecastDetails details = new ExchangeForecastDetails();
        details.setRate(rate);
        details.setAmount(amount);
        details.setFee(fee);
        details.setResult(result);

        return details;
    }
}

