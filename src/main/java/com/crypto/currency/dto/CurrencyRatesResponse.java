package com.crypto.currency.dto;

import lombok.Data;

import java.util.Map;

@Data
public class CurrencyRatesResponse {
    private String source;
    private Map<String, Double> rates;
}

