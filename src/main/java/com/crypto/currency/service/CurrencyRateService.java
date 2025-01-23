package com.crypto.currency.service;

import com.crypto.currency.dto.CurrencyRatesResponse;

import java.util.List;

public interface CurrencyRateService {
    CurrencyRatesResponse getRates(String baseSymbol, List<String> filters);
}

