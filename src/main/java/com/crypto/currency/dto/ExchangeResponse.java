package com.crypto.currency.dto;

import lombok.Data;

import java.util.Map;

@Data
public class ExchangeResponse {

    private String from;
    private Map<String, ExchangeRateDetails> conversions;
}

