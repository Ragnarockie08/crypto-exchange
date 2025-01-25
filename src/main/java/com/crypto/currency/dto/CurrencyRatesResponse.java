package com.crypto.currency.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
public class CurrencyRatesResponse {
    private String source;
    private Map<String, BigDecimal> rates;
}

