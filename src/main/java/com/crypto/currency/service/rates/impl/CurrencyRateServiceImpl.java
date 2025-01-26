package com.crypto.currency.service.rates.impl;

import com.crypto.currency.dto.CurrencyRatesResponse;
import com.crypto.currency.exception.error.ApiException;
import com.crypto.currency.provider.CryptoProvider;
import com.crypto.currency.service.rates.CurrencyRateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

//TODO: For provider strategy management create ProviderManager
// for selection of api to be used based on some application property
@RequiredArgsConstructor
@Service
@Slf4j
public class CurrencyRateServiceImpl implements CurrencyRateService {

    private final CryptoProvider cryptoProvider;

    @Override
    public CurrencyRatesResponse getRates(String baseSymbol, List<String> rates) {

        if (baseSymbol == null || baseSymbol.isBlank()) {
            log.error("Error base currency symbol must not be null or empty. {}", baseSymbol);
            throw new ApiException("Base currency symbol must not be null or empty.");
        }

        log.debug("Fetching rates for baseSymbol={} with filters={}", baseSymbol, rates);
        Map<String, BigDecimal> rawRates = cryptoProvider.getRatesForCurrency(baseSymbol, rates);

        if (rawRates == null || rawRates.isEmpty()) {
            log.error("No rates found for baseSymbol={}", baseSymbol);
            throw new ApiException("No rates found for currency: " + baseSymbol);
        }

        CurrencyRatesResponse response = new CurrencyRatesResponse();
        response.setSource(baseSymbol);
        response.setRates(rawRates);

        log.debug("Rates retrieved successfully for baseSymbol={}, totalRates={}",
                baseSymbol, rawRates.size());

        return response;
    }
}

