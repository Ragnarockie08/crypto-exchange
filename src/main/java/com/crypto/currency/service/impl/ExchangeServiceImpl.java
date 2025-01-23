package com.crypto.currency.service.impl;

import com.crypto.currency.dto.ExchangeRequest;
import com.crypto.currency.dto.ExchangeResponse;
import com.crypto.currency.service.CurrencyRateService;
import com.crypto.currency.service.ExchangeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExchangeServiceImpl implements ExchangeService {

    private final CurrencyRateService currencyRateService;

    @Override
    public ExchangeResponse calculateExchange(ExchangeRequest request) {

        return null;
    }
}

