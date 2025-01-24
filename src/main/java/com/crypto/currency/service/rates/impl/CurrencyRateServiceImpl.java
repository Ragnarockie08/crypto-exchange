package com.crypto.currency.service.rates.impl;

import com.crypto.currency.dto.CurrencyRatesResponse;
import com.crypto.currency.exception.error.ApiException;
import com.crypto.currency.exception.error.ProviderException;
import com.crypto.currency.provider.CryptoRateProvider;
import com.crypto.currency.service.rates.CurrencyRateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

//TODO: Create separate validator service
@RequiredArgsConstructor
@Service
@Slf4j
public class CurrencyRateServiceImpl implements CurrencyRateService {

    private final CryptoRateProvider cryptoRateProvider;

    @Override
    public CurrencyRatesResponse getRates(String baseSymbol, List<String> rates) {

        if (baseSymbol == null || baseSymbol.isBlank()) {
            throw new ApiException("Base currency symbol must not be null or empty.");
        }

        log.info("Fetching rates for baseSymbol={} with filters={}", baseSymbol, rates);
        Map<String, Double> rawRates;
        try {
            rawRates = cryptoRateProvider.getRatesForCurrency(baseSymbol, rates);
        } catch (Exception e) {
            log.error("Error fetching rates from provider for symbol={}", baseSymbol, e);
            throw new ProviderException("Failed to retrieve rates for currency: " + baseSymbol, e);
        }

        if (rawRates == null || rawRates.isEmpty()) {
            log.warn("No rates found for baseSymbol={}", baseSymbol);
            throw new ProviderException("No rates found for currency: " + baseSymbol);
        }

        if (rates != null && !rates.isEmpty()) {
            List<String> lowerFilters = rates.stream()
                    .map(String::toLowerCase)
                    .collect(Collectors.toList());

            rawRates = rawRates.entrySet().stream()
                    .filter(entry -> lowerFilters.contains(entry.getKey().toLowerCase()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            if (rawRates.isEmpty()) {
                log.warn("Filters={} removed all rates for baseSymbol={}", rates, baseSymbol);
                throw new ApiException("No matching filtered rates found for currency: " + baseSymbol);
            }
        }

        CurrencyRatesResponse response = new CurrencyRatesResponse();
        response.setSource(baseSymbol);
        response.setRates(rawRates);

        log.debug("Rates retrieved successfully for baseSymbol={}, totalRates={}",
                baseSymbol, rawRates.size());

        return response;
    }
}

