package com.crypto.currency.service.rates;

import com.crypto.currency.dto.CurrencyRatesResponse;

import java.util.List;

public interface CurrencyRateService {
    CurrencyRatesResponse getRates(String baseSymbol, List<String> filters);
}

