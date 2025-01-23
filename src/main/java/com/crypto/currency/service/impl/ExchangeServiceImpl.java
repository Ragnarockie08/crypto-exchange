package com.crypto.currency.service.impl;

import com.crypto.currency.dto.CurrencyRatesResponse;
import com.crypto.currency.dto.ExchangeRateDetails;
import com.crypto.currency.dto.ExchangeRequest;
import com.crypto.currency.dto.ExchangeResponse;
import com.crypto.currency.service.CurrencyRateService;
import com.crypto.currency.service.ExchangeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExchangeServiceImpl implements ExchangeService {

    public static final double FEE_VALUE = 0.01;
    private final CurrencyRateService currencyRateService;

    @Override
    public ExchangeResponse calculateExchange(ExchangeRequest request) {
        log.debug("Starting exchange calculation for source currency: {}, target currencies: {}, amount: {}",
                request.getFrom(), request.getTo(), request.getAmount());

        CurrencyRatesResponse ratesResponse = currencyRateService.getRates(request.getFrom(), request.getTo());

        Map<String, Double> rateMap = ratesResponse.getRates();

        ExchangeResponse response = populateExchangeDetails(request, rateMap);
        log.debug("Completed exchange calculation for source currency: {}, response: {}", request.getFrom(), response);

        return response;
    }

    private ExchangeResponse populateExchangeDetails(ExchangeRequest request,
                                         Map<String, Double> rateMap) {

        ExchangeResponse response = new ExchangeResponse();
        response.setFrom(request.getFrom());

        Map<String, ExchangeRateDetails> detailsMap = request.getTo().stream()
                .filter(targetCurrency -> rateMap.get(targetCurrency) != null) // Skip invalid currencies
                .collect(Collectors.toMap(
                        targetCurrency -> targetCurrency, // Key: target currency name
                        targetCurrency -> buildExchangeRateDetails(
                                request.getAmount(),
                                rateMap.get(targetCurrency)
                        )
                ));
        response.setConversions(detailsMap);

        return response;
    }

    private ExchangeRateDetails buildExchangeRateDetails(double amount,
                                                         double rate) {
        double feeInSource = calculateFee(amount);
        double netAmountInSource = getNetAmountInSource(amount, feeInSource);
        double resultInTarget = getResultInTarget(rate, netAmountInSource);

        ExchangeRateDetails details = new ExchangeRateDetails();
        details.setRate(rate);
        details.setAmount(amount);
        details.setFee(feeInSource);
        details.setResult(resultInTarget);

        return details;
    }

    private double getNetAmountInSource(double amount, double feeInSource) {
        return amount - feeInSource;
    }

    private double getResultInTarget(double rate, double netAmountInSource) {
        return netAmountInSource * rate;
    }

    private double calculateFee(double amount) {
        return getResultInTarget(FEE_VALUE, amount);
    }
}

