package com.crypto.currency.dto;

import lombok.Data;

@Data
public class ExchangeRateDetails {
    private Double rate;
    private Double amount;
    private Double result;
    private Double fee;
}

