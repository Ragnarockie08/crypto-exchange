package com.crypto.currency.service.exchange;

import com.crypto.currency.dto.ExchangeRequest;
import com.crypto.currency.dto.ExchangeResponse;

public interface ExchangeService {
    /**
     * Calculates the exchange amounts from 'fromCurrency' to each of the
     * 'toCurrencies' at the current rate, applying a fee on the 'fromCurrency' side.
     *
     * @param request ExchangeRequest containing source currency, target currencies, amount.
     * @return ExchangeResponse containing the detailed conversion results.
     */
    ExchangeResponse calculateExchange(ExchangeRequest request);
}

